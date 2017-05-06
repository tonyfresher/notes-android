package com.tasks.notes.helpers;


import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.tasks.notes.classes.Filter;
import com.tasks.notes.classes.Note;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;

public class FileSystemHelper {
    public final static Gson gsonSerializer = new GsonBuilder()
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
            Note[] notes = jsonToNotes(input);

            DatabaseHelper dh = new DatabaseHelper(context);
            for (Note note : notes) {
                dh.insert(note);
            }
        }
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
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
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
        return gsonSerializer.toJson(notes);
    }

    public static Note[] jsonToNotes(String json)
            throws JsonParseException {
        Note[] notes = gsonSerializer.fromJson(json, Note[].class);
        if (notes == null)
            throw new JsonParseException("Wrong format");
        return notes;
    }
}
