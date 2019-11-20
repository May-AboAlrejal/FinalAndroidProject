package com.mayabo.finalandroidproject.recipe;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.mayabo.finalandroidproject.R;

import java.io.File;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recipe_single_layout);
        title = findViewById(R.id.title_single);
        foodUrl = findViewById(R.id.url_single);
        itemProgress = findViewById(R.id.item_progress);

        Intent dataFromPreviousPage = getIntent();
        titleStr = dataFromPreviousPage.getStringExtra("title");
        urlFood = dataFromPreviousPage.getStringExtra("url");

        Toast.makeText(this, "Welcome to recipe", Toast.LENGTH_LONG).show();
        imageURL = dataFromPreviousPage.getStringExtra("imageUrl");
        itemImage = findViewById(R.id.image_single);

        title.setText(title.getText() + titleStr);
        foodUrl.setText(foodUrl.getText() + urlFood);


        SingleQuery singlequery = new SingleQuery();
        singlequery.execute();
        itemProgress.setVisibility(View.VISIBLE);


    }


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
            String queryURL = "http://openweathermap.org/img/w/10d.png";

            try {
                URL url = new URL(queryURL);
                image = getImage(url);
                FileOutputStream outputStream = openFileOutput(titleStr, Context.MODE_PRIVATE);
                image.compress(Bitmap.CompressFormat.PNG, 80, outputStream);
                outputStream.flush();
                outputStream.close();

                for (int i = 1; i< 100; i *= 10) {
                 publishProgress(i);
                 Thread.sleep(200);
                }

            } catch (Exception e) {
                e.printStackTrace();
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
    }


}


