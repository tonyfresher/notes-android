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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static DatabaseHelper instance;

    public static DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context);
        }
        return instance;
    }

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

    private DatabaseHelper(Context context) {
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


    public synchronized void dropTable() {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            db.execSQL(DROP_TABLE_QUERY);
            db.execSQL(CREATE_TABLE_QUERY);
        }
    }

    public HandyTask<Void, Void> dropTableTask() {
        return new HandyTask<>((task, params) -> {
            dropTable();
            return null;
        });
    }

    public AsyncTask<Void, Integer, Void> dropTableAsync() {
        return dropTableTask().execute();
    }


    public synchronized void insert(Note note) {
        ContentValues values = getContentValuesFromNote(note);

        try (SQLiteDatabase db = this.getWritableDatabase()) {
            db.insert(TABLE_NAME, null, values);
        }
    }

    public synchronized void insertList(List<Note> notes) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            for (Note note : notes) {
                db.insert(TABLE_NAME, null, getContentValuesFromNote(note));
            }
        }
    }

    public HandyTask<Note, Void> insertTask() {
        return new HandyTask<>((task, params) -> {
            final Note note = params[0];
            insert(note);
            return null;
        });
    }

    public AsyncTask<Note, Integer, Void> insertAsync(Note note) {
        return insertTask().execute(note);
    }


    public synchronized void replace(long row, Note note) {
        ContentValues values = getContentValuesFromNote(note);

        try (SQLiteDatabase db = this.getWritableDatabase()) {
            db.update(TABLE_NAME, values,
                    String.format("%s=?", NOTE_ROWID),
                    new String[]{Long.toString(row)});
        }
    }

    public HandyTask<Object, Void> replaceTask() {
        return new HandyTask<>((task, params) -> {
            final long row = (long) params[0];
            final Note note = (Note) params[1];
            replace(row, note);
            return null;
        });
    }

    public AsyncTask<Object, Integer, Void> replaceAsync(long row, Note note) {
        return replaceTask().execute(row, note);
    }


    public synchronized void delete(long row) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            db.delete(TABLE_NAME, String.format("%s=%s", NOTE_ROWID, row), null);
        }
    }

    public HandyTask<Long, Void> deleteTask() {
        return new HandyTask<>((task, params) -> {
            final long row = params[0];
            delete(row);
            return null;
        });
    }

    public AsyncTask<Long, Integer, Void> deleteAsync(long row) {
        return deleteTask().execute(row);
    }


    public synchronized void refreshViewedDate(long row, String visited) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            db.execSQL(UPDATE_VIEWED_WHERE_ROWID_QUERY,
                    new String[]{visited, Long.toString(row)});
        }
    }

    public HandyTask<Object, Void> refreshViewedDateTask() {
        return new HandyTask<>((task, params) -> {
            final long row = (long) params[0];
            final String visited = (String) params[1];
            refreshViewedDate(row, visited);
            return null;
        });
    }

    public AsyncTask<Object, Integer, Void> refreshViewedDateAsync(long row, String visited) {
        return refreshViewedDateTask().execute(row, visited);
    }


    private synchronized List<Note> getItems(String query, String[] args) {
        List<Note> notes = new ArrayList<>();

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

        return notes;
    }


    public List<Note> getOrderedItems(@NonNull Comparator<Note> comparator) {
        List<Note> items = getItems(SELECT_ALL_QUERY, null);
        Collections.sort(items, comparator);
        return items;
    }

    public HandyTask<Comparator<Note>, List<Note>> getOrderedItemsTask() {
        return new HandyTask<>((task, params) -> {
            Comparator<Note> comparator = params[0];
            return getOrderedItems(comparator);
        });
    }

    public AsyncTask<Comparator<Note>, Integer, List<Note>> getOrderedItemsAsync(@NonNull Comparator<Note> comparator) {
        return getOrderedItemsTask().execute(comparator);
    }


    public synchronized List<Note> searchBySubstring(String substring) {
        return getItems(SEARCH_SUBSTRING,
                new String[]{"%" + substring + "%", "%" + substring + "%"});
    }

    public HandyTask<String, List<Note>> searchBySubstringTask() {
        return new HandyTask<>((task, params) -> {
            String substring = params[0];
            return searchBySubstring(substring);
        });
    }

    public AsyncTask<String, Integer, List<Note>> searchBySubstringAsync() {
        return new HandyTask<>((task, params) -> {
            String substring = params[0];
            return searchBySubstring(substring);
        });
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