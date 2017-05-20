package com.tasks.notes.helpers;


import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.tasks.notes.R;
import com.tasks.notes.classes.Filter;
import com.tasks.notes.classes.Note;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

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

            List<Note> notesInDb = dh.getOrderedItems(comparator);

            insertion:
            for (Note that : notesToImport) {
                for (Note other : notesInDb) {
                    if (that.contentEquals(other)) {
                        continue insertion;
                    }
                }
                dh.insert(that);
            }
        }
    }

    public static HandyTask<Object, String> importNotesTask() {
        return new HandyTask<>(params -> {
            final Context context = (Context) params[0];
            final Uri uri = (Uri) params[1];

            try {
                importNotes(context, uri);
                return context.getString(R.string.successfully_imported);
            } catch (IllegalAccessException e) {
                return context.getString(R.string.cant_read);
            } catch (IOException | JsonParseException e) {
                return context.getString(R.string.wrong_file);
            }
        });
    }

    public static AsyncTask<Object, Integer, String> importNotesAsync(Context context, @NonNull Uri uri) {
        return importNotesTask().execute(context, uri);
    }


    public static String exportNotes(List<Note> notes)
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

    public static HandyTask<Object, String> exportNotesTask() {
        return new HandyTask<>(params -> {
            Context context = (Context) params[0];
            List<Note> notes = (List<Note>) params[1];

            try {
                exportNotes(notes);
                return context.getString(R.string.successfully_imported);
            } catch (IllegalAccessException e) {
                return context.getString(R.string.cant_read);
            } catch (IOException | JsonParseException e) {
                return context.getString(R.string.wrong_file);
            }
        });
    }

    public static AsyncTask<Object, Integer, String> exportNotesAsync(Context context, List<Note> notes) {
        return exportNotesTask().execute(context, notes);
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
