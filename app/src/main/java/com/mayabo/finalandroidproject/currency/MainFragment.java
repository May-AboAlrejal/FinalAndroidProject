package com.mayabo.finalandroidproject.currency;

import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.mayabo.finalandroidproject.R;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is a fragment to use in different model of Android devices like phones, tablets
 */
public class MainFragment extends Fragment {

    private boolean isTablet;
    List<FavoriteItem> favoriteList = new ArrayList<>();
    MyFavListAdapter myFavAdapter;

    /**
     * This method is to set if the device is tablet.
     * @param tablet: boolean
     *
     */
    public void setTablet(boolean tablet){ this.isTablet = tablet;}


    /**
     * This method is overridden from super class. It is used to inflate a fragment view. After
     * setting a list view, this method will get a database and get all the data in that database to
     * pass to list view. If user puss a long click, then a Arlert Dialog will ask user if he/she want
     * to delete the conversion from list view and database. Finally it will return the view.
     * @param inflater: Layout Inflater
     * @param container: ViewGroup
     * @param savedInstanceState: Bundle
     * @return result: View
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        View result = inflater.inflate(R.layout.currency_fragment_main, container, false);
        ListView listView = (ListView) result.findViewById(R.id.theList);

        // create an adapter object and send it to the listView
        myFavAdapter = new MyFavListAdapter();
        listView.setAdapter(myFavAdapter);
        //get a database:
        MyDatabaseOpenHelper dbOpener = new MyDatabaseOpenHelper(getActivity());
        SQLiteDatabase db = dbOpener.getWritableDatabase();

        //query all the results from the database:
        String[] columns = {MyDatabaseOpenHelper.COL_ID, MyDatabaseOpenHelper.COL_FROM, MyDatabaseOpenHelper.COL_TO};
        Cursor results = db.query(false, MyDatabaseOpenHelper.TABLE_NAME, columns, null, null, null, null, null, null);


        if (results.moveToFirst()) {
            do {
                this.favoriteList.add(new FavoriteItem(
                        results.getString(1),
                        results.getString(2)
                ));
            } while (results.moveToNext());
        }
        // when user puss the item, it will create a new dialog to ask
        // if the user wants to remove the conversion from favorite list
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                FavoriteItem pair = ((MyFavListAdapter) listView.getAdapter()).getItem(position);

                builder.setMessage(R.string.dialog_removeQuestion)
                        .setPositiveButton(R.string.bt_remove, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dbOpener.remove(pair);
                                favoriteList.remove(pair);
                                myFavAdapter.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //
                            }
                        })
                        .create().show();
                return true;
            }
        });

        return result;
    }


    /**
     * This is a inner class which set up data and view of the list view
     */
    protected class MyFavListAdapter extends BaseAdapter {

        /**
         * getCount method is to get the number of items in the favourite list.
         * @return favoriteList.size: inr
         */
        @Override
        public int getCount() {
            return favoriteList.size();
        }

        /**
         * getItem method is to get the item in the list from given position.
         * @param position: int
         * @return item: FavoriteItem
         */
        @Override
        public FavoriteItem getItem(int position) {
            return favoriteList.get(position);
        }

        /**
         * This method is to get itemId from position
         * @param position
         * @return
         */
        @Override
        public long getItemId(int position) {
            return favoriteList.get(position).getId();
        }

        /**
         * This method is to get view of each item in the favourite list to pass to the list view
         * @param position: int
         * @param convertView: View
         * @param parent: ViewGroup
         * @return convertView: View
         */
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            FavoriteItem favPair = getItem(position);
            convertView = getLayoutInflater().inflate(R.layout.currency_favorite_list, parent, false);
            //set text for the textview in the convertView
            ((TextView) convertView.findViewById(R.id.tv_baseMoneyCol)).setText(favPair.getFrom());
            ((TextView) convertView.findViewById(R.id.tv_convertedMoneyCol)).setText(favPair.getTo());
            return convertView;
        }
    }
}

