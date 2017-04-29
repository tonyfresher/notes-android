package com.tasks.notes.helpers;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;
import com.tasks.notes.Note;

import java.lang.reflect.Type;
import java.util.Collection;

public class ImportExportHelper {
    public static Gson gsonSerializer = new GsonBuilder()
            .registerTypeAdapter(Note.class, new NoteSerializer())
            .create();

    public static String notesToJson(Note[] notes) {
        StringBuilder sb = new StringBuilder();
        for (Note note : notes) {
            sb.append(gsonSerializer.toJson(note));
        }
        return sb.toString();
    }

    /*public static Collection<Note> jsonToNotes(String json) {
        Type t = new TypeToken<Collection<Note>>(){}.getType();
        return gsonSerializer.fromJson(json, t);
    }*/

    public static class NoteSerializer implements JsonSerializer<Note> {
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
    }
}
