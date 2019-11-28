package com.mayabo.finalandroidproject.news;

import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

/**
 * The class creates a database if not excites
 * The table "NEWS" will hold all the information of an article
 * Each row is an article
 * Each column is an article attribute
 */
public class NewsBaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "NEWSDATABASE";
    public static final int VERSION_NUM = 2;
    public static final String TABLE_NAME = "NEWS";
    public static final String COL_ID = "_id";

    public NewsBaseHelper(Activity activity) {
        super(activity, DATABASE_NAME, null, VERSION_NUM);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + "( "
                + COL_ID +" INTEGER PRIMARY KEY AUTOINCREMENT,"
                + NewsApiResponse.AUTHOR + " TEXT, "
                + NewsApiResponse.TITLE + " TEXT, "
                + NewsApiResponse.DESCRIPTION + " TEXT, "
                + NewsApiResponse.URL + " TEXT, "
                + NewsApiResponse.URL_TO_IMAGE + " TEXT, "
                + NewsApiResponse.IMAGE_NAME + " TEXT, "
                + NewsApiResponse.PUBLISHED_AT + " TEXT, "
                + NewsApiResponse.CONTENT + " TEXT, "
                + NewsApiResponse.SOURCE  + " TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Log.i("Database upgrade", "Old version:" + oldVersion + " newVersion:"+newVersion);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Log.i("Database downgrade", "Old version:" + oldVersion + " newVersion:"+newVersion);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }


}
