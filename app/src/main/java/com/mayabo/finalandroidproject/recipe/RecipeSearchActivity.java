package com.mayabo.finalandroidproject.recipe;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.snackbar.Snackbar;
import com.mayabo.finalandroidproject.CarStationFinderActivity;
import com.mayabo.finalandroidproject.CurrencyConversionActivity;
import com.mayabo.finalandroidproject.MainActivity;
import com.mayabo.finalandroidproject.NewsHeadlinesActivity;
import com.mayabo.finalandroidproject.R;

/**
 * This is the final project
 * Topic is specify to Recipe Search Engine
 * This project is a group project
 * Each person take one topic
 * My Topic is : Recipe
 * @author The Dai Phong Le
 * @version 1.0
 * @since 2019-11-11
 */

public class RecipeSearchActivity extends AppCompatActivity {



    public static final String AUTHOR_NAME = "Phong Le";
    public static final String VERSION = "10.1v";
    Menu menu;
    Toolbar tbar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recipe_home);
        tbar = findViewById(R.id.toolbar);
        setSupportActionBar(tbar);
        EditText filter = (EditText) findViewById(R.id.search_edit);


//        TextView authorName = findViewById(R.id.author);
//        TextView versionNumber = findViewById(R.id.version);
//
//        authorName.setText(authorName.getText().toString() + AUTHOR_NAME);
//        versionNumber.setText(versionNumber.getText().toString() + VERSION);


        Button searchButton = (Button) findViewById(R.id.view_search);


        Button viewFavourite = (Button) findViewById(R.id.view_favourite);



        //Checking if the search Button is exist
        if (searchButton != null) {
            searchButton.setOnClickListener(clk -> {
                String input = filter.getText().toString();
                if (TextUtils.isEmpty(filter.getText())) {
                    Toast.makeText(this, "Lasagna Or Chicken Breast Only", Toast.LENGTH_LONG).show();
                }
                else {
                        Intent goToSearch = new Intent(RecipeSearchActivity.this, SearchingActivity.class);
                        goToSearch.putExtra("searchFilter", filter.getText().toString());
                        startActivity(goToSearch);
                }
            });
        }

        //checking the viewFavourite button is exist
        if (viewFavourite != null) {
            viewFavourite.setOnClickListener(clk -> {
                Intent goToList = new Intent(RecipeSearchActivity.this, ListFavouriteActivity.class);
                startActivity(goToList);
            });

        }

    }
    /**
     * This is onOptionItemSelected method will work with any item in the toolbar that is clicked
     * @Param item
     * using param item to get the itemId
     * in the case of id equal to its name then call intent to go to another activity
     */

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId())

        {
            case R.id.news:
                Toast.makeText(this, "News BBC", Toast.LENGTH_SHORT).show();
                Intent goToNewsHeadlinesActivity = new Intent(this, NewsHeadlinesActivity.class);
                startActivity(goToNewsHeadlinesActivity);
                break;
            case R.id.station:
                Toast.makeText(this, "Charging Station", Toast.LENGTH_SHORT).show();
                Intent goToCarStationFinderActivity = new Intent(this, CarStationFinderActivity.class);
                startActivity(goToCarStationFinderActivity);
                break;
            case R.id.coin:
                Toast.makeText(this, "Currency", Toast.LENGTH_SHORT).show();
                    Intent goToCurrencyConversionActivity = new Intent(this, CurrencyConversionActivity.class);
                    startActivity(goToCurrencyConversionActivity);
                break;
            case R.id.food:
                Toast.makeText(this, "Food", Toast.LENGTH_SHORT).show();
                Intent goToRecipeSearchActivity= new Intent(this, RecipeSearchActivity.class);
                startActivity(goToRecipeSearchActivity);
                break;
            case R.id.intro_recipe:
                Toast.makeText(this, "This is the Instruction For Recipe", Toast.LENGTH_SHORT).show();
                instructionAlert();
                break;
        }
        return true;
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.recipe_menu, menu);
        this.menu = menu;
        return true;
    }




    /**
     * InstructionAlert is a method that take no parameter
     * this will be called to pop up a custom dialog
     * set up the textView
     */

    public void instructionAlert()
    {
        View middle = getLayoutInflater().inflate(R.layout.recipe_instruction_dialog, null);
        TextView authorName = middle.findViewById(R.id.author);
        TextView versionNumber = middle.findViewById(R.id.version);

        authorName.setText(authorName.getText() + AUTHOR_NAME);
        versionNumber.setText(versionNumber.getText() + VERSION);

        //Alert Diaglog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //this is call the builder pattern, the order of calling function does not matter

        //positive and negative are the button
        builder.setPositiveButton("Positive", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // What to do on Accept
                    }
                })
                .setNegativeButton("Negative", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                }).setView(middle);
        //can have third button as neutral button
        //showing stuff
        builder.create().show();
    }





}
