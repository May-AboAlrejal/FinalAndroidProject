package com.mayabo.finalandroidproject.chargestationfinder;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.WindowManager;

import com.mayabo.finalandroidproject.R;

import static android.view.View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
import static android.view.WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS;

public class SettingsActivity extends AppCompatActivity {
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
        getWindow().getDecorView().setSystemUiVisibility(FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS | SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
    }
}
