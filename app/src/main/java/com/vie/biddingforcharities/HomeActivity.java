package com.vie.biddingforcharities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;

public class HomeActivity extends Activity {
    DrawerLayout NavLayout;
    ListView NavList;
    ImageButton NavDrawerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

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
    }

    //Adds a second button click to fully exit the app
    //prevents accident sign outs
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Signing Out...")
                .setMessage("You Are About To Exit")
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
}
