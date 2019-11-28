package com.mayabo.finalandroidproject.news;

import android.content.Context;
import android.net.Uri;
import android.os.Build;

import com.mayabo.finalandroidproject.R;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.RequiresApi;


public class NewsApiRequest {

    public static final String NEWS_URL = "https://newsapi.org/v2/everything";
    public static final String KEYWORD = "q";
    public static final String KEYWORD_IN_TITLE = "qInTitle";
    public static final String SORT_BY = "sortBy";
    public static final String LANGUAGE = "language";
    public static final String API_KEY = "apiKey";
    public static final String PAGE_SIZE = "pageSize";
    public static final String SIZE = "5";
    public static final String KEY = "0c64d719c28f4eb9b557ac89ad04b679";
    public Map<String, String> sortByList;
    public Map<String, String> languageList;
    private Context c;
    /**
     * From https://newsapi.org/docs/endpoints get a list of sortBy inputs
     *
     * @return a Map of ways to sort the output
     */
    private Map getSortBy() {
        Map<String, String> sortBy = new HashMap();
        sortBy.put("relevancy", c.getString(R.string.relevancy));
        sortBy.put("popularity", c.getString(R.string.popularity));
        sortBy.put("publishedAt", c.getString(R.string.published));
        return sortBy;
    }

    public NewsApiRequest() { }

    public NewsApiRequest(Context c) {
        this.c = c;
        this.sortByList = getSortBy();
        this.languageList = getLanguage();
    }

    /**
     * From https://newsapi.org/docs/endpoints get a list of languages inputs
     * @return a Map of preferred languages
     */
    private Map getLanguage() {
        Map<String, String> languages = new HashMap();

        languages.put("ar", c.getString(R.string.ar));
        languages.put("de", c.getString(R.string.de));
        languages.put("en", c.getString(R.string.en));
        languages.put("fr", c.getString(R.string.fr));
        languages.put("hi", c.getString(R.string.hi));
        languages.put("it", c.getString(R.string.it));
        languages.put("nl", c.getString(R.string.nl));
        languages.put("no", c.getString(R.string.no));
        languages.put("pt", c.getString(R.string.pt));
        languages.put("ru", c.getString(R.string.ru));
        languages.put("zh", c.getString(R.string.zh));
        return languages;
    }

    /**
     * Get the key for a value in a Map
     * source https://www.baeldung.com/java-map-key-from-value
     * @param value   a string used to search for a key
     * @param mapName a string name used find which Map to search
     * @return key String
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public String getKeyFromValue(String value, String mapName) {
        Map<String, String> map = null;
        final String[] key = {null};
        switch (mapName) {
            case "sortBy":
                map = sortByList;
                break;
            case "language":
                map = languageList;
                break;
        }
        map.forEach((k, v) -> {
            if(v.equals(value)){
                key[0] = k;
            }
        });

        return key[0];

    }

    /**
     * source https://github.com/udacity/Sunshine-Version-2/blob/2.07_build_url_with_params/app/src/main/java/com/example/android/sunshine/app/ForecastFragment.java#L141
     * Construct the url
     *
     * @return URL
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public URL urlBuilder(Map<String, String> urlParameters) {
        URL url = null;
        Uri.Builder builtUri = Uri.parse(NEWS_URL)
                .buildUpon()
                .appendQueryParameter(API_KEY, KEY)
                .appendQueryParameter(PAGE_SIZE, SIZE);


        urlParameters.forEach((k, v) -> {
            try {
                builtUri.appendQueryParameter(k, URLEncoder.encode(v, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        });

        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }
}
