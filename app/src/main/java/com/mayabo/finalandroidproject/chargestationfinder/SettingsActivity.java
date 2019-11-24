package com.mayabo.finalandroidproject.chargestationfinder;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.mayabo.finalandroidproject.R;

import static android.view.View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
import static android.view.WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS;

public class SettingsActivity extends AppCompatActivity {
    private int mOrigNavigationBarColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charge_station_finder_settings);

        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        backupNavigationBarColor();
        setupNavigationBarColor();
    }

    @Override
    public void onResume() {
        super.onResume();
        setupNavigationBarColor();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        restoreNavigationBarColor();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
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
}
