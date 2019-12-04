package com.mayabo.finalandroidproject.recipe;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentActivity;

import com.mayabo.finalandroidproject.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


/**
 * This is the final project
 * Topic is specify to Recipe Search Engine
 * This project is a group project
 * Each person take one topic
 * My Topic is : Recipe
 *
 * @author The Dai Phong Le
 * @version 1.0
 * @since 2019-11-11
 */

public class SearchingActivity extends AppCompatActivity {

    public static final String ACTIVITY_NAME = "SearchingActivity";
    public static final String NAME_ACTIVITY = "NAME ACTIVITY";
    ArrayList<Recipe> foodList;
    ProgressBar myProgressBar;
    ListView theList;
    String userFilter;
    CustomListAdapter adapter;
    Menu menu;
    Toolbar tbar;
    Bitmap image;
    Bitmap imageToSet;
    public static ArrayList<Bitmap> imageList = new ArrayList<>();

    public static ArrayList<String> imgUrls = new ArrayList<>();


    //Fragment attributes:
    public static final String TITLE_SELECTED = "TITLE";
    public static final String SOURCE_URL = "URL";
    public static final String IMAGE_URL = "IMAGE URL";
    public static final String IMAGE_ID = "IMAGE ID";
    public static final String ITEM_POSITION = "POSITION";
    public static final String ITEM_ID = "ID";
    public static final String ID_IN_DB = "DATAID";
    public static final String USER_FILTER = "USER FILTER";
    public static final int EMPTY_ACTIVITY = 123;

    Bundle dataToPass;
    Bundle dataBack;


    /**
     * @Override Oncreate() to create the activity
     * calling setContentView to set the view for this Acvity
     * {@Link ListView} to get the list view
     */

    LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recipe_favourite);
        theList = (ListView) findViewById(R.id.list_favourite);
        myProgressBar = (ProgressBar) findViewById(R.id.progressBar);

        Intent dataFromPreviousPage = getIntent();
        userFilter = dataFromPreviousPage.getStringExtra("searchFilter");
        dataBack = getIntent().getExtras();

        if (dataBack.getString(SearchingActivity.USER_FILTER) != null)
            userFilter = dataBack.getString(SearchingActivity.USER_FILTER);


        tbar = (Toolbar) findViewById(R.id.toolbar);
        tbar.setTitle("Recipe Of " + userFilter);
        tbar.setBackgroundColor(getResources().getColor(R.color.tBar));
        tbar.setTitleTextColor(getResources().getColor(R.color.lightBackground));
        tbar.getOverflowIcon().setColorFilter(getResources().getColor(R.color.colorHome), PorterDuff.Mode.SRC_ATOP);
        setSupportActionBar(tbar);


        boolean isTablet = findViewById(R.id.fragmentLocation) != null; //check if the FrameLayout is loaded

        //linearLayout = findViewById(R.id.linear_layout);
        if (userFilter != null) {
            if (userFilter.equals("Lasagna") || userFilter.equals("Chicken Breast")) {
                RecipeQuery theQuery = new RecipeQuery(this);
                theQuery.execute();
                myProgressBar.setVisibility(View.VISIBLE);
            } else {
                Toast.makeText(this, "Filter Error", Toast.LENGTH_LONG).show();
//                Snackbar snackbar = Snackbar
//                        .make(, "Go back?", Snackbar.LENGTH_LONG)
//                        //setAction is when you click on the e lambda
//                        .setAction("Yes", e -> {
//                            Intent goToSearch = new Intent(SearchingActivity.this, RecipeSearchActivity.class);
//                            startActivity(goToSearch);
//                        });
//                snackbar.setActionTextColor(Color.GREEN);
//                snackbar.show();
            }
        }


        /**This will set the list to be clickable using the method
         * setOnItemClickListener
         * {@link Recipe} : choosenRecipe
         * */

        theList.setOnItemClickListener((parent, view, position, id) -> {

            Log.e("You clicked on :", " item " + position);

            Recipe chosenRecipe = foodList.get(position);

            dataToPass = new Bundle();
            dataToPass.putString(NAME_ACTIVITY, ACTIVITY_NAME);
            dataToPass.putString(TITLE_SELECTED, chosenRecipe.getTitle());
            dataToPass.putString(SOURCE_URL, chosenRecipe.getUrl());
            dataToPass.putString(IMAGE_URL, chosenRecipe.getImgUrl());
            dataToPass.putInt(ITEM_POSITION, position);
            dataToPass.putLong(ITEM_ID, id);
            dataToPass.putLong(ID_IN_DB, chosenRecipe.getId());
            dataToPass.putString(IMAGE_ID, chosenRecipe.getImageID());
            dataToPass.putString(USER_FILTER, userFilter);


            if (isTablet) {
                /**
                 * The FragmentManager can add, remove, or replace a Fragment
                 * that is currently loaded into a FrameLayout*/
                FragmentDetails dFragment = new FragmentDetails(); //add a DetailFragment
                dFragment.setArguments(dataToPass); //pass it a bundle for information
                dFragment.setTablet(true);  //tell the fragment if it's running on a tablet or not
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentLocation, dFragment) //Add the fragment in FrameLayout
                        .commit(); //actually load the fragment.


            } else //isPhone
            {
                Intent nextActivity = new Intent(SearchingActivity.this, EmptyActivity.class);
                nextActivity.putExtras(dataToPass); //send data to next activity
                startActivity(nextActivity);
//                startActivityForResult(nextActivity, EMPTY_ACTIVITY); //make the transition
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
                if (addMessageId((int)id)) {

                    Toast.makeText(this, "Successfully Added", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "Can't Add", Toast.LENGTH_LONG).show();
                }
            }
        }
    }


    public boolean addMessageId(int id) {
        boolean found = false;
        String inDataTitle;
        String inSelectedTitle;
        DatabaseHandler db = new DatabaseHandler(this);

        if ( db.addRecipe(foodList.get(id)) == 1) {
            adapter.notifyDataSetChanged();
//            Toast.makeText(this, "Successfully Added", Toast.LENGTH_LONG).show();
            return true;
        } else {
//            Toast.makeText(this, "Can't Add", Toast.LENGTH_LONG).show();
            return false;
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.exit:
                Toast.makeText(this, "Welcome Back!", Toast.LENGTH_SHORT).show();
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


    /**
     * This class is a inner class that will work with the Internet connection
     * This class will need to extends AsyncTask
     * This class needs to implements the doInBackGround, onPreExecute, onPostExecute, OnProgressUpdate
     */
    private class RecipeQuery extends AsyncTask<String, Integer, String> {
        String title;
        String foodUrl;
        String imageUrl;
        Recipe recipe;
        String imageID;
        String result = null;
        Context context;

        public RecipeQuery(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... strings) {
            result = null;
            String queryURL = "http://torunski.ca/FinalProjectLasagna.json";

            switch (userFilter) {
                case "Lasagna":
                    queryURL = "http://torunski.ca/FinalProjectLasagna.json";
                    break;
                case "Chicken Breast":
                    queryURL = "http://torunski.ca/FinalProjectChickenBreast.json";
            }


            try {

                // Connect to the server:
                URL url = new URL(queryURL);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream inStream = urlConnection.getInputStream();

                //Set up the JSON object parser:
                // json is UTF-8 by default
                BufferedReader reader = new BufferedReader(new InputStreamReader(inStream, "UTF-8"), 8);
                StringBuilder sb = new StringBuilder();

                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }

                //creating a new JSON object
                try {
                    JSONObject jObject = new JSONObject(sb.toString());
                    JSONArray recipesJArray = jObject.getJSONArray("recipes");


                    for (int i = 0; i < recipesJArray.length(); i++) {
                        JSONObject recipeJSON = recipesJArray.getJSONObject(i);
                        title = recipeJSON.getString("title");
                        foodUrl = recipeJSON.getString("source_url");
                        imageUrl = recipeJSON.getString("image_url");
                        imageID = recipeJSON.getString("recipe_id");


                        recipe = new Recipe(title, imageID, imageUrl, foodUrl);

                        foodList.add(recipe);
                    }

                    for (int j = 0; j < 85; j++) {
                        publishProgress(j);
                        try {
                            Thread.sleep(20);
                        } catch (Exception e) {
                        }
                    }
                } catch (org.json.JSONException e) {
                    e.printStackTrace();
                    result = "JSON Creating Object Error";
                }


                Log.d("Response: ", "> " + line);
                urlConnection.disconnect();

            } catch (MalformedURLException mfe) {
                result = "Malformed URL exception";
            } catch (IOException ioe) {
                result = "IO Exception. Is the Wifi connected?";
            }

            publishProgress(90);
            try {
                Thread.sleep(500);
            } catch (Exception e) {
            }

            return result;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            foodList = new ArrayList<>();

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            adapter = new CustomListAdapter(context, foodList);
            theList.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            myProgressBar.setVisibility(View.INVISIBLE);

        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            myProgressBar.setProgress(values[0]);
            myProgressBar.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, RecipeSearchActivity.class);
        finish();
        startActivity(intent);
    }
}


