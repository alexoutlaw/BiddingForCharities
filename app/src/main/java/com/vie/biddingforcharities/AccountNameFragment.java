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

public class AccountNameFragment extends Fragment {
    SubmitNameUpdateListener NameListener;

    EditText FirstNameInput, LastNameInput;
    Button UpdateNameButton;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_account_name, container, false);

        FirstNameInput = (EditText) fragmentView.findViewById(R.id.first_name_input);
        LastNameInput = (EditText) fragmentView.findViewById(R.id.last_name_input);
        UpdateNameButton = (Button) fragmentView.findViewById(R.id.update_name_button);

        // Preset Values
        User user = ((Global)getActivity().getApplication()).getUser();
        FirstNameInput.setText(user.getFirstName());
        LastNameInput.setText(user.getLastName());

        UpdateNameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NameListener.SubmitNameUpdate(FirstNameInput.getText().toString(), LastNameInput.getText().toString());
            }
        });

        return fragmentView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            NameListener = (SubmitNameUpdateListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement SubmitNameUpdateListener");
        }
    }

    public interface  SubmitNameUpdateListener {
        public void SubmitNameUpdate(String firstName, String lastName);
    }
}
