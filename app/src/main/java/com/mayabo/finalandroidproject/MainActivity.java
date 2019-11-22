package com.mayabo.finalandroidproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);

        // test git push
        Toolbar tBar = findViewById(R.id.toolbar);
        setSupportActionBar(tBar);

        Button carStationFinder = findViewById(R.id.carStationFinder);
        Button currencyConversion = findViewById(R.id.currencyConversion);
        Button newsHeadlines = findViewById(R.id.newsHeadlines);
        Button recipeSearch = findViewById(R.id.recipeSearch);

        carStationFinder.setOnClickListener( clk -> {
            Intent goToCarStationFinderActivity = new Intent(MainActivity.this, ChargeStationFinderActivity.class);
            startActivity(goToCarStationFinderActivity);
        });

        currencyConversion.setOnClickListener( clk -> {
            Intent goToCurrencyConversionActivity = new Intent(MainActivity.this, CurrencyConversionActivity.class);
            startActivity(goToCurrencyConversionActivity);
        });

        newsHeadlines.setOnClickListener( clk -> {
            Intent goToNewsHeadlinesActivity = new Intent(MainActivity.this, NewsHeadlinesActivity.class);
            startActivity(goToNewsHeadlinesActivity);
        });

        recipeSearch.setOnClickListener( clk -> {
            Intent goToRecipeSearchActivity= new Intent(MainActivity.this, RecipeSearchActivity.class);
            startActivity(goToRecipeSearchActivity);
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.car:
                Intent goToCarStationFinderActivity = new Intent(MainActivity.this, ChargeStationFinderActivity.class);
                startActivity(goToCarStationFinderActivity);
                break;
            case R.id.food:
                Intent goToRecipeSearchActivity= new Intent(MainActivity.this, RecipeSearchActivity.class);
                startActivity(goToRecipeSearchActivity);
                break;
            case R.id.news:
                Intent goToNewsHeadlinesActivity = new Intent(MainActivity.this, NewsHeadlinesActivity.class);
                startActivity(goToNewsHeadlinesActivity);
                break;
            case R.id.money:
                Intent goToCurrencyConversionActivity = new Intent(MainActivity.this, CurrencyConversionActivity.class);
                startActivity(goToCurrencyConversionActivity);
                break;
        }

        return true;
    }
}
