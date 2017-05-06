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

import java.lang.reflect.Type;
import java.text.ParseException;
import java.util.Date;

import static com.tasks.notes.helpers.DateHelper.ISO8601_DATE_FORMAT;


public class Filter implements Parcelable {
    public final static String INTENT_EXTRA = "Filter";

    private String name = "";
    private int color;
    private String createdFrom;
    private String createdTo;
    private String editedFrom;
    private String editedTo;
    private String viewedFrom;
    private String viewedTo;

    public Filter() {
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
            if ((color == 0 || color == note.getColor())

                    && (createdFrom == null || dateBefore(createdFrom, note.getCreated()))
                    && (createdTo == null || dateBefore(note.getCreated(), createdTo))

                    && (editedFrom == null || dateBefore(editedFrom, note.getEdited()))
                    && (editedTo == null || dateBefore(note.getEdited(), editedTo))

                    && (viewedFrom == null || dateBefore(viewedFrom, note.getViewed()))
                    && (viewedTo == null || dateBefore(note.getViewed(), viewedTo))) {
                return true;
            }
        } catch (ParseException e) {
        }
        return false;
    }

    private boolean dateBefore(String s1, String s2)
            throws ParseException {
        Date d1 = ISO8601_DATE_FORMAT.parse(s1);
        Date d2 = ISO8601_DATE_FORMAT.parse(s2);
        return d1.before(d2);
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

    public static class Serializer implements JsonSerializer<Filter>, JsonDeserializer<Filter> {
        @Override
        public JsonElement serialize(final Filter filter, final Type type, final JsonSerializationContext context) {
            JsonObject result = new JsonObject();
            addStringToJson(result, "name", filter.name);

            if (filter.color != 0) {
                result.add("color", new JsonPrimitive(filter.color));
            }

            addStringToJson(result, "createdFrom", filter.createdFrom);
            addStringToJson(result, "createdTo", filter.createdTo);

            addStringToJson(result, "editedFrom", filter.editedFrom);
            addStringToJson(result, "editedTo", filter.editedTo);

            addStringToJson(result, "viewedFrom", filter.viewedFrom);
            addStringToJson(result, "viewedTo", filter.viewedTo);

            return result;
        }

        private static void addStringToJson(JsonObject jObject, String name, String field) {
            if (field != null) {
                jObject.add(name, new JsonPrimitive(field));
            }
        }

        @Override
        public Filter deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            JsonObject jObject = json.getAsJsonObject();
            String name = jObject.get("name").getAsString();

            int color = jObject.has("color") ? jObject.get("color").getAsInt() : 0;

            String createdFrom = jObject.has("createdFrom") ? jObject.get("createdFrom").getAsString() : null;
            String createdTo = jObject.has("createdTo") ? jObject.get("createdTo").getAsString() : null;

            String editedFrom = jObject.has("editedFrom") ? jObject.get("editedFrom").getAsString() : null;
            String editedTo = jObject.has("editedTo") ? jObject.get("editedTo").getAsString() : null;

            String viewedFrom = jObject.has("viewedFrom") ? jObject.get("viewedFrom").getAsString() : null;
            String viewedTo = jObject.has("viewedTo") ? jObject.get("viewedTo").getAsString() : null;

            return new Filter(name, color,
                    createdFrom, createdTo,
                    editedFrom, editedTo,
                    viewedFrom, viewedTo);
        }
    }
}
