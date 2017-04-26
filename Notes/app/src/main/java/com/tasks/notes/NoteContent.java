package com.tasks.notes;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class NoteContent implements Serializable, Parcelable {
    public final static String NAME = "NoteContent";

    public long id;
    public String name;
    public String description;
    public int color;

    public NoteContent() {
    }

    public NoteContent(long id, String name, String description, int color) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.color = color;
    }

    protected NoteContent(Parcel in) {
        id = in.readLong();
        name = in.readString();
        description = in.readString();
        color = in.readInt();
    }

    public static final Creator<NoteContent> CREATOR = new Creator<NoteContent>() {
        @Override
        public NoteContent createFromParcel(Parcel in) {
            return new NoteContent(in);
        }

        @Override
        public NoteContent[] newArray(int size) {
            return new NoteContent[size];
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
    }
}