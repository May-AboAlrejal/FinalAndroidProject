package com.mayabo.finalandroidproject.recipe;
import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * This is the final project
 * Topic is specify to Recipe Search Engine
 * This project is a group project
 * Each person take one topic
 * My Topic is : Recipe
 * @author The Dai Phong Le
 * @version 1.0
 * @since 2019-11-11
 */

public class MyDatabaseOpenHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "RecipeDatabaseFile";
    public static final int VERSION_NUM = 10;
    public static final String TABLE_NAME = "Recipe";
    public static final String COL_TITLE = "TITLE";
    public static final String COL_ID = "_ID";
    public static final String COL_URL = "URL";

    public static final String CREATE_TABLE = "CREATE TABLE "+TABLE_NAME
            + "( "+COL_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"+COL_TITLE+" TEXT, "+COL_URL+" TEXT)";

    public MyDatabaseOpenHelper(Activity ctx){
        //The factory parameter should be null, unless you know a lot about Database Memory management
        /**Context ctx: the Activity where the database is being opened
         * String databaseName – this is the file that will contain the data.
         * CursorFactory – An object to create Cursor objects, normally this is null.
         * Int version – What is the version of your database
         */
        super(ctx, DATABASE_NAME, null, VERSION_NUM );
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //        db.execSQL("CREATE TABLE " + TABLE_NAME + "( "
//                + COL_ID +" INTEGER PRIMARY KEY AUTOINCREMENT,"
//                + COL_NAME +" TEXT, " + COL_EMAIL + " TEXT)");

        //Make sure you put spaces between SQL statements and Java strings:
        db.execSQL(CREATE_TABLE);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        Log.i("Database upgrade", "Old version:" + oldVersion + " newVersion:"+newVersion);

        //Delete the old table:
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

        //Create a new table:
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        Log.i("Database downgrade", "Old version:" + oldVersion + " newVersion:"+newVersion);

        //Delete the old table:
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

        //Create a new table:
        onCreate(db);
    }
}
