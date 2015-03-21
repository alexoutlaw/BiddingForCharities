package com.vie.biddingforcharities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.vie.biddingforcharities.logic.Pair;
import com.vie.biddingforcharities.logic.User;

import org.json.JSONObject;

import java.util.ArrayList;

public class AuctionFormActivity extends Activity {
    DrawerLayout NavLayout;
    ListView NavList;
    Button HomeButton;
    ImageButton NavDrawerButton;

    ArrayList<Pair> Categories = new ArrayList<>();
    ArrayList<Pair> Folders = new ArrayList<>();
    ArrayList<Pair> Consignors = new ArrayList<>();
    ArrayList<Pair> ReturnPolicies = new ArrayList<>();
    ArrayList<Pair> PaymentPolicies = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auctionform);

        NavLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavList = (ListView) findViewById(R.id.navList);
        NavDrawerButton = (ImageButton) findViewById(R.id.nav_drawer_expand);

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
    }

    @Override
    public void onResume() {
        super.onResume();

        // Get Cache
        User user = ((Global) getApplication()).getUser();
        Categories = null;//user.getCategories();
        Folders = user.getFolders();
        Consignors = user.getConsignors();
        ReturnPolicies = user.getReturnPolicies();
        PaymentPolicies = user.getPaymentPolicies();

        // If no Cache, grab from mobile call
        if(Categories == null) {
            new AlertDialog.Builder(this)
                    .setTitle("Missing Category")
                    .setMessage("You must add a category before creating auctions")
                    .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(AuctionFormActivity.this, SellerCategoriesActivity.class));
                        }
                    })
                    .show();
        }
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Closing...")
                .setMessage("Reset Item Form?")
                .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Clear data, then exit
                        AuctionFormActivity.this.finish();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    public void onCategoryTaskFinish(String data) {
        try {
            //Deserialize
            JSONObject json = new JSONObject(data);
        }
        catch(Exception e) {
            e.printStackTrace();
            Toast.makeText(this, getResources().getString(R.string.generic_error), Toast.LENGTH_LONG).show();
        }
    }

    public void onFolderTaskFinish(String data) {
        try {
            //Deserialize
            JSONObject json = new JSONObject(data);
        }
        catch(Exception e) {
            e.printStackTrace();
            Toast.makeText(this, getResources().getString(R.string.generic_error), Toast.LENGTH_LONG).show();
        }
    }

    public void onConsignorTaskFinish(String data) {
        try {
            //Deserialize
            JSONObject json = new JSONObject(data);
        }
        catch(Exception e) {
            e.printStackTrace();
            Toast.makeText(this, getResources().getString(R.string.generic_error), Toast.LENGTH_LONG).show();
        }
    }

    public void onReturnTaskFinish(String data) {
        try {
            //Deserialize
            JSONObject json = new JSONObject(data);
        }
        catch(Exception e) {
            e.printStackTrace();
            Toast.makeText(this, getResources().getString(R.string.generic_error), Toast.LENGTH_LONG).show();
        }
    }

    public void onPaymentTaskFinish(String data) {
        try {
            //Deserialize
            JSONObject json = new JSONObject(data);
        }
        catch(Exception e) {
            e.printStackTrace();
            Toast.makeText(this, getResources().getString(R.string.generic_error), Toast.LENGTH_LONG).show();
        }
    }

    public void onUpdateTaskFinish(String data) {
        try {
            //Deserialize
            JSONObject json = new JSONObject(data);
        }
        catch(Exception e) {
            e.printStackTrace();
            Toast.makeText(this, getResources().getString(R.string.generic_error), Toast.LENGTH_LONG).show();
        }
    }
}
