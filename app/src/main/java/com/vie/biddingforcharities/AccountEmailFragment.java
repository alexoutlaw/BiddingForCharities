package com.vie.biddingforcharities;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.vie.biddingforcharities.logic.User;

public class AccountEmailFragment extends Fragment {
    SubmitEmailUpdateListener EmailListener;

    TextView CurrentEmailText;
    EditText NewEmailInput, ConfirmEmailInput, PasswordInput;
    Button UpdateEmailButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_account_email, container, false);

        CurrentEmailText = (TextView) fragmentView.findViewById(R.id.account_current_email);
        NewEmailInput = (EditText) fragmentView.findViewById(R.id.account_new_email);
        ConfirmEmailInput = (EditText) fragmentView.findViewById(R.id.account_confirm_email);
        PasswordInput = (EditText) fragmentView.findViewById(R.id.account_password);
        UpdateEmailButton = (Button) fragmentView.findViewById(R.id.update_email_button);

        // Preset Values
        User user = ((Global)getActivity().getApplication()).getUser();
        CurrentEmailText.setText(user.getEmail());

        UpdateEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EmailListener.SubmitEmailUpdate(NewEmailInput.getText().toString(), PasswordInput.getText().toString());
            }
        });

        return fragmentView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            EmailListener = (SubmitEmailUpdateListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement SubmitEmailUpdateListener");
        }
    }

    public interface  SubmitEmailUpdateListener {
        public void SubmitEmailUpdate(String newEmail, String password);
    }
}
