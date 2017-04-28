package com.tasks.notes;

import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Note implements Serializable, Parcelable {
    public final static String NAME = "Note";
    public final static int DEFAULT_COLOR = Color.parseColor("#ffffff");
    public final static SimpleDateFormat ISO8601_DATE_FORMAT =
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

    public long id;
    public String name;
    public String description;
    public int color = DEFAULT_COLOR;

    public Date created = new Date();
    public Date edited = new Date();
    public Date viewed = new Date();

    public Note() {
    }

    public Note(long id, String name, String description, int color) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.color = color;
        this.created = new Date();
        this.edited = new Date();
        this.viewed = new Date();
    }

    public Note(long id, String name, String description, int color,
                Date created, Date edited, Date viewed) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.color = color;
        this.created = created;
        this.edited = edited;
        this.viewed = viewed;
    }

    public Note(long id, String name, String description, int color,
                String created, String edited, String viewed) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.color = color;
        this.created = getDateFromString(created);
        this.edited = getDateFromString(edited);
        this.viewed = getDateFromString(viewed);
    }

    protected Note(Parcel in) {
        id = in.readLong();
        name = in.readString();
        description = in.readString();
        color = in.readInt();
        created = (Date) in.readSerializable();
        edited = (Date) in.readSerializable();
        viewed = (Date) in.readSerializable();

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
        dest.writeString(name);
        dest.writeString(description);
        dest.writeInt(color);
        dest.writeSerializable(created);
        dest.writeSerializable(edited);
        dest.writeSerializable(viewed);
    }

    private static Date getDateFromString(String source) {
        try {
            return ISO8601_DATE_FORMAT.parse(source);
        } catch (ParseException e) {
            return new Date();
        }
    }
}