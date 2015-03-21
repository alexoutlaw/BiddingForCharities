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
import com.vie.biddingforcharities.SellerFolderActivity;

public class AddSettingDialog extends DialogFragment {
    public enum FormType {
        Create,
        Edit
    }
    FormType Type;

    Context Context;

    public AddSettingDialog() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        Context = getActivity();

        Bundle args = getArguments();
        String type = args.getString("type");
        String DefaultText = args.getString("default_text", "");

        switch(type) {
            case "add":
            case "create":
                Type = FormType.Create;
                break;
            case "edit":
            case "update":
                Type = FormType.Edit;
                break;
            default:
                Type = FormType.Create;
        }

        View DialogView = inflater.inflate(R.layout.dialog_addsetting, container);

        // Input
        final EditText SettingInput = (EditText) DialogView.findViewById(R.id.dialog_setting_input);
        SettingInput.setText(DefaultText);

        // Submit Button
        Button FormSubmitButton = (Button) DialogView.findViewById(R.id.dialog_bid_button);
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
            } else if(Context instanceof SellerFolderActivity) {
                ((SellerFolderActivity) Context).startAddTask(input);
            }
        } else if (Type == FormType.Edit) {
            if(Context instanceof SellerCategoriesActivity) {
                ((SellerCategoriesActivity) Context).startUpdateTask(input);
            } else if(Context instanceof SellerFolderActivity) {
                ((SellerFolderActivity) Context).startUpdateTask(input);
            }
        }
    }
}
