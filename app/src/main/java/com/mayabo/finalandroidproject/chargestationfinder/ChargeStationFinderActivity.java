package com.mayabo.finalandroidproject.chargestationfinder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BlendMode;
import android.graphics.BlendModeColorFilter;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.AppBarLayout;
import com.mayabo.finalandroidproject.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.view.View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
import static android.view.WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS;

public class ChargeStationFinderActivity extends AppCompatActivity {
    private TextView mLatitudeView;
    private TextView mLongitudeView;
    private ListView mSearchResultsView;
    private TextView mEmptyInfoView;
    private SwipeRefreshLayout mSwipeRefreshView;
    private ImageView mSwapFieldsView;
    private ImageView mGetLocationView;
    private List<Record> mSearchResults;
    private MyAdapter mSearchResultAdapter;
    private Drawable mPrimaryIconInfo;
    private Drawable mPrimaryIconFavorite;
    private int mOrigNavigationBarColor;

    public static List<Record> favorites;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charge_station_finder);

        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        backupNavigationBarColor();
        setupNavigationBarColor();

        AppBarLayout appBarLayout = findViewById(R.id.appbar);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            int scrollRange = -1;
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
            }
        });

        mSearchResults = new ArrayList<>();
        favorites = new ArrayList<>();
        mPrimaryIconInfo = fillIconWithColor(R.drawable.outline_info_24, getColor(R.color.colorPrimary));
        mPrimaryIconFavorite = fillIconWithColor(R.drawable.outline_favorite_24, getColor(R.color.colorSecondary));
        ((ImageView) findViewById(R.id.my_location)).setImageDrawable(fillIconWithColor(R.drawable.outline_my_location_24, Color.parseColor("#ffffff")));
        ((ImageView) findViewById(R.id.swap_fields)).setImageDrawable(fillIconWithColor(R.drawable.outline_swap_vert_24, Color.parseColor("#ffffff")));

        Cursor cursor = new RecordOpenHelper(this).getAll();
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                Record record = new Record(
                    cursor.getString(cursor.getColumnIndex(RecordOpenHelper.COLUMN_TITLE)),
                    "",
                    cursor.getString(cursor.getColumnIndex(RecordOpenHelper.COLUMN_LATITUDE)),
                    cursor.getString(cursor.getColumnIndex(RecordOpenHelper.COLUMN_LONGITUDE)),
                    true
                );
                if (!ChargeStationFinderActivity.favorites.contains(record))
                    ChargeStationFinderActivity.favorites.add(record);
            } while (cursor.moveToNext());
        }

        mSearchResultAdapter = new MyAdapter();

        mLatitudeView = findViewById(R.id.latitude);
        mLongitudeView = findViewById(R.id.longitude);
        mSearchResultsView = findViewById(R.id.search_result);
        mEmptyInfoView = findViewById(R.id.info_empty);
        mSwipeRefreshView = findViewById(R.id.swipe_to_refresh);
        mSwapFieldsView = findViewById(R.id.swap_fields);
        mGetLocationView = findViewById(R.id.my_location);

        mLatitudeView.setOnKeyListener((view, i, keyEvent) -> false);

        mLatitudeView.setOnTouchListener((view, motionEvent) -> {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                mLatitudeView.playSoundEffect(SoundEffectConstants.CLICK);
            }
            return super.onTouchEvent(motionEvent);
        });
        mLatitudeView.setOnFocusChangeListener((view, b) -> {
            if (b) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            } else {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
        mLongitudeView.setOnTouchListener((view, motionEvent) -> {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                mLongitudeView.playSoundEffect(SoundEffectConstants.CLICK);
            }
            return false;
        });

        mSearchResultsView.setAdapter(mSearchResultAdapter);
        mSearchResultsView.setOnItemClickListener((parent, item, position, id) -> {
            Record record = mSearchResultAdapter.getItem(position);
            View content = getLayoutInflater().inflate(R.layout.charge_station_detail, parent, false);
            ((TextView) content.findViewById(R.id.title)).setText(record.getTitle());
            ((TextView) content.findViewById(R.id.latitude)).setText(record.getLatitude());
            ((TextView) content.findViewById(R.id.longitude)).setText(record.getLongitude());
            ((TextView) content.findViewById(R.id.contact)).setText(record.getContact());
            new AlertDialog.Builder(this)
                .setIcon(mPrimaryIconInfo)
                .setTitle(record.getTitle())
                .setView(content)
                .setNegativeButton("Cancel", (dialogInterface, i) -> {})
                .create().show();
        });

        mSwapFieldsView.setOnClickListener(view -> {
            CharSequence s = mLatitudeView.getText();
            mLatitudeView.setText(mLongitudeView.getText());
            mLongitudeView.setText(s);
        });

        mGetLocationView.setOnClickListener(view -> {
            LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
            if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 0);
            }
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                mLatitudeView.setText(String.valueOf(location.getLatitude()));
                mLongitudeView.setText(String.valueOf(location.getLongitude()));
            }
        });

        mSwipeRefreshView.setOnRefreshListener(() -> search());
    }

    @Override
    public void onResume() {
        super.onResume();
        setupNavigationBarColor();
        sortResultWithFavorite();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        restoreNavigationBarColor();
    }

    private void backupNavigationBarColor() {
        mOrigNavigationBarColor = getWindow().getNavigationBarColor();
    }

    private void restoreNavigationBarColor() {
        getWindow().setNavigationBarColor(mOrigNavigationBarColor);
    }

    private void setupNavigationBarColor() {
        getWindow().setNavigationBarColor(getWindow().getDecorView().getRootView().getSolidColor());
        getWindow().getDecorView().setSystemUiVisibility(FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS | SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
    }

    private void search() {
        mEmptyInfoView.setText("Loading...");
        mEmptyInfoView.setVisibility(View.VISIBLE);
        mSearchResultsView.setVisibility(View.GONE);
        mSearchResults.clear();
        mSearchResultAdapter.notifyDataSetChanged();
        new MyQuery().execute(new QueryParams(
            ChargeStationFinderActivity.this.mLatitudeView.getText().toString(),
            ChargeStationFinderActivity.this.mLongitudeView.getText().toString()
        ));
        sortResultWithFavorite();
    }

    private void sortResultWithFavorite() {
        Collections.sort(mSearchResults, (left, right) -> {
            if (left.isFavorite() || favorites.contains(left)) {
                if (right.isFavorite() || favorites.contains(right)) {
                    return 0;
                } else {
                    return -1;
                }
            } else {
                if (right.isFavorite() || favorites.contains(right)) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });
        mSearchResultAdapter.notifyDataSetChanged();
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
        if (menu != null) {
            for(int i = 0; i < menu.size(); i++){
                Drawable drawable = menu.getItem(i).getIcon();
                if(drawable != null) {
                    drawable.mutate();
                    drawable.setColorFilter(new BlendModeColorFilter(getColor(R.color.colorPrimary), BlendMode.SRC_ATOP));
                }
            }
            if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
                try {
                    Method m = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                    m.setAccessible(true);
                    m.invoke(menu, true);
                } catch (Exception e) {
                    Log.e(getClass().getSimpleName(), "onMenuOpened...unable to set icons for overflow menu", e);
                }
            }
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_favorites:
                startActivity(new Intent(this, ActivityFavorites.class));
                return true;
            case R.id.item_about:
                new AlertDialog.Builder(this)
                    .setIcon(mPrimaryIconInfo)
                    .setTitle("About")
                    .setView(getLayoutInflater().inflate(
                        R.layout.about_charge_station_finder_dialog,
                        null, false
                    ))
                    .create().show();
                return true;
            case R.id.item_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            case R.id.item_exit:
                finish();
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

    private Drawable fillIconWithColor(int resId, int color) {
        Drawable icon = getResources().getDrawable(resId, getTheme());
        icon.mutate();
        icon.setColorFilter(new BlendModeColorFilter(color, BlendMode.SRC_ATOP));
        return icon;
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
            ImageView favorite = convertView.findViewById(R.id.ic_favorite);
            title.setText(record.getTitle());
            contact.setText(record.getContact());
            favorite.setOnClickListener(view -> {
                ImageView imageView = view.findViewById(R.id.ic_favorite);
                RecordOpenHelper db = new RecordOpenHelper(ChargeStationFinderActivity.this);
                if (record.isFavorite()) {
                    imageView.setImageDrawable(getResources().getDrawable(R.drawable.outline_favorite_border_24, getTheme()));
                    record.setIsFavorite(false);
                    favorites.remove(record);
                    db.remove(record);
                } else {
                    imageView.setImageDrawable(mPrimaryIconFavorite);
                    record.setIsFavorite(true);
                    favorites.add(record);
                    db.insert(record);
                }
            });
            if (record.isFavorite() || favorites.contains(record)) {
                record.setIsFavorite(true);
                favorite.setImageDrawable(mPrimaryIconFavorite);
            }
            return convertView;
        }
    }

    private class QueryParams {
        private static final String baseUrl = "https://api.openchargemap.io/v3/poi/?output=json";
        private String latitude;
        private String longitude;

        private QueryParams(String latitude, String longitude) {
            this.setLatitude(latitude);
            this.setLongitude(longitude);
        }

        private String buildQueryStatement() {
            if (this.getLatitude() == null && this.getLongitude() == null) {
                return baseUrl;
            }
            return baseUrl +
                "&latitude=" + this.getLatitude() +
                "&longitude=" + this.getLongitude();
        }

        private String getLatitude() {
            return latitude;
        }

        private void setLatitude(String latitude) {
            try {
                double value = Double.valueOf(latitude);
                if (value <= 90 && value >= -90) {
                    this.latitude = latitude;
                    return;
                }
            }
            catch (Exception e) {
            }
            Toast.makeText(
                ChargeStationFinderActivity.this,
                "Invalid latitude: \"" + latitude + "\", ignoring",
                Toast.LENGTH_SHORT
            ).show();
        }

        private String getLongitude() {
            return longitude;
        }

        private void setLongitude(String longitude) {
            try {
                double value = Double.valueOf(longitude);
                if (value <= 180 && value >= -180) {
                    this.longitude = longitude;
                    return;
                }
            }
            catch (Exception e) {
            }
            Toast.makeText(
                ChargeStationFinderActivity.this,
                "Invalid longitude: \"" + longitude + "\", ignoring",
                Toast.LENGTH_SHORT
            ).show();
        }
    }

    private class MyQuery extends AsyncTask<QueryParams, Record, Void> {
        @Override
        protected Void doInBackground(QueryParams... queryParams) {
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
                    Record record = new Record(title, contact, latitude, longitude, false);
                    record.setIsFavorite(favorites.contains(record));
                    this.publishProgress(record);
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
