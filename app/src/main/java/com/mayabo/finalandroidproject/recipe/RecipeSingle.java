package com.mayabo.finalandroidproject.recipe;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
;
import java.util.List;


import com.mayabo.finalandroidproject.R;

import java.io.File;
import java.io.FileInputStream;

import java.io.FileOutputStream;
import java.io.IOException;

import java.net.HttpURLConnection;
import java.net.URL;

import android.graphics.drawable.Drawable;


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

public class RecipeSingle extends AppCompatActivity {

    public static final String ACTIVITY_NAME = "RECIPE_SINGLE_ACTIVITY";

    TextView title;
    TextView foodUrl;
    TextView tapIn;
    String titleStr;
    String urlFood;
    String imageURL;
    Bitmap image = null;
    ImageView itemImage;
    ImageView heart;
    ProgressBar itemProgress;
    String imageID;
    Recipe rep;

    Button savebtn;
    DatabaseHandler db;

    int heartSave = R.drawable.save;
    int heartDelete = R.drawable.delete;

    String activityName;


    List<Recipe> recipes;

    Toolbar tbar;

    Menu menu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recipe_single_layout);
        title = findViewById(R.id.title_single);
        foodUrl = findViewById(R.id.url_single);
        itemProgress = findViewById(R.id.item_progress);
        itemImage = findViewById(R.id.image_single);
//        savebtn = (Button) findViewById(R.id.save_btn);
        heart = (ImageView) findViewById(R.id.heartAction);
        tapIn = (TextView) findViewById(R.id.tapIn);

        tbar = findViewById(R.id.toolbar);
        tbar.setTitle("Recipe Details");
        tbar.setTitleTextColor(getResources().getColor(R.color.titleColor));
        setSupportActionBar(tbar);

        Intent dataFromPreviousPage = getIntent();

        activityName = dataFromPreviousPage.getStringExtra("ActivityName");

        titleStr = dataFromPreviousPage.getStringExtra("title");
        urlFood = dataFromPreviousPage.getStringExtra("url");
        imageURL = dataFromPreviousPage.getStringExtra("imageUrl");
        imageID = dataFromPreviousPage.getStringExtra("imageID");

        //Get a new object ready for any action
        rep = new Recipe(titleStr, imageID, imageURL, urlFood);

        //Setting text
        title.setText(titleStr);
        foodUrl.setText(foodUrl.getText() + urlFood);

        SingleQuery singlequery = new SingleQuery(this);
        singlequery.execute(imageURL, imageID);
        itemProgress.setVisibility(View.VISIBLE);

        db = new DatabaseHandler(this);
        recipes = db.getAllRecipes();
        switch (activityName) {
            case "ListFavouriteActivity":
                tapIn.setText("Tap Heart To Delete");
                heart.setImageResource(heartSave);
                break;
            case "SearchingActivity":

                for (int i = 0; i < recipes.size(); i++) {
                    Log.e("DataID", recipes.get(i).getImageID());
                    Log.e("DownloadID", rep.getImageID());
                    if (recipes.get(i).getImageID().equals(rep.getImageID())) {
                        heart.setImageResource(heartSave);

                    }
                }

                break;
        }


        heart.setOnClickListener(clk -> {
            boolean duplicate = false;
            if (activityName.equals("ListFavouriteActivity")) {
                db.deleteRecipe(rep);
                heart.setImageResource(heartDelete);
                Intent intent = new Intent(this, ListFavouriteActivity.class);
                finish();
                startActivity(intent);
                Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show();
            } else {

                //checking duplicate value
                for (Recipe r : recipes) {
                    if (r.getTitle().equals(rep.getTitle())) duplicate = true;
                }
                if (!duplicate) {
                    db.addRecipe(rep);
                    heart.setImageResource(heartSave);
                    finish();
                    Toast.makeText(this, "Saved In Favourite", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "It's Already Saved", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.exit:
                switch (activityName) {
                    case "ListFavouriteActivity":
                        Intent goToRecipeSearchActivity = new Intent(this, ListFavouriteActivity.class);
                        startActivity(goToRecipeSearchActivity);
                        break;
                    case "SearchingActivity":
                        goToRecipeSearchActivity = new Intent(this, RecipeSearchActivity.class);

                        startActivity(goToRecipeSearchActivity);
                        break;
                }
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        switch (activityName) {
            case "ListFavouriteActivity":
                Intent goFavourite = new Intent(this, ListFavouriteActivity.class);
                finish();
                startActivity(goFavourite);
                break;
        }

    }

    public boolean fileExistance(String fileName) {
        Log.i(ACTIVITY_NAME, getBaseContext().getFileStreamPath(fileName).toString());
        File file = getBaseContext().getFileStreamPath(fileName);
        return file.exists();
    }


    private class SingleQuery extends AsyncTask<String, Integer, String> {



        String result;
        Context context;


        public SingleQuery(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... strings) {
            result = null;
            String queryURL = strings[0].substring(0, 4) + "s" + strings[0].substring(4, strings[0].length());
            String imageName = strings[1] + ".jpeg";

            if (fileExistance(imageName)) {
                FileInputStream inputStream = null;
                try {
                    inputStream = new FileInputStream(getBaseContext().getFileStreamPath(imageName));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                image = BitmapFactory.decodeStream(inputStream);
                Log.i(ACTIVITY_NAME, "Image already exists");


                for (int i = 60; i < 90; i++) {
                    publishProgress(i);
                    try {
                        Thread.sleep(20);
                    } catch (Exception e) {
                    }
                }

            } else {
                try {

                    URL url = new URL(queryURL);
                    image = getImage(url);


                    if (image != null ) {
                        FileOutputStream outputStream = openFileOutput(imageName, Context.MODE_PRIVATE);
                        image.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
                        outputStream.flush();
                        outputStream.close();
                        Log.i(ACTIVITY_NAME, "Downloading new image");
                    }

                    for (int i = 20; i < 90; i++) {
                        publishProgress(i);
                        try {
                            Thread.sleep(20);
                        } catch (Exception e) {
                        }
                    }

                } catch (IOException ioe) {
                    result = "IO Exception. Is the Wifi Connected?";
                }
            }

            publishProgress(95);
            try {
                Thread.sleep(200);
            } catch (Exception e) {
            }

            return result;
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (image != null) {
                itemImage.setImageBitmap(image);
            } else {
                Toast.makeText(context, "Image is not available for this Recipe", Toast.LENGTH_SHORT).show();
                itemImage.setImageResource(R.drawable.food);
            }


            itemProgress.setVisibility(View.INVISIBLE);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            itemProgress.setProgress(values[0]);
            itemProgress.setVisibility(View.VISIBLE);
        }


        //Image
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


        public boolean fileExistance(String fileName) {
            File file = getBaseContext().getFileStreamPath(fileName);
            return file.exists();
        }
    }


}


