package com.tasks.notes.classes;

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

import org.joda.time.LocalDate;

import java.lang.reflect.Type;
import java.text.ParseException;

import static com.tasks.notes.helpers.DateHelper.ISO8601_DATE_FORMAT;


public class Filter implements Parcelable {
    public final static String INTENT_EXTRA = "Filter";

    private String name;
    private int color;
    private String createdFrom;
    private String createdTo;
    private String editedFrom;
    private String editedTo;
    private String viewedFrom;
    private String viewedTo;

    public Filter() {
        name = "";
    }

    public Filter(String name, int color,
                  String createdFrom, String createdTo,
                  String editedFrom, String editedTo,
                  String viewedFrom, String viewedTo) {
        this.name = name;
        this.color = color;
        this.createdFrom = createdFrom;
        this.createdTo = createdTo;
        this.editedFrom = editedFrom;
        this.editedTo = editedTo;
        this.viewedFrom = viewedFrom;
        this.viewedTo = viewedTo;
    }

    public Filter(Parcel in) {
        name = in.readString();
        color = in.readInt();
        createdFrom = in.readString();
        createdTo = in.readString();
        editedFrom = in.readString();
        editedTo = in.readString();
        viewedFrom = in.readString();
        viewedTo = in.readString();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getCreatedFrom() {
        return createdFrom;
    }

    public void setCreatedFrom(String createdFrom) {
        this.createdFrom = createdFrom;
    }

    public String getCreatedTo() {
        return createdTo;
    }

    public void setCreatedTo(String createdTo) {
        this.createdTo = createdTo;
    }

    public String getEditedFrom() {
        return editedFrom;
    }

    public void setEditedFrom(String editedFrom) {
        this.editedFrom = editedFrom;
    }

    public String getEditedTo() {
        return editedTo;
    }

    public void setEditedTo(String editedTo) {
        this.editedTo = editedTo;
    }

    public String getViewedFrom() {
        return viewedFrom;
    }

    public void setViewedFrom(String viewedFrom) {
        this.viewedFrom = viewedFrom;
    }

    public String getViewedTo() {
        return viewedTo;
    }

    public void setViewedTo(String viewedTo) {
        this.viewedTo = viewedTo;
    }

    public boolean check(Note note) {
        try {
            return ((color == 0 || color == note.getColor())
                    && (createdFrom == null || dateBefore(createdFrom, note.getCreated()))
                    && (createdTo == null || dateBefore(note.getCreated(), createdTo))
                    && (editedFrom == null || dateBefore(editedFrom, note.getEdited()))
                    && (editedTo == null || dateBefore(note.getEdited(), editedTo))
                    && (viewedFrom == null || dateBefore(viewedFrom, note.getViewed()))
                    && (viewedTo == null || dateBefore(note.getViewed(), viewedTo)));
        } catch (ParseException e) {
            return false;
        }
    }

    private boolean dateBefore(String s1, String s2)
            throws ParseException {
        LocalDate d1 = new LocalDate(ISO8601_DATE_FORMAT.parse(s1));
        LocalDate d2 = new LocalDate(ISO8601_DATE_FORMAT.parse(s2));
        return d1.compareTo(d2) <= 0;
    }

    public static final Creator<Filter> CREATOR = new Creator<Filter>() {
        @Override
        public Filter createFromParcel(Parcel in) {
            return new Filter(in);
        }

        @Override
        public Filter[] newArray(int size) {
            return new Filter[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(color);
        dest.writeString(createdFrom);
        dest.writeString(createdTo);
        dest.writeString(editedFrom);
        dest.writeString(editedTo);
        dest.writeString(viewedFrom);
        dest.writeString(viewedTo);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Filter filter = (Filter) o;

        if (color != filter.color) return false;
        if (name != null ? !name.equals(filter.name) : filter.name != null) return false;
        if (createdFrom != null ? !createdFrom.equals(filter.createdFrom) : filter.createdFrom != null)
            return false;
        if (createdTo != null ? !createdTo.equals(filter.createdTo) : filter.createdTo != null)
            return false;
        if (editedFrom != null ? !editedFrom.equals(filter.editedFrom) : filter.editedFrom != null)
            return false;
        if (editedTo != null ? !editedTo.equals(filter.editedTo) : filter.editedTo != null)
            return false;
        if (viewedFrom != null ? !viewedFrom.equals(filter.viewedFrom) : filter.viewedFrom != null)
            return false;
        return viewedTo != null ? viewedTo.equals(filter.viewedTo) : filter.viewedTo == null;

    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + color;
        result = 31 * result + (createdFrom != null ? createdFrom.hashCode() : 0);
        result = 31 * result + (createdTo != null ? createdTo.hashCode() : 0);
        result = 31 * result + (editedFrom != null ? editedFrom.hashCode() : 0);
        result = 31 * result + (editedTo != null ? editedTo.hashCode() : 0);
        result = 31 * result + (viewedFrom != null ? viewedFrom.hashCode() : 0);
        result = 31 * result + (viewedTo != null ? viewedTo.hashCode() : 0);
        return result;
    }

    public static class Serializer implements JsonSerializer<Filter>, JsonDeserializer<Filter> {
        @Override
        public JsonElement serialize(final Filter filter, final Type type, final JsonSerializationContext context) {
            JsonObject result = new JsonObject();
            tryAddString(result, "name", filter.name);

            if (filter.color != 0) {
                result.add("color", new JsonPrimitive(filter.color));
            }

            tryAddString(result, "createdFrom", filter.createdFrom);
            tryAddString(result, "createdTo", filter.createdTo);

            tryAddString(result, "editedFrom", filter.editedFrom);
            tryAddString(result, "editedTo", filter.editedTo);

            tryAddString(result, "viewedFrom", filter.viewedFrom);
            tryAddString(result, "viewedTo", filter.viewedTo);

            return result;
        }

        @Override
        public Filter deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            JsonObject object = json.getAsJsonObject();
            String name = tryGetString(object, "name");

            int color = object.has("color") ? object.get("color").getAsInt() : 0;

            String createdFrom = tryGetString(object, "createdFrom");
            String createdTo = tryGetString(object, "createdTo");

            String editedFrom = tryGetString(object, "editedFrom");
            String editedTo = tryGetString(object, "editedTo");

            String viewedFrom = tryGetString(object, "viewedFrom");
            String viewedTo = tryGetString(object, "viewedTo");

            return new Filter(name, color,
                    createdFrom, createdTo,
                    editedFrom, editedTo,
                    viewedFrom, viewedTo);
        }

        private static void tryAddString(JsonObject object, String name, String value) {
            if (value != null) object.add(name, new JsonPrimitive(value));
        }

        private static String tryGetString(JsonObject object, String name) {
            return object.has(name) ? object.get(name).getAsString() : null;
        }
    }
}
