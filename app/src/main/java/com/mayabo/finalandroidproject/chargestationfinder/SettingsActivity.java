package com.mayabo.finalandroidproject.chargestationfinder;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.mayabo.finalandroidproject.R;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charge_station_finder_settings);

        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
