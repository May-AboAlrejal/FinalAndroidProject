package com.mayabo.finalandroidproject.news;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.mayabo.finalandroidproject.R;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;

public class NewsArticleListActivity extends AppCompatActivity {

    private BaseAdapter myAdapter;
    private ListView newsListView;
    private ArrayList<NewsApiResponse> newsArticles;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_article_list_activity);

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
            TextView authorText = thisRow.findViewById(R.id.authorText);
            TextView sourceText = thisRow.findViewById(R.id.sourceText);

            Button viewArticleButton = thisRow.findViewById(R.id.viewArticleButton);

            viewArticleButton.setOnClickListener(clik -> {
                Intent goToNewsArticleContentActivity = new Intent(NewsArticleListActivity.this, NewsArticleContentActivity.class);
                goToNewsArticleContentActivity.putExtra("article", newsApiResponse);
                startActivityForResult(goToNewsArticleContentActivity, 17);
            });

            titleText.setText(newsApiResponse.getTitle());
            authorText.setText(newsApiResponse.getAuthor());
            sourceText.setText(newsApiResponse.getSource());

            return thisRow;
        }
    }
}
