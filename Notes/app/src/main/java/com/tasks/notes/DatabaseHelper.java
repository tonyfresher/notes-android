package com.tasks.notes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {
    private final static String TABLE_NAME = "Notes";
    private final static String NOTE_ROWID = "_id";
    private final static String NOTE_NAME = "Name";
    private final static String NOTE_DESCRIPTION = "Description";
    private final static String NOTE_COLOR = "Color";
    private final static String NOTE_CREATED = "Created";
    private final static String NOTE_EDITED = "Edited";
    private final static String NOTE_VIEWED = "Viewed";

    private final static String CREATE_TABLE_QUERY = String.format(
            "CREATE TABLE %s(%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT, %s TEXT, %s INTEGER, " +
                    "%s TEXT, %s TEXT, %s TEXT)",
            TABLE_NAME, NOTE_ROWID, NOTE_NAME, NOTE_DESCRIPTION, NOTE_COLOR,
            NOTE_CREATED, NOTE_EDITED, NOTE_VIEWED);
    private final static String DROP_TABLE_QUERY = "DROP TABLE IF EXISTS " + TABLE_NAME;
    private final static String SELECT_ALL_QUERY = "SELECT  * FROM " + TABLE_NAME;

    public DatabaseHelper(Context context) {
        super(context, String.format("%sDatabase.db", TABLE_NAME), null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_QUERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TABLE_QUERY);
        onCreate(db);
    }

    public void insert(Note note) {
        ContentValues values = getContentValuesFromNote(note);

        try (SQLiteDatabase db = this.getWritableDatabase()) {
            db.insert(TABLE_NAME, null, values);
        }
    }

    public void replace(long row, Note note) {
        ContentValues values = getContentValuesFromNote(note);

        try (SQLiteDatabase db = this.getWritableDatabase()) {
            db.update(TABLE_NAME, values,
                    String.format("%s=?", NOTE_ROWID),
                    new String[]{Long.toString(row)});
        }
    }

    public void delete(long row) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            db.delete(TABLE_NAME, String.format("%s=%s", NOTE_ROWID, row), null);
        }
    }

    public Note[] getAllItems() {
        ArrayList<Note> notes = new ArrayList<>();

        try (SQLiteDatabase db = this.getWritableDatabase()) {
            Cursor cursor = db.rawQuery(SELECT_ALL_QUERY, null);

            if (cursor.moveToFirst()) {
                int iRow = cursor.getColumnIndex(NOTE_ROWID);
                int iName = cursor.getColumnIndex(NOTE_NAME);
                int iDescription = cursor.getColumnIndex(NOTE_DESCRIPTION);
                int iColor = cursor.getColumnIndex(NOTE_COLOR);
                int iCreated = cursor.getColumnIndex(NOTE_CREATED);
                int iEdited = cursor.getColumnIndex(NOTE_EDITED);
                int iViewed = cursor.getColumnIndex(NOTE_VIEWED);


                do {
                    Note item = new Note(
                            cursor.getLong(iRow),
                            cursor.getString(iName),
                            cursor.getString(iDescription),
                            cursor.getInt(iColor),
                            cursor.getString(iCreated),
                            cursor.getString(iEdited),
                            cursor.getString(iViewed));

                    notes.add(item);
                } while (cursor.moveToNext());
            }
        }

        Note[] notesArray = new Note[notes.size()];
        return notes.toArray(notesArray);
    }

    public void refreshViewedDate(long row, String visited) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            db.execSQL(
                    "UPDATE ? SET ?=? WHERE ?=?",
                    new String[]{TABLE_NAME, NOTE_VIEWED, visited, NOTE_ROWID, Long.toString(row)});
        }
    }

    private ContentValues getContentValuesFromNote(Note note) {
        ContentValues values = new ContentValues();
        values.put(NOTE_NAME, note.name);
        values.put(NOTE_DESCRIPTION, note.description);
        values.put(NOTE_COLOR, note.color);
        values.put(NOTE_CREATED, Note.ISO8601_DATE_FORMAT.format(note.created));
        values.put(NOTE_EDITED, Note.ISO8601_DATE_FORMAT.format(note.edited));
        values.put(NOTE_VIEWED, Note.ISO8601_DATE_FORMAT.format(note.viewed));
        return values;
    }
}
