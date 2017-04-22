package com.tasks.multiplescreens;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class ItemContent implements Serializable, Parcelable {
    public final static String NAME = "ItemContent";

    public int id;
    public String name;
    public String description;
    public int color;

    public ItemContent() {
    }

    public ItemContent(int id, String name, String description, int color) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.color = color;
    }

    protected ItemContent(Parcel in) {
        id = in.readInt();
        name = in.readString();
        description = in.readString();
        color = in.readInt();
    }

    public static final Creator<ItemContent> CREATOR = new Creator<ItemContent>() {
        @Override
        public ItemContent createFromParcel(Parcel in) {
            return new ItemContent(in);
        }

        @Override
        public ItemContent[] newArray(int size) {
            return new ItemContent[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeInt(color);
    }
}