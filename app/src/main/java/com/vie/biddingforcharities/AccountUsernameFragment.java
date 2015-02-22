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

public class AccountUsernameFragment extends Fragment {
    SubmitUsernameUpdateListener UsernameListener;

    EditText UserNameInput;
    Button UpdateUsernameButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_account_username, container, false);

        UserNameInput = (EditText) fragmentView.findViewById(R.id.username_input);
        UpdateUsernameButton = (Button) fragmentView.findViewById(R.id.update_username_button);

        // Preset Values
        User user = ((Global)getActivity().getApplication()).getUser();
        UserNameInput.setText(user.getUserName());

        UpdateUsernameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UsernameListener.SubmitUsernameUpdate(UserNameInput.getText().toString());
            }
        });

        return fragmentView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            UsernameListener = (SubmitUsernameUpdateListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement SubmitUsernameUpdateListener");
        }
    }

    public interface SubmitUsernameUpdateListener {
        public void SubmitUsernameUpdate(String userName);
    }
}
