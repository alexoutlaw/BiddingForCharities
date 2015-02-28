package com.vie.biddingforcharities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.vie.biddingforcharities.logic.GetInfoTask;
import com.vie.biddingforcharities.logic.Utilities;

import org.json.JSONObject;

public class CharityRequestActivity extends Activity {
    DrawerLayout NavLayout;
    ListView NavList;
    Button HomeButton, RequestButton;
    ImageButton NavDrawerButton;

    ProgressDialog spinner;

    EditText EmailInput, NameInput, PhoneInput, CompanyInput, AddressInput, CityInput, StateInput, ZipInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charityrequest);

        NavLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavList = (ListView) findViewById(R.id.navList);
        NavDrawerButton = (ImageButton) findViewById(R.id.nav_drawer_expand);
        EmailInput = (EditText) findViewById(R.id.email_input);
        NameInput = (EditText) findViewById(R.id.name_input);
        PhoneInput = (EditText) findViewById(R.id.phone_input);
        CompanyInput = (EditText) findViewById(R.id.company_input);
        AddressInput = (EditText) findViewById(R.id.address_input);
        CityInput = (EditText) findViewById(R.id.city_input);
        StateInput = (EditText) findViewById(R.id.state_input);
        ZipInput = (EditText) findViewById(R.id.zip_input);

        // Build Side Nav Menu
        ((Global) getApplication()).BuildNavigationMenu(NavList);
        NavDrawerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavLayout.openDrawer(Gravity.LEFT);
            }
        });

        // Build Top Nav Menu
        HomeButton = (Button) findViewById(R.id.home_button);
        HomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

        RequestButton = (Button) findViewById(R.id.request_button);
        RequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Show Spinner
                spinner = new ProgressDialog(CharityRequestActivity.this);
                spinner.setMessage("Registering...");
                spinner.setCanceledOnTouchOutside(false);
                spinner.show();

                //Register with Server
                String queryStr = Utilities.BuildQueryParams(
                        new String[][]{
                                new String[]{"email", EmailInput.getText().toString()},
                                new String[]{"name", NameInput.getText().toString()},
                                new String[]{"phone", PhoneInput.getText().toString()},
                                new String[]{"company", CompanyInput.getText().toString()},
                                new String[]{"address", AddressInput.getText().toString()},
                                new String[]{"city", CityInput.getText().toString()},
                                new String[]{"state", StateInput.getText().toString()},
                                new String[]{"zip", ZipInput.getText().toString()}
                        });
                new GetInfoTask(CharityRequestActivity.this).execute("requestCharity", queryStr);
            }
        });
    }

    public void onTaskFinish(String data) {
        //Dismiss Spinner
        if(spinner != null && spinner.isShowing()) {
            spinner.dismiss();
        }

        try {
            //Deserialize
            JSONObject json = new JSONObject(data);
            int email_result = json.getInt("email_result");
            if(email_result > 0) {
                new AlertDialog.Builder(this)
                        .setTitle("Success!")
                        .setMessage("Charity Request Successfully Submitted!")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startActivity(new Intent(CharityRequestActivity.this, HomeActivity.class));
                                finish();
                            }
                        })
                        .show();
            }
            else {
                Toast.makeText(this, "ERROR: Could Not Send Request", Toast.LENGTH_LONG).show();
            }
        }
        catch(Exception e) {
            e.printStackTrace();
            Toast.makeText(this, getResources().getString(R.string.generic_error), Toast.LENGTH_LONG).show();
        }
    }
}
