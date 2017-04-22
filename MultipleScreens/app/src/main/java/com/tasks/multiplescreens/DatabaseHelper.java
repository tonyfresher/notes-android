package com.tasks.multiplescreens;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {
    private final static String TABLE_NAME = "Items";
    private final static String ITEM_ROWID = "_id";
    private final static String ITEM_NAME = "Name";
    private final static String ITEM_DESCRIPTION = "Description";
    private final static String ITEM_COLOR = "Color";

    public DatabaseHelper(Context context) {
        super(context, String.format("%sDatabase.db", TABLE_NAME), null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String tableEmp = String.format(
                "CREATE TABLE %s(%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT, %s TEXT, %s INT)",
                TABLE_NAME, ITEM_ROWID, ITEM_NAME, ITEM_DESCRIPTION, ITEM_COLOR);
        db.execSQL(tableEmp);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(String.format("DROP TABLE IF EXISTS %s", TABLE_NAME));
        onCreate(db);
    }

    public void insert(ItemContent item) {
        ContentValues values = new ContentValues();
        values.put(ITEM_NAME, item.name);
        values.put(ITEM_DESCRIPTION, item.description);
        values.put(ITEM_COLOR, item.color);

        try (SQLiteDatabase db = this.getWritableDatabase()) {
            db.insert(TABLE_NAME, null, values);
        }
    }

    public void replace(int row, ItemContent newItem) {
        ContentValues values = new ContentValues();
        values.put(ITEM_NAME, newItem.name);
        values.put(ITEM_DESCRIPTION, newItem.description);
        values.put(ITEM_COLOR, newItem.color);

        try (SQLiteDatabase db = this.getWritableDatabase()) {
            db.update(TABLE_NAME, values,
                    String.format("%s=?", ITEM_ROWID),
                    new String[]{Integer.toString(row)});
        }
    }

    public void delete(int row) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            db.delete(TABLE_NAME, String.format("%s=%s", ITEM_ROWID, row), null);
        }
    }

    public ItemContent[] getAllItems() {
        ArrayList<ItemContent> items = new ArrayList();

        try (SQLiteDatabase db = this.getWritableDatabase()) {
            String selectQuery = String.format("SELECT  * FROM %s", TABLE_NAME);
            Cursor cursor = db.rawQuery(selectQuery, null);

            if (cursor.moveToFirst()) {
                int iRow = cursor.getColumnIndex(ITEM_ROWID);
                int iName = cursor.getColumnIndex(ITEM_NAME);
                int iDescription = cursor.getColumnIndex(ITEM_DESCRIPTION);
                int iColor = cursor.getColumnIndex(ITEM_COLOR);

                do {
                    ItemContent item = new ItemContent(
                            cursor.getInt(iRow),
                            cursor.getString(iName),
                            cursor.getString(iDescription),
                            cursor.getInt(iColor));

                    items.add(item);
                } while (cursor.moveToNext());
            }
        }

        ItemContent[] itemsArray = new ItemContent[items.size()];
        return items.toArray(itemsArray);
    }
}
