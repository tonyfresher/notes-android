package com.tasks.notes.helpers;


import android.content.ContentResolver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.tasks.notes.ListFragment;
import com.tasks.notes.R;
import com.tasks.notes.classes.Filter;
import com.tasks.notes.classes.Note;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class ImportExportHelper implements Closeable {

    private static ImportExportHelper instance;

    public static ImportExportHelper getInstance(ListFragment fragment) {
        if (instance == null) {
            instance = new ImportExportHelper(fragment);
        }
        return instance;
    }

    public final static Gson GSON_SERIALIZER = new GsonBuilder()
            .registerTypeAdapter(Note.class, new Note.Serializer())
            .registerTypeAdapter(Filter.class, new Filter.Serializer())
            .create();

    public final static String FILE_NAME = "itemlist.ili";

    public final static int ACTION_IMPORT = 42;
    public final static int ACTION_EXPORT = 24;

    private static final int NOTIFICATION_ID_IMPORT = 6;
    private static final int NOTIFICATION_ID_EXPORT = 7;

    public final static String ARG_URI = "uri";
    public final static String ARG_NOTES = "notes";
    public final static String ARG_STATUS = "status";

    private final ContentResolver contentResolver;
    private final DatabaseHelper databaseHelper;
    private WeakReference<ListFragment> fragmentRef;

    private final HandlerThread handlerThread;
    private final Handler workerHandler;
    private final Handler mainHandler;

    private ImportExportHelper(ListFragment fragment) {
        this.fragmentRef = new WeakReference<>(fragment);
        contentResolver = fragment.getContext().getContentResolver();
        databaseHelper = DatabaseHelper.getInstance(fragment.getContext());

        mainHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case ImportExportHelper.ACTION_IMPORT: {
                        ListFragment fragment = fragmentRef.get();
                        if (fragment != null) {
                            fragment.refreshList();
                            Toast.makeText(fragment.getContext(),
                                    msg.getData().getString(ImportExportHelper.ARG_STATUS),
                                    Toast.LENGTH_SHORT).show();
                        }

                        if (msg.obj != null) {
                            NotificationEnvelope notification = (NotificationEnvelope) msg.obj;
                            notification.close();
                        }
                        break;
                    }
                    case ImportExportHelper.ACTION_EXPORT: {
                        if (fragmentRef.get() != null) {
                            Toast.makeText(fragmentRef.get().getContext(),
                                    msg.getData().getString(ImportExportHelper.ARG_STATUS),
                                    Toast.LENGTH_SHORT).show();
                        }

                        if (msg.obj != null) {
                            NotificationEnvelope notification = (NotificationEnvelope) msg.obj;
                            notification.close();
                        }
                        break;
                    }
                }
            }
        };

        handlerThread = new HandlerThread("import/export");
        handlerThread.start();
        workerHandler = new Handler(handlerThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case ACTION_IMPORT:
                        obtainImportMessage(msg);
                        break;
                    case ACTION_EXPORT:
                        obtainExportMessage(msg);
                        break;
                }
            }
        };
    }

    private static synchronized void importNotes(ContentResolver resolver,
                                                 DatabaseHelper dbHelper, Uri uri)
            throws IllegalAccessException, IOException, JsonParseException {
        if (!isExternalStorageReadable()) {
            throw new IllegalAccessException();
        }

        try (InputStream fis = resolver.openInputStream(uri)) {
            String input = getInputContent(fis, "UTF-8");
            Note[] notesToImport = jsonToNotes(input);
            List<Note> notesInDb =
                    dbHelper.getOrderedItems(Note.BY_CREATED_DESCENDING_COMPARATOR);

            insertion:
            for (Note that : notesToImport) {
                for (Note other : notesInDb) {
                    if (that.contentEquals(other)) {
                        continue insertion;
                    }
                }
                dbHelper.insert(that);
            }
        }
    }

    public void sendImportMessage(Uri uri) {
        Bundle args = new Bundle();
        args.putParcelable(ARG_URI, uri);

        Message msg = Message.obtain(workerHandler, ACTION_IMPORT);
        msg.setData(args);

        if (fragmentRef.get() != null) {
            final NotificationEnvelope notification = new NotificationEnvelope(
                    fragmentRef.get().getContext(), NOTIFICATION_ID_IMPORT, "Import", true);
            notification.start();
            msg.obj = notification;
        }

        msg.sendToTarget();
    }

    private void obtainImportMessage(Message msg) {
        Uri uri = msg.getData().getParcelable(ARG_URI);

        Bundle args = new Bundle();
        try {
            importNotes(contentResolver, databaseHelper, uri);
            if (fragmentRef.get() != null) {
                args.putString(ARG_STATUS,
                        fragmentRef.get().getString(R.string.successfully_imported));
            }
        } catch (IllegalAccessException e) {
            if (fragmentRef.get() != null) {
                args.putString(ARG_STATUS,
                        fragmentRef.get().getString(R.string.cant_read));
            }
        } catch (IOException | JsonParseException e) {
            if (fragmentRef.get() != null) {
                args.putString(ARG_STATUS,
                        fragmentRef.get().getString(R.string.wrong_file));
            }
        }

        Message reply = mainHandler.obtainMessage(ACTION_IMPORT, msg.obj);
        reply.setData(args);
        reply.sendToTarget();
    }


    private static synchronized String exportNotes(List<Note> notes)
            throws IllegalAccessException, IOException {
        if (!isExternalStorageWritable()) {
            throw new IllegalAccessException();
        }

        File dir = Environment.getExternalStorageDirectory();
        File file = new File(dir, FILE_NAME);
        file.createNewFile();

        try (PrintStream ps = new PrintStream(
                new FileOutputStream(file.getAbsolutePath()))) {
            String json = notesToJson(notes);
            ps.print(json);
        }

        return file.getCanonicalPath();
    }

    public void sendExportMessage(List<Note> notes) {
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_NOTES, (ArrayList<Note>) notes);

        Message msg = workerHandler.obtainMessage(ACTION_EXPORT);
        msg.setData(args);

        if (fragmentRef.get() != null) {
            final NotificationEnvelope notification = new NotificationEnvelope(
                    fragmentRef.get().getContext(), NOTIFICATION_ID_EXPORT, "Export", true);
            notification.start();
            msg.obj = notification;
        }

        msg.sendToTarget();
    }

    private void obtainExportMessage(Message msg) {
        List<Note> notes = msg.getData().getParcelableArrayList(ARG_NOTES);

        Bundle args = new Bundle();
        try {
            String path = exportNotes(notes);
            if (fragmentRef.get() != null) {
                args.putString(ARG_STATUS,
                        fragmentRef.get().getString(R.string.successfully_exported_to) + path);
            }
        } catch (IllegalAccessException e) {
            if (fragmentRef.get() != null) {
                args.putString(ARG_STATUS,
                        fragmentRef.get().getString(R.string.cant_read));
            }
        } catch (IOException | JsonParseException e) {
            if (fragmentRef.get() != null) {
                args.putString(ARG_STATUS,
                        fragmentRef.get().getString(R.string.wrong_file));
            }
        }

        Message reply = mainHandler.obtainMessage(ACTION_EXPORT, msg.obj);
        reply.setData(args);
        reply.sendToTarget();
    }


    public void close() {
        handlerThread.quit();
    }


    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }


    public static String getInputContent(InputStream fis, String encoding)
            throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(fis, encoding))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        }
    }

    public static String notesToJson(List<Note> notes) {
        return GSON_SERIALIZER.toJson(notes);
    }

    public static Note[] jsonToNotes(String json)
            throws JsonParseException {
        Note[] notes = GSON_SERIALIZER.fromJson(json, Note[].class);
        if (notes == null)
            throw new JsonParseException("Wrong format");
        return notes;
    }
}