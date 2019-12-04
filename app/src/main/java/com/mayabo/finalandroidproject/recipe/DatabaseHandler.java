package com.mayabo.finalandroidproject.recipe;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

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




    //Constructor taking acivity
    public DatabaseHandler(Activity ctx){
        //The factory parameter should be null
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

    /**
     * Add Recipe Function take
     * @Param: Recipe
     * Check boolean for found the duplicate
     * return an int because I don't want boolean
     * creating a ContentValue to store the attributes
     *
     * */

    public int addRecipe(Recipe recipe) {
        boolean found = false;
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COL_TITLE, recipe.getTitle()); // Recipe Name
        values.put(COL_IMAGE_ID, recipe.getImageID()); // Recipe Image ID
        values.put(COL_IMAGE_URL, recipe.getImgUrl()); // Recipe Image URL
        values.put(COL_URL, recipe.getUrl()); // Recipe Source URL

                    String inDataTitle;
                    String inSelectedTitle;

                    //call getAll in addAll getting a temporary list to iterate over the database object
                    ArrayList<Recipe> myRecipes = new ArrayList<>();
                    myRecipes.addAll(this.getAllRecipes());

                        for (int i = 0; i < myRecipes.size(); i++) {
                            inDataTitle = myRecipes.get(i).getTitle();
                            inSelectedTitle = recipe.getTitle();
                            if (inDataTitle.equals(inSelectedTitle)) {
                                found = true;
                                break;
                            }
                    }

                    if (!found) {

                        db.insert(TABLE_RECIPE, null, values);
                        db.close(); // Closing database connection
                        return 1;
                    } else {
                        return -1;
                    }
    }


    // Deleting single Recipe
    public void deleteRecipe(Recipe recipe) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_RECIPE, COL_TITLE + " = ?",
                new String[] { String.valueOf(recipe.getTitle()) });
        db.close();
    }


    // code to get all contacts in a list view
    public List<Recipe> getAllRecipes() {
        List<Recipe> recipeList = new ArrayList<Recipe>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_RECIPE;

        SQLiteDatabase db = this.getWritableDatabase();
        //calling the rawQuery to return a result into the Cursor
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Recipe recipe = new Recipe();
                recipe.setId(Long.parseLong(cursor.getString(0)));
                recipe.setTitle(cursor.getString(1));
                recipe.setImgUrl(cursor.getString(2));
                recipe.setImageID(cursor.getString(3));
                recipe.setUrl(cursor.getString(4));

                // Adding contact to list
                recipeList.add(recipe);
            } while (cursor.moveToNext());
        }

        // return a Recipe list
        return recipeList;
    }


}
