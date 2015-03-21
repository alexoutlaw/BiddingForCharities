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
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.vie.biddingforcharities.logic.GetInfoTask;
import com.vie.biddingforcharities.logic.Pair;
import com.vie.biddingforcharities.logic.User;
import com.vie.biddingforcharities.logic.Utilities;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class AuctionFormActivity extends Activity {
    DrawerLayout NavLayout;
    ListView NavList;
    Button HomeButton;
    ImageButton NavDrawerButton;

    ProgressDialog spinner;

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

        // Get Cache, if not cached, go pull
        // Any missing items must be handled by the user immediately
        User user = ((Global) getApplication()).getUser();

        Categories = user.getCategories();
        if (Categories == null) {
            // Show Spinner
            spinner = new ProgressDialog(AuctionFormActivity.this);
            spinner.setMessage("Getting Categories...");
            spinner.setCanceledOnTouchOutside(false);
            spinner.show();

            // Load Categories
            String queryStr = Utilities.BuildQueryParams(
                    new String[][]{
                            new String[]{"user_guid", user.getUserGuid()},
                            new String[]{"user_id", String.valueOf(user.getUserID())},
                            new String[]{"mode", "0"}
                    });
            new GetInfoTask(AuctionFormActivity.this).execute(GetInfoTask.SourceType.updateSellerCategories.toString(), queryStr);
        } else {
            checkForExistingCategory();
        }

        Folders = user.getFolders();
        if (Folders == null) {
            // Show Spinner
            spinner = new ProgressDialog(AuctionFormActivity.this);
            spinner.setMessage("Getting Folders...");
            spinner.setCanceledOnTouchOutside(false);
            spinner.show();

            // Load Folders
            String queryStr = Utilities.BuildQueryParams(
                    new String[][]{
                            new String[]{"user_guid", user.getUserGuid()},
                            new String[]{"user_id", String.valueOf(user.getUserID())},
                            new String[]{"mode", "0"}
                    });
            new GetInfoTask(AuctionFormActivity.this).execute(GetInfoTask.SourceType.updateSellerFolders.toString(), queryStr);
        } else {
            checkForExistingFolder();
        }

        ReturnPolicies = user.getReturnPolicies();
        if (ReturnPolicies == null) {
            //TODO
        } else {
            checkForExistingReturnPolicy();
        }

        // not implemented
        //Consignors = user.getConsignors();
        //PaymentPolicies = user.getPaymentPolicies();
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

    public void checkForExistingCategory() {
        if (Categories == null || Categories.size() < 1) {
            new AlertDialog.Builder(this)
                    .setCancelable(false)
                    .setTitle("Missing Category")
                    .setMessage("You must add a Category before creating auctions")
                    .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(AuctionFormActivity.this, SellerCategoriesActivity.class));
                        }
                    })
                    .show();
        }
    }

    public void onCategoryTaskFinish(String data) {
        try {
            //Deserialize
            JSONObject json = new JSONObject(data);
            JSONArray cats = (JSONArray) json.get("cats");

            Categories = new ArrayList<>();
            for (int i = 0; i < cats.length(); i++) {
                final JSONObject cat = (JSONObject) cats.get(i);
                int cat_id = cat.getInt("cat_id");
                String cat_name = cat.getString("cat_name");

                Categories.add(new Pair(cat_name, cat_id));
            }

            // Update Cache
            ((Global) getApplication()).getUser().updateCategories(Categories);

            checkForExistingCategory();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, getResources().getString(R.string.generic_error), Toast.LENGTH_LONG).show();
        }

        //Dismiss Spinner
        if (spinner != null && spinner.isShowing()) {
            spinner.dismiss();
        }
    }

    public void checkForExistingFolder() {
        if (Folders == null || Folders.size() < 1) {
            new AlertDialog.Builder(this)
                    .setCancelable(false)
                    .setTitle("Missing Folder")
                    .setMessage("You must add a Folder before creating auctions")
                    .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(AuctionFormActivity.this, SellerFolderActivity.class));
                        }
                    })
                    .show();
        }
    }

    public void onFolderTaskFinish(String data) {
        try {
            //Deserialize
            JSONObject json = new JSONObject(data);
            JSONArray folders = (JSONArray) json.get("folders");

            Folders = new ArrayList<>();
            for(int i = 0; i < folders.length(); i++) {
                final JSONObject folder = (JSONObject) folders.get(i);
                int folder_id = folder.getInt("folder_id");
                String crumb = folder.getString("crumb");

                Folders.add(new Pair(crumb, folder_id));
            }

            // Update Cache
            ((Global) getApplication()).getUser().updateFolders(Folders);

            checkForExistingFolder();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, getResources().getString(R.string.generic_error), Toast.LENGTH_LONG).show();
        }

        //Dismiss Spinner
        if (spinner != null && spinner.isShowing()) {
            spinner.dismiss();
        }
    }

    public void checkForExistingReturnPolicy() {
        if (ReturnPolicies == null) {
            new AlertDialog.Builder(this)
                    .setCancelable(false)
                    .setTitle("Missing Return Policy")
                    .setMessage("You must add a Return Policy before creating auctions")
                    .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(AuctionFormActivity.this, SellerReturnPolicyActivity.class));
                        }
                    })
                    .show();
        }
    }

    public void onReturnTaskFinish(String data) {
        try {
            //Deserialize
            JSONObject json = new JSONObject(data);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, getResources().getString(R.string.generic_error), Toast.LENGTH_LONG).show();
        }
    }

    public void onConsignorTaskFinish(String data) {
        try {
            //Deserialize
            JSONObject json = new JSONObject(data);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, getResources().getString(R.string.generic_error), Toast.LENGTH_LONG).show();
        }
    }

    public void onPaymentTaskFinish(String data) {
        try {
            //Deserialize
            JSONObject json = new JSONObject(data);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, getResources().getString(R.string.generic_error), Toast.LENGTH_LONG).show();
        }
    }

    public void onUpdateTaskFinish(String data) {
        try {
            //Deserialize
            JSONObject json = new JSONObject(data);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, getResources().getString(R.string.generic_error), Toast.LENGTH_LONG).show();
        }
    }
}
