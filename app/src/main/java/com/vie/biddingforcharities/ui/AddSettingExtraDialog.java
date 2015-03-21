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
import com.vie.biddingforcharities.SellerReturnPolicyActivity;

public class AddSettingExtraDialog extends DialogFragment {
     public enum FormType {
         Create,
         Edit
     }
     FormType Type;

     Context Context;

     public AddSettingExtraDialog() {}

     @Override
     public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
         super.onCreateView(inflater, container, savedInstanceState);

         Context = getActivity();

         Bundle args = getArguments();
         String type = args.getString("type");
         String DefaultName = args.getString("default_name", "");
         String DefaultExtra = args.getString("default_extra", "");

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

         View DialogView = inflater.inflate(R.layout.dialog_addsettingextra, container);

         // Name
         final EditText NameInput = (EditText) DialogView.findViewById(R.id.dialog_setting_input);
         NameInput.setText(DefaultName);

         // Name
         final EditText ExtraInput = (EditText) DialogView.findViewById(R.id.dialog_setting_input_extra);
         ExtraInput.setText(DefaultExtra);

         // Submit Button
         Button FormSubmitButton = (Button) DialogView.findViewById(R.id.dialog_bid_button);
         FormSubmitButton.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 FormSubmit(NameInput.getText().toString(), ExtraInput.getText().toString());
             }
         });

         //Allow scrolling
         getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

         return DialogView;
     }

     private void FormSubmit(String name, String extra) {
         if(Type == FormType.Create) {
             if(Context instanceof SellerReturnPolicyActivity) {
                 ((SellerReturnPolicyActivity) Context).startAddTask(name, extra);
             }
         } else if (Type == FormType.Edit) {
             if(Context instanceof SellerReturnPolicyActivity) {
                 ((SellerReturnPolicyActivity) Context).startUpdateTask(name, extra);
             }
         }
     }
 }
