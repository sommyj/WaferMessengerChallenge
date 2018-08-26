package com.sommy.android.wafermessengerchallenge;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sommy.android.wafermessengerchallenge.utilities.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static android.graphics.Color.rgb;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String> {

    private static final int COUNTRY_SEARCH_LOADER = 2;

    private RecyclerView mCountryRecyclerView;
    private ProgressBar mLoadingIndicator;
    private TextView mErrorMessageTextView;

    private CountryListAdapter mCountryListAdapter;
    final int PURPLE = rgb(85, 26, 139);
    private Paint p = new Paint();
    private boolean passedAnchorPoint = false;
    public static int disablePosition = -1;

    //initiale array list to hold the data of countries
    private List<String> countriesNameList = new ArrayList<>();
    private List<String> currenciesNameList =new ArrayList<>();
    private List <String> languagesList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mErrorMessageTextView = findViewById(R.id.error_textView);
        mLoadingIndicator = findViewById(R.id.pb_loading_indicator);
        mCountryRecyclerView = findViewById(R.id.country_recyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);

        mCountryRecyclerView.setLayoutManager(layoutManager);

        mCountryRecyclerView.setHasFixedSize(true);

        mCountryListAdapter = new CountryListAdapter(this);
        mCountryRecyclerView.setAdapter(mCountryListAdapter);

         /*
         Add a touch helper to the RecyclerView to recognize when a user swipes to delete an item.
         An ItemTouchHelper enables touch behavior (like swipe and move) on each ViewHolder,
         and uses callbacks to signal when a user is performing these actions.
         */
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            // Called when a user swipes left on a ViewHolder
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                View itemView = viewHolder.itemView;

                // Retrieve the position of the country to delete
                final int position = viewHolder.getAdapterPosition();
                countriesNameList.remove(position);
                currenciesNameList.remove(position);
                languagesList.remove(position);

                //Reseting the disable position in MainActivity
                MainActivity.disablePosition = -1;

                mCountryListAdapter.notifyDataSetChanged();

            }

            //Disable Swipe for position displaying bomb in RecyclerView
            @Override
            public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder holder) {
                int position = holder.getAdapterPosition();
                return position == disablePosition ? 0 : super.getSwipeDirs(recyclerView, holder);
            }

            //Displays bomb image button on clear view
            @Override
            public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                super.clearView(recyclerView, viewHolder);
                View itemView = viewHolder.itemView;
                if(passedAnchorPoint)
                itemView.findViewById(R.id.btn_remove).setVisibility(View.VISIBLE);

                passedAnchorPoint = false;
            }

            /**
             * Used to display background color and image on swipe
             * @param c
             * @param recyclerView
             * @param viewHolder
             * @param dX
             * @param dY
             * @param actionState
             * @param isCurrentlyActive
             */
            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                View itemView = viewHolder.itemView;

                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height / 8.5f;

                    final ColorDrawable background = new ColorDrawable(PURPLE);
                    final Drawable backgroundDrawable = getResources().getDrawable(R.drawable.bomb);

                    if(dX < 0) {
                        /*Set color in swipe background*/
                        background.setBounds((int) (itemView.getRight() + dX), itemView.getTop(),
                                itemView.getRight(), itemView.getBottom());
                        background.draw(c);

                    }

                    if (dX < -(itemView.getWidth()/5)) {
                        passedAnchorPoint = true;
                        disablePosition = viewHolder.getAdapterPosition();
                            /*Set image in swipe background*/
                        backgroundDrawable.setBounds(itemView.getRight() - backgroundDrawable.getMinimumWidth(), (int) (itemView.getTop() + width),
                                itemView.getRight(), (int) (itemView.getBottom() - width));

                            /*Set color in swipe background*/
                            background.setBounds((int) (itemView.getRight() + dX), itemView.getTop(),
                                    itemView.getRight(), itemView.getBottom());
                            background.draw(c);
                            backgroundDrawable.draw(c);

                    }

                if (dX < -(itemView.getWidth()/1.5)) {
                    passedAnchorPoint = true;
                    disablePosition = -1;
                    /*Set image in swipe background*/
                    backgroundDrawable.setBounds(itemView.getRight() - backgroundDrawable.getMinimumWidth(), (int) (itemView.getTop() + width),
                            itemView.getRight(), (int) (itemView.getBottom() - width));

                    /*Set color in swipe background*/
                    background.setBounds((int) (itemView.getRight() + dX), itemView.getTop(),
                            itemView.getRight(), itemView.getBottom());
                    background.draw(c);
                    backgroundDrawable.draw(c);

                }

                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        }).attachToRecyclerView(mCountryRecyclerView);

    }

    /**
     * This method start the content view if there is active network or send and error message if not.
     */
    protected void onResume(){
        super.onResume();
        getSupportLoaderManager().initLoader(COUNTRY_SEARCH_LOADER, null, this);

    }

    /**
     * This method will make the error message visible and hide the ListView.
     */
    private void showErrorMessage(){
        /* First, make sure the JSON data is invisible */
        mCountryRecyclerView.setVisibility(View.INVISIBLE);
        /* Then, make sure the error is visible */
        mErrorMessageTextView.setVisibility(View.VISIBLE);
    }

    /**
     * This method will make the View for the JSON data visible and
     * hide the error message.
     */
    private void showJsonDataView() {
        /* First, make sure the error is invisible */
        mErrorMessageTextView.setVisibility(View.INVISIBLE);
        /* Then, make sure the JSON data is visible */
        mCountryRecyclerView.setVisibility(View.VISIBLE);
    }

    /**
     * This method is used when we are resetting data, so that at one point in time during a
     * refresh of our data, you can see that there is no data showing.
     */
    private void invalidateData() {
        List<String> countriesNameList = new ArrayList<>();
        List<String> currenciesNameList =new ArrayList<>();
        List <String> languagesList = new ArrayList<>();

        mCountryListAdapter.setCountryJsonData(countriesNameList, currenciesNameList, languagesList );

        showJsonDataView();
    }

    private void passDataToAdapter(String data) {

        //Getting the Json values of String result received.
        JSONArray array;
        try {
            array = new JSONArray(data);
            for (int i = 0; i < array.length(); i++) {
                //Get json objects out of json array
                JSONArray currencies = array.getJSONObject(i).getJSONArray("currencies");
                JSONArray languages = array.getJSONObject(i).getJSONArray("languages");

                //Add data gotten from json to arraylist
                countriesNameList.add(array.getJSONObject(i).getString("name"));
                currenciesNameList.add(currencies.getJSONObject(0).getString("name"));
                languagesList.add(languages.getJSONObject(0).getString("name"));

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mCountryListAdapter.setCountryJsonData(countriesNameList, currenciesNameList, languagesList );

    }


    /**
     * This method is used to fetch for the list of Countries using the restcountries.eu API.
     * it runs on background thread
     * @param id The LoaderManager an ID
     * @param args The bundle that will receive data from initialize loader.
     * @return An AsyncTaskLoader that will return the Loader to the onLoadFinished method.
     */
    @NonNull
    @Override
    public Loader<String> onCreateLoader(int id, @Nullable Bundle args) {
        return new AsyncTaskLoader<String>(this) {

            String mCountryJson;

            @Override
            protected void onStartLoading(){

                mLoadingIndicator.setVisibility(View.VISIBLE);
                if(mCountryJson != null){
                    deliverResult(mCountryJson);
                }else {
                    forceLoad();
                }
            }

            @Override
            public String loadInBackground() {
                try {
                    URL countryUrl = NetworkUtils.buildUrl();
                    return NetworkUtils.getResponseFromHttpUrl(countryUrl);
                } catch (Exception e) {
                    Log.e("MainActivity", e.getMessage(), e);
                }
                return null;
            }

            @Override
            public void deliverResult(String countryJson) {
                mCountryJson = countryJson;
                super.deliverResult(countryJson);
            }
        };
    }

    @Override
    public void onLoadFinished(@NonNull Loader<String> loader, String data) {

        /* When we finish loading, we want to hide the loading indicator from the user. */
        mLoadingIndicator.setVisibility(View.INVISIBLE);

        /*
         * If the results are null, we assume an error has occurred.
         */
        if (null == data) {
            showErrorMessage();
        } else {
            showJsonDataView();
            passDataToAdapter(data);
        }

    }

    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) {

    }

    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_refresh, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem menuItem){
        int itemThatWasSelected = menuItem.getItemId();
        if(itemThatWasSelected == R.id.refresh_menu) {
            invalidateData();
            getSupportLoaderManager().restartLoader(COUNTRY_SEARCH_LOADER, null, this);

        }
        return super.onOptionsItemSelected(menuItem);
    }

    public void onItemViewClick(View view) {
        //Reseting the disable position in MainActivity
        disablePosition = -1;
        mCountryListAdapter.notifyDataSetChanged();
    }
}
