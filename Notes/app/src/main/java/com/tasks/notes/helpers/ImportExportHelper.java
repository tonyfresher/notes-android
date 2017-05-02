package com.tasks.notes.helpers;


import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.tasks.notes.Note;
import com.tasks.notes.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.lang.reflect.Type;

public class ImportExportHelper {
    public final static Gson gsonSerializer = new GsonBuilder()
            .registerTypeAdapter(Note.class, new NoteSerializer())
            .create();
    public final static String FILE_NAME = "itemlist.ili";

    public static void importNotes(Context context, @NonNull Uri uri)
            throws IllegalAccessException, IOException {
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

    public static Note[] jsonToNotes(String json) {
        return gsonSerializer.fromJson(json, Note[].class);
    }

    public static class NoteSerializer implements JsonSerializer<Note>, JsonDeserializer<Note> {
        @Override
        public JsonElement serialize(final Note note, final Type type, final JsonSerializationContext context) {
            JsonObject result = new JsonObject();
            result.add("title", new JsonPrimitive(note.getTitle()));
            result.add("description", new JsonPrimitive(note.getDescription()));
            result.add("color", new JsonPrimitive(
                    String.format("#%06X", (0xFFFFFF & note.getColor()))));
            result.add("created", new JsonPrimitive(note.getCreated()));
            result.add("edited", new JsonPrimitive(note.getEdited()));
            result.add("viewed", new JsonPrimitive(note.getViewed()));
            return result;
        }

        @Override
        public Note deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            JsonObject jObject = json.getAsJsonObject();
            String title = jObject.get("title").getAsString();
            String description = jObject.get("description").getAsString();
            int color = Color.parseColor(jObject.get("color").getAsString());
            String created = jObject.get("created").getAsString();
            String edited = jObject.get("edited").getAsString();
            String viewed = jObject.get("viewed").getAsString();
            return new Note(0, title, description, color, created, edited, viewed);
        }
    }
}
