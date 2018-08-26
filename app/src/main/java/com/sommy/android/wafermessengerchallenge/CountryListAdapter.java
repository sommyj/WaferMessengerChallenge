package com.sommy.android.wafermessengerchallenge;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class CountryListAdapter extends RecyclerView.Adapter<CountryListAdapter.CountryListAdapterViewHolder> {

    private static final String TAG = CountryListAdapter.class.getSimpleName();
    private final Context context;

    private List<String> countriesNameList = new ArrayList<>();
    private List<String> currenciesNameList =new ArrayList<>();
    private List <String> languagesList = new ArrayList<>();


    /**
     * Creates a UserListAdapter.
     */
    public CountryListAdapter(Context context) {
        this.context = context;
    }

    /**
     * This gets called when each new ViewHolder is created. This happens when the RecyclerView
     * is laid out. Enough ViewHolders will be created to fill the screen and allow for scrolling.
     *
     * @param parent The ViewGroup that these ViewHolders are contained within.
     * @param viewType The ViewType integer is used to provide a different layout.
     * @return A new CountryListAdapterViewHolder that holds the View for each list item
     */
    @NonNull
    @Override
    public CountryListAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        int layoutForListItem = R.layout.country_list_layout;
        boolean shouldAttachToparentImmediately = false;

        View view = inflater.inflate(layoutForListItem, parent, shouldAttachToparentImmediately);
        return new CountryListAdapterViewHolder(view);
    }

    /**
     * OnBindViewHolder is called by the RecyclerView to display the data at the specified
     * position. In this method, we update the contents of the ViewHolder to display the Country
     * details for this particular position, using the "position" argument that is conveniently
     * passed into us.
     *
     * @param holder The ViewHolder which should be updated to represent the
     *                                  contents of the item at the given position in the data set.
     * @param position                  The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull final CountryListAdapterViewHolder holder, final int position) {
        String countryString = countriesNameList.get(position);
        String currencyString = currenciesNameList.get(position);
        String languageString = languagesList.get(position);

        holder.mCountryTextView.setText(countryString);
        holder.mCurrencyTextView.setText(currencyString);
        holder.mLanguageTextView.setText(languageString);

        //Removing the delete button on every refresh
        holder.mRemoveButton.setVisibility(View.GONE);

        //Bomb image button click listener. if visible and clicked on it deletes that particular row.
        holder.mRemoveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                countriesNameList.remove(position);
                currenciesNameList.remove(position);
                languagesList.remove(position);
                view.setVisibility(View.GONE);
                //Reseting the disable position in MainActivity
                MainActivity.disablePosition = -1;
                notifyDataSetChanged();
            }
        });
    }

    /* This method simply returns the number of items to display. It is used behind the scenes
     * to help layout our Views and for animations.
     *
     * @return The number of items available in restcountry.eu query
     */
    @Override
    public int getItemCount() {
        if(null == countriesNameList) {
            return 0;
        }else {
            return countriesNameList.size();
        }
    }

    /**
     * This method is used to set the country details on a CountryListAdapter if we've already
     * created one. This is handy when we get new data from the web but don't want to create a
     * new CountryListAdapter to display it.
     *
     * @param countriesNameList The new country name list data to be displayed.
     * @param currenciesNameList The new currency name list data to be displayed.
     * @param languagesList The new language list data to be displayed.
     */
    public void setCountryJsonData(List<String> countriesNameList, List<String> currenciesNameList,
                            List<String> languagesList) {
        this.countriesNameList = countriesNameList;
        this.currenciesNameList = currenciesNameList;
        this.languagesList = languagesList;
        notifyDataSetChanged();
    }

    /**
     * Cache of the children views for a country, currency and language list item.
     */
    public class CountryListAdapterViewHolder extends RecyclerView.ViewHolder{

        private final TextView mCountryTextView;
        private final TextView mCurrencyTextView;
        private final TextView mLanguageTextView;
        private final ImageButton mRemoveButton;

        public CountryListAdapterViewHolder(View itemView) {
            super(itemView);

            mCountryTextView = itemView.findViewById(R.id.countryName_textView);
            mCurrencyTextView = itemView.findViewById(R.id.currencyValue_textView);
            mLanguageTextView = itemView.findViewById(R.id.languageValue_textView);
            mRemoveButton = itemView.findViewById(R.id.btn_remove);

        }

    }

}
