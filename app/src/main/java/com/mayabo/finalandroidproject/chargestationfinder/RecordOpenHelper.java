package com.mayabo.finalandroidproject.chargestationfinder;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class RecordOpenHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Favorites";
    public static final String TABLE_NAME = "FAVORITES";
    public static final String COLUMN_ID = "ID";
    public static final String COLUMN_TITLE = "TITLE";
    public static final String COLUMN_LONGITUDE = "LONGITUDE";
    public static final String COLUMN_LATITUDE = "LATITUDE";

    public RecordOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
            "CREATE TABLE " + TABLE_NAME + "(" +
                COLUMN_ID        + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TITLE     + " TEXT, " +
                COLUMN_LONGITUDE + " TEXT, " +
                COLUMN_LATITUDE  + " TEXT" +
            ");"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public long insert(Record record) {
        Cursor cursor = getAll();
        cursor.moveToFirst();
        while (cursor.moveToNext()) {
            if (
        cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)).equalsIgnoreCase(record.getTitle()) &&
        cursor.getString(cursor.getColumnIndex(COLUMN_LONGITUDE)).equalsIgnoreCase(record.getLongitude()) &&
        cursor.getString(cursor.getColumnIndex(COLUMN_LATITUDE)).equalsIgnoreCase(record.getLatitude())
            ) return -1;
        }
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, record.getTitle());
        values.put(COLUMN_LONGITUDE, record.getLongitude());
        values.put(COLUMN_LATITUDE, record.getLatitude());
        return db.insert(TABLE_NAME, null, values);
    }

    public boolean contains(Record record) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(
            TABLE_NAME,
            new String[] {
                COLUMN_TITLE,
                COLUMN_LONGITUDE,
                COLUMN_LATITUDE
            },
            COLUMN_TITLE + " == ? AND " + COLUMN_LATITUDE + " == ? AND " + COLUMN_LONGITUDE + " == ?",
            new String[] {
                record.getTitle(),
                record.getLatitude(),
                record.getLongitude()
            },
            null,
            null,
            null
        );
        return cursor.getCount() > 0;
    }

    public int remove(Record record) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(
            TABLE_NAME,
    COLUMN_TITLE + " == ? AND " + COLUMN_LATITUDE + " == ? AND " + COLUMN_LONGITUDE + " == ?",
            new String[] {
                record.getTitle(),
                record.getLatitude(),
                record.getLongitude()
            }
        );
    }

    public Record removeById(long id) {
        Record record = null;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(
            TABLE_NAME,
            new String[] {
                COLUMN_TITLE,
                COLUMN_LONGITUDE,
                COLUMN_LATITUDE
            },
            "ID == ?",
            new String[] {String.valueOf(id)},
            null,
            null,
            null
        );
        if (cursor.getCount() > 0) {
            db.delete(TABLE_NAME, "ID == ?", new String[]{String.valueOf(id)});
            cursor.moveToFirst();
            record = new Record(
                cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)),
                "",
                cursor.getString(cursor.getColumnIndex(COLUMN_LATITUDE)),
                cursor.getString(cursor.getColumnIndex(COLUMN_LONGITUDE)),
                false
            );
        }
        return record;
    }

    public Cursor getAll() {
        return this.getWritableDatabase().query(
            false,
            TABLE_NAME,
            new String[] {
                COLUMN_ID,
                COLUMN_TITLE,
                COLUMN_LONGITUDE,
                COLUMN_LATITUDE
            },
            null,
            null,
            null,
            null,
            null,
            null
        );
    }
}
