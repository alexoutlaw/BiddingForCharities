package com.vie.biddingforcharities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

public class MainActivity extends Activity {
    public static final String PREFS_NAME = "General_Prefs";

    Button LoginButton, RegisterButton;
    CheckBox RememberMeSelect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

        LoginButton = (Button) findViewById(R.id.login_button);
        RegisterButton = (Button) findViewById(R.id.register_button);
        RememberMeSelect = (CheckBox) findViewById(R.id.remember_me_select);

        //Set saved "Remember Me" choice
        RememberMeSelect.setChecked(settings.getBoolean("RememberMeSelect", false));

        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Save "Remember Me" choice
                SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean("RememberMeSelect", RememberMeSelect.isChecked());
                editor.commit();

                //Navigate to Login
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            }
        });

        /*RegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, RegisterActivity.class));
            }
        });*/
    }
}
