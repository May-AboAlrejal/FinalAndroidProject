package com.mayabo.finalandroidproject.chargestationfinder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
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
    private TextView mEmptyInfoView;
    private SwipeRefreshLayout mSwipeRefreshView;
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
        mEmptyInfoView = findViewById(R.id.info_empty);
        mSwipeRefreshView = findViewById(R.id.swipe_to_refresh);
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
                search();
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
            }
        });

        mSearchResultsView.setAdapter(mSearchResultAdapter);

        mSwipeRefreshView.setOnRefreshListener(() -> search());
    }

    private void search() {
        mEmptyInfoView.setText("Loading...");
        mEmptyInfoView.setVisibility(View.VISIBLE);
        mSearchResultsView.setVisibility(View.GONE);
        mSearchResults.clear();
        mSearchResultAdapter.notifyDataSetChanged();
        new MyQuery().execute(new ChargeStationFinderActivity.QueryParam(
                ChargeStationFinderActivity.this.mLatitudeView.getText().toString(),
                ChargeStationFinderActivity.this.mLongitudeView.getText().toString()
        ));
    }

    @Override
    public void onBackPressed() {
        if (mLatitudeView.hasFocus() || mLongitudeView.hasFocus()) {
            mLatitudeView.clearFocus();
            mLongitudeView.clearFocus();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.charge_station_finder_main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_favorites:
                startActivity(new Intent(this, ActivityFavorites.class));
                return true;
//            case R.id.item_about:
//                new AlertDialog.Builder(this)
//                        .setTitle("About")
//                        .setView(getLayoutInflater().inflate(R.layout.about_dialog, null, false))
//                        .create().show();
//                return true;
            case R.id.item_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            default:
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View view = getCurrentFocus();
            if (view == mLatitudeView || view == mLongitudeView) {
                Rect outRect = new Rect();
                view.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    Log.d("focus", "touchevent");
                    view.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
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
            TextView contact = convertView.findViewById(R.id.contact);
            title.setText(record.getTitle());
            contact.setText(record.getContact());
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

    private class MyQuery extends AsyncTask<QueryParam, Record, Void> {
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
                    this.publishProgress(new Record(title, contact, latitude, longitude));
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
        protected void onProgressUpdate(Record... value) {
            ChargeStationFinderActivity.this.mSearchResults.add(value[0]);
            ChargeStationFinderActivity.this.mSearchResultAdapter.notifyDataSetChanged();
        }

        @Override
        protected void onPostExecute(Void result) {
            mSwipeRefreshView.setRefreshing(false);
            if (mSearchResults.isEmpty()) {
                mSearchResultsView.setVisibility(View.GONE);
                mEmptyInfoView.setVisibility(View.VISIBLE);
                mEmptyInfoView.setText("Nothing here...");
            } else {
                mEmptyInfoView.setVisibility(View.GONE);
                mSearchResultsView.setVisibility(View.VISIBLE);
            }
        }
    }
}
