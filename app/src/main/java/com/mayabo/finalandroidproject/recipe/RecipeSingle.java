package com.mayabo.finalandroidproject.recipe;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

import com.mayabo.finalandroidproject.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
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

public class RecipeSingle extends AppCompatActivity {

    public static final String ACTIVITY_NAME = "RECIPE_SINGLE_ACTIVITY";

    TextView title;
    TextView foodUrl;
    String titleStr;
    String urlFood;
    String imageURL;
    Bitmap image = null;
    ImageView itemImage;
    ProgressBar itemProgress;
    String imageID;
    Recipe rep;

    Button savebtn;
    DatabaseHandler db;


    List<Recipe> recipes;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recipe_single_layout);
        title = findViewById(R.id.title_single);
        foodUrl = findViewById(R.id.url_single);
        itemProgress = findViewById(R.id.item_progress);
        itemImage = findViewById(R.id.image_single);
        savebtn = (Button) findViewById(R.id.save_btn);

        Intent dataFromPreviousPage = getIntent();

        String activityName = dataFromPreviousPage.getStringExtra("ActivityName");

        titleStr = dataFromPreviousPage.getStringExtra("title");
        urlFood = dataFromPreviousPage.getStringExtra("url");

        imageURL = dataFromPreviousPage.getStringExtra("imageUrl");
        imageID = dataFromPreviousPage.getStringExtra("imageID");

        //Setting text
        title.setText(title.getText() + titleStr);
        foodUrl.setText(foodUrl.getText() + urlFood);

        SingleQuery singlequery = new SingleQuery();
        singlequery.execute(imageURL, imageID);
        itemProgress.setVisibility(View.VISIBLE);


        switch (activityName) {
            case "ListFavouriteActivity":
                savebtn.setText("Remove");
                break;
            case "SearchingActivity":
                break;

        }

        //Get a new object ready for any action
        rep = new Recipe(titleStr, imageID, imageURL, urlFood);
        savebtn.setOnClickListener(clk -> {
            boolean duplicate = false;

            db = new DatabaseHandler(this);
            if (activityName.equals("ListFavouriteActivity")) {
                db.deleteRecipe(rep);
                Intent intent = new Intent(this, ListFavouriteActivity.class);
                startActivityForResult(intent, 30);
                Toast.makeText(this, "Deleted", Toast.LENGTH_LONG).show();
            } else {
                recipes = db.getAllRecipes();
                //checking duplicate value
                for (Recipe r : recipes) {
                    if (r.getTitle().equals(rep.getTitle())) duplicate = true;
                }
                if (!duplicate) {
                    db.addRecipe(rep);
                    Toast.makeText(this, "Added", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "Can't add duplicate", Toast.LENGTH_LONG).show();
                }
            }
        });


    }


    public boolean fileExistance(String fileName) {
        Log.i(ACTIVITY_NAME, getBaseContext().getFileStreamPath(fileName).toString());
        File file = getBaseContext().getFileStreamPath(fileName);
        return file.exists();
    }


    private class SingleQuery extends AsyncTask<String, Integer, String> {

        String result;

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

            } else {
                try {

                    URL url = new URL(queryURL);
                    image = getImage(url);
                    FileOutputStream outputStream = openFileOutput(imageName, Context.MODE_PRIVATE);
                    image.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
                    outputStream.flush();
                    outputStream.close();
                    Log.i(ACTIVITY_NAME, "Downloading new image");

                } catch (IOException ioe) {
                    result = "IO Exception. Is the Wifi Connected?";
                }
            }

            return result;
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            itemImage.setImageBitmap(image);
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


