package com.tasks.notes.helpers;


import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.tasks.notes.classes.Filter;
import com.tasks.notes.classes.Note;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Comparator;

public class FileSystemHelper {
    public final static Gson GSON_SERIALIZER = new GsonBuilder()
            .registerTypeAdapter(Note.class, new Note.Serializer())
            .registerTypeAdapter(Filter.class, new Filter.Serializer())
            .create();
    public final static String FILE_NAME = "itemlist.ili";


    public static void importNotes(Context context, @NonNull Uri uri)
            throws IllegalAccessException, IOException, JsonParseException {
        if (!isExternalStorageReadable()) {
            throw new IllegalAccessException();
        }

        ContentResolver cr = context.getContentResolver();
        try (InputStream fis = cr.openInputStream(uri)) {
            String input = getInputContent(fis, "UTF-8");
            DatabaseHelper dh = new DatabaseHelper(context);
            Comparator<Note> comparator = Note.BY_CREATED_DESCENDING_COMPARATOR;

            Note[] notesToImport = jsonToNotes(input);
            Arrays.sort(notesToImport, comparator);

            Note[] notesInDb = dh.getOrderedItems(comparator);

            insertion:
            for (Note that : notesToImport) {
                for (Note other : notesInDb) {
                    if (that.contentEquals(other)) {
                        continue insertion;
                    }
                }
                dh.insertAsync(that);
            }
        }
    }

    public AsyncTask<Object, Integer, Void> importNotesAsync(Context context, @NonNull Uri uri) {
        AsyncTask<Object, Integer, Void> t = new AsyncTask<Object, Integer, Void>() {
            @Override
            protected Void doInBackground(Object... params) {
                final Context context = (Context) params[0];
                final Uri uri = (Uri) params[1];

                try {
                    importNotes(context, uri);
                } catch (IllegalAccessException | IOException | JsonParseException e) {
                    cancel(true);
                }
                return null;
            }
        };

        return t.execute(context, uri);
    }

    public static String exportNotes(Note[] notes)
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

    public static String notesToJson(Note[] notes) {
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
