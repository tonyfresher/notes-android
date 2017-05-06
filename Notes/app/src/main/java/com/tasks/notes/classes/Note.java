package com.tasks.notes.classes;

import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.Comparator;
import java.util.Date;

import com.tasks.notes.helpers.ColorsHelper;

import static com.tasks.notes.helpers.DateHelper.ISO8601_DATE_FORMAT;

public class Note implements Serializable, Parcelable {
    public final static String INTENT_EXTRA = "Note";

    private long id;
    private String title;
    private String description;
    private int color;

    private String created;
    private String edited;
    private String viewed;

    public Note() {
        color = ColorsHelper.DEFAULT_COLOR;
        String now = ISO8601_DATE_FORMAT.format(new Date());
        created = now;
        edited = now;
        viewed = now;
    }

    public Note(long id, String title, String description, int color,
                String created, String edited, String viewed) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.color = color;
        this.created = created;
        this.edited = edited;
        this.viewed = viewed;
    }

    protected Note(Parcel in) {
        id = in.readLong();
        title = in.readString();
        description = in.readString();
        color = in.readInt();
        created = in.readString();
        edited = in.readString();
        viewed = in.readString();

    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getEdited() {
        return edited;
    }

    public void setEdited(String edited) {
        this.edited = edited;
    }

    public String getViewed() {
        return viewed;
    }

    public void setViewed(String viewed) {
        this.viewed = viewed;
    }


    public static final Creator<Note> CREATOR = new Creator<Note>() {
        @Override
        public Note createFromParcel(Parcel in) {
            return new Note(in);
        }

        @Override
        public Note[] newArray(int size) {
            return new Note[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeInt(color);
        dest.writeString(created);
        dest.writeString(edited);
        dest.writeString(viewed);
    }

    public static class Serializer implements JsonSerializer<Note>, JsonDeserializer<Note> {
        @Override
        public JsonElement serialize(final Note note, final Type type, final JsonSerializationContext context) {
            JsonObject result = new JsonObject();
            result.add("title", new JsonPrimitive(note.title));
            result.add("description", new JsonPrimitive(note.description));
            result.add("color", new JsonPrimitive(
                    String.format("#%06X", (0xFFFFFF & note.color))));
            result.add("created", new JsonPrimitive(note.created));
            result.add("edited", new JsonPrimitive(note.edited));
            result.add("viewed", new JsonPrimitive(note.viewed));
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

    public final static Comparator<Note> BY_NAME_COMPARATOR = new Comparator<Note>() {
        @Override
        public int compare(Note o1, Note o2) {
            return o1.title.compareTo(o2.title);
        }
    };
    public final static Comparator<Note> BY_NAME_DESCENDING_COMPARATOR = new Comparator<Note>() {
        @Override
        public int compare(Note o1, Note o2) {
            return o2.title.compareTo(o1.title);
        }
    };
    public final static Comparator<Note> BY_CREATED_DESCENDING_COMPARATOR = new Comparator<Note>() {
        @Override
        public int compare(Note o1, Note o2) {
            return o2.created.compareTo(o1.created);
        }
    };
    public final static Comparator<Note> BY_EDITED_DESCENDING_COMPARATOR = new Comparator<Note>() {
        @Override
        public int compare(Note o1, Note o2) {
            return o2.edited.compareTo(o1.edited);
        }
    };
    public final static Comparator<Note> BY_VIEWED_DESCENDING_COMPARATOR = new Comparator<Note>() {
        @Override
        public int compare(Note o1, Note o2) {
            return o2.viewed.compareTo(o1.viewed);
        }
    };
}