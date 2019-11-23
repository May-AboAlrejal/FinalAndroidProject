package com.mayabo.finalandroidproject.recipe;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
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
    ArrayList<Recipe> foodList;
    ProgressBar myProgressBar;
    ListView theList;
    String userFilter;
    CustomListAdapter adapter;


    /**
     * @Override Oncreate() to create the activity
     * calling setContentView to set the view for this Acvity
     * {@Link ListView} to get the list view
     */

    LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.searching_acvitity_layout);
        theList = (ListView) findViewById(R.id.list_search);
        myProgressBar = (ProgressBar) findViewById(R.id.progressBar);

        Intent dataFromPreviousPage = getIntent();
        userFilter = dataFromPreviousPage.getStringExtra("searchFilter");


        linearLayout = findViewById(R.id.linear_layout);
        if (userFilter != null) {

            if (userFilter.equals("Lasagna") || userFilter.equals("Chicken Breast")) {
                Toast.makeText(this, "Searching ... ", Toast.LENGTH_LONG).show();
                RecipeQuery theQuery = new RecipeQuery(this);
                theQuery.execute();
                myProgressBar.setVisibility(View.VISIBLE);
            } else {
                Toast.makeText(this, "Your search is wrong\n No result", Toast.LENGTH_LONG).show();
                Snackbar snackbar = Snackbar
                        .make(linearLayout, "Go back?", Snackbar.LENGTH_LONG)
                        //setAction is when you click on the e lambda
                        .setAction("Yes", e -> {
                            Intent goToSearch = new Intent(SearchingActivity.this, RecipeSearchActivity.class);
                            startActivity(goToSearch);
                        });
                snackbar.setActionTextColor(Color.GREEN);
                snackbar.show();
            }


        } else {

            Toast.makeText(this, "You Search For Nothing", Toast.LENGTH_LONG).show();
        }


        /**This will set the list to be clickable using the method
         * setOnItemClickListener
         * {@link Recipe} : choosenRecipe
         * */

        theList.setOnItemClickListener((parent, view, position, id) -> {
            Log.e("You clicked on :", " item " + position);
            Recipe chosenRecipe = foodList.get(position);
            Intent singlePage = new Intent(SearchingActivity.this, RecipeSingle.class);
            singlePage.putExtra("ActivityName", ACTIVITY_NAME);
            singlePage.putExtra("title", chosenRecipe.getTitle());
            singlePage.putExtra("url", chosenRecipe.getUrl());
            singlePage.putExtra("imageUrl", chosenRecipe.getImgUrl());
            singlePage.putExtra("imageID", chosenRecipe.getImageID());
            startActivity(singlePage);
        });

    }


    /**
     * This is a custom dialog method
     * this will need to have builder
     * the view will be set to its layout
     * The elements need to be call to use the middle.findViewById
     */


    public void alertDialog() {
        View middle = getLayoutInflater().inflate(R.layout.title_item, null);
        TextView tv = findViewById(R.id.title_TextView);

        tv.setText("You clicked my button!");

        //Alert Diaglog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //this is call the builder pattern, the order of calling function does not matter

        //positive and negative are the button
        builder.setMessage("The Message")
                .setPositiveButton("Positive", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // What to do on Accept
                    }
                })
                .setNegativeButton("Negative", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // What to do on Cancel
                    }
                }).setView(middle);

        //show
        builder.create().show();
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

                publishProgress(50);
                try {
                    Thread.sleep(10);
                } catch (Exception e) {
                }


                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }

                //creating a new JSON object
                try {
                    JSONObject jObject = new JSONObject(sb.toString());
                    JSONArray recipesJArray = jObject.getJSONArray("recipes");

//                    for (int j = 0; j < 100; j++) {
//                        publishProgress(j);
//                        try {
//                            Thread.sleep(10);
//                        } catch (Exception e) {
//                        }
//                    }

                    for (int i = 0; i < recipesJArray.length(); i++) {
                        JSONObject recipeJSON = recipesJArray.getJSONObject(i);
                        title = recipeJSON.getString("title");
                        foodUrl = recipeJSON.getString("source_url");
                        imageUrl = recipeJSON.getString("image_url");
                        imageID = recipeJSON.getString("recipe_id");
                        recipe = new Recipe(title, imageID, imageUrl, foodUrl);
                        foodList.add(recipe);
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
            publishProgress(100);
            try {
                Thread.sleep(10);
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


}


