package com.mayabo.finalandroidproject.recipe;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.snackbar.Snackbar;
import com.mayabo.finalandroidproject.R;

public class EmptyActivity extends AppCompatActivity {

    Bundle dataToPass;
    Toolbar tbar;

    Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empty);
        dataToPass = getIntent().getExtras(); //get the data that was passed from FragmentExample
        Log.i("In Empty Activity", dataToPass.getString(SearchingActivity.NAME_ACTIVITY));

        tbar = findViewById(R.id.toolbar);
        tbar.setTitle("Details");
        tbar.setTitleTextColor(getResources().getColor(R.color.lightBackground));
        tbar.setBackgroundColor(getResources().getColor(R.color.tBar));
        setSupportActionBar(tbar);





        FrameLayout fragmentLayout = (FrameLayout) findViewById(R.id.fragmentLocation);

        if (dataToPass.getString("Add Result") != null ) {

            if (dataToPass.getString("Add Result").equals("Success")) {
                Toast.makeText(this, "Successfully Added", Toast.LENGTH_LONG).show();

            } else {
                Snackbar snackbar = Snackbar
                        .make(fragmentLayout, "Can't Add Duplicated!", Snackbar.LENGTH_LONG);
                snackbar.setAction("Go To Favourite", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getBaseContext(), ListFavouriteActivity.class);
                        startActivity(intent);
                    }
                });
                snackbar.show();
            }
            Log.i("Not Null", dataToPass.getString("Add Result"));

        } else {
            Log.i("Some Thing", "Some thing");
        }

        //Creating Fragment for the first time loading the item
        FragmentDetails dFragment = new FragmentDetails();
        dFragment.setArguments( dataToPass ); //pass data to the the fragment
        dFragment.setTablet(false); //tell the Fragment that it's on a phone.
        getSupportFragmentManager()//get the support manager to use the builder design pattern
                .beginTransaction()
                .replace(R.id.fragmentLocation, dFragment) // add to the FrameLayout the fragment
                //.addToBackStack("AnyName")
                .commit(); // call the action
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, SearchingActivity.class);
        intent.putExtras(dataToPass);
        finish();
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId())

        {
            case R.id.exit:
                Toast.makeText(this, "Welcome Back!", Toast.LENGTH_SHORT).show();
                this.intentToMain();
                finish();
                break;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.exit_to_home, menu);
        this.menu = menu;
        return true;
    }

    protected void intentToMain() {
        Intent goToMain = new Intent(this, RecipeSearchActivity.class);
        startActivity(goToMain);
    }
}
