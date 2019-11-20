package com.mayabo.finalandroidproject.recipe;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.mayabo.finalandroidproject.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
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
 * @author The Dai Phong Le
 * @version 1.0
 * @since 2019-11-11
 */

public class SearchingActivity extends AppCompatActivity {

    /**
     * AppcombatActivity
     * Creating a list of working variables
     * Initialize it in the onCreate
     */

    public static final String ACTIVITY_NAME = "SearchingActivity";
    ArrayList<Recipe> foodList;
    ArrayList<String> titleList;
    ProgressBar myProgressBar;
    TextView titleView;
    TextView urlView;
    ListView theList;
    EditText searchFilter;
    Button searchBtn;
    MyOwnAdapter myAdapter;
    String userFilter;
    String[] test;
    String fileName;

    //Map<String, String> capitalCities;

    int positionClicked = 0;

    /**
     * @Override Oncreate() to create the activity
     * calling setContentView to set the view for this Acvity
     * {@link ListView} to get the list view
     *
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.searching_acvitity_layout);
        theList = (ListView) findViewById(R.id.list_search);
        //searchFilter = (EditText) findViewById(R.id.search_filter);
        myProgressBar = (ProgressBar)findViewById(R.id.progressBar);

        Intent dataFromPreviousPage = getIntent();
        userFilter = dataFromPreviousPage.getStringExtra("searchFilter");



        if (userFilter != null) {
            RecipeQuery theQuery = new RecipeQuery();
            theQuery.execute();
            myProgressBar.setVisibility(View.VISIBLE);
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
            singlePage.putExtra("title", chosenRecipe.getTitle());
            singlePage.putExtra("url", chosenRecipe.getUrl());
            singlePage.putExtra("imageUrl", chosenRecipe.getImgUrl());
            singlePage.putExtra("id", chosenRecipe.getId());
            startActivity(singlePage);
        });

    }

    /**getImage return of type Bitmap
     * Checking for connection response code
     * checking for the url connection
     * @param : URL
     * @return BitMap
     * */


    //Image
    protected static Bitmap getImage(URL url) {

        HttpURLConnection iconConn = null;
        try {
            iconConn = (HttpURLConnection) url.openConnection();
            iconConn.connect();
            int response = iconConn.getResponseCode();
            if (response == 200) {
                return BitmapFactory.decodeStream(iconConn.getInputStream());
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (iconConn != null) {
                iconConn.disconnect();
            }
        }
    }

    /**This method is a customize method
     *Checking if the file is exist
     * @Param fileName
     */

    public boolean fileExistance(String fileName) {
        Log.i(ACTIVITY_NAME, getBaseContext().getFileStreamPath(fileName).toString());
        File file = getBaseContext().getFileStreamPath(fileName);
        return file.exists();
    }




    /**This class is a custom dialog
     * this will need to have builder
     * the view will be set to its layout
     * The elements need to be call to use the middle.findViewById
     */


    public void alertDialog()
    {
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


        //can have third button as neutral button

        //showing stuff
        builder.create().show();
    }

//
    //This class needs 4 functions to work properly:

    /**This is the inner class to populate the listView
     * This class need to extends BaseAdapter
     * implements 4 methods: getCount(), getItem, getView, getItemID
     * */
    protected class MyOwnAdapter extends BaseAdapter {

        @Override
        public int getCount() { return foodList.size(); }

        public Recipe getItem(int position) {  return foodList.get(position); }


        public View getView(int position, View old, ViewGroup parent) {

//            inflater = getLayoutInflater();
            LayoutInflater inflater = getLayoutInflater();
            View rowView;
            TextView rowTitle;

            Recipe thisRow = getItem(position);

            rowView = inflater.inflate(R.layout.title_item, null);
            rowTitle = (TextView) rowView.findViewById(R.id.title_TextView);

            rowTitle.setText(thisRow.getTitle());
            return rowView;
        }

        public long getItemId(int position) { return position; }
    }


    /**This class is a inner class that will work with the Internet connection
     * This class will need to extends AsyncTask
     * This class needs to implements the doInBackGround, onPreExecute, onPostExecute, OnProgressUpdate
     */
    private class RecipeQuery extends AsyncTask<String, Integer, String> {
        String title;
        String foodUrl;
        String imageUrl;
        Recipe recipe;
        Bitmap image = null;
        String result = null;

        //public AsyncResponse delegate = null;





        @Override
        protected String doInBackground(String... strings) {
            result = null;
            String queryURL="http://torunski.ca/FinalProjectLasagna.json";

            switch (userFilter) {
                case "Lasagna":
                    queryURL = "http://torunski.ca/FinalProjectLasagna.json";
                    break;
                case "Chicken Breast":
                    queryURL = "http://torunski.ca/FinalProjectChickenBreast.json";
            }




            int progress = 0;
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

                    for (int i =0; i<recipesJArray.length(); i++) {
                        JSONObject recipeJSON = recipesJArray.getJSONObject(i);
                        title = recipeJSON.getString("title");
                        foodUrl = recipeJSON.getString("source_url");
                        imageUrl = recipeJSON.getString("image_url");
                        //long id = recipeJSON.getLong("recipe_id");
                        recipe = new Recipe(title, imageUrl, foodUrl);
                        foodList.add(recipe);


                        if (progress < 100) {
                            progress = (i * 10) + 5;
                            publishProgress(progress);
                            try {
                                Thread.sleep(300);
                            } catch (Exception e) {
                                Log.i("ASYNC FROZE", "Frozen");
                            }
                        }
                    }
                } catch (org.json.JSONException  e) {
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
            myAdapter = new MyOwnAdapter();
            theList.setAdapter(myAdapter);
            myAdapter.notifyDataSetChanged();
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


