package com.mayabo.finalandroidproject.chargestationfinder;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.mayabo.finalandroidproject.R;
import com.mayabo.finalandroidproject.chargestationfinder.settings.Settings;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
import static android.view.WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS;

public class SettingsActivity extends AppCompatActivity {
    private ListView mSettingsView;
    private List<Integer> mSettings;
    private MyAdapter mSettingsAdapter;
    private int mOrigNavigationBarColor;

    /**
     * Initializes class variables to initial states.
     * @param savedInstanceState previous status
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charge_station_finder_settings);

        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        backupNavigationBarColor();
        setupNavigationBarColor();

        mSettings = new ArrayList<Integer>() {{
            add(Settings.GROUP_GENERAL);
            add(Settings.MAX_RESULTS);
            add(Settings.DISTANCE_UNIT);
            add(Settings.DIVIDER);
            add(Settings.GROUP_OTHERS);
            add(Settings.CLEAR_FAVORITES);
        }};
        mSettingsAdapter = new MyAdapter();

        mSettingsView = findViewById(R.id.settings);
        mSettingsView.setAdapter(mSettingsAdapter);
    }

    /**
     * Restores previous status.
     */
    @Override
    public void onResume() {
        super.onResume();
        setupNavigationBarColor();
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
     * Calls onBackPressed.
     * @return true
     */
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
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

    private class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return mSettings.size();
        }

        @Override
        public Integer getItem(int position) {
            return mSettings.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = getLayoutInflater().inflate(R.layout.charge_station_finder_settings, parent, false);
            int settingId = getItem(position);
            Settings.layoutFor(settingId, convertView, SettingsActivity.this);
            return convertView;
        }
    }
}
