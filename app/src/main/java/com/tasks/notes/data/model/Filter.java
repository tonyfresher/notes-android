package com.tasks.notes.data.model;

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
import com.tasks.notes.utility.DateFormats;

import java.lang.reflect.Type;


public class Filter implements Parcelable {

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
        return ((color == 0 || color == note.getColor())
                && (createdFrom == null || DateFormats.before(createdFrom, note.getCreated()))
                && (createdTo == null || DateFormats.before(note.getCreated(), createdTo))
                && (editedFrom == null || DateFormats.before(editedFrom, note.getEdited()))
                && (editedTo == null || DateFormats.before(note.getEdited(), editedTo))
                && (viewedFrom == null || DateFormats.before(viewedFrom, note.getViewed()))
                && (viewedTo == null || DateFormats.before(note.getViewed(), viewedTo)));
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
        public final static String NAME = "name";
        public final static String COLOR = "color";
        public final static String CREATED_FROM = "createdFrom";
        public final static String CREATED_TO = "createdTo";
        public final static String EDITED_FROM = "editedFrom";
        public final static String EDITED_TO = "editedTo";
        public final static String VIEWED_FROM = "viewedFrom";
        public final static String VIEWED_TO = "viewedTo";

        @Override
        public JsonElement serialize(final Filter filter, final Type type,
                                     final JsonSerializationContext context) {
            JsonObject result = new JsonObject();
            tryAddString(result, NAME, filter.name);

            if (filter.color != 0) {
                result.add(COLOR, new JsonPrimitive(filter.color));
            }

            tryAddString(result, CREATED_FROM, filter.createdFrom);
            tryAddString(result, CREATED_TO, filter.createdTo);

            tryAddString(result, EDITED_FROM, filter.editedFrom);
            tryAddString(result, EDITED_TO, filter.editedTo);

            tryAddString(result, VIEWED_FROM, filter.viewedFrom);
            tryAddString(result, VIEWED_TO, filter.viewedTo);

            return result;
        }

        @Override
        public Filter deserialize(JsonElement json, Type typeOfT,
                                  JsonDeserializationContext context)
                throws JsonParseException {
            JsonObject object = json.getAsJsonObject();
            String name = tryGetString(object, NAME);

            int color = object.has(COLOR) ? object.get(COLOR).getAsInt() : 0;

            String createdFrom = tryGetString(object, CREATED_FROM);
            String createdTo = tryGetString(object, CREATED_TO);

            String editedFrom = tryGetString(object, EDITED_FROM);
            String editedTo = tryGetString(object, EDITED_TO);

            String viewedFrom = tryGetString(object, VIEWED_FROM);
            String viewedTo = tryGetString(object, VIEWED_TO);

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
