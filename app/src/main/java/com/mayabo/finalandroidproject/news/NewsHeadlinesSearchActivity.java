package com.mayabo.finalandroidproject.news;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.mayabo.finalandroidproject.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.AlertDialog.Builder;
import androidx.annotation.RequiresApi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;

public class NewsHeadlinesSearchActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private Context thisApp;
    private EditText searchBody;
    private EditText searchTitle;

    private NewsApiRequest newsApiRequest;
    private Spinner spinnerSortBy;
    private Spinner spinnerLanguage;
    private URL newsURL;
    private ArrayList<NewsApiResponse> newsArticles = new ArrayList();

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_activity);

        thisApp = this;

        /**
         * Get the spinner from XML
         * Initialize a new instance of {@link NewsApiRequest}
         * Create a new {@link List} of languages values and sortBy values
         * from {@link NewsApiRequest#sortByList} and {@link NewsApiRequest#languageList}
         * Create two new {@link ArrayAdapter} for each spinner then
         * #spinnerLanguage#setAdapter() #spinnerSortBy#setAdapter()
         */
        spinnerSortBy = findViewById(R.id.spinnerSortBy);
        spinnerLanguage = findViewById(R.id.spinnerLanguage);
        newsApiRequest = new NewsApiRequest();
        List<String> sortByValues = new ArrayList<>(newsApiRequest.sortByList.values());
        List<String> languages = new ArrayList<>(newsApiRequest.languageList.values());

        ArrayAdapter<String> adapterSortBy = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, sortByValues);
        ArrayAdapter<String> adapterLanguage = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, languages);

        spinnerSortBy.setAdapter(adapterSortBy);
        spinnerLanguage.setAdapter(adapterLanguage);

        /**
         * Get some element from XML
         */
        searchBody = findViewById(R.id.searchBody);
        searchTitle = findViewById(R.id.searchTitle);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        /**
         * Get user inputs to add them as parameters to url
         * execute the URL request
         */
        Button searchButton = findViewById(R.id.searchButton);
        searchButton.setOnClickListener(clik -> {

            Map<String, String> urlPrameters = new HashMap<>(4);

            String searchBodyText = searchBody.getText().toString().trim();
            if (searchBodyText.length() >= 1) {
                urlPrameters.put(NewsApiRequest.KEYWORD, searchBodyText);
            }

            String searchTitleText = searchTitle.getText().toString().trim();
            if (searchTitleText.length() >= 1) {
                urlPrameters.put(NewsApiRequest.KEYWORD_IN_TITLE, searchTitleText);
            }

            String sortByText = spinnerSortBy.getSelectedItem().toString();
            if (sortByText.trim().length() >= 1) {
                urlPrameters.put(NewsApiRequest.SORT_BY, newsApiRequest.getKeyFromValue(sortByText, "sortBy"));
            }

            String languageText = spinnerLanguage.getSelectedItem().toString();
            if (languageText.trim().length() >= 1) {
                urlPrameters.put(NewsApiRequest.LANGUAGE, newsApiRequest.getKeyFromValue(languageText, "language"));
            }

            if (checkUserInput(searchTitleText, searchBodyText)) {
                newsURL = newsApiRequest.urlBuilder(urlPrameters);

                Snackbar.make(searchTitle,"Searching for related articles!", Snackbar.LENGTH_LONG).show();

                NewsQuery newsQuery = new NewsQuery();
                newsQuery.execute();
            } else {
                Builder b = new Builder(thisApp);
                b.setTitle("Error:")
                        .setMessage("Please fill at least one of the search text!")
                        .setPositiveButton("OK", (clk, btn) -> { /* do this when clicked */ })
                        .create()
                        .show();
            }

        });
    }

    /**
     * Validate user input, at least one of the search field must have a value
     * @param title a String
     * @param body a String
     * @return boolean false if the two EditText are empty, true if one of them has value
     */
    private boolean checkUserInput(String title, String body) {
        if(title.length() == 0 && body.length() == 0){
            return false;
        }
        return true;
    }

    private class NewsQuery extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... strings) {
            String ret = null;
            try {
                HttpURLConnection urlConnection = (HttpURLConnection) newsURL.openConnection();
                InputStream inStream = urlConnection.getInputStream();

                BufferedReader reader = new BufferedReader(new InputStreamReader(inStream, "UTF-8"), 8);
                StringBuilder sb = new StringBuilder();

                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                String result = sb.toString();

                newsArticles = getAllArticles(result);

            } catch (MalformedURLException mfe) {
                ret = "Malformed URL exception";
            } catch (IOException ioe) {
                ret = "IO Exception. Is the Wifi connected?";
            }

            return ret;
        }

        /**
         * Process the response to find relative data
         *
         * @param result a String coming as response to the url request
         * @return ArrayList of articles
         */
        private ArrayList<NewsApiResponse> getAllArticles(String result) {
            ArrayList<NewsApiResponse> newsArticles = new ArrayList<>();
            try {
                JSONObject jObject = new JSONObject(result);
                String status = jObject.getString(NewsApiResponse.STATUS);
                if (status.equals("ok")) {
                    JSONArray articles = jObject.getJSONArray(NewsApiResponse.ARTICLES);
                    int numberOfArticles = articles.length();
                    if (numberOfArticles >= 1) {
                        for (int i = 0, j = 100 / numberOfArticles; i < numberOfArticles; i++, j++) {

                            String author = articles.getJSONObject(i).getString(NewsApiResponse.AUTHOR);
                            String title = articles.getJSONObject(i).getString(NewsApiResponse.TITLE);
                            String description = articles.getJSONObject(i).getString(NewsApiResponse.DESCRIPTION);
                            String url = articles.getJSONObject(i).getString(NewsApiResponse.URL);
                            String urlToImage = articles.getJSONObject(i).getString(NewsApiResponse.URL_TO_IMAGE);
                            String publishedAt = articles.getJSONObject(i).getString(NewsApiResponse.PUBLISHED_AT);
                            String content = articles.getJSONObject(i).getString(NewsApiResponse.CONTENT);
                            String source = articles.getJSONObject(i).getJSONObject(NewsApiResponse.SOURCE).getString(NewsApiResponse.NAME);
                            newsArticles.add(new NewsApiResponse(author, title, description, url, urlToImage, publishedAt, content, source));

                            publishProgress(j);

                        }
                    }
                } else if (status.equals("error")) {
                    String message = jObject.getString("message");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return newsArticles;

        }

        @Override                       //Type 2
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(values[0]);
        }

        /**
         * Check the number of articles and display a Toast message for no results or a Sncakbar for results
         * If articles found start a new activity to display the results
         * @param sentFromDoInBackground a string object coming as a return value from {@link #doInBackground(String...)}
         */
        @Override                   //Type 3
        protected void onPostExecute(String sentFromDoInBackground) {
            super.onPostExecute(sentFromDoInBackground);

            progressBar.setVisibility(View.INVISIBLE);

            if(newsArticles.size() >= 1){

                Snackbar.make(searchTitle,newsArticles.size() + " articles found!", Snackbar.LENGTH_LONG).show();

                Intent goToNewsArticleListActivity = new Intent(NewsHeadlinesSearchActivity.this, NewsArticleListActivity.class);
                goToNewsArticleListActivity.putExtra("NewsArticlesList", newsArticles);
                startActivity(goToNewsArticleListActivity);
            } else {
                Toast.makeText(thisApp, "Sorry! No articles found.", Toast.LENGTH_LONG).show();
            }
        }
    }
}
