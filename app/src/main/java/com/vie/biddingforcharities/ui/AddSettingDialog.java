package com.vie.biddingforcharities.ui;

import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.vie.biddingforcharities.R;
import com.vie.biddingforcharities.SellerCategoriesActivity;

public class AddSettingDialog extends DialogFragment {
    public enum FormType {
        Create,
        Edit
    }
    FormType Type;

    EditText SettingInput;
    Button FormSubmitButton;

    Context Context;
    String DefaultText;

    public AddSettingDialog() {
    }

    public AddSettingDialog(Context context, FormType type) {
        Context = context;
        Type = type;
        DefaultText = "";
    }

    public AddSettingDialog(Context context, FormType type, String defaultValue) {
        Context = context;
        Type = type;
        DefaultText = defaultValue;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View DialogView = inflater.inflate(R.layout.dialog_addsetting, container);

        // Input
        SettingInput = (EditText) DialogView.findViewById(R.id.dialog_setting_input);
        SettingInput.setText(DefaultText);

        // Submit Button
        FormSubmitButton = (Button) DialogView.findViewById(R.id.dialog_bid_button);
        FormSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FormSubmit(SettingInput.getText().toString());
            }
        });

        //Allow scrolling
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        return DialogView;
    }

    private void FormSubmit(String input) {
        if(Type == FormType.Create) {
            if(Context instanceof SellerCategoriesActivity) {
                ((SellerCategoriesActivity) Context).startAddTask(input);
            }
        } else if (Type == FormType.Edit) {
            if(Context instanceof SellerCategoriesActivity) {
                ((SellerCategoriesActivity) Context).startUpdateTask(input);
            }
        }
    }
}
