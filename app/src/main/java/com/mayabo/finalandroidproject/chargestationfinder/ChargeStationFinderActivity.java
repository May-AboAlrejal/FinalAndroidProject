package com.mayabo.finalandroidproject.chargestationfinder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.mayabo.finalandroidproject.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ChargeStationFinderActivity extends AppCompatActivity {
    private TextView mLatitudeView;
    private TextView mLongitudeView;
    private ListView mSearchResultsView;
    private List<Record> mSearchResults;
    private MyAdapter mSearchResultAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charge_station_finder);

        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mSearchResults = new ArrayList<>();

        mLatitudeView = findViewById(R.id.latitude);
        mLongitudeView = findViewById(R.id.longitude);
        mSearchResultsView = findViewById(R.id.search_result);
        mSearchResultAdapter = new MyAdapter();

        mLatitudeView.setOnFocusChangeListener((view, b) -> {
            if (b) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            } else {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                mLongitudeView.requestFocus();
            }
        });

        mLongitudeView.setOnKeyListener((view, keyCode, keyEvent) -> {
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                mLongitudeView.clearFocus();
            }
            return false;
        });
        mLongitudeView.setOnFocusChangeListener((view, b) -> {
            if (b) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            } else {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                InputMethodManager imm =  (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(findViewById(R.id.longitude).getRootView().getWindowToken(), 0);
                new MyQuery().execute();
            }
        });

        mSearchResultsView.setAdapter(mSearchResultAdapter);
    }

    @Override
    public void onBackPressed() {
        mLatitudeView.clearFocus();
        mLongitudeView.clearFocus();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return ChargeStationFinderActivity.this.mSearchResults.size();
        }

        @Override
        public Record getItem(int position) {
            return ChargeStationFinderActivity.this.mSearchResults.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Record record = this.getItem(position);
            convertView = getLayoutInflater().inflate(R.layout.charge_station_brief, parent, false);
            TextView title = convertView.findViewById(R.id.title);
//            TextView number = convertView.findViewById(R.id.contact);
            title.setText(record.getTitle());
//            number.setText(record.getContact());
            return convertView;
        }
    }

    private class QueryParam {
        private static final String baseUrl = "https://api.openchargemap.io/v3/poi/?output=json";
        private String latitude;
        private String longitude;

        private QueryParam(String latitude, String longitude) {
            this.setLatitude(latitude);
            this.setLongitude(longitude);
        }

        private String buildQueryStatement() {
            return baseUrl +
                    "&latitude=" + this.getLatitude() +
                    "&longitude=" + this.getLongitude();
        }

        private String getLatitude() {
            return latitude;
        }

        private void setLatitude(String latitude) {
            this.latitude = latitude;
        }

        private String getLongitude() {
            return longitude;
        }

        private void setLongitude(String longitude) {
            this.longitude = longitude;
        }
    }

    private class MyQuery extends AsyncTask<QueryParam, Integer, Void> {
        @Override
        protected Void doInBackground(QueryParam... queryParams) {
            try {
                HttpURLConnection connection = (HttpURLConnection) new URL(queryParams[0].buildQueryStatement()).openConnection();
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8), 8);
                StringBuilder stringBuilder = new StringBuilder();
                for (String line; (line = reader.readLine()) != null; ) { stringBuilder.append(line); }
                JSONArray jArray = new JSONArray(stringBuilder.toString());
                for (int i = 0; i < jArray.length(); i++) {
                    JSONObject jObject = jArray.getJSONObject(i).getJSONObject("AddressInfo");
                    String title = jObject.getString("Title");
                    String contact = jObject.getString("ContactTelephone1");
                    String latitude = String.valueOf(jObject.getDouble("Latitude"));
                    String longitude = String.valueOf(jObject.getDouble("Longitude"));
                    ChargeStationFinderActivity.this.mSearchResults.add(new Record(title, contact, latitude, longitude));
                    this.publishProgress(i);
                }
            }
            catch (MalformedURLException e) {
                e.printStackTrace();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onProgressUpdate(Integer... value) {
            ChargeStationFinderActivity.this.mSearchResultAdapter.notifyDataSetChanged();
        }
    }
}
