package com.mayabo.finalandroidproject.news;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.mayabo.finalandroidproject.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class FragmentNewsArticleContent extends Fragment {

    private boolean isTablet;
    private Bundle dataFromActivity;
    private int position;
    private ImageView imageView;
    private String imageURL;
    private Bitmap image;
    private Context thisApp;
    private String imageName;
    private String imageFile;
    private NewsBaseHelper dbOpener;
    private SQLiteDatabase db;
    private Button articleButton;

    public void setTablet(boolean tablet) {
        isTablet = tablet;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        dataFromActivity = getArguments();
        position = dataFromActivity.getInt(NewsArticleListActivity.ARTICLE_POSITION);
        NewsApiResponse article = (NewsApiResponse) dataFromActivity.getSerializable("article");
        boolean searchedArticleButton = dataFromActivity.getBoolean(NewsHeadlinesSearchActivity.SEARCHED_BUTTON);
        boolean savedArticleButton = dataFromActivity.getBoolean(NewsHeadlinesSearchActivity.SAVED_BUTTON);

        View result = inflater.inflate(R.layout.news_article_content_activity, container, false);

        TextView title = result.findViewById(R.id.titleText);
        title.setText(article.getTitle());
        TextView author = result.findViewById(R.id.authorText);
        author.setText(article.getAuthor());
        TextView source = result.findViewById(R.id.sourceText);
        source.setText(article.getSource());
        TextView publishedAt = result.findViewById(R.id.publishedAtText);
        publishedAt.setText(article.getPublishedAt());
        TextView url = result.findViewById(R.id.urlText);
        url.setText(article.getUrl());
        TextView content = result.findViewById(R.id.contentText);
        content.setText(article.getContent());

        thisApp = getActivity();

        imageView = result.findViewById(R.id.imageView);
        imageURL = article.getUrlToImage();

        imageName = URLUtil.guessFileName(article.getUrlToImage(), null, null).split("\\.")[0];
        imageFile = imageName + ".png";
        //check if the image is in a file before downloading it
        if (fileExistence(imageFile)) {
            FileInputStream fis = null;
            try {
                fis = thisApp.openFileInput(imageFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            image = BitmapFactory.decodeStream(fis);
            imageView.setImageBitmap(image);
        } else {
            Toast.makeText(thisApp, getString(R.string.downloadImage), Toast.LENGTH_LONG).show();
            ImageQuery imageQuery = new ImageQuery();
            imageQuery.execute(imageURL);
        }

        // check if the article is saved in the database
        dbOpener = new NewsBaseHelper(getActivity());
        db = dbOpener.getWritableDatabase();

        String checkURL = article.getUrl();

        Cursor c = db.query(false, NewsBaseHelper.TABLE_NAME, new String[]{NewsApiResponse.URL}, NewsApiResponse.URL + " like ? ", new String[]{checkURL}, null, null, null, null);
        int rowCount = c.getCount();

        articleButton = result.findViewById(R.id.articleButton);
        if (searchedArticleButton) {
            if(rowCount == 0){
                articleButton.setText(getString(R.string.saveArticle));
            } else {
                articleButton.setText(getString(R.string.saved));
            }
        } else if (savedArticleButton) {
            articleButton.setText(getString(R.string.deleteArticle));
        }
        // check what button was clicked in the previous activity to implement the right functionality
        // if search was clicked then it is a save button
        // if view saved is clicked then it is a delete button
        articleButton.setOnClickListener(clik -> {
            if (searchedArticleButton) {
                saveArticle(article);

            } else if (savedArticleButton) {
                deleteArticle(article, position);
            }
        });

        return result;
    }

    /**
     * this method save the article into the database
     *
     * @param article the article selected
     */
    private void saveArticle(NewsApiResponse article) {
        String checkURL = article.getUrl();
        Cursor c = db.query(false, NewsBaseHelper.TABLE_NAME, new String[]{NewsApiResponse.URL}, NewsApiResponse.URL + " like ? ", new String[]{checkURL}, null, null, null, null);
        int rowCount = c.getCount();
        if (rowCount == 0) {
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
            if (article.getUrlToImage().length() >= 4) {
                //check if the image is in a file before downloading it
                if (!fileExistence(imageFile)) {
                    saveImage(imageName, image);
                    newRowValues.put(NewsApiResponse.IMAGE_NAME, imageName);
                }
            }
            db.insert(NewsBaseHelper.TABLE_NAME, NewsApiResponse.IMAGE_NAME, newRowValues);
            Toast.makeText(thisApp, getString(R.string.articleSaved), Toast.LENGTH_LONG).show();
            articleButton.setText(getString(R.string.saved));
        } else {
            Toast.makeText(thisApp, getString(R.string.canntSave), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * this method delete the article from the database
     */
    private void deleteArticle(NewsApiResponse article, int position) {
        if (isTablet) {
            NewsArticleListActivity parent = (NewsArticleListActivity) getActivity();
            parent.deleteArticle((int) article.getId(), position);
            parent.getSupportFragmentManager().beginTransaction().remove(this).commit();
        } else {
            NewsArticleContentActivity parent = (NewsArticleContentActivity) getActivity();
            Intent backToFragmentExample = new Intent();
            backToFragmentExample.putExtra(NewsArticleListActivity.ARTICLE_POSITION, position);
            backToFragmentExample.putExtra(NewsArticleListActivity.ARTICLE_ID, (int) article.getId());
            parent.setResult(Activity.RESULT_OK, backToFragmentExample);
            parent.finish();
        }
    }


    /**
     * This method save the image of type png into a file
     *
     * @param imageName this name is extracted from image url
     * @param image     this is the downloaded image from the url
     */
    private void saveImage(String imageName, Bitmap image) {
        try {
            FileOutputStream outputStream = thisApp.openFileOutput(imageName + ".png", Context.MODE_PRIVATE);
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
     *
     * @param imageFile this is concatenated imageName + imageType(png)
     * @return boolean
     */
    private boolean fileExistence(String imageFile) {
        File file = thisApp.getFileStreamPath(imageFile);
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
            Toast.makeText(thisApp, getString(R.string.imageDownloaded), Toast.LENGTH_LONG).show();
        }
    }

}
