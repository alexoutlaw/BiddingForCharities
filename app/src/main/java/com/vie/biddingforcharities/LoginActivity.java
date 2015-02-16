package com.vie.biddingforcharities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity {
    public static final String PREFS_NAME = "General_Prefs";

    Button LoginButton, RegisterButton;
    EditText UserNameInput, PasswordInput;
    CheckBox RememberMeSelect;

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

                //Navigate to Home
                startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                finish();
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
}
