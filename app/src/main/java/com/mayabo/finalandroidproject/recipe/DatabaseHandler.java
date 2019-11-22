package com.mayabo.finalandroidproject.recipe;
import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.content.ContentValues;
import android.database.Cursor;

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

public class DatabaseHandler extends SQLiteOpenHelper {
    //database info
    public static final String DATABASE_NAME = "RecipeDataFile";
    public static final int VERSION_NUM = 1;

    //table name
    public static final String TABLE_RECIPE = "UserList";

    //column
    public static final String COL_TITLE = "Title";
    public static final String COL_ID = "_ID";
    public static final String COL_URL = "Url";
    public static final String COL_IMAGE_URL = "Image_Url";
    public static final String COL_IMAGE_ID = "Image_ID";

    //create table query
    public static final String CREATE_TSEARCH = "CREATE TABLE " + TABLE_RECIPE
            + "( " + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COL_TITLE + " TEXT, " + COL_IMAGE_URL +
            " TEXT, "+ COL_IMAGE_ID+" TEXT, " + COL_URL + " TEXT" + ")";




    public DatabaseHandler(Activity ctx){
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
        db.execSQL(CREATE_TSEARCH);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        Log.i("Database upgrade", "Old version:" + oldVersion + " newVersion:"+newVersion);

        //Delete the old table:
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPE);

        //Create a new table:
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        Log.i("Database downgrade", "Old version:" + oldVersion + " newVersion:"+newVersion);

        //Delete the old table:
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPE);

        //Create a new table:
        onCreate(db);
    }


    void addRecipe(Recipe recipe) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();


        values.put(COL_TITLE, recipe.getTitle()); // Recipe Name
        values.put(COL_IMAGE_ID, recipe.getImageID()); // Recipe Image ID
        values.put(COL_IMAGE_URL, recipe.getImgUrl()); // Recipe Image URL
        values.put(COL_URL, recipe.getUrl()); // Recipe Source URL


        // Inserting Row
        db.insert(TABLE_RECIPE, null, values);
        //2nd argument is String containing nullColumnHack
        db.close(); // Closing database connection
    }



    // code to get the single contact
    Recipe getRecipe(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_RECIPE, new String[] { COL_ID,
                        COL_TITLE, COL_IMAGE_ID, COL_IMAGE_URL, COL_URL }, COL_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        //Recipe (long id, String title, String imageID, String imgUrl, String url)
        long cID = Long.parseLong(cursor.getString(0));
        String cTitle = cursor.getString(1);
        String cImageID = cursor.getString(2);
        String cImageUrl = cursor.getString(3);
        String cUrl = cursor.getString(4);

        Recipe recipe = new Recipe(cID,cTitle,cImageID,cImageUrl,cUrl);
        // return contact
        return recipe;
    }


    // Deleting single Recipe
    public void deleteRecipe(Recipe recipe) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_RECIPE, COL_ID + " = ?",
                new String[] { String.valueOf(recipe.getId()) });
        db.close();
    }

    // Getting contacts Count
    public int getRecipesCount() {
        String countQuery = "SELECT  * FROM " + TABLE_RECIPE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }


}
