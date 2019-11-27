package com.mayabo.finalandroidproject.recipe;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import android.graphics.drawable.BitmapDrawable;

import java.io.BufferedInputStream;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.snackbar.Snackbar;
import com.mayabo.finalandroidproject.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import android.graphics.PorterDuff;


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
    Menu menu;
    Toolbar tbar;
    Bitmap image;
    Bitmap imageToSet;
    public static ArrayList<Bitmap> imageList = new ArrayList<>();

    public static ArrayList<String> imgUrls = new ArrayList<>();


    //Fragment attributes:
    public static final String ITEM_SELECTED = "ITEM";
    public static final String ITEM_POSITION = "POSITION";
    public static final String ITEM_ID = "ID";
    public static final String WHO_SEND = "ISSEND";
    public static final String ID_IN_DB = "DBID";
    public static final int EMPTY_ACTIVITY = 123;



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

        tbar = (Toolbar) findViewById(R.id.toolbar);
        tbar.setTitle("Recipe Of "+userFilter);
        tbar.setTitleTextColor(getResources().getColor(R.color.titleColor));
        tbar.getOverflowIcon().setColorFilter(getResources().getColor(R.color.titleColor), PorterDuff.Mode.SRC_ATOP);
        setSupportActionBar(tbar);


        //boolean isTablet = findViewById(R.id.fragmentLocation) != null; //check if the FrameLayout is loaded



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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId())

        {
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

                        String imgUrl = imageUrl.substring(0, 4) + "s" + imageUrl.substring(4);
                        String imageName = imageID + ".jpeg";

//                        imageList.add(downloadImage(imgUrl, imageName));

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


        protected Bitmap downloadImage(String imageUrl, String imageName) {
            if (fileExistance(imageName)) {
                FileInputStream inputStream = null;
                try {
                    inputStream = new FileInputStream(getBaseContext().getFileStreamPath(imageName));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                image = BitmapFactory.decodeStream(inputStream);
                Log.i(ACTIVITY_NAME, "Image already exists");
            } else {
                try {

                    URL url = new URL(imageUrl);
                    image = getImage(url);
                    FileOutputStream outputStream = openFileOutput(imageName, Context.MODE_PRIVATE);
                    image.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
                    outputStream.flush();
                    outputStream.close();
                    Log.i(ACTIVITY_NAME, "Downloading new image");

                    } catch (Exception e) { e.printStackTrace();}
                }
            return image;
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

        protected boolean fileExistance(String fileName) {
            File file = getBaseContext().getFileStreamPath(fileName);
            return file.exists();
        }

        protected Bitmap getImage(URL url) {

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

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            myProgressBar.setProgress(values[0]);
            myProgressBar.setVisibility(View.VISIBLE);
        }

    }




}


