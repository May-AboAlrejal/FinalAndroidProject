package com.mayabo.finalandroidproject.currency;


import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.mayabo.finalandroidproject.R;


/**
 * This activity is used for phone
 */
public class FavoriteListActivity extends AppCompatActivity {

    /**
     * Set view and instantiate a fragment to use in this activity.
     * @param savedInstanceState
     */
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.currency_activity_favorite_list);
    MainFragment dFragment = new MainFragment(); //add a MainFragment
    dFragment.setTablet(true);  //tell the fragment if it's running on a tablet or not
    getSupportFragmentManager()
            .beginTransaction()
            .add(R.id.fragment_frame_layout, dFragment) //Add the fragment in FrameLayout
            .commit(); //actually load the fragment.
    }

} //end of class
