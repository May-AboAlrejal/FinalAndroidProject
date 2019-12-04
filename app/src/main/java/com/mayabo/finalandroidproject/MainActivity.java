package com.mayabo.finalandroidproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.mayabo.finalandroidproject.currency.CurrencyConversionActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);

        Button carStationFinder = (Button)findViewById(R.id.carStationFinder);
        Button currencyConversion = (Button)findViewById(R.id.currencyConversion);
        Button newsHeadlines = (Button)findViewById(R.id.newsHeadlines);
        Button recipeSearch = (Button)findViewById(R.id.recipeSearch);

        carStationFinder.setOnClickListener( clk -> {
            Intent goToCarStationFinderActivity = new Intent(MainActivity.this, CarStationFinderActivity.class);
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
}
