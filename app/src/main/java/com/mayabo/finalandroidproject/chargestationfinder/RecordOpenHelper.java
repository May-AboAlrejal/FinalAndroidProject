package com.mayabo.finalandroidproject.chargestationfinder;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class RecordOpenHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "Favorites";
    public static final String TABLE_NAME = "FAVORITES";
    public static final String COLUMN_ID = "ID";
    public static final String COLUMN_TITLE = "TITLE";
    public static final String COLUMN_LONGITUDE = "LONGITUDE";
    public static final String COLUMN_LATITUDE = "LATITUDE";
    public static final String COLUMN_CONTACT = "CONTACT";
    public static final String COLUMN_ADDRESS = "ADDRESS";

    /**
     *
     * @param context the activity that accesses this database
     */
    public RecordOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Creates table FAVORITES if not exists
     * @param db database to use
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
            "CREATE TABLE " + TABLE_NAME + "(" +
                COLUMN_ID        + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TITLE     + " TEXT, " +
                COLUMN_LONGITUDE + " TEXT, " +
                COLUMN_LATITUDE  + " TEXT, " +
                COLUMN_CONTACT   + " TEXT, " +
                COLUMN_ADDRESS   + " TEXT" +
            ");"
        );
    }

    /**
     * Drops the table FAVORITES if version has been upgraded.
     * @param db database to use.
     * @param oldVersion found version
     * @param newVersion version should be
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        reset();
    }

    /**
     * Drops the table FAVORITES if version has been downgraded.
     * @param db database to use
     * @param oldVersion found version
     * @param newVersion version should be
     */
    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        reset();
    }

    public void reset() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    /**
     * Adds a new Record to database.
     * @param record record to add
     * @return id of the new row added
     */
    public long insert(Record record) {
        Cursor cursor = getAll();
        cursor.moveToFirst();
        while (cursor.moveToNext()) {
            if (
        cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)).contentEquals(record.getTitle()) &&
        cursor.getString(cursor.getColumnIndex(COLUMN_LONGITUDE)).contentEquals(record.getLongitude()) &&
        cursor.getString(cursor.getColumnIndex(COLUMN_LATITUDE)).contentEquals(record.getLatitude())
            ) return -1;
        }
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, record.getTitle());
        values.put(COLUMN_LONGITUDE, record.getLongitude());
        values.put(COLUMN_LATITUDE, record.getLatitude());
        values.put(COLUMN_CONTACT, record.getContact());
        values.put(COLUMN_ADDRESS, record.getAddress());
        return db.insert(TABLE_NAME, null, values);
    }

    /**
     * Removes the specified Record.
     * @param record record to remove
     * @return the number of rows affected
     */
    public int remove(Record record) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(
            TABLE_NAME,
            COLUMN_TITLE + " == ? AND " +
            COLUMN_LATITUDE + " == ? AND " +
            COLUMN_LONGITUDE + " == ?",
            new String[] {
                record.getTitle(),
                record.getLatitude(),
                record.getLongitude()
            }
        );
    }

    public Cursor getAll() {
        return this.getWritableDatabase().query(
            false,
            TABLE_NAME,
            new String[] {
                COLUMN_ID,
                COLUMN_TITLE,
                COLUMN_LONGITUDE,
                COLUMN_LATITUDE,
                COLUMN_CONTACT,
                COLUMN_ADDRESS
            },
            null,
            null,
            null,
            null,
            null,
            null
        );
    }

    public List<Record> getAllRecords() {
        return new ArrayList<Record>() {{
            Cursor cursor = getAll();
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    Record record = new Record(
                        cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_CONTACT)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_ADDRESS)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_LATITUDE)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_LONGITUDE)),
                        true
                    );
                    if (!contains(record))
                        add(record);
                } while (cursor.moveToNext());
            }
        }};
    }
}
