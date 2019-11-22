package com.mayabo.finalandroidproject.recipe;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.mayabo.finalandroidproject.R;

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

public class ListFavouriteActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_favourite_acvitity);





    }





    /**
     * This is the favourite list is loaded from database list
     * The list will load the adapter and post everything in the database to the list view
     * using custom adapter
     * using the same list view that is create
     * click on the item will take the user to another single page
     * the single page will have the Remove button
     * Delete the item from the database
     * */
}
