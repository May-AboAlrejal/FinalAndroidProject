package com.mayabo.finalandroidproject.recipe;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.mayabo.finalandroidproject.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class FragmentDetails extends Fragment {

    private boolean isTablet;
    private Bundle dataFromActivity;
    private long id;
    TextView title;
    TextView foodUrl;
    TextView tapIn;
    TextView frameIntro;
    Button actionButton;
    String titleStr;
    String urlFood;
    String imageURL;
    Bitmap image = null;
    ImageView itemImage;
    ImageView heart;
    ProgressBar itemProgress;
    Menu menu;
    SingleQuery singlequery;
    String activityName;
    Fragment currentFragment;
    Toolbar tbar;


    public void setTablet(boolean tablet) {
        isTablet = tablet;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View result = inflater.inflate(R.layout.fragment_detail, container, false);
        actionButton = (Button) result.findViewById(R.id.userAction);
        title = (TextView) result.findViewById(R.id.title_single);
        foodUrl = (TextView) result.findViewById(R.id.url_single);
        itemProgress = (ProgressBar) result.findViewById(R.id.item_progress);
        itemImage = (ImageView) result.findViewById(R.id.image_single);
        currentFragment = getActivity().getSupportFragmentManager().findFragmentById(R.id.container);
        tbar = (Toolbar) result.findViewById(R.id.toolbar);

        dataFromActivity = getArguments();
//        tapIn = (TextView) result.findViewById(R.id.tapIn);
//        savebtn = (Button) findViewById(R.id.save_btn);
//        heart = (ImageView) result.findViewById(R.id.heartAction);


        return result;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        title.setText(dataFromActivity.getString(ListFavouriteActivity.TITLE_SELECTED));
        foodUrl.setText(dataFromActivity.getString(ListFavouriteActivity.SOURCE_URL));

        imageURL = dataFromActivity.getString(ListFavouriteActivity.IMAGE_URL);

        activityName = dataFromActivity.getString(SearchingActivity.NAME_ACTIVITY);

        if (activityName.equals(ListFavouriteActivity.ACTIVITY_NAME)) {
            actionButton.setText("REMOVE");
        } else {
            actionButton.setText("ADD");
        }

        singlequery = new SingleQuery(getContext());
        singlequery.execute(imageURL, dataFromActivity.getString(ListFavouriteActivity.IMAGE_ID));
        itemProgress.setVisibility(View.VISIBLE);

        actionButton.setOnClickListener(clk -> {
            id = dataFromActivity.getLong(SearchingActivity.ITEM_ID);

            if (activityName.equals(SearchingActivity.ACTIVITY_NAME)) {

                if (isTablet) { //both the list and details are on the screen:

                    SearchingActivity addAction = (SearchingActivity) getActivity();
                    if (addAction.addMessageId((int) id)) {
                        Snackbar snackbar = Snackbar
                                .make(view, "Successfully Added To Favourite!", Snackbar.LENGTH_LONG);
                        snackbar.show();
                    } else {

                        Snackbar snackbar = Snackbar
                                .make(view, "You Already Added This Recipe, Can't Add Duplicate Item!", Snackbar.LENGTH_LONG);
                        snackbar.setAction("Go To Favourite", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getActivity(), ListFavouriteActivity.class);
                                startActivity(intent);
                            }
                        });
                        snackbar.show();
                    }




                }
                //for Phone:
                else //You are only looking at the details, you need to go back to the previous list page
                {
                    EmptyActivity parent = (EmptyActivity) getActivity();
//
                    DatabaseHandler db = new DatabaseHandler(getActivity());
//                    //String title, String imageID, String imgUrl, String url
                    String title = dataFromActivity.getString(SearchingActivity.TITLE_SELECTED);
                    String imageID = dataFromActivity.getString(SearchingActivity.IMAGE_ID);
                    String imgUrl = dataFromActivity.getString(SearchingActivity.IMAGE_URL);
                    String url = dataFromActivity.getString(SearchingActivity.SOURCE_URL);
                    Recipe newRep = new Recipe(title, imageID, imgUrl, url);

                    Intent nextActivity = new Intent(getContext(), EmptyActivity.class);
                    dataFromActivity.putString("Add Result", "Success");
//
                    if (db.addRecipe(newRep) == 1) {

                    Log.i("Title: ", title);
                    Log.i("Image ID: ", imageID);
                    Log.i("Image URL: ", imgUrl);
                    Log.i("URL: ", url);





                    } else {
                        dataFromActivity.putString("Add Result", "Fail");
                        Log.i("Fail", dataFromActivity.getString(SearchingActivity.TITLE_SELECTED));
//                        parent.finish();
                    }

//                    boolean found = false;

//                    Intent backToFragmentExample = new Intent();
//                    backToFragmentExample.putExtra(SearchingActivity.ITEM_ID, dataFromActivity.getLong(SearchingActivity.ITEM_ID));


//                    parent.setResult(Activity.RESULT_OK, backToFragmentExample); //send data back to PreviousActivity in onActivityResult()
//                    parent.startActivity(backToFragmentExample); //send data back to PreviousActivity in onActivityResult()

                    Log.i("Clicked Added", dataFromActivity.getString(SearchingActivity.NAME_ACTIVITY));
//                    Intent nextActivity = new Intent(getContext(), EmptyActivity.class);
//                    nextActivity.putExtras(dataFromActivity); //send data to next activity
//                    parent.startActivityForResult(nextActivity, SearchingActivity.EMPTY_ACTIVITY); //make the transition
//
//                    DatabaseHandler db = new DatabaseHandler(getActivity());
//
//                    String inDataTitle;
//                    String inSelectedTitle;
//
//                    ArrayList<Recipe> myRecipes = new ArrayList<>();
//                    myRecipes.addAll(db.getAllRecipes());
//
//                    for (int i = 0; i < myRecipes.size(); i++) {
//                        inDataTitle = myRecipes.get(i).getTitle();
//                        inSelectedTitle = dataFromActivity.getString(SearchingActivity.TITLE_SELECTED);
//                        if (inDataTitle.equals(inSelectedTitle)) {
//                            found = true;
//                            break;
//                        }
//                    }
//
//                    if (!found) {
//                        Snackbar snackbar = Snackbar
//                                .make(view, "Successfully Added To Favourite!", Snackbar.LENGTH_LONG);
//                        snackbar.show();
//                        parent.finish();
//                    } else {
//                        Snackbar snackbar = Snackbar
//                                .make(view, "You Already Added This Recipe, Can't Add Duplicate Item!", Snackbar.LENGTH_LONG);
//                        snackbar.setAction("Go To Favourite", new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                Intent intent = new Intent(getActivity(), ListFavouriteActivity.class);
//                                startActivity(intent);
//                            }
//                        });
//                        snackbar.show();
//                        parent.finish();
//                    }
//
//
//
                    nextActivity.putExtras(dataFromActivity); //send data to next activity
                    startActivity(nextActivity);

                }

            }
            else {

                if(isTablet) { //both the list and details are on the screen:
                    ListFavouriteActivity parent = (ListFavouriteActivity) getActivity();
                    parent.deleteMessageId((int)id); //this deletes the item and updates the list
                    Snackbar snackbar = Snackbar
                            .make(view, "Remove From List Favourite!", Snackbar.LENGTH_LONG);
                    snackbar.show();

                    //now remove the fragment since you deleted it from the database:
                    // this is the object to be removed, so remove(this):
                    parent.getSupportFragmentManager()//get the fragment manager
                            .beginTransaction()//start the transaction
                            .remove(this)//call remove action
                            .commit();// action transaction commit from the remove action
                }
                //for Phone:
                else //You are only looking at the details, you need to go back to the previous list page
                {
                    EmptyActivity parent = (EmptyActivity) getActivity();
                    Intent backToFragmentExample = new Intent();
                    backToFragmentExample.putExtra(ListFavouriteActivity.ITEM_ID, dataFromActivity.getLong(ListFavouriteActivity.ITEM_ID ));
                    parent.setResult(Activity.RESULT_OK, backToFragmentExample); //send data back to FragmentExample in onActivityResult()
                    parent.finish(); //go back
                }

            }

        });
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.exit:
                switch (dataFromActivity.getString(ListFavouriteActivity.NAME_ACTIVITY)) {
                    case "ListFavouriteActivity":
                        Intent goToRecipeSearchActivity = new Intent(getContext(), ListFavouriteActivity.class);
                        startActivity(goToRecipeSearchActivity);
                        break;
                    case "SearchingActivity":
                        goToRecipeSearchActivity = new Intent(getContext(), RecipeSearchActivity.class);
                        startActivity(goToRecipeSearchActivity);
                        break;
                }
                break;
        }
        return true;
    }


    private class SingleQuery extends AsyncTask<String, Integer, String> {


        String result;
        Context context;


        public SingleQuery(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... strings) {
            result = null;
            String queryURL = strings[0].substring(0, 4) + "s" + strings[0].substring(4, strings[0].length());
            String imageName = strings[1] + ".jpeg";

            if (fileExistance(imageName)) {
                FileInputStream inputStream = null;
                try {
                    inputStream = new FileInputStream(getActivity().getFileStreamPath(imageName));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                image = BitmapFactory.decodeStream(inputStream);


                for (int i = 60; i < 90; i++) {
                    publishProgress(i);
                    try {
                        Thread.sleep(20);
                    } catch (Exception e) {
                    }
                }

            } else {
                try {

                    URL url = new URL(queryURL);
                    image = getImage(url);


                    if (image != null) {

                        FileOutputStream outputStream = getActivity().openFileOutput(imageName, Context.MODE_PRIVATE);

                        image.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
                        outputStream.flush();
                        outputStream.close();

                    }

                    for (int i = 20; i < 90; i++) {
                        publishProgress(i);
                        try {
                            Thread.sleep(20);
                        } catch (Exception e) {
                        }
                    }

                } catch (IOException ioe) {
                    result = "IO Exception. Is the Wifi Connected?";
                }
            }

            publishProgress(95);
            try {
                Thread.sleep(200);
            } catch (Exception e) {
            }

            return result;
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (image != null) {
                itemImage.setImageBitmap(image);
            } else {
//                Toast.makeText(context, "Image is not available for this Recipe", Toast.LENGTH_SHORT).show();
                Snackbar s = Snackbar.make(getView(), "Image is not available for this Recipe", Snackbar.LENGTH_SHORT);
                s.show();
                itemImage.setImageResource(R.drawable.food);
            }


            itemProgress.setVisibility(View.INVISIBLE);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            itemProgress.setProgress(values[0]);
            itemProgress.setVisibility(View.VISIBLE);
        }


        //Image
        protected Bitmap getImage(URL url) {

            HttpURLConnection iconConn = null;
            try {
                iconConn = (HttpURLConnection) url.openConnection();
                iconConn.connect();
                int response = iconConn.getResponseCode();
                if (response == 200) {
                    return BitmapFactory.decodeStream(iconConn.getInputStream());
                } else {
                    return null;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            } finally {
                if (iconConn != null) {
                    iconConn.disconnect();
                }
            }
        }


        public boolean fileExistance(String fileName) {
            File file = getActivity().getFileStreamPath(fileName);
            return file.exists();
        }
    }
}
