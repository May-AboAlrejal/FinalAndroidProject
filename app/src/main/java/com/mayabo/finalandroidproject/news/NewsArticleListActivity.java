package com.mayabo.finalandroidproject.news;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.mayabo.finalandroidproject.R;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;

public class NewsArticleListActivity extends AppCompatActivity {

    private BaseAdapter myAdapter;
    private Context thisApp;
    private ListView newsListView;
    private ArrayList<NewsApiResponse> newsArticles;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_article_list_activity);

        thisApp = this;

        Intent dataFromPreviousPage = getIntent();
        newsArticles = (ArrayList<NewsApiResponse>) dataFromPreviousPage.getSerializableExtra("NewsArticlesList");

        newsListView = findViewById(R.id.newsListView);
        newsListView.setAdapter(myAdapter = new MyListAdapter());
        //myAdapter.notifyDataSetChanged();
    }

    private class MyListAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return newsArticles.size();
        }

        @Override
        public NewsApiResponse getItem(int i) {
            return newsArticles.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View thisRow = view;
            thisRow = getLayoutInflater().inflate(R.layout.news_article_activity, null);

            NewsApiResponse newsApiResponse = getItem(i);

            TextView titleText = thisRow.findViewById(R.id.titleText);
            TextView descriptionText = thisRow.findViewById(R.id.descriptionText);
            ImageView articleImage = thisRow.findViewById(R.id.articleImage);

            Button viewArticleButton = thisRow.findViewById(R.id.viewArticleButton);

            viewArticleButton.setOnClickListener(clik -> {
                Intent goToNewsArticleContentActivity = new Intent(NewsArticleListActivity.this, NewsArticleContentActivity.class);
                goToNewsArticleContentActivity.putExtra("article", newsApiResponse);
                startActivityForResult(goToNewsArticleContentActivity, 17);
            });

            titleText.setText(newsApiResponse.getTitle());
            descriptionText.setText(newsApiResponse.getDescription());
            articleImage.setImageBitmap(newsApiResponse.getImage());

            return thisRow;
        }
    }

    private class ImageAsyncTask extends AsyncTask<String , Integer, String> {

        @Override
        protected String doInBackground(String... strings) {
            String ret = null;
            Bitmap image = null;
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

        }
    }

}
