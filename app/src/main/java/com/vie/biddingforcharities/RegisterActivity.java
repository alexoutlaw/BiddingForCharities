package com.vie.biddingforcharities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.vie.biddingforcharities.logic.GetInfoTask;
import com.vie.biddingforcharities.logic.Utilities;

import org.json.JSONObject;

public class RegisterActivity extends Activity {
    Button LoginButton, RegisterButton;
    EditText EmailInput, PassInput, ConfirmInput, FirstInput, LastInput;

    String Email, Password;

    ProgressDialog spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        LoginButton = (Button) findViewById(R.id.login_button);
        RegisterButton = (Button) findViewById(R.id.register_button);
        EmailInput = (EditText) findViewById(R.id.email_input);
        PassInput = (EditText) findViewById(R.id.password_input);
        ConfirmInput = (EditText) findViewById(R.id.confirm_input);
        FirstInput = (EditText) findViewById(R.id.first_input);
        LastInput = (EditText) findViewById(R.id.last_input);

        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
            }
        });

        RegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Email = EmailInput.getText().toString();
                Password = PassInput.getText().toString();
                String confirm = ConfirmInput.getText().toString();
                String first = FirstInput.getText().toString();
                String last = LastInput.getText().toString();

                if(Password.compareTo(confirm) != 0) {
                    Toast.makeText(RegisterActivity.this, "Passwords Do Not Match", Toast.LENGTH_LONG).show();
                } else {
                    if(Email.length() > 0 && Password.length() > 0 && first.length() > 0 && last.length() > 0) {
                        //Show Spinner
                        spinner = new ProgressDialog(RegisterActivity.this);
                        spinner.setMessage("Registering...");
                        spinner.setCanceledOnTouchOutside(false);
                        spinner.show();

                        //Register with Server
                        String queryStr = Utilities.BuildQueryParams(
                                new String[][]{
                                        new String[]{"email", Email},
                                        new String[]{"pwd", Password},
                                        new String[]{"first", first},
                                        new String[]{"last", last}
                                });
                        new GetInfoTask(RegisterActivity.this).execute("registerUser", queryStr);
                    }
                    else  {
                        Toast.makeText(RegisterActivity.this, "Missing Required Fields", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    public void onTaskFinish(String data) {
        try {
            JSONObject json = new JSONObject(data);
            if(json.getInt("signup_error") == 0) {
                new AlertDialog.Builder(RegisterActivity.this)
                        .setTitle("Success")
                        .setMessage("An email was sent containing instructions to confirm your new BFC account.")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Save presets
                                SharedPreferences settings = getSharedPreferences(((Global)getApplication()).getPrefsName(), 0);
                                SharedPreferences.Editor editor = settings.edit();
                                editor.putString("LoginUserName", Email);
                                editor.putString("LoginPassword", Password);
                                editor.putBoolean("RememberMeSelect", true);
                                editor.commit();

                                //Navigate to Login
                                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                finish();

                                dialog.dismiss();
                            }
                        })
                        .show();
            }
            else {
                if(!json.isNull("user_already_exists") && json.getInt("user_already_exists") > 0) {
                    Toast.makeText(this, "Email Already Registered, Please Log In.", Toast.LENGTH_LONG).show();
                } else if(!json.isNull("email_result") && json.getInt("email_result") > 0) {
                    Toast.makeText(this, "Confirmation Email Failed To Send, Please Try Again Later.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "Unknown Error Occurred, Please Try Again.", Toast.LENGTH_LONG).show();
                }
            }
        }
        catch(Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Problem retrieving data", Toast.LENGTH_LONG).show();
        }

        //Dismiss Spinner
        if(spinner != null && spinner.isShowing()) {
            spinner.dismiss();
        }
    }
}