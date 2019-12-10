package com.mayabo.finalandroidproject.chargestationfinder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BlendMode;
import android.graphics.BlendModeColorFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
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
import java.util.Locale;

import static android.view.View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
import static android.view.WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS;

public class ChargeStationFinderActivity extends AppCompatActivity {
    private TextView mLatitudeView;
    private TextView mLongitudeView;
    private RecyclerView mSearchResultsView;
    private TextView mEmptyInfoView;
    private SwipeRefreshLayout mSwipeRefreshView;
    private ImageView mSwapFieldsView;
    private ImageView mGetLocationView;
    private ConstraintLayout mSearchBarView;
    private FrameLayout mFragmentView;
    private List<Record> mSearchResults;
    private MyAdapter mSearchResultAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private Drawable mColoredIconInfo;
    private Drawable mColoredIconFavorite;
    private Drawable mPlainIconFavorite;
    private int mOrigNavigationBarColor;
    private boolean mIsSearchExpanded;
    private boolean mIsSearching;
    private boolean mHasFragment;
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;
    private boolean mDistanceUnit;

    public static List<Record> favorites;

    /**
     * Initializes class variables to initial states.
     * @param savedInstanceState previous status
     */
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

        favorites = new RecordOpenHelper(this).getAllRecords();
        mSearchResults = new ArrayList<>();
        mIsSearchExpanded = true;
        mIsSearching = false;
        mFragmentView = findViewById(R.id.frame_layout);
        mHasFragment = mFragmentView != null;
        mColoredIconInfo = fillIconWithColor(R.drawable.outline_info_24, getColor(R.color.colorPrimary));
        mColoredIconFavorite = fillIconWithColor(R.drawable.outline_star_24, getColor(R.color.colorAccent));
        mPlainIconFavorite = getResources().getDrawable(R.drawable.outline_star_border_24, getTheme());


        mSearchResultAdapter = new MyAdapter();
        mLayoutManager = new LinearLayoutManager(this);

        mLatitudeView = findViewById(R.id.latitude);
        mLongitudeView = findViewById(R.id.longitude);
        mSearchResultsView = findViewById(R.id.search_result);
        mEmptyInfoView = findViewById(R.id.info_empty);
        mSwipeRefreshView = findViewById(R.id.swipe_to_refresh);
        mSwapFieldsView = findViewById(R.id.swap_fields);
        mGetLocationView = findViewById(R.id.my_location);
        mSearchBarView = findViewById(R.id.search_bar);


        this.mPreferences = getApplicationContext().getSharedPreferences("charge_station_finder_pref", 0);
        mEditor = mPreferences.edit();
        String longitude = this.mPreferences.getString("longitude", "");
        String latitude = this.mPreferences.getString("latitude",      "");
        mDistanceUnit = mPreferences.getBoolean("distance_unit", true);

        mLongitudeView.setText(longitude);
        mLatitudeView.setText(latitude);

        mSwapFieldsView.setImageDrawable(fillIconWithColor(R.drawable.outline_swap_vert_24, getColor(R.color.colorSecondaryTextLight)));
        mGetLocationView.setImageDrawable(fillIconWithColor(R.drawable.outline_my_location_24, getColor(R.color.colorSecondaryTextLight)));

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
                mGetLocationView.setVisibility(View.GONE);
            } else {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                mGetLocationView.setVisibility(View.VISIBLE);
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
                mSwapFieldsView.setVisibility(View.GONE);
            } else {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                mSwapFieldsView.setVisibility(View.VISIBLE);
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
        mSearchResultsView.setLayoutManager(mLayoutManager);
        new ItemTouchHelper(new SwipeToDeleteCallback()).attachToRecyclerView(mSearchResultsView);

        mSwapFieldsView.setOnClickListener(view -> {
            CharSequence s = mLatitudeView.getText();
            mLatitudeView.setText(mLongitudeView.getText());
            mLongitudeView.setText(s);
        });

        mGetLocationView.setOnClickListener(view -> {
            LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                new AlertDialog.Builder(ChargeStationFinderActivity.this)
                    .setIcon(mColoredIconInfo)
                    .setTitle(Manifest.permission.ACCESS_FINE_LOCATION)
                    .setPositiveButton("Ok", (dialogInterface, i) -> requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 0))
                    .create().show();
            }
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (location == null) {
                    Toast.makeText(this, "location not available", Toast.LENGTH_SHORT).show();
                }
                else {
                    mLatitudeView.setText(String.valueOf(location.getLatitude()));
                    mLongitudeView.setText(String.valueOf(location.getLongitude()));
                }
            }
        });

        mSwipeRefreshView.setColorSchemeResources(R.color.colorAccent);
        mSwipeRefreshView.setOnRefreshListener(this::search);
    }

    /**
     * Restores previous status.
     */
    @Override
    public void onResume() {
        super.onResume();
        setupNavigationBarColor();
        boolean distanceUnit = mPreferences.getBoolean("distance_unit", true);
        if (mDistanceUnit != distanceUnit) {
            mDistanceUnit = distanceUnit;
            if (!mSearchResults.isEmpty()) {
                search();
            }
        } else {
            sortResults();
            mSearchResultAdapter.notifyDataSetChanged();
        }
    }

    /**
     * Save current status.
     */
    @Override
    protected void onPause() {
        super.onPause();
        mEditor.putString("latitude", mLatitudeView.getText().toString());
        mEditor.putString("longitude", mLongitudeView.getText().toString());
        mEditor.commit();
    }

    /**
     * Restore to default settings.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        restoreNavigationBarColor();
    }

    /**
     * Save the default settings.
     */
    private void backupNavigationBarColor() {
        mOrigNavigationBarColor = getWindow().getNavigationBarColor();
    }

    /**
     * Revert to the default settings.
     */
    private void restoreNavigationBarColor() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().setNavigationBarColor(mOrigNavigationBarColor);
    }

    /**
     * Set to custom settings.
     */
    private void setupNavigationBarColor() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(getColor(R.color.colorPrimary));
        getWindow().setNavigationBarColor(getWindow().getDecorView().getRootView().getSolidColor());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getWindow().getDecorView().setSystemUiVisibility(FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS | SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        } else {
            getWindow().getDecorView().setSystemUiVisibility(FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }
    }

    /**
     * Creates a new thread and fetch data from server. Updates ListView.
     */
    private void search() {
        if (!mIsSearching) {
            mEmptyInfoView.setText(R.string.loading);
            mEmptyInfoView.setVisibility(View.VISIBLE);
            mSearchResultsView.setVisibility(View.GONE);
            mSearchResults.clear();
            mSearchResultAdapter.notifyDataSetChanged();
            new MyQuery().execute(new QueryParams(
                ChargeStationFinderActivity.this.mLatitudeView.getText().toString(),
                ChargeStationFinderActivity.this.mLongitudeView.getText().toString(),
                Integer.valueOf(ChargeStationFinderActivity.this.mPreferences.getString("max_results", "0")),
                mDistanceUnit
            ));
        }
    }

    /**
     * Sorts elements in the ListView, puts favorites to top and orders by distance from near to far.
     */
    private void sortResults() {
        Collections.sort(mSearchResults, (left, right) -> {
            if (left.isFavorite() || favorites.contains(left)) {
                if (right.isFavorite() || favorites.contains(right)) {
                    if (left.getDistance() == null) {
                        if (right.getDistance() == null) {
                            return 0;
                        } else {
                            return 1;
                        }
                    } else {
                        if (right.getDistance() == null) {
                            return -1;
                        } else {
                            return left.getDistance().compareTo(right.getDistance());
                        }
                    }
                } else {
                    return -1;
                }
            } else {
                if (right.isFavorite() || favorites.contains(right)) {
                    return 1;
                } else {
                    if (left.getDistance() == null) {
                        if (right.getDistance() == null) {
                            return 0;
                        } else {
                            return 1;
                        }
                    } else {
                        if (right.getDistance() == null) {
                            return -1;
                        } else {
                            return left.getDistance().compareTo(right.getDistance());
                        }
                    }
                }
            }
        });
    }

    /**
     * Removes an item from the favorite list and updates the ListView.
     * @param position opsition the item to remove
     */
    public void removeFavoriteItem(int position) {
        Record record = favorites.remove(position);
        int index = mSearchResults.indexOf(record);
        if (index >= 0) {
            mSearchResults.get(index).setIsFavorite(false);
            sortResults();
            mSearchResultAdapter.notifyDataSetChanged();
        }
    }

    /**
     * Adds an item to the favorite list and updates the ListView.
     * @param position position to put the new item
     * @param record the item to add
     */
    public void addFavoriteItem(int position, Record record) {
        favorites.add(position, record);
        int index = mSearchResults.indexOf(record);
        if (index >= 0) {
            mSearchResults.get(index).setIsFavorite(true);
            sortResults();
            mSearchResultAdapter.notifyDataSetChanged();
        }
    }

    /**
     * Unfocuses all the EditTexts or go back to main menu.
     */
    @Override
    public void onBackPressed() {
        if (mLatitudeView.hasFocus() || mLongitudeView.hasFocus()) {
            mLatitudeView.clearFocus();
            mLongitudeView.clearFocus();
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Calls onBackPressed.
     * @return true
     */
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    /**
     * Inflates the menu.
     * @param menu menu to inflate
     * @return true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.charge_station_finder_main_menu, menu);
        if (menu != null) {
            for(int i = 0; i < menu.size(); i++){
                MenuItem item = menu.getItem(i);
                Drawable drawable = item.getIcon();
                if(drawable != null && item.getOrder() < 900) {
                    drawable.mutate();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        drawable.setColorFilter(new BlendModeColorFilter(getColor(R.color.colorPrimary), BlendMode.SRC_ATOP));
                    } else {
                        drawable.setColorFilter(getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
                    }
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

    /**
     * Performs jobs depends on with item is selected.
     * @param item item selected
     * @return true if the event is consumed.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_favorites:
                if (mHasFragment) {
                    if (mFragmentView.getVisibility() == View.GONE) {
                        mFragmentView.setVisibility(View.VISIBLE);
                    } else {
                        mFragmentView.setVisibility(View.GONE);
                    }
                    FragmentFavorite fragment = new FragmentFavorite();
                    getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.frame_layout, fragment)
                        .commit();
                }
                else {
                    startActivity(new Intent(this, ActivityFavorites.class));
                }
                return true;
            case R.id.item_about:
                new AlertDialog.Builder(this)
                    .setIcon(mColoredIconInfo)
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
                break;
            case R.id.item_toggle_search:
                if (mIsSearchExpanded) {
                    mIsSearchExpanded = false;
                    mSearchBarView.setVisibility(View.GONE);
                } else {
                    mIsSearchExpanded = true;
                    mSearchBarView.setVisibility(View.VISIBLE);
                }
                break;
            default:
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Changes the focus for EditTexts.
     * @param event the input event that is fired.
     * @return true if the event is consumed.
     */
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

    /**
     * Creates a drawable icon from a drawable id and a color id.
     * @param resId id of icon
     * @param color id of color to use
     * @return drawable icon with specified color
     */
    private Drawable fillIconWithColor(int resId, int color) {
        Drawable icon = getResources().getDrawable(resId, getTheme());
        icon.mutate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            icon.setColorFilter(new BlendModeColorFilter(color, BlendMode.SRC_ATOP));
        } else {
            icon.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        }
        return icon;
    }

    private class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View content = inflater.inflate(R.layout.charge_station_brief, parent, false);
            return new ViewHolder(content);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Record record = mSearchResults.get(position);
            holder.title.setText(record.getTitle());
            holder.distance.setText(String.format(Locale.getDefault(), "%.2f %s", record.getDistance(), mDistanceUnit ? "KMs" : "Miles"));
            if (record.getAddress() != null) {
                holder.address.setText(record.getAddress());
            }
            if (record.getContact() != null) {
                holder.contact.setText(record.getContact());
            }
            if (favorites.contains(record)) {
                record.setIsFavorite(true);
                holder.isFavorite.setImageDrawable(mColoredIconFavorite);
            } else {
                holder.isFavorite.setImageDrawable(mPlainIconFavorite);
            }
            holder.isFavorite.setOnClickListener(view -> {
                RecordOpenHelper db = new RecordOpenHelper(ChargeStationFinderActivity.this);
                if (record.isFavorite()) {
                    holder.isFavorite.setImageDrawable(mPlainIconFavorite);
                    record.setIsFavorite(false);
                    favorites.remove(record);
                    db.remove(record);
                } else {
                    holder.isFavorite.setImageDrawable(mColoredIconFavorite);
                    record.setIsFavorite(true);
                    favorites.add(record);
                    db.insert(record);
                }
                int index = mSearchResults.indexOf(record);
                mSearchResults.remove(index);
                mSearchResultAdapter.notifyItemRemoved(index);
                mSearchResults.add(record);
                sortResults();
                index = mSearchResults.indexOf(record);
                mSearchResultAdapter.notifyItemInserted(index);
                if (record.isFavorite()) {
                    if (index < 1) {
                        mLayoutManager.scrollToPosition(0);
                    } else {
                        mLayoutManager.scrollToPosition(index - 1);
                    }
                }
                if (mHasFragment && mFragmentView.getVisibility() == View.VISIBLE) {
                    FragmentFavorite fragment = new FragmentFavorite();
                    getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frame_layout, fragment)
                        .commit();
                }
            });
            holder.itemView.setOnClickListener(view -> {
                View content = getLayoutInflater().inflate(R.layout.charge_station_detail, null, false);
                ((TextView) content.findViewById(R.id.title)).setText(record.getTitle());
                ((TextView) content.findViewById(R.id.latitude)).setText(record.getLatitude());
                ((TextView) content.findViewById(R.id.longitude)).setText(record.getLongitude());
                ((TextView) content.findViewById(R.id.contact)).setText(record.getContact());
                new AlertDialog.Builder(ChargeStationFinderActivity.this)
                    .setIcon(mColoredIconInfo)
                    .setTitle(record.getTitle())
                    .setView(content)
                    .setPositiveButton("Open map", (dialogInterface, i) -> {
                        Uri gmmIntentUri = Uri.parse("geo:" + record.getLatitude() + "," + record.getLongitude() + "?q=" + record.getAddress());
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        mapIntent.setPackage("com.google.android.apps.maps");
                        startActivity(mapIntent);
                    })
                    .setNegativeButton("Cancel", (dialogInterface, i) -> {})
                    .create().show();
            });
        }

        @Override
        public int getItemCount() {
            return mSearchResults.size();
        }

        private class ViewHolder extends RecyclerView.ViewHolder {
            public TextView title;
            public TextView contact;
            public TextView address;
            public TextView distance;
            public ImageView isFavorite;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                title = itemView.findViewById(R.id.title);
                contact = itemView.findViewById(R.id.contact);
                address = itemView.findViewById(R.id.address);
                distance = itemView.findViewById(R.id.distance);
                isFavorite = itemView.findViewById(R.id.ic_favorite);
            }
        }
    }

    public class SwipeToDeleteCallback extends ItemTouchHelper.SimpleCallback {
        private final ColorDrawable background;
        private final ColorDrawable divider;
        private final ColorDrawable clearDivider;

        public SwipeToDeleteCallback() {
            super(0,ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
            background = new ColorDrawable(getColor(R.color.colorAccentDark));
            divider = new ColorDrawable(Color.parseColor("#D0D0D0"));
            clearDivider = new ColorDrawable(Color.parseColor("#ffffff"));
        }
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }
        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        }
        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            super.onChildDraw(c, recyclerView, viewHolder, dX / 4, dY, actionState, isCurrentlyActive);
            View itemView = viewHolder.itemView;
            int backgroundCornerOffset = 20;
            ColorDrawable whichDivider;

            if (dX > 0) { // Swiping to the right
                background.setBounds(
                    itemView.getLeft(),
                    itemView.getTop(),
                    itemView.getLeft() + ((int) dX) + backgroundCornerOffset,
                    itemView.getBottom()
                );
                whichDivider = divider;
            } else if (dX < 0) { // Swiping to the left
                background.setBounds(
                    itemView.getRight() + ((int) dX) - backgroundCornerOffset,
                    itemView.getTop(),
                    itemView.getRight(),
                    itemView.getBottom()
                );
                whichDivider = divider;
            } else { // view is unSwiped
                background.setBounds(0, 0, 0, 0);
                whichDivider = clearDivider;
            }

            whichDivider.setBounds(
                itemView.getLeft(),
                itemView.getBottom(),
                itemView.getRight(),
                itemView.getBottom() + 1
            );

            background.draw(c);
            whichDivider.draw(c);
        }
    }

    private class QueryParams {
        private static final String baseUrl = "https://api.openchargemap.io/v3/poi/?output=json";
        private String latitude;
        private String longitude;
        private Integer maxResult;
        private boolean distanceUnit;

        private QueryParams(String latitude, String longitude, Integer maxResult, boolean distanceUnit) {
            this.setLatitude(latitude);
            this.setLongitude(longitude);
            this.setMaxResult(maxResult);
            this.setDistanceUnit(distanceUnit);
        }

        private String buildQueryStatement() {
            StringBuilder url = new StringBuilder(baseUrl);
            if (this.getLatitude() != null && !this.getLatitude().isEmpty()) {
                url.append("&latitude=").append(this.getLatitude());
            }
            if (this.getLongitude() != null && !this.getLongitude().isEmpty()) {
                url.append("&longitude=").append(this.getLongitude());
            }
            if (this.getMaxResult() != null && this.getMaxResult() > 0) {
                url.append("&maxresults=").append(this.getMaxResult());
            }
            url.append("&distanceunit=").append(this.getDistanceUnit() ? "KM" : "Miles");
            return url.toString();
        }

        private String getLatitude() {
            return latitude;
        }

        private void setLatitude(String latitude) {
            try {
                if (latitude.isEmpty()) {
                    this.latitude = latitude;
                }
                else {
                    double value = Double.valueOf(latitude);
                    if (value <= 90 && value >= -90) {
                        this.latitude = latitude;
                    } else {
                        throw new Exception();
                    }
                }
            }
            catch (Exception e) {
                Toast.makeText(
                    ChargeStationFinderActivity.this,
                    "Invalid latitude: \"" + latitude + "\"",
                    Toast.LENGTH_SHORT
                ).show();
            }
        }

        private String getLongitude() {
            return longitude;
        }

        private void setLongitude(String longitude) {
            try {
                if (longitude.isEmpty()) {
                    this.longitude = longitude;
                }
                else {
                    double value = Double.valueOf(longitude);
                    if (value <= 180 && value >= -180) {
                        this.longitude = longitude;
                    } else {
                        throw new Exception();
                    }
                }
            }
            catch (Exception e) {
                Toast.makeText(
                    ChargeStationFinderActivity.this,
                    "Invalid longitude: \"" + longitude + "\"",
                    Toast.LENGTH_SHORT
                ).show();
            }
        }

        public Integer getMaxResult() {
            return maxResult;
        }

        public void setMaxResult(Integer maxResult) {
            this.maxResult = maxResult;
        }

        private boolean getDistanceUnit() {
            return this.distanceUnit;
        }

        public void setDistanceUnit(boolean distanceUnit) {
            this.distanceUnit = distanceUnit;
        }
    }

    private class MyQuery extends AsyncTask<QueryParams, Record, Void> {
        @Override
        protected Void doInBackground(QueryParams... queryParams) {
            mIsSearching = true;
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
                    String address1 = jObject.getString("AddressLine1");
                    String address2 = jObject.getString("AddressLine2");
                    String address = null;
                    if (!address1.contentEquals("null")) {
                        address = address1;
                    }
                    if (!address2.contentEquals("null")) {
                        address += address2;
                    }
                    Double distance = jObject.getString("Distance").contentEquals("null") ? null : jObject.getDouble("Distance");
                    Record record = new Record(title, contact, address, latitude, longitude, false);
                    record.setDistance(distance);
                    int index = favorites.indexOf(record);
                    if (index >= 0) {
                        favorites.remove(index);
                        favorites.add(index, record);
                    }
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
                mEmptyInfoView.setText("Nothing here");
            } else {
                mEmptyInfoView.setVisibility(View.GONE);
                mSearchResultsView.setVisibility(View.VISIBLE);
                sortResults();
                mSearchResultAdapter.notifyDataSetChanged();
            }
            mIsSearching = false;
        }
    }
}
