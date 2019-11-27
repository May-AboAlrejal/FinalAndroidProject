package com.mayabo.finalandroidproject.news;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.mayabo.finalandroidproject.R;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import androidx.appcompat.app.AppCompatActivity;

/**
 * The class show the content of one article
 */
public class NewsArticleContentActivity extends AppCompatActivity {

    private ImageView imageView;
    private String imageURL;
    private Bitmap image;
    private Context thisApp;

    private NewsBaseHelper dbOpener = new NewsBaseHelper(this);
    private SQLiteDatabase db;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_article_content_activity);

        db = dbOpener.getWritableDatabase();

        Intent dataFromPreviousPage = getIntent();

        NewsApiResponse article = (NewsApiResponse) dataFromPreviousPage.getSerializableExtra("article");

        TextView title = findViewById(R.id.titleText);
        title.setText(article.getTitle());
        TextView author = findViewById(R.id.authorText);
        author.setText(article.getAuthor());
        TextView source = findViewById(R.id.sourceText);
        source.setText(article.getSource());
        TextView publishedAt = findViewById(R.id.publishedAtText);
        publishedAt.setText(article.getPublishedAt());
        TextView url = findViewById(R.id.urlText);
        url.setText(article.getUrl());
        TextView content = findViewById(R.id.contentText);
        content.setText(article.getContent());

        imageView = findViewById(R.id.imageView);
        imageURL = article.getUrlToImage();

        thisApp = this;
        Toast.makeText(thisApp, "Downloading Image!", Toast.LENGTH_LONG).show();

        ImageQuery imageQuery = new ImageQuery();
        imageQuery.execute(imageURL);


        // if the user click the save button this article will be saved in NEWS table
        Button saveArticleButton = findViewById(R.id.saveArticleButton);
        saveArticleButton.setOnClickListener(clik -> {
            ContentValues newRowValues = new ContentValues();

            newRowValues.put(NewsApiResponse.AUTHOR, article.getAuthor());
            newRowValues.put(NewsApiResponse.TITLE, article.getTitle());
            newRowValues.put(NewsApiResponse.DESCRIPTION, article.getDescription());
            newRowValues.put(NewsApiResponse.URL, article.getUrl());
            newRowValues.put(NewsApiResponse.URL_TO_IMAGE, article.getUrlToImage());

            newRowValues.put(NewsApiResponse.PUBLISHED_AT, article.getPublishedAt());
            newRowValues.put(NewsApiResponse.CONTENT, article.getContent());
            newRowValues.put(NewsApiResponse.SOURCE, article.getSource());

            // extract image name from urlToImage remove file type
            if(article.getUrlToImage().length() >=4){

                String imageName = URLUtil.guessFileName(article.getUrlToImage(), null, null).split("\\.")[0];
                //check if the image is in a file before downloading it
                String imageFile = imageName + ".png";
                if(!fileExistence(imageFile)) {
                    saveImage(imageName, image);
                    newRowValues.put(NewsApiResponse.IMAGE_NAME, imageName);
                }
            }

            db.insert(NewsBaseHelper.TABLE_NAME, NewsApiResponse.IMAGE_NAME, newRowValues);

        });
    }

    /**
     * This method save the image of type png into a file
     * @param imageName this name is extracted from image url
     * @param image this is the downloaded image from the url
     */
    private void saveImage(String imageName, Bitmap image) {
        try {
            FileOutputStream outputStream = openFileOutput(imageName + ".png", Context.MODE_PRIVATE);
            image.compress(Bitmap.CompressFormat.PNG, 80, outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * The method checks if the image does excites in the phone
     * @param imageFile this is concatenated imageName + imageType(png)
     * @return boolean
     */
    private boolean fileExistence(String imageFile) {
        File file = getBaseContext().getFileStreamPath(imageFile);
        return file.exists();
    }

    /**
     * This class is used to get an image from an url
     */
    private class ImageQuery extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... strings) {
            String ret = null;

            try {
                URL url = new URL(strings[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                int responseCode = connection.getResponseCode();
                if (responseCode == 200) {
                    image = BitmapFactory.decodeStream(connection.getInputStream());
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return ret;
        }

        @Override                       //Type 2
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override                   //Type 3
        protected void onPostExecute(String sentFromDoInBackground) {
            super.onPostExecute(sentFromDoInBackground);
            imageView.setImageBitmap(image);
            Toast.makeText(thisApp, "Image downloaded!", Toast.LENGTH_LONG).show();
        }
    }
}
