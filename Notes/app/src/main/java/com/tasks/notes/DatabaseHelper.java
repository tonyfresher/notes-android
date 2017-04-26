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

    private final static String CREATE_TABLE_QUERY = String.format(
            "CREATE TABLE %s(%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT, %s TEXT, %s INTEGER)",
            TABLE_NAME, NOTE_ROWID, NOTE_NAME, NOTE_DESCRIPTION, NOTE_COLOR);
    private final static String DROP_TABLE_QUERY = String.format(
            "DROP TABLE IF EXISTS %s", TABLE_NAME);
    private final static String SELECT_ALL_QUERY = String.format(
            "SELECT  * FROM %s", TABLE_NAME);

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

    public void insert(NoteContent note) {
        ContentValues values = new ContentValues();
        values.put(NOTE_NAME, note.name);
        values.put(NOTE_DESCRIPTION, note.description);
        values.put(NOTE_COLOR, note.color);

        try (SQLiteDatabase db = this.getWritableDatabase()) {
            db.insert(TABLE_NAME, null, values);
        }
    }

    public void replace(long row, NoteContent newNote) {
        ContentValues values = new ContentValues();
        values.put(NOTE_NAME, newNote.name);
        values.put(NOTE_DESCRIPTION, newNote.description);
        values.put(NOTE_COLOR, newNote.color);

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

    public NoteContent[] getAllItems() {
        ArrayList<NoteContent> notes = new ArrayList();

        try (SQLiteDatabase db = this.getWritableDatabase()) {
            Cursor cursor = db.rawQuery(SELECT_ALL_QUERY, null);

            if (cursor.moveToFirst()) {
                int iRow = cursor.getColumnIndex(NOTE_ROWID);
                int iName = cursor.getColumnIndex(NOTE_NAME);
                int iDescription = cursor.getColumnIndex(NOTE_DESCRIPTION);
                int iColor = cursor.getColumnIndex(NOTE_COLOR);

                do {
                    NoteContent item = new NoteContent(
                            cursor.getLong(iRow),
                            cursor.getString(iName),
                            cursor.getString(iDescription),
                            cursor.getInt(iColor));

                    notes.add(item);
                } while (cursor.moveToNext());
            }
        }

        NoteContent[] notesArray = new NoteContent[notes.size()];
        return notes.toArray(notesArray);
    }
}
