package com.vie.biddingforcharities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.vie.biddingforcharities.netcode.GetInfoTask;

import org.json.JSONObject;

public class RegisterActivity extends Activity {
    Button LoginButton, RegisterButton;
    EditText EmailInput, PassInput, ConfirmInput, FirstInput, LastInput;

    ProgressDialog spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        LoginButton = (Button) findViewById(R.id.login_button);
        RegisterButton = (Button) findViewById(R.id.register_button);
        EmailInput = (EditText) findViewById(R.id.email_input);
        PassInput = (EditText) findViewById(R.id.pass_input);
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
                String password = PassInput.getText().toString();
                String confirm = ConfirmInput.getText().toString();

                if(password == confirm) {
                    Toast.makeText(RegisterActivity.this, "Passwords Do Not Match", Toast.LENGTH_LONG).show();
                } else {
                    //Show Spinner
                    spinner = new ProgressDialog(RegisterActivity.this);
                    spinner.setMessage("Registering...");
                    spinner.setCanceledOnTouchOutside(false);
                    spinner.show();

                    //Register with Server
                    String email = EmailInput.getText().toString();
                    String first = FirstInput.getText().toString();
                    String last = LastInput.getText().toString();
                    String queryStr = "?email=" + email + "&pwd=" + password + " &first" + first + "&last" + last;
                    new GetInfoTask(RegisterActivity.this).execute("registerUser", queryStr);
                }
            }
        });
    }

    public void onTaskFinish(GetInfoTask task, String data) {
        try {
            JSONObject json = new JSONObject(data);
            int error = json.getInt("signup_error");
            if(error > 0) {
                //TODO: send user to email

                //Navigate to Home
                startActivity(new Intent(RegisterActivity.this, HomeActivity.class));
                finish();
            }
            else {
                //TODO: check for specific error
                Toast.makeText(this, "Invalid Login", Toast.LENGTH_LONG);
            }
        }
        catch(Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Problem retrieving data", Toast.LENGTH_LONG).show();
        }
    }
}