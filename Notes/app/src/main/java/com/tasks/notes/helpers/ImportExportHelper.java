package com.tasks.notes.helpers;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.lang.reflect.Type;

public class ImportExportHelper {
    public final static Gson gsonSerializer = new GsonBuilder()
            .registerTypeAdapter(Note.class, new NoteSerializer())
            .create();
    public final static String FILE_NAME = "itemlist.ili";

    public static Note[] importNotes(Context context, @NonNull String filename) {
        try {
            if (!isExternalStorageReadable()) {
                Toast.makeText(context, "External storage is not readable", Toast.LENGTH_SHORT).show();
                return null;
            }
            try (FileInputStream fis = new FileInputStream(filename)) {
                return jsonToNotes(getFileContent(fis, "UTF-8"));
            }
        } catch (IOException e) {
            Toast.makeText(context, "No such file", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    public static void exportNotes(Context context, Note[] notes) {
        try {
            if (!isExternalStorageWritable()) {
                Toast.makeText(context, "External storage is not writable", Toast.LENGTH_SHORT).show();
                return;
            }

            File dir = Environment.getExternalStorageDirectory();
            File notesFile = new File(dir, FILE_NAME);
            notesFile.createNewFile();

            try (PrintStream ps = new PrintStream(
                    new FileOutputStream(notesFile.getAbsolutePath()))) {
                String json = notesToJson(notes);
                ps.print(json);
            }

            Toast.makeText(context, String.format(
                    "Notes saved to %s", notesFile.getCanonicalPath()),
                    Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(context, "No such file", Toast.LENGTH_SHORT).show();
        }
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

    public static String getFileContent(FileInputStream fis, String encoding)
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
