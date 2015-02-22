package com.vie.biddingforcharities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.vie.biddingforcharities.logic.GetInfoTask;
import com.vie.biddingforcharities.logic.User;
import com.vie.biddingforcharities.logic.Utilities;

import org.json.JSONObject;

public class HomeActivity extends Activity {
    DrawerLayout NavLayout;
    ListView NavList;
    ImageButton NavDrawerButton;
    Button ItemCheckoutButton, PayInvoiceButton, ReadMessageButton, ItemsBidButton;
    TextView WelcomeBanner;

    ProgressDialog spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        NavLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavList = (ListView) findViewById(R.id.navList);
        NavDrawerButton = (ImageButton) findViewById(R.id.nav_drawer_expand);
        ItemCheckoutButton = (Button) findViewById(R.id.items_to_checkout);
        PayInvoiceButton = (Button) findViewById(R.id.invoices_to_pay);
        ReadMessageButton = (Button) findViewById(R.id.messages_to_read);
        ItemsBidButton = (Button) findViewById(R.id.bidding_on_items);
        WelcomeBanner = (TextView) findViewById(R.id.welcome_message);

        // Build Side Nav Menu
        ((Global) getApplication()).BuildNavigationMenu(NavList);
        NavDrawerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavLayout.openDrawer(Gravity.LEFT);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        // Set Top banner
        User user = ((Global)getApplication()).getUser();
        WelcomeBanner.setText("Welcome to Bidding For Charities, " + user.getUserName() + "!");

        // Show Spinner
        spinner = new ProgressDialog(HomeActivity.this);
        spinner.setMessage("Getting Updates...");
        spinner.setCanceledOnTouchOutside(false);
        spinner.show();
    }

    @Override
    public void onResume() {
        super.onResume();

        // Update Tiles
        User user = ((Global)getApplication()).getUser();
        String queryStr = Utilities.BuildQueryParams(
                new String[][]{
                        new String[]{"user_guid", user.getUserGuid()}
                });
        new GetInfoTask(HomeActivity.this).execute("getWelcome", queryStr);
    }

    @Override
    public void onDestroy() {
        // HomeActivity should always be on the stack if user is logged in, chain destroy up to Application
        ((Global) getApplication()).onDestroy();
        super.onDestroy();
    }

    //Adds a second button click to fully exit the app
    //prevents accidental sign outs
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Signing Out...")
                .setMessage("Sign Out of Bidding For Charities?")
                .setPositiveButton("Sign Out", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Clear data, then exit
                        HomeActivity.this.finish();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    public void onTaskFinish(String data) {
        try {
            //Deserialize
            JSONObject json = new JSONObject(data);
            boolean HasItemsToCheckout = json.getInt("HasItemsToCheckout") > 0;
            boolean HasInvoicesToPay = json.getInt("HasInvoicesToPay") > 0;
            boolean HasMessageToRead = json.getInt("HasMessageToRead") > 0;
            boolean IsBiddingOnItems = json.getInt("IsBiddingOnItems") > 0;

            if(HasItemsToCheckout) {
                ItemCheckoutButton.setBackgroundResource(R.drawable.tinted_glass_button);
                ItemCheckoutButton.setText(R.string.home_items_to_checkout_positive);
                ItemCheckoutButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Navigate to Website
                        String url = "http://www.biddingforcharities.com/ended_items_create_invoices.php";
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        startActivity(i);
                    }
                });
            }
            else {
                ItemCheckoutButton.setBackgroundResource(R.drawable.glass_button);
                ItemCheckoutButton.setText(R.string.home_items_to_checkout_negative);
                ItemCheckoutButton.setClickable(false);
            }

            if(HasInvoicesToPay) {
                PayInvoiceButton.setBackgroundResource(R.drawable.tinted_glass_button);
                PayInvoiceButton.setText(R.string.home_invoice_to_pay_positive);
                PayInvoiceButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Navigate to Website
                        String url = "http://www.biddingforcharities.com/invoice_search.php";
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        startActivity(i);
                    }
                });
            }
            else {
                PayInvoiceButton.setBackgroundResource(R.drawable.glass_button);
                PayInvoiceButton.setText(R.string.home_invoice_to_pay_negative);
                PayInvoiceButton.setClickable(false);
            }

            if(HasMessageToRead) {
                ReadMessageButton.setBackgroundResource(R.drawable.tinted_glass_button);
                ReadMessageButton.setText(R.string.home_messages_to_read_positive);
                ReadMessageButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Navigate to Website
                        String url = "http://www.biddingforcharities.com/contact.php";
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        startActivity(i);
                    }
                });
            }
            else {
                ReadMessageButton.setBackgroundResource(R.drawable.glass_button);
                ReadMessageButton.setText(R.string.home_messages_to_read_negative);
                ReadMessageButton.setClickable(false);
            }

            if(IsBiddingOnItems) {
                ItemsBidButton.setBackgroundResource(R.drawable.tinted_glass_button);
                ItemsBidButton.setText(R.string.home_bidding_on_items_positive);
                ItemsBidButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //TODO: Navigate to My Bids
                        //startActivity(new Intent(HomeActivity.this, BidListActivity.class));
                        Toast.makeText(HomeActivity.this, "My Bids, Coming Soon!", Toast.LENGTH_LONG).show();
                    }
                });
            }
            else {
                ItemsBidButton.setBackgroundResource(R.drawable.glass_button);
                ItemsBidButton.setText(R.string.home_bidding_on_items_negative);
                ItemsBidButton.setClickable(false);
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
