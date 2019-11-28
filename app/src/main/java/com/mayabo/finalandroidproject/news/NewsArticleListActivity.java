package com.mayabo.finalandroidproject.news;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
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
    private NewsBaseHelper dbOpener = new NewsBaseHelper(this);
    private SQLiteDatabase db;
    private boolean searchedArticleButton;
    private boolean savedArticleButton;
    private boolean isTablet;
    public static final int EMPTY_ACTIVITY = 17;
    public static final String ARTICLE_POSITION = "POSITION";
    public static final String ARTICLE_ID = "id";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_article_list_activity);

        db = dbOpener.getWritableDatabase();

        thisApp = this;

        Intent dataFromPreviousPage = getIntent();

        // check if it is a tablet of a phone
        isTablet = findViewById(R.id.fragmentLocation) != null;

        searchedArticleButton = dataFromPreviousPage.getBooleanExtra(NewsHeadlinesSearchActivity.SEARCHED_BUTTON, false);
        savedArticleButton = dataFromPreviousPage.getBooleanExtra(NewsHeadlinesSearchActivity.SAVED_BUTTON, false);

        // check which button was pressed to return the correct list
        if(searchedArticleButton){
            newsArticles = (ArrayList<NewsApiResponse>) dataFromPreviousPage.getSerializableExtra(NewsHeadlinesSearchActivity.SEARCHED_ARTICLES);
        } else if (savedArticleButton) {
            newsArticles = getSavedArticles();
        }

        newsListView = findViewById(R.id.newsListView);
        newsListView.setAdapter(myAdapter = new MyListAdapter());

    }


    private ArrayList<NewsApiResponse> getSavedArticles() {

        ArrayList<NewsApiResponse> newsArticles = new ArrayList();
        String[] colNames = {NewsBaseHelper.COL_ID, NewsApiResponse.AUTHOR,NewsApiResponse.TITLE, NewsApiResponse.DESCRIPTION, NewsApiResponse.URL,
                NewsApiResponse.URL_TO_IMAGE, NewsApiResponse.IMAGE_NAME, NewsApiResponse.PUBLISHED_AT,
                NewsApiResponse.CONTENT, NewsApiResponse.SOURCE};

        Cursor c = db.query(false, NewsBaseHelper.TABLE_NAME, colNames, null, null, null, null, null, null);


        while (c.moveToNext()) {
            long id = c.getLong(c.getColumnIndex(NewsBaseHelper.COL_ID));
            String author = c.getString(c.getColumnIndex(NewsApiResponse.AUTHOR));
            String title = c.getString(c.getColumnIndex(NewsApiResponse.TITLE));
            String description = c.getString(c.getColumnIndex(NewsApiResponse.DESCRIPTION));
            String url = c.getString(c.getColumnIndex(NewsApiResponse.URL));
            String urlToImage = c.getString(c.getColumnIndex(NewsApiResponse.URL_TO_IMAGE));
            String publishedAt = c.getString(c.getColumnIndex(NewsApiResponse.PUBLISHED_AT));
            String content = c.getString(c.getColumnIndex(NewsApiResponse.CONTENT));
            String source = c.getString(c.getColumnIndex(NewsApiResponse.SOURCE));
            NewsApiResponse article = new NewsApiResponse(author, title, description, url, urlToImage, publishedAt, content, source);
            article.setId(id);
            newsArticles.add(article);
        }
        return newsArticles;
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

            Button viewArticleButton = thisRow.findViewById(R.id.viewArticleButton);

            viewArticleButton.setOnClickListener(clik -> {
                Bundle dataToPass = new Bundle();
                dataToPass.putBoolean(NewsHeadlinesSearchActivity.SEARCHED_BUTTON, searchedArticleButton);
                dataToPass.putBoolean(NewsHeadlinesSearchActivity.SAVED_BUTTON, savedArticleButton);
                dataToPass.putSerializable("article", newsApiResponse);
                dataToPass.putInt(ARTICLE_POSITION, i);

                if (isTablet) {
                    FragmentNewsArticleContent dFragment = new FragmentNewsArticleContent();
                    dFragment.setArguments(dataToPass);
                    dFragment.setTablet(true);
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragmentLocation, dFragment)
                            .commit();
                } else {
                    Intent goToNewsArticleContentActivity = new Intent(NewsArticleListActivity.this, NewsArticleContentActivity.class);
                    goToNewsArticleContentActivity.putExtras(dataToPass);
                    startActivityForResult(goToNewsArticleContentActivity, EMPTY_ACTIVITY);
                }
            });

            titleText.setText(newsApiResponse.getTitle());
            return thisRow;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EMPTY_ACTIVITY) {
            if (resultCode == RESULT_OK) {
                int id = data.getIntExtra(ARTICLE_ID, 0);
                int position = data.getIntExtra(ARTICLE_POSITION, 0);
                deleteArticle(id, position);
            }
        }
    }

    /**
     * this method delete the article from the database
     */
    public void deleteArticle(int id, int position) {
        int delete = db.delete(NewsBaseHelper.TABLE_NAME, NewsBaseHelper.COL_ID + "=?", new String[] {Long.toString(id)});
        newsArticles.remove(position);
        myAdapter.notifyDataSetChanged();
    }
}
