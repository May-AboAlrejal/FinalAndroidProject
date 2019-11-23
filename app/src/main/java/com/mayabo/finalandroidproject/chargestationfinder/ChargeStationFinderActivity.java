package com.mayabo.finalandroidproject.chargestationfinder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.mayabo.finalandroidproject.R;

public class ChargeStationFinderActivity extends AppCompatActivity {
    private TextView mLatitudeView;
    private TextView mLongitudeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charge_station_finder);

        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mLatitudeView = findViewById(R.id.latitude);
        mLongitudeView = findViewById(R.id.longitude);

        mLatitudeView.setClickable(true);
        mLatitudeView.setFocusable(true);
        mLatitudeView.setFocusableInTouchMode(true);
        mLatitudeView.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
        mLatitudeView.setShowSoftInputOnFocus(true);
        mLatitudeView.setCursorVisible(true);
        mLatitudeView.setTextIsSelectable(true);
        mLatitudeView.setSelectAllOnFocus(true);
        mLatitudeView.setOnFocusChangeListener((view, b) -> {
            if (b) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            } else {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                mLongitudeView.requestFocus();
            }
        });

        mLongitudeView.setClickable(true);
        mLongitudeView.setFocusable(true);
        mLongitudeView.setFocusableInTouchMode(true);
        mLongitudeView.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
        mLongitudeView.setShowSoftInputOnFocus(true);
        mLongitudeView.setCursorVisible(true);
        mLongitudeView.setTextIsSelectable(true);
        mLongitudeView.setSelectAllOnFocus(true);
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
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
