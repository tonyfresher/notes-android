package com.tasks.notes.classes;

import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

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

import com.tasks.notes.helpers.ColorsHelper;

import org.joda.time.DateTime;

import static com.tasks.notes.helpers.DateHelper.ISO8601_DATE_FORMAT;

public class Note implements Serializable, Parcelable {
    public final static String INTENT_EXTRA = "note";

    private long id;
    private String title;
    private String description;
    private String imageUrl;
    private int color;

    @NonNull
    private String created;
    @NonNull
    private String edited;
    @NonNull
    private String viewed;

    public Note() {
        color = ColorsHelper.DEFAULT_NOTE_COLOR;
        String now = ISO8601_DATE_FORMAT.print(new DateTime());
        created = now;
        edited = now;
        viewed = now;
    }

    public Note(String title, String description) {
        this();
        this.title = title;
        this.description = description;
    }

    public Note(long id, String title, String description, String imageUrl,
                int color, String created, String edited, String viewed) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
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
        imageUrl = in.readString();
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
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
        dest.writeString(imageUrl);
    }

    public boolean contentEquals(Note note) {
        if (note == null) return false;
        return (color == note.color &&
                (title != null ?
                        title.equals(note.title) : note.title == null) &&
                (description != null ?
                        description.equals(note.description) : note.description == null) &&
                (imageUrl != null ?
                        imageUrl.equals(note.imageUrl) : note.imageUrl == null));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Note note = (Note) o;

        if (id != note.id) return false;
        if (color != note.color) return false;
        if (title != null ? !title.equals(note.title) : note.title != null) return false;
        if (description != null ? !description.equals(note.description) : note.description != null)
            return false;
        if (!created.equals(note.created)) return false;
        if (!edited.equals(note.edited)) return false;
        if (!viewed.equals(note.viewed)) return false;
        return imageUrl != null ? imageUrl.equals(note.imageUrl) : note.imageUrl == null;

    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + color;
        result = 31 * result + created.hashCode();
        result = 31 * result + edited.hashCode();
        result = 31 * result + viewed.hashCode();
        result = 31 * result + (imageUrl != null ? imageUrl.hashCode() : 0);
        return result;
    }

    public static class Serializer implements JsonSerializer<Note>, JsonDeserializer<Note> {
        public final static String TITLE = "title";
        public final static String DESCRIPTION = "description";
        public final static String IMAGE_URL = "imageUrl";
        public final static String COLOR = "color";
        public final static String CREATED = "created";
        public final static String EDITED = "edited";
        public final static String VIEWED = "viewed";

        @Override
        public JsonElement serialize(final Note note, final Type type, final JsonSerializationContext context) {
            JsonObject result = new JsonObject();
            tryAddString(result, TITLE, note.title);
            tryAddString(result, DESCRIPTION, note.description);
            tryAddString(result, IMAGE_URL, note.imageUrl);
            result.add(COLOR, new JsonPrimitive(
                    String.format("#%06X", (0xFFFFFF & note.color))));
            result.add(CREATED, new JsonPrimitive(note.created));
            result.add(EDITED, new JsonPrimitive(note.edited));
            result.add(VIEWED, new JsonPrimitive(note.viewed));

            return result;
        }

        @Override
        public Note deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            JsonObject object = json.getAsJsonObject();
            String title = tryGetString(object, TITLE);
            String description = tryGetString(object, DESCRIPTION);
            String imageUrl = tryGetString(object, IMAGE_URL);
            int color = Color.parseColor(object.get(COLOR).getAsString());
            String created = object.get(CREATED).getAsString();
            String edited = object.get(EDITED).getAsString();
            String viewed = object.get(VIEWED).getAsString();

            return new Note(0, title, description, imageUrl, color, created, edited, viewed);
        }

        private static void tryAddString(JsonObject object, String name, String value) {
            if (value != null) object.add(name, new JsonPrimitive(value));
        }

        private static String tryGetString(JsonObject object, String name) {
            return object.has(name) ? object.get(name).getAsString() : null;
        }
    }

    public final static Comparator<Note> BY_NAME_COMPARATOR =
            (Note o1, Note o2) ->  {
                if (o1.title == null ^ o2.title == null) {
                    return (o1.title == null) ? -1 : 1;
                }
                if (o2.title == null && o2.title == null) {
                    return 0;
                }
                return o1.title.compareToIgnoreCase(o2.title);
            };
    public final static Comparator<Note> BY_CREATED_DESCENDING_COMPARATOR =
            (Note o1, Note o2) -> o2.created.compareTo(o1.created);
    public final static Comparator<Note> BY_EDITED_DESCENDING_COMPARATOR =
            (Note o1, Note o2) -> o2.edited.compareTo(o1.edited);
    public final static Comparator<Note> BY_VIEWED_DESCENDING_COMPARATOR =
            (Note o1, Note o2) -> o2.viewed.compareTo(o1.viewed);
}