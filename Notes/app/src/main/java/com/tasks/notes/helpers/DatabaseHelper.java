package com.tasks.notes.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.tasks.notes.classes.Note;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private final static String TABLE_NAME = "Notes";
    private final static String NOTE_ROWID = "_id";
    private final static String NOTE_TITLE = "Name";
    private final static String NOTE_DESCRIPTION = "Description";
    private final static String NOTE_COLOR = "Color";
    private final static String NOTE_CREATED = "Created";
    private final static String NOTE_EDITED = "Edited";
    private final static String NOTE_VIEWED = "Viewed";
    private final static String NOTE_IMAGE_URL = "imageUrl";

    private final static String CREATE_TABLE_QUERY = String.format(
            "CREATE TABLE %s(%s INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "%s TEXT, %s TEXT, %s TEXT, %s INTEGER, %s TEXT, %s TEXT, %s TEXT)",
            TABLE_NAME, NOTE_ROWID, NOTE_TITLE, NOTE_DESCRIPTION, NOTE_IMAGE_URL,
            NOTE_COLOR, NOTE_CREATED, NOTE_EDITED, NOTE_VIEWED);
    private final static String DROP_TABLE_QUERY = "DROP TABLE IF EXISTS " + TABLE_NAME;
    private final static String SELECT_ALL_QUERY = "SELECT  * FROM " + TABLE_NAME;
    private final static String UPDATE_VIEWED_WHERE_ROWID_QUERY = String.format(
            "UPDATE %s SET %s=? WHERE %s=?", TABLE_NAME, NOTE_VIEWED, NOTE_ROWID);
    private final static String SEARCH_SUBSTRING = String.format(
            "SELECT * FROM %s WHERE %s LIKE ? OR %s LIKE ?",
            TABLE_NAME, NOTE_TITLE, NOTE_DESCRIPTION);

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

    public void dropTable() {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            db.execSQL(DROP_TABLE_QUERY);
            db.execSQL(CREATE_TABLE_QUERY);
        }
    }

    public void insert(Note note) {
        ContentValues values = getContentValuesFromNote(note);

        try (SQLiteDatabase db = this.getWritableDatabase()) {
            db.insert(TABLE_NAME, null, values);
        }
    }

    public AsyncTask<Note, Integer, Void> insertAsync(Note note) {
        AsyncTask<Note, Integer, Void> t = new AsyncTask<Note, Integer, Void>() {
            @Override
            protected Void doInBackground(Note... params) {
                final Note note = params[0];
                insert(note);
                return null;
            }
        };
        return t.execute(note);
    }

    public void replace(long row, Note note) {
        ContentValues values = getContentValuesFromNote(note);

        try (SQLiteDatabase db = this.getWritableDatabase()) {
            db.update(TABLE_NAME, values,
                    String.format("%s=?", NOTE_ROWID),
                    new String[]{Long.toString(row)});
        }
    }

    public AsyncTask<Object, Integer, Void> replaceAsync(long row, Note note) {
        AsyncTask<Object, Integer, Void> t = new AsyncTask<Object, Integer, Void>() {
            @Override
            protected Void doInBackground(Object... params) {
                final long row = (long) params[0];
                final Note note = (Note) params[1];
                replace(row, note);
                return null;
            }
        };
        return t.execute(row, note);
    }

    public void delete(long row) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            db.delete(TABLE_NAME, String.format("%s=%s", NOTE_ROWID, row), null);
        }
    }

    public AsyncTask<Long, Integer, Void> deleteAsync(long row) {
        AsyncTask<Long, Integer, Void> t = new AsyncTask<Long, Integer, Void>() {
            @Override
            protected Void doInBackground(Long... params) {
                final long row = params[0];
                delete(row);
                return null;
            }
        };
        return t.execute(row);
    }

    public void refreshViewedDate(long row, String visited) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            db.execSQL(UPDATE_VIEWED_WHERE_ROWID_QUERY,
                    new String[]{visited, Long.toString(row)});
        }
    }

    public AsyncTask<Object, Integer, Void> refreshViewedDateAsync(long row, String visited) {
        AsyncTask<Object, Integer, Void> t = new AsyncTask<Object, Integer, Void>() {
            @Override
            protected Void doInBackground(Object... params) {
                final long row = (long) params[0];
                final String visited = (String) params[1];
                refreshViewedDate(row, visited);
                return null;
            }
        };
        return t.execute(row, visited);
    }

    public Note[] getItems(String query, String[] args) {
        ArrayList<Note> notes = new ArrayList<>();

        try (SQLiteDatabase db = this.getWritableDatabase()) {
            Cursor c = db.rawQuery(query, args);

            if (c.moveToFirst()) {
                int iRow = c.getColumnIndex(NOTE_ROWID);
                int iName = c.getColumnIndex(NOTE_TITLE);
                int iDescription = c.getColumnIndex(NOTE_DESCRIPTION);
                int iImageUrl = c.getColumnIndex(NOTE_IMAGE_URL);
                int iColor = c.getColumnIndex(NOTE_COLOR);
                int iCreated = c.getColumnIndex(NOTE_CREATED);
                int iEdited = c.getColumnIndex(NOTE_EDITED);
                int iViewed = c.getColumnIndex(NOTE_VIEWED);


                do {
                    Note item = new Note(
                            c.getLong(iRow),
                            c.getString(iName),
                            c.getString(iDescription),
                            c.getString(iImageUrl),
                            c.getInt(iColor),
                            c.getString(iCreated),
                            c.getString(iEdited),
                            c.getString(iViewed));

                    notes.add(item);
                } while (c.moveToNext());
            }

            c.close();
        }

        Note[] notesArray = new Note[notes.size()];
        return notes.toArray(notesArray);
    }

    public Note[] getOrderedItems(@NonNull Comparator<Note> comparator) {
        Note[] items = getItems(SELECT_ALL_QUERY, null);
        Arrays.sort(items, comparator);
        return items;
    }

    public AsyncTask<Comparator<Note>, Integer, Note[]> getOrderedItemsAsync(
            @NonNull Comparator<Note> comparator) {
        AsyncTask<Comparator<Note>, Integer, Note[]> t =
                new AsyncTask<Comparator<Note>, Integer, Note[]>() {
                    @Override
                    protected Note[] doInBackground(Comparator<Note>... params) {
                        Comparator<Note> comparator = params[0];
                        Note[] items = getItems(SELECT_ALL_QUERY, null);
                        Arrays.sort(items, comparator);
                        return items;
                    }
                };
        return t.execute(comparator);
    }

    public Note[] searchBySubstring(String substring) {
        return getItems(SEARCH_SUBSTRING,
                new String[]{"%" + substring + "%", "%" + substring + "%"});
    }

    private ContentValues getContentValuesFromNote(Note note) {
        ContentValues values = new ContentValues();
        values.put(NOTE_TITLE, note.getTitle());
        values.put(NOTE_DESCRIPTION, note.getDescription());
        values.put(NOTE_IMAGE_URL, note.getImageUrl());
        values.put(NOTE_COLOR, note.getColor());
        values.put(NOTE_CREATED, note.getCreated());
        values.put(NOTE_EDITED, note.getEdited());
        values.put(NOTE_VIEWED, note.getViewed());
        return values;
    }
}