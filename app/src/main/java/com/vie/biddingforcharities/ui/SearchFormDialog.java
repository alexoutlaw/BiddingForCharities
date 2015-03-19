package com.vie.biddingforcharities.ui;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.vie.biddingforcharities.AuctionSearchActivity;
import com.vie.biddingforcharities.R;

import java.util.ArrayList;

public class SearchFormDialog extends DialogFragment {
    EditText SearchTitleInput;
    Spinner CategorySpinner, PageSpinner, SortSpinner;
    Button SearchFormButton;

    public ArrayList<String> categories = new ArrayList<>();

    public SearchFormDialog() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        final AuctionSearchActivity activity = (AuctionSearchActivity) getActivity();

        View DialogView = inflater.inflate(R.layout.dialog_auctionsearch, container);

        // Title Input
        SearchTitleInput = (EditText) DialogView.findViewById(R.id.title_input);

        // Category Dropdown
        CategorySpinner = (Spinner) DialogView.findViewById(R.id.category_spinner);
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter(activity, android.R.layout.simple_spinner_item, categories);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        CategorySpinner.setAdapter(categoryAdapter);

        // Page Dropdown
        PageSpinner = (Spinner) DialogView.findViewById(R.id.page_spinner);
        ArrayAdapter<CharSequence> pageAdapter = ArrayAdapter.createFromResource(activity, R.array.search_page_items, android.R.layout.simple_spinner_item);
        pageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        PageSpinner.setAdapter(pageAdapter);

        // Sort Dropdown
        SortSpinner = (Spinner) DialogView.findViewById(R.id.sort_spinner);
        ArrayAdapter<CharSequence> sortAdapter = ArrayAdapter.createFromResource(activity, R.array.search_sort_items, android.R.layout.simple_spinner_item);
        sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        SortSpinner.setAdapter(sortAdapter);

        // Sumbit Button
        SearchFormButton = (Button) DialogView.findViewById(R.id.search_form_button);
        SearchFormButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String titleInput = SearchTitleInput.getText().toString();
                int categorySelect = CategorySpinner.getSelectedItemPosition();
                int pageSelect = PageSpinner.getSelectedItemPosition();
                int sortSelect = SortSpinner.getSelectedItemPosition();


                activity.startSearchTask(titleInput, categorySelect, pageSelect, sortSelect);
            }
        });

        //Allow scrolling
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        // Prevent automatic keyboard
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        return DialogView;
    }

    public void UpdateCategories(ArrayList<String> categoryList) {
        categories = categoryList;
    }
}
