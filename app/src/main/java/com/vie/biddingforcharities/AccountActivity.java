package com.vie.biddingforcharities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.vie.biddingforcharities.logic.GetInfoTask;
import com.vie.biddingforcharities.logic.User;
import com.vie.biddingforcharities.logic.Utilities;
import com.vie.biddingforcharities.ui.AccountPageAdapter;
import com.vie.biddingforcharities.ui.ZoomPageTransformer;

import org.json.JSONObject;

public class AccountActivity extends FragmentActivity implements AccountNameFragment.SubmitNameUpdateListener
        , AccountUsernameFragment.SubmitUsernameUpdateListener
        , AccountEmailFragment.SubmitEmailUpdateListener
        , AccountAddressFragment.SubmitAddressUpdateListener {
    DrawerLayout NavLayout;
    ListView NavList;
    Button HomeButton;
    ImageButton NavDrawerButton;
    ViewPager Pager;
    AccountPageAdapter Adapter;

    String tempFirstName, tempLastName, tempUserName, tempEmail;

    ProgressDialog spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        NavLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavList = (ListView) findViewById(R.id.navList);
        NavDrawerButton = (ImageButton) findViewById(R.id.nav_drawer_expand);
        Pager = (ViewPager) findViewById(R.id.pager);

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

        // Build SwipePager
        Adapter = new AccountPageAdapter(getSupportFragmentManager(), this);
        Pager.setAdapter(Adapter);
        Pager.setPageTransformer(true, new ZoomPageTransformer());
        PagerSlidingTabStrip PagerTabString = (PagerSlidingTabStrip) findViewById(R.id.pager_tab_strip);
        PagerTabString.setAllCaps(false);
        PagerTabString.setIndicatorColorResource(R.color.white);
        PagerTabString.setIndicatorHeight(4);
        PagerTabString.setViewPager(Pager);
    }

    @Override
    public void SubmitNameUpdate(String firstName, String lastName) {
        // Show Spinner
        spinner = new ProgressDialog(this);
        spinner.setMessage("Updating Name...");
        spinner.setCanceledOnTouchOutside(false);
        spinner.show();

        // save now so we can update local User if success
        tempFirstName = firstName;
        tempLastName = lastName;

        // Start update call
        User user = ((Global)getApplication()).getUser();
        String queryStr = Utilities.BuildQueryParams(
                new String[][]{
                        new String[]{"user_guid", user.getUserGuid()},
                        new String[]{"user_id", String.valueOf(user.getUserID())},
                        new String[]{"mode", "1"},
                        new String[]{"first", firstName},
                        new String[]{"last", lastName},
                });
        new GetInfoTask(this).execute("updateAccountName", queryStr);
    }

    public void onNameTaskFinish(String data) {
        try {
            //Deserialize
            JSONObject json = new JSONObject(data);
            int updated = json.getInt("was_updated");
            if(updated > 0) {
                // Success, save and continue
                Toast.makeText(this, "Account Name Successfully Updated!", Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(this, "ERROR: Could Not Update Account Name", Toast.LENGTH_LONG).show();
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

    @Override
    public void SubmitUsernameUpdate(String userName) {
        // Show Spinner
        spinner = new ProgressDialog(this);
        spinner.setMessage("Updating Username...");
        spinner.setCanceledOnTouchOutside(false);
        spinner.show();

        // save now so we can update local User if success
        tempUserName = userName;

        // Start update call
        User user = ((Global)getApplication()).getUser();
        String queryStr = Utilities.BuildQueryParams(
                new String[][]{
                        new String[]{"user_guid", user.getUserGuid()},
                        new String[]{"user_id", String.valueOf(user.getUserID())},
                        new String[]{"mode", "4"},
                        new String[]{"user_name", userName},
                        new String[]{"user_name_id", String.valueOf(user.getUserID())},
                });
        new GetInfoTask(this).execute("updateAccountUsername", queryStr);
    }

    public void onUsernameTaskFinish(String data) {
        try {
            //Deserialize
            JSONObject json = new JSONObject(data);
            int updated = json.getInt("user_name_id");
            if(updated > 0) {
                // Success, save and continue
                ((Global)getApplication()).getUser().updateUserName(tempUserName, updated);
                Toast.makeText(this, "Username Successfully Updated!", Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(this, "ERROR: Could Not Update Username", Toast.LENGTH_LONG).show();
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

    @Override
    public void SubmitEmailUpdate(String newEmail, String password) {
        // Show Spinner
        spinner = new ProgressDialog(this);
        spinner.setMessage("Updating Email...");
        spinner.setCanceledOnTouchOutside(false);
        spinner.show();

        // save now so we can update local User if success
        tempEmail = newEmail;

        // Start update call
        User user = ((Global)getApplication()).getUser();
        String queryStr = Utilities.BuildQueryParams(
                new String[][]{
                        new String[]{"user_guid", user.getUserGuid()},
                        new String[]{"user_id", String.valueOf(user.getUserID())},
                        new String[]{"mode", "2"},
                        new String[]{"new_email", newEmail},
                        new String[]{"pwd", password},
                });
        new GetInfoTask(this).execute("updateAccountEmail", queryStr);
    }

    public void onEmailTaskFinish(String data) {
        try {
            //Deserialize
            JSONObject json = new JSONObject(data);
            int userAlreadyExists = json.getInt("user_already_exists");
            if(userAlreadyExists > 0) {
                int updated = json.getInt("was_updated");
                if(updated > 0) {
                    // Success, save and continue
                    ((Global)getApplication()).getUser().updateEmail(tempEmail);
                    Toast.makeText(this, "Email Successfully Updated!", Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(this, "ERROR: Could Not Update Email", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(this, "ERROR: Email Is Already In Use", Toast.LENGTH_LONG).show();
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

    @Override
    public void SubmitAddressUpdate(String email, String company, String first, String last, String street1, String street2, String country, String city, String state, String zip, String phone) {
        // Show Spinner
        spinner = new ProgressDialog(this);
        spinner.setMessage("Updating Address...");
        spinner.setCanceledOnTouchOutside(false);
        spinner.show();

        // Start update call
        User user = ((Global)getApplication()).getUser();
        String queryStr = Utilities.BuildQueryParams(
                new String[][]{
                        new String[]{"user_guid", user.getUserGuid()},
                        new String[]{"user_id", String.valueOf(user.getUserID())},
                        new String[]{"mode", "1"},
                        new String[]{"address_email", email},
                        new String[]{"address_company", company},
                        new String[]{"address_first", first},
                        new String[]{"address_last", last},
                        new String[]{"address_street", street1},
                        new String[]{"address_street2", street2},
                        new String[]{"address_city", city},
                        new String[]{"address_state", state},
                        new String[]{"address_zip", zip},
                        new String[]{"address_country", country},
                        new String[]{"address_phone", phone}
                });
        new GetInfoTask(this).execute("updateAccountAddress", queryStr);
    }

    public void onAddressTaskFinish(String data) {
        try {
            //Deserialize
            JSONObject json = new JSONObject(data);
            int updated = json.getInt("address_id");
            if(updated > 0) {
                // Success, save and continue
                ((Global)getApplication()).getUser().updateAddressId(updated);
                Toast.makeText(this, "Address Successfully Updated!", Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(this, "ERROR: Could Not Update Address", Toast.LENGTH_LONG).show();
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
