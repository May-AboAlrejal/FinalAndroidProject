package com.mayabo.finalandroidproject.currency;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.snackbar.Snackbar;
import com.mayabo.finalandroidproject.R;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * This is main class activity of the currency conversion
 */
public class CurrencyConversionActivity extends AppCompatActivity {
    private EditText et_moneyNumber;
    private EditText et_srcCurrency;
    private EditText et_dstCurrency;
    private Button bt_convert;
    private ProgressBar progressBar;
    private ListView convertResultListView;
    private Toolbar toolBar;
    private Toast toast;

    List<Rate> convertedResultList = new ArrayList<>();
    MyOwnAdapter myAdapter;


    /**
     * Inner class using 2 strings. These two variables will be used in the Asyntask
     */
    private class CurrencyConversionParams {
        public String base;
        public String dests;

        /**
         * The constructor using 2 parameters
         * @param base: String
         * @param dests: String
         */
        public CurrencyConversionParams(String base, String dests) {
            this.base = base;
            this.dests = dests;
        }
    }

    /**
     * Inner class using 2 variables. These two variables will be used in Asynctask
     */
    private class CurrencyConversionProgress {
        public Integer progress;
        public Rate rate;

        /**
         * The constructor using instance of class Rate
         * @param rate: Rate
         */
        public CurrencyConversionProgress(Rate rate) {
            this.progress = null;
            this.rate = rate;
        }

        /**
         * The constructor using 1 parameter
         * @param progress: int
         */
        public CurrencyConversionProgress(int progress) {
            this.rate = null;
            this.progress = progress;
        }
    }

    /**
     * This inner class extends from AsyncTask to get the information from the Internet
     */
    protected class CurrencyConversion extends AsyncTask<CurrencyConversionParams, CurrencyConversionProgress, String> {

        private String convertDate;
        private String baseMoney;


        /**
         * The doInBackgound method use instances of class CurrencyConversionParams. This method will
         * get the base, rates, date from url which is completed by users by using getCurrencyFromUrl method
         * @param params: CurrencyConversionParams
         * @return ret: String
         */
        @Override
        protected String doInBackground(CurrencyConversionParams... params){
            String ret = null;
            try {
                this.getCurrencyFromUrl(params[0].base, params[0].dests);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            }
            return ret;
        }


        /**
         * This method is to get currency from url. The url is completed by user. When the user enter
         * the currency code he/she wants to convert to another currency/other currencies, and the amount
         * of money the user wants to convert. The method will use the 2 parameters base and symbols to
         * get the information destination money(the currency users wants to convert to), base money, rate
         * and date of conversion
         * @param base: String
         * @param symbols: String
         * @throws IOException
         * @throws XmlPullParserException
         * @throws JSONException
         */

        protected void getCurrencyFromUrl(String base, String symbols) throws IOException, XmlPullParserException, JSONException {
            try {
                String currencyQuery = "https://api.exchangeratesapi.io/latest?base=" + base + "&symbols=" + symbols;
                URL url = new URL(currencyQuery);
                HttpURLConnection urlCurrencyConn = (HttpURLConnection) url.openConnection();
                InputStream inStream = urlCurrencyConn.getInputStream();

                BufferedReader reader = new BufferedReader(new InputStreamReader(inStream, "UTF-8"), 8);
                StringBuilder sb = new StringBuilder();

                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                String result = sb.toString();
                JSONObject jObject = new JSONObject(result);
                JSONObject rates = jObject.getJSONObject("rates");
                String[] dstMoneys = symbols.split(",");
                convertDate = jObject.getString("date");
                baseMoney = jObject.getString("base");

                for (String dstMoney : dstMoneys) {
                    double rate = rates.getDouble(dstMoney);
                    publishProgress(new CurrencyConversionProgress(new Rate(dstMoney, baseMoney, rate, convertDate)));
                }


                publishProgress(new CurrencyConversionProgress(100));
                try {
                    Thread.sleep(500);
                } catch (Exception e) {
                }
                urlCurrencyConn.disconnect();
            } catch (Exception e) {}


        }

        /**
         * This method is to display the progress completed
         * @param values
         */
        protected void onProgressUpdate(CurrencyConversionProgress... values){
            super.onProgressUpdate(values);
            if (values[0].progress != null) {
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setProgress(values[0].progress);
            } else if (values[0].rate != null) {
                convertedResultList.add(values[0].rate);
                myAdapter.notifyDataSetChanged();
                //Update GUI stuff only:
            }

        }

        /**
         * This method is to complete the process. If the input is not correct (wrong codes of currency)
         * It will show the Snackbar to prompt user to re-enter the code. Then it sets invisible the progressBar
         * @param sendFromDoInBackground: String
         */
        protected void onPostExecute(String sendFromDoInBackground){
            if (convertedResultList.isEmpty()) {
                View view = findViewById(R.id.main_layout);
                Snackbar snackbar = Snackbar
                        .make(view, R.string.toast_wrongInput, Snackbar.LENGTH_LONG);
                snackbar.setAction(R.string.snackbar_tryAgain, v ->{
                            et_srcCurrency.setText("");
                            et_dstCurrency.setText("");
                            et_moneyNumber.setText("");
                        }
                );
                snackbar.setActionTextColor(Color.RED);
                snackbar.show();
            }
            progressBar.setVisibility(View.INVISIBLE);
        }

    }// end of class CurrencyConversion

    /**
     * inner class MyOwnAdapter
     * this class is to set the view of the favorite
     * when the appication re-launched
     */

    protected class MyOwnAdapter extends BaseAdapter {

        /**
         * getCount method is to get the number of rates in the result list.
         *  @return convertedResultList.size(): int
         */
        @Override
        public int getCount() {
            return convertedResultList.size();
        }

        /**
         * getItem method is to get the item in the list from given position.
         * @param position: int
         * @return item: Rate
         */
        @Override
        public Rate getItem(int position) {
            return convertedResultList.get(position);
        }

        /**
         * This method is to get itemId from position
         * @param position: int
         * @return position: long
         */
        @Override
        public long getItemId(int position) {
            return position;
        }

        /**
         * This method is to get view of each item in the rate list to pass to the list view
         * @param position: int
         * @param currentView: View
         * @param parent: ViewGroup
         * @return currentView: view
         */
        @Override
        public View getView(int position, View currentView, ViewGroup parent) {
            Rate rate = getItem(position);

            LayoutInflater inflater = getLayoutInflater();

            // this view is to show the result
            currentView = inflater.inflate(R.layout.currency_exchange_view, parent, false);
            TextView tvBaseCurCode = currentView.findViewById(R.id.base_currency_code);
            TextView baseAmount = currentView.findViewById(R.id.tv_base_amount);
            TextView tvDstCurCode= currentView.findViewById(R.id.dst_currency_code);
            TextView dstAmount = currentView.findViewById(R.id.tv_dst_amount);
            tvBaseCurCode.setText(" " + rate.getCurrencyBase() + " = ");
            baseAmount.setText("$" + et_moneyNumber.getText().toString());
            tvDstCurCode.setText(" " + rate.getCurrencyCode());
            String amountInput = et_moneyNumber.getText().toString();
            double exchangedMoney = 1;
            if (!amountInput.isEmpty()) {
                exchangedMoney = Double.parseDouble(amountInput);
            }
            exchangedMoney *= rate.getCurrencyRate();
            dstAmount.setText("$" + new DecimalFormat("#0.000").format(exchangedMoney));


            return currentView;
        }
    } // end of class MyOwnAdapter


    /**
     * The onCreate method is the main method of this class. It sets the view when user first launch
     * the application. It also defines the variables related to all of the edit texts, buttons, progressbar
     * list view, share preferences, etc. When the user enter the appropriated information like the
     * base currency, destination currency, and the amount of currency the user wants to convert, and
     * click the button convert. If all the field is not empty, the method will call the AsyncTask subclass
     * to get the information from URL. And then display the list view of result.
     * If user click on one of the result, an ArlertDialog will appears with information of the conversion,
     * such as base money, destination money, rate, and date of conversion. There is a button if the user
     * wants to add the conversion to database and favorite list. If the conversion already existed in the database,
     * it will show a Toast, otherwise it will add to database.
     * @param savedInstanceState: Bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.currency_activity_main);
        this.et_moneyNumber = (EditText) findViewById(R.id.et_inputMoney);
        this.et_srcCurrency = (EditText) findViewById(R.id.et_baseMoney);
        this.et_dstCurrency = (EditText) findViewById(R.id.et_dstMoney);
        this.bt_convert = (Button) findViewById(R.id.bt_convert);
        this.progressBar = (ProgressBar) findViewById(R.id.pb_convertProgress);
        this.convertResultListView = (ListView) findViewById(R.id.lv_result);


        SharedPreferences sharePref = getApplicationContext().getSharedPreferences("last_convert", MODE_PRIVATE);
        et_srcCurrency.setText(sharePref.getString("base_currency", ""));
        et_dstCurrency.setText(sharePref.getString("dst_currency", ""));
        et_moneyNumber.setText(sharePref.getString("amount_money", ""));

        toast = Toast.makeText(this, "",Toast.LENGTH_SHORT);
        toolBar = (Toolbar) findViewById(R.id.tbar_menu);
        setSupportActionBar(toolBar);

        // create an adapter object and send it to the listView
        myAdapter = new MyOwnAdapter();

        bt_convert.setOnClickListener(click -> {
            if (et_srcCurrency.getText().toString()==null || et_srcCurrency.getText().toString().isEmpty()){
                Toast.makeText(this, R.string.toast_nullScrCurrency,Toast.LENGTH_SHORT).show();
            } else if (et_dstCurrency.getText().toString()==null || et_dstCurrency.getText().toString().isEmpty()){
                Toast.makeText(this, R.string.toast_nullDstCurrency,Toast.LENGTH_SHORT).show();
            } else if (et_moneyNumber.getText().toString()==null || et_moneyNumber.getText().toString().isEmpty()){
                Toast.makeText(this, R.string.toast_nullAmountOfMoney,Toast.LENGTH_SHORT).show();
            } else {

                convertedResultList.clear();
                myAdapter.notifyDataSetChanged();
                new CurrencyConversion().execute(new CurrencyConversionParams(et_srcCurrency.getText().toString(),
                        et_dstCurrency.getText().toString()));
            }
        });

        // set adapter for view
        convertResultListView.setAdapter(myAdapter);

        convertResultListView.setOnItemClickListener((parent, view, position, id) -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(CurrencyConversionActivity.this);
            LayoutInflater inflater = getLayoutInflater();
            View infoView = inflater.inflate(R.layout.currency_info_view, parent, false);

            TextView currencyCode = infoView.findViewById(R.id.dst_currency_code);
            TextView currencyBase = infoView.findViewById(R.id.base_currency_code);
            TextView convertDate = infoView.findViewById(R.id.convert_date);
            TextView convertRate = infoView.findViewById(R.id.convert_rate);

            Rate selected = ((MyOwnAdapter) convertResultListView.getAdapter()).getItem(position);
            currencyCode.setText(selected.getCurrencyCode());
            currencyBase.setText(selected.getCurrencyBase());
            convertDate.setText(selected.getDateOfConversion());
            convertRate.setText(String.valueOf(selected.getCurrencyRate()));

            builder.setPositiveButton(R.string.bt_addToFavList, (dialog, which) -> {
                boolean conversionExist = false;
                //get a database:
                MyDatabaseOpenHelper dbOpener = new MyDatabaseOpenHelper(CurrencyConversionActivity.this);
                SQLiteDatabase db = dbOpener.getWritableDatabase();

                //query all the results from the database:
                String [] columns = {MyDatabaseOpenHelper.COL_ID, MyDatabaseOpenHelper.COL_FROM, MyDatabaseOpenHelper.COL_TO};
                Cursor results = db.query(false, MyDatabaseOpenHelper.TABLE_NAME, columns, null, null, null, null, null, null);
                //check if the conversion is in the database
                if (results.moveToFirst()) {
                    do {
                        String savedBase = results.getString(results.getColumnIndex("BASECURRENCY"));
                        String savedDst = results.getString(results.getColumnIndex("CONVERTEDCURRENCY"));
                        if (currencyBase.getText().toString().equals(savedBase) && currencyCode.getText().toString().equals(savedDst)) {
                            conversionExist = true;
                            break;
                        }
                    } while (results.moveToNext());
                }
                if (!conversionExist) {
                    dbOpener.insertConversion(selected.toFavoriteItem());
                    Toast.makeText(CurrencyConversionActivity.this, R.string.toast_addToFavorite, Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(CurrencyConversionActivity.this, R.string.toast_conversionExist, Toast.LENGTH_SHORT).show();
                }
//                        reloadFavoriteList();
            }).setView(view);
            builder.setView(infoView);
            builder.create().show();
        });

//        reloadFavoriteList();

    }// end of onCreate


    /**
     * This method is to inflate the menu of the toolbar
     * @param menu: Menu
     * @return true: boolean
     */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.currency_toolbar, menu);

        return true;
    }

    /**
     * This method is to use fragment to load the favorite list in tablet or move to another activity
     * in phone use case
     */
    public void reloadFavoriteList() {
        boolean isTablet = findViewById(R.id.fragment_frame_layout) != null; //check if the FrameLayout is loaded
        if (isTablet) {
            MainFragment dFragment = new MainFragment(); //add a MainFragment
            dFragment.setTablet(true);  //tell the fragment if it's running on a tablet or not
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_frame_layout, dFragment) //Add the fragment in FrameLayout
                    .commit(); //actually load the fragment.

        } else {
            Intent goToFavAct = new Intent(CurrencyConversionActivity.this, FavoriteListActivity.class);
            startActivity(goToFavAct);

        }
    }

    /**
     * This method is to set the action when user click on the menu item on the tool bar.
     * @param item: MenuItem
     * @return true: boolean
     */

    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        switch (item.getItemId()) {
            case R.id.it_favorite:
                reloadFavoriteList();
                break;
            case R.id.it_help:
                dialogBox();
                return true;
            default:
                toast.setText(R.string.toast_clickedOverFlowMenu);
                toast.show();
        }
        return true;
    }


    /**
     * This method is to show the dialogBox when user click on the help menu item in the toolbar
     */

    public void dialogBox(){
        View dialogBox =  getLayoutInflater().inflate(R.layout.currency_dialog_help, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogBox);
        builder.setPositiveButton(R.string.back, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).setView(dialogBox);
        builder.create().show();

    }

    /**
     * This method is overriden from super class, it uses SharePreferences to save the last input from users
     */
    @Override
    protected void onPause(){
        super.onPause();
        SharedPreferences sharePref = getSharedPreferences("last_convert", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharePref.edit();
        editor.putString("base_currency", et_srcCurrency.getText().toString());
        editor.putString("dst_currency", et_dstCurrency.getText().toString());
        editor.putString("amount_money", et_moneyNumber.getText().toString());
        editor.commit();
    }
} // end of class main activity

