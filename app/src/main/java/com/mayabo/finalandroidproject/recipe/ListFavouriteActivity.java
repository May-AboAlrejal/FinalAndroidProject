package com.mayabo.finalandroidproject.recipe;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.mayabo.finalandroidproject.R;

import java.util.ArrayList;

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

    ListView theList;
    CustomListAdapter adapter;
    ArrayList<Recipe> savedList;
    Toolbar tbar;
    Menu menu;


    int numberRecipe;
    public static final String ACTIVITY_NAME = "ListFavouriteActivity";
    public static final String NAME_ACTIVITY = "NAME ACTIVITY";
    public static final String TITLE_SELECTED = "TITLE";
    public static final String SOURCE_URL = "URL";
    public static final String IMAGE_URL = "IMAGE URL";
    public static final String IMAGE_ID = "IMAGE ID";
    public static final String ITEM_POSITION = "POSITION";
    public static final String ITEM_ID = "ID";
    public static final String ID_IN_DB = "DATAID";

    public static final int EMPTY_ACTIVITY = 123;



    /**
     * This is the favourite list is loaded from database list
     * The list will load the adapter and post everything in the database to the list view
     * using custom adapter
     * using the same list view that is create
     * click on the item will take the user to another single page
     * the single page will have the Remove button
     * Delete the item from the database
     * */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recipe_favourite);
        tbar = (Toolbar) findViewById(R.id.toolbar);
        //check if the FrameLayout is loaded
        boolean isTablet = findViewById(R.id.fragmentLocation) != null;
        theList = (ListView) findViewById(R.id.list_favourite);


        DatabaseHandler db = new DatabaseHandler(this);

        savedList = new ArrayList<Recipe>(db.getAllRecipes());
        numberRecipe = savedList.size();
        tbar.setTitle("Recipe Favourite List");
        tbar.setBackgroundColor(getResources().getColor(R.color.tBar));
        tbar.setTitleTextColor(getResources().getColor(R.color.lightBackground));
        setSupportActionBar(tbar);


        adapter = new CustomListAdapter(this, savedList);
        theList.setAdapter(adapter);
        adapter.notifyDataSetChanged();



        //Set the list item on click listener
            theList.setOnItemClickListener((parent, view, position, id) -> {

                Log.e("You clicked on :", " item " + position);
                Recipe chosenRecipe = savedList.get(position);
                Bundle dataToPass = new Bundle();
                dataToPass.putString(NAME_ACTIVITY, ACTIVITY_NAME);
                dataToPass.putString(TITLE_SELECTED, chosenRecipe.getTitle());
                dataToPass.putString(SOURCE_URL, chosenRecipe.getUrl());
                dataToPass.putString(IMAGE_URL, chosenRecipe.getImgUrl());
                dataToPass.putInt(ITEM_POSITION, position);
                dataToPass.putLong(ITEM_ID, id);
                dataToPass.putLong(ID_IN_DB, chosenRecipe.getId());
                dataToPass.putString(IMAGE_ID, chosenRecipe.getImageID());
                if(isTablet)
                {
                    /**
                     * The FragmentManager can add, remove, or replace a Fragment
                     * that is currently loaded into a FrameLayout
                     * */
                    FragmentDetails dFragment = new FragmentDetails(); //add a DetailFragment
                    dFragment.setArguments( dataToPass ); //pass it a bundle for information
                    dFragment.setTablet(true);  //tell the fragment if it's running on a tablet or not
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragmentLocation, dFragment) //Add the fragment in FrameLayout
                            .commit(); //actually load the fragment.
                }
                else //isPhone
                {
                    Intent nextActivity = new Intent(ListFavouriteActivity.this, EmptyActivity.class);
                    nextActivity.putExtras(dataToPass); //send data to next activity
                    startActivityForResult(nextActivity, EMPTY_ACTIVITY); //make the transition
                }
            });

    }

    //This function only gets called on the phone. The tablet never goes to a new activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == EMPTY_ACTIVITY)
        {
            if(resultCode == RESULT_OK) //if you hit the delete button instead of back button
            {
                long id = data.getLongExtra(ITEM_ID, 0);
                deleteMessageId((int)id);
                Toast.makeText(this, "Successfully Removed!", Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * Delete Method to delete and updateAdapter populate a new ListView
     *
     * */
    public void deleteMessageId(int id)
    {
        Log.i("Delete this message:" , " id="+id);
        DatabaseHandler db = new DatabaseHandler(this);
        db.deleteRecipe(savedList.get(id));
        savedList.remove(id);
        adapter.notifyDataSetChanged();
    }

    protected void intentToMain() {
        Intent goToMain = new Intent(this, RecipeSearchActivity.class);
        startActivity(goToMain);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.intentToMain();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId())

        {
            case R.id.exit:
                Toast.makeText(this, "Welcome Back!", Toast.LENGTH_SHORT).show();
                this.intentToMain();
                finish();
                break;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.exit_to_home, menu);
        this.menu = menu;
        return true;
    }

}
