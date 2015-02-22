package com.vie.biddingforcharities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.vie.biddingforcharities.logic.GetInfoTask;
import com.vie.biddingforcharities.logic.User;
import com.vie.biddingforcharities.logic.Utilities;

import org.json.JSONObject;

public class LoginActivity extends Activity {
    Button LoginButton, RegisterButton;
    EditText UserNameInput, PasswordInput;
    TextView ForgetPassword;
    CheckBox RememberMeSelect;

    ProgressDialog spinner;

    String UserName, Password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        SharedPreferences settings = getSharedPreferences(((Global)getApplication()).getPrefsName(), 0);

        LoginButton = (Button) findViewById(R.id.login_button);
        RegisterButton = (Button) findViewById(R.id.register_button);
        UserNameInput = (EditText) findViewById(R.id.uname_input);
        PasswordInput = (EditText) findViewById(R.id.pass_input);
        ForgetPassword = (TextView) findViewById(R.id.login_forget);
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
                UserName = UserNameInput.getText().toString();
                Password = PasswordInput.getText().toString();
                String queryStr = Utilities.BuildQueryParams(
                        new String[][]{
                                new String[]{"u", UserName},
                                new String[]{"p", Password}
                        });
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

        ForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to Website
                String url = "http://www.biddingforcharities.com/forgot.php";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
    }

    public void onTaskFinish(String data) {
        try {
            //Check if valid
            JSONObject json = new JSONObject(data);
            int loggedIn = json.getInt("login");
            if(loggedIn > 0) {
                //Deserialize
                String user_email = json.getString("Email");
                int user_id = json.getInt("UserId");
                String user_guid = json.getString("UserGuid");
                int user_type = json.getInt("UserType");
                int user_name_id = json.getInt("UserNameId");
                String user_name = json.getString("UserName");
                int user_address_id = json.getInt("AddressId");
                boolean user_has_invoice = Boolean.getBoolean(json.getString("HasSellerInvoiceDefaults"));

                //Store User
                ((Global) getApplication()).setUser(new User (user_email, user_id, user_guid, user_type, user_name_id, user_name, user_address_id, user_has_invoice));

                boolean remember = RememberMeSelect.isChecked();
                if(remember) {
                    //Save choice
                    SharedPreferences settings = getSharedPreferences(((Global)getApplication()).getPrefsName(), 0);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("LoginUserName", UserName);
                    editor.putString("LoginPassword", Password);
                    editor.putBoolean("RememberMeSelect", remember);
                    editor.commit();
                }

                //Navigate to Home
                startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                finish();
            }
            else {
                Toast.makeText(this, "Invalid Login", Toast.LENGTH_LONG).show();
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
