package com.vie.biddingforcharities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends Activity {
    Button LoginButton, RegisterButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        LoginButton = (Button) findViewById(R.id.login_button);
        RegisterButton = (Button) findViewById(R.id.register_button);

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
                String password = ((EditText) findViewById(R.id.password_input)).getText().toString();
                String confirm = ((EditText) findViewById(R.id.confirm_input)).getText().toString();

                if(password == confirm) {
                    Toast.makeText(RegisterActivity.this, "Passwords Do Not Match", Toast.LENGTH_LONG).show();
                } else {
                    startActivity(new Intent(RegisterActivity.this, HomeActivity.class));
                    finish();
                }
            }
        });
    }
}