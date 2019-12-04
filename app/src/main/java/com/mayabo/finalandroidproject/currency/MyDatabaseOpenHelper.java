package com.mayabo.finalandroidproject.currency;

import android.app.Activity;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


/**
 * This class is to set up the database which has 3 columns
 */
public class MyDatabaseOpenHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "MyFavoriteItem";
    public static final String TABLE_NAME = "Favoriteitem";
    public static final int VERSION_NUM = 1;
    public static final String COL_ID = "_id";
    public static final String COL_FROM = "BASECURRENCY";
    public static final String COL_TO = "CONVERTEDCURRENCY";


    /**
     * This method is constructor which pass one parameter ctx
     * @param ctx: Activity
     */
    public MyDatabaseOpenHelper (Activity ctx) {
        super(ctx, DATABASE_NAME, null, VERSION_NUM);
    }

    /**
     * This method is to create table using parameter database
     * @param db
     */
    public void onCreate (SQLiteDatabase db){
        db.execSQL("CREATE TABLE " + TABLE_NAME + "( "
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COL_FROM + " TEXT, " + COL_TO + " TEXT)");
    }

    /**
     * This method is to delete the old database and upgrade the new one
     * @param db: SQLiteDatabase
     * @param oldVersion: int
     * @param newVersion: int
     */
    public void onUpgrade (SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i("Database upgrade", "Old version: " + oldVersion + " new version" + newVersion);

        // Delete the old table:
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

        // Create a new table:
        onCreate(db);
    }

    /**
     * This method is to delete the newer database and downgrade the older one
     * @param db: SQLiteDatabase
     * @param oldVersion: int
     * @param newVersion: int
     */
    public void onDowngrade (SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i("Database upgrade", "Old version: " + oldVersion + " new version" + newVersion);

        // Delete the old table:
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

        // Create a new table:
        onCreate(db);
    }

    /**
     * This method is to insertConversion using one item in FavoriteItem as a parameter
     * @param item: FavoriteItem
     * @return id: long
     */
    //this method is to add item to database
    public long insertConversion (FavoriteItem item) {
        SQLiteDatabase db = getWritableDatabase();
        String baseCur = item.getFrom();
        String dstCur = item.getTo();

        // add to the database and get the new ID
        ContentValues cv = new ContentValues();

        // add base currency in the BASECURRENCY column
        cv.put(MyDatabaseOpenHelper.COL_FROM, baseCur);
        // add convert currency in the CONVERTEDCURRENCY column
        cv.put(MyDatabaseOpenHelper.COL_TO, dstCur);

        //insert in the database:
        long id = db.insert(MyDatabaseOpenHelper.TABLE_NAME, null, cv);
        return id;
    }

    /**
     * This method is to remove item from database using object of FavoriteItem as a parameter
     * @param item: FavoriteItem
     * @return int.
     */

    public int remove(FavoriteItem item){
        SQLiteDatabase db = this.getWritableDatabase();

        return db.delete(TABLE_NAME, COL_FROM + " = ? AND "
                + COL_TO + " = ?", new String[] { item.getFrom(), item.getTo()});
    }


}
