package com.vie.biddingforcharities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.vie.biddingforcharities.netcode.GetInfoTask;

import org.json.JSONObject;

public class LoginActivity extends Activity {
    public static final String PREFS_NAME = "General_Prefs";

    Button LoginButton, RegisterButton;
    EditText UserNameInput, PasswordInput;
    CheckBox RememberMeSelect;

    ProgressDialog spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

        LoginButton = (Button) findViewById(R.id.login_button);
        RegisterButton = (Button) findViewById(R.id.register_button);
        UserNameInput = (EditText) findViewById(R.id.uname_input);
        PasswordInput = (EditText) findViewById(R.id.pass_input);
        RememberMeSelect = (CheckBox) findViewById(R.id.remember_me_select);

        //Set saved "Remember Me"
        if(settings.getBoolean("RememberMeSelect", false)) {
            UserNameInput.setText(settings.getString("LoginUserName", ""));
            PasswordInput.setText(settings.getString("LoginPassword", ""));
            RememberMeSelect.setChecked(true);
        }

        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Show Spinner
                spinner = new ProgressDialog(LoginActivity.this);
                spinner.setMessage("Logging In...");
                spinner.setCanceledOnTouchOutside(false);
                spinner.show();

                //Check Login
                String uname = UserNameInput.getText().toString();
                String pass = PasswordInput.getText().toString();
                String queryStr = "?u=" + uname + "&p=" + pass;
                new GetInfoTask(LoginActivity.this).execute("checkLogin", queryStr);
            }
        });

        RegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                finish();
            }
        });
    }

    public void onTaskFinish(GetInfoTask task, String data) {
        try {
            //Deserialize
            JSONObject json = new JSONObject(data);
            int UserId = json.getInt("UserId");
            if(UserId > 0) {
                String uname = UserNameInput.getText().toString();
                String pass = PasswordInput.getText().toString();
                boolean remember = RememberMeSelect.isChecked();

                if(remember) {
                    //Save choice
                    SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("LoginUserName", uname);
                    editor.putString("LoginPassword", pass);
                    editor.putBoolean("RememberMeSelect", remember);
                    editor.commit();
                }

                //TODO: save user vars

                //Navigate to Home
                startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                finish();
            }
            else {
                Toast.makeText(this, "Invalid Login", Toast.LENGTH_LONG);
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
