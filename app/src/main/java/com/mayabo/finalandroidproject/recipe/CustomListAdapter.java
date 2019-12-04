package com.mayabo.finalandroidproject.recipe;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import 	android.widget.BaseAdapter;
import  android.content.Context;
import android.widget.TextView;

import com.mayabo.finalandroidproject.R;

import java.util.ArrayList;

    /**
     * This is a custom Adapter class
     * This class will deal with the listview adapter
     * Function as Populate
     * This class need to extends BaseAdapter
     * implements 4 methods: getCount(), getItem, getView, getItemID
     */



public class CustomListAdapter extends BaseAdapter {
    private Context context; //context
    private ArrayList<Recipe> recipes; //data source of the list adapter

    /**
     * Public constructor takes 2 parameters
     * @Param: Context, ArrayList<Recipe>
     * */

    //public constructor
    public CustomListAdapter(Context context, ArrayList<Recipe> recipes) {
        this.context = context;
        this.recipes = recipes;
    }

    @Override
    public int getCount() {
        return recipes.size(); //returns total of items in the list
    }

    @Override
    public Object getItem(int position) {
        return recipes.get(position); //returns list item at the specified position
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View viewOld, ViewGroup parent) {
        // inflate the layout for each list row
        if (viewOld == null) {
            viewOld = LayoutInflater.from(context).
                    inflate(R.layout.title_item, parent, false);
        }

        // get current item to be displayed
        Recipe currentRecipe = (Recipe) getItem(position);

        // get the TextView for item name and item description
        TextView title = (TextView) viewOld.findViewById(R.id.title_TextView);


        //sets the text for item name and item description from the current item object
        title.setText(currentRecipe.getTitle());


        // returns the view for the current row
        return viewOld;
    }
}
