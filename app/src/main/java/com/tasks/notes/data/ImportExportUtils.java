package com.tasks.notes.data;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.tasks.notes.R;
import com.tasks.notes.data.model.Filter;
import com.tasks.notes.data.model.Note;
import com.tasks.notes.data.storage.StorageProvider;
import com.tasks.notes.ui.infrastructure.NotificationWrapper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class ImportExportUtils {

    public final static Gson GSON_SERIALIZER = new GsonBuilder()
            .registerTypeAdapter(Note.class, new Note.Serializer())
            .registerTypeAdapter(Filter.class, new Filter.Serializer())
            .create();

    public final static String FILE_NAME = "itemlist.ili";

    public final static int STATUS_SUCCESSFULLY_IMPORTED = 0;
    public final static int STATUS_SUCCESSFULLY_EXPORTED = 1;
    public final static int STATUS_CANT_READ = 2;
    public final static int STATUS_WRONG_FILE = 3;

    public final static int ACTION_IMPORT = 42;
    public final static int ACTION_EXPORT = 24;

    public static final int NOTIFICATION_ID_IMPORT = 6;
    public static final int NOTIFICATION_ID_EXPORT = 7;

    public final static String ARG_STATUS = "status";
    private final static String ARG_URI = "uri";
    private final static String ARG_NOTES = "notes";
    private final static String ARG_EXPORTED_PATH = "exported_path";

    @Inject
    private static void importNotes(Context context, StorageProvider provider, Uri uri)
            throws IllegalAccessException, IOException, JsonParseException {
        if (!isExternalStorageReadable()) {
            throw new IllegalAccessException();
        }

        try (InputStream fis = context.getContentResolver().openInputStream(uri)) {
            String input = getInputContent(fis, "UTF-8");
            Note[] notesToImport = jsonToNotes(input);
            List<Note> notesInDb = provider.getAll();

            insertion:
            for (Note that : notesToImport) {
                for (Note other : notesInDb) {
                    if (that.contentEquals(other)) {
                        continue insertion;
                    }
                }
                provider.save(that);
            }
        }
    }

    private void obtainImportMessage(Message msg) {
        Uri uri = msg.getData().getParcelable(ARG_URI);

        Bundle args = new Bundle();
        try {
            importNotes(contentResolver, databaseProvider, uri);
            args.putInt(ARG_STATUS, STATUS_SUCCESSFULLY_IMPORTED);
        } catch (IllegalAccessException e) {
            args.putInt(ARG_STATUS, STATUS_CANT_READ);
        } catch (IOException | JsonParseException e) {
            args.putInt(ARG_STATUS, STATUS_WRONG_FILE);
        }

        Message reply = mainHandler.obtainMessage(ACTION_IMPORT, msg.obj);
        reply.setData(args);
        reply.sendToTarget();
    }


    private static synchronized String exportNotes(StorageProvider )
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

    public void sendExportMessage(List<Note> notes, NotificationWrapper notification) {
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_NOTES, (ArrayList<Note>) notes);

        Message msg = workerHandler.obtainMessage(ACTION_EXPORT);
        msg.setData(args);
        msg.obj = notification;

        msg.sendToTarget();
    }

    private void obtainExportMessage(Message msg) {
        List<Note> notes = msg.getData().getParcelableArrayList(ARG_NOTES);

        Bundle args = new Bundle();
        try {
            String path = exportNotes(notes);
            args.putInt(ARG_STATUS, STATUS_SUCCESSFULLY_EXPORTED);
            args.putString(ARG_EXPORTED_PATH, path);
        } catch (IllegalAccessException e) {
            args.putInt(ARG_STATUS, STATUS_CANT_READ);
        } catch (IOException | JsonParseException e) {
            args.putInt(ARG_STATUS, STATUS_WRONG_FILE);
        }

        Message reply = mainHandler.obtainMessage(ACTION_EXPORT, msg.obj);
        reply.setData(args);
        reply.sendToTarget();
    }

    public void close() {
        handlerThread.quit();
    }


    public static String statusToString(Context context, int status) {
        switch (status) {
            case STATUS_SUCCESSFULLY_IMPORTED:
                return context.getString(R.string.successfully_imported);
            case STATUS_SUCCESSFULLY_EXPORTED:
                return context.getString(R.string.successfully_exported_to);
            case STATUS_CANT_READ:
                return context.getString(R.string.cant_read);
            case STATUS_WRONG_FILE:
                return context.getString(R.string.wrong_file);
        }
        throw new IllegalArgumentException();
    }


    private static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    private static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }


    private static String getInputContent(InputStream fis, String encoding)
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

    private static String notesToJson(List<Note> notes) {
        return GSON_SERIALIZER.toJson(notes);
    }

    private static Note[] jsonToNotes(String json)
            throws JsonParseException {
        Note[] notes = GSON_SERIALIZER.fromJson(json, Note[].class);
        if (notes == null)
            throw new JsonParseException("Wrong format");
        return notes;
    }
}