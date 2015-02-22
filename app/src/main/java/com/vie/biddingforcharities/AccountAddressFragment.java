package com.vie.biddingforcharities;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.vie.biddingforcharities.logic.User;

public class AccountAddressFragment extends Fragment {
    SubmitAddressUpdateListener AddressListener;

    EditText EmailInput, CompanyInput, FirstInput, LastInput, StreetOneInput, StreetTwoInput, CountryInput, CityInput, StateInput, ZipInput, PhoneInput;
    Button UpdateAddressButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_account_address, container, false);

        EmailInput = (EditText) fragmentView.findViewById(R.id.account_address_email);
        CompanyInput = (EditText) fragmentView.findViewById(R.id.account_address_company);
        FirstInput = (EditText) fragmentView.findViewById(R.id.account_address_first);
        LastInput = (EditText) fragmentView.findViewById(R.id.account_address_last);
        StreetOneInput = (EditText) fragmentView.findViewById(R.id.account_address_street1);
        StreetTwoInput = (EditText) fragmentView.findViewById(R.id.account_address_street2);
        CountryInput = (EditText) fragmentView.findViewById(R.id.account_address_country);
        CityInput = (EditText) fragmentView.findViewById(R.id.account_address_city);
        StateInput = (EditText) fragmentView.findViewById(R.id.account_address_state);
        ZipInput = (EditText) fragmentView.findViewById(R.id.account_address_zip);
        PhoneInput = (EditText) fragmentView.findViewById(R.id.account_address_phone);
        UpdateAddressButton = (Button) fragmentView.findViewById(R.id.update_address_button);

        // Preset Values
        User user = ((Global)getActivity().getApplication()).getUser();
        EmailInput.setText(user.getEmail());
        //different than account name
        //FirstInput.setText(user.getFirstName());
        //LastInput.setText(user.getLastName());

        UpdateAddressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddressListener.SubmitAddressUpdate(
                        EmailInput.getText().toString(),
                        CompanyInput.getText().toString(),
                        FirstInput.getText().toString(),
                        LastInput.getText().toString(),
                        StreetOneInput.getText().toString(),
                        StreetTwoInput.getText().toString(),
                        CountryInput.getText().toString(),
                        CityInput.getText().toString(),
                        StateInput.getText().toString(),
                        ZipInput.getText().toString(),
                        PhoneInput.getText().toString()
                );
            }
        });

        //TODO: country, state dropdown

        return fragmentView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            AddressListener = (SubmitAddressUpdateListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement SubmitAddressUpdateListener");
        }
    }

    public interface  SubmitAddressUpdateListener {
        public void SubmitAddressUpdate(String email, String company, String first, String last, String street1, String street2, String country, String city, String state, String Zip, String phone);
    }
}
