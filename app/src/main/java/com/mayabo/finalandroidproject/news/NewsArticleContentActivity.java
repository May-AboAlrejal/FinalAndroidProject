package com.mayabo.finalandroidproject.news;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mayabo.finalandroidproject.R;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import androidx.appcompat.app.AppCompatActivity;

public class NewsArticleContentActivity extends AppCompatActivity {

    private ImageView imageView;
    private String imageURL;
    private Bitmap image;
    private Context thisApp;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_article_content_activity);

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

        NewsQuery newsQuery = new NewsQuery();
        newsQuery.execute(imageURL);

    }

    private class NewsQuery extends AsyncTask<String, Integer, String> {

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
