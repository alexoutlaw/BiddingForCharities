package com.vie.biddingforcharities;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.Button;

public class LoginActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        /*Button loginButton = (Button) findViewById(R.id.buttonLogin);
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/myriadpro_reg.otf");
        loginButton.setTypeface(font);*/
    }
}
