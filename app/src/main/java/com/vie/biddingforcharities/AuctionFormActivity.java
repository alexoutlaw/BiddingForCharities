package com.vie.biddingforcharities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.fourmob.datetimepicker.date.DatePickerDialog.OnDateSetListener;
import com.sleepbot.datetimepicker.time.RadialPickerLayout;
import com.sleepbot.datetimepicker.time.TimePickerDialog;
import com.sleepbot.datetimepicker.time.TimePickerDialog.OnTimeSetListener;
import com.vie.biddingforcharities.logic.GetInfoTask;
import com.vie.biddingforcharities.logic.Pair;
import com.vie.biddingforcharities.logic.Trio;
import com.vie.biddingforcharities.logic.User;
import com.vie.biddingforcharities.logic.Utilities;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AuctionFormActivity extends FragmentActivity implements OnDateSetListener, OnTimeSetListener {
    DrawerLayout NavLayout;
    ListView NavList;
    Button HomeButton;
    ImageButton NavDrawerButton;

    ProgressDialog editSpinner, categorySpinner, folderSpinner, policySpinner, updateSpinner;
    EditText TitleText, DescriptionText, SkuText, InvQuantityText, AucQuantityText, MinBidText, AucReserveText, ShippingText, ShippingAddText;
    TextView StartDateText, StartTimeText, EndDateText, EndTimeText;
    Button SubmitButton;

    Spinner FolderSpinner, CategoryOneSpinner, CategoryTwoSpinner, ReturnPolicySpinner;

    ArrayList<Pair> Categories = new ArrayList<>();
    ArrayList<Pair> Folders = new ArrayList<>();
    ArrayList<Trio> ReturnPolicies = new ArrayList<>();
    //ArrayList<Pair> Consignors = new ArrayList<>();
    //ArrayList<Pair> PaymentPolicies = new ArrayList<>();

    int ItemID;
    int StartYear, StartMonth, StartDay, StartHour, StartMinute
            , EndYear, EndMonth, EndDay, EndHour, EndMinute;
    String lastTimePickerOpened;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auctionform);

        // RESTRICTED: seller only
        if (((Global) getApplication()).getUser().getUserType() == User.UserTypes.STANDARD) {
            Toast.makeText(this, "Access Restricted to Sellers only, please submit a request to become a Seller.", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, CharityRequestActivity.class));
            finish();
        }

        // Prevent automatic keyboard
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        NavLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavList = (ListView) findViewById(R.id.navList);
        NavDrawerButton = (ImageButton) findViewById(R.id.nav_drawer_expand);
        TitleText = (EditText) findViewById(R.id.create_title);
        DescriptionText = (EditText) findViewById(R.id.create_description);
        FolderSpinner = (Spinner) findViewById(R.id.create_folder);
        SkuText = (EditText) findViewById(R.id.create_sku);
        InvQuantityText = (EditText) findViewById(R.id.create_inventory_quantity);
        CategoryOneSpinner = (Spinner) findViewById(R.id.create_category_one);
        CategoryTwoSpinner = (Spinner) findViewById(R.id.create_category_two);
        AucQuantityText = (EditText) findViewById(R.id.create_auction_quantity);
        MinBidText = (EditText) findViewById(R.id.create_min_bid);
        AucReserveText = (EditText) findViewById(R.id.create_auction_reserve);
        ShippingText = (EditText) findViewById(R.id.create_shipping);
        ShippingAddText = (EditText) findViewById(R.id.create_shipping_add);
        ReturnPolicySpinner = (Spinner) findViewById(R.id.create_return_policy);
        StartDateText = (TextView) findViewById(R.id.create_start_date);
        StartTimeText = (TextView) findViewById(R.id.create_start_time);
        EndDateText = (TextView) findViewById(R.id.create_end_date);
        EndTimeText = (TextView) findViewById(R.id.create_end_time);
        SubmitButton = (Button) findViewById(R.id.create_button);

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

        // Date/Time Picker Stuff
        final Calendar calendar = Calendar.getInstance();
        final DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        final TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(this, 0, 0, false);
        StartDateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.setVibrate(false);
                datePickerDialog.setYearRange(2015, 2030);
                datePickerDialog.setCloseOnSingleTapDay(false);
                datePickerDialog.show(getSupportFragmentManager(), "start_date");
            }
        });
        StartTimeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lastTimePickerOpened = "start_time";
                timePickerDialog.setVibrate(false);
                timePickerDialog.setCloseOnSingleTapMinute(false);
                timePickerDialog.show(getSupportFragmentManager(), "start_time");
            }
        });
        EndDateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.setVibrate(false);
                datePickerDialog.setYearRange(2015, 2030);
                datePickerDialog.setCloseOnSingleTapDay(false);
                datePickerDialog.show(getSupportFragmentManager(), "end_date");
            }
        });
        EndTimeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lastTimePickerOpened = "end_time";
                timePickerDialog.setVibrate(false);
                timePickerDialog.setCloseOnSingleTapMinute(false);
                timePickerDialog.show(getSupportFragmentManager(), "end_time");
            }
        });

        // Submit Button
        SubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startUpdateTask();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        // Get Cache, if not cached, go pull
        // Any missing items must be handled by the user immediately
        User user = ((Global) getApplication()).getUser();
        String queryStr = Utilities.BuildQueryParams(
                new String[][]{
                        new String[]{"user_guid", user.getUserGuid()},
                        new String[]{"user_id", String.valueOf(user.getUserID())},
                        new String[]{"mode", "0"}
                });

        Categories = user.getCategories();
        if (Categories == null) {
            // Show Spinner
            categorySpinner = new ProgressDialog(AuctionFormActivity.this);
            categorySpinner.setMessage("Getting Categories...");
            categorySpinner.setCanceledOnTouchOutside(false);
            categorySpinner.show();

            // Load Categories
            new GetInfoTask(AuctionFormActivity.this).execute(GetInfoTask.SourceType.updateSellerCategories.toString(), queryStr);
        } else {
            checkForExistingCategory();
        }

        Folders = user.getFolders();
        if (Folders == null) {
            // Show Spinner
            folderSpinner = new ProgressDialog(AuctionFormActivity.this);
            folderSpinner.setMessage("Getting Folders...");
            folderSpinner.setCanceledOnTouchOutside(false);
            folderSpinner.show();

            // Load Folders
            new GetInfoTask(AuctionFormActivity.this).execute(GetInfoTask.SourceType.updateSellerFolders.toString(), queryStr);
        } else {
            checkForExistingFolder();
        }

        ReturnPolicies = user.getReturnPolicies();
        if (ReturnPolicies == null) {
            // Show Spinner
            policySpinner = new ProgressDialog(AuctionFormActivity.this);
            policySpinner.setMessage("Getting Return Policies...");
            policySpinner.setCanceledOnTouchOutside(false);
            policySpinner.show();

            // Load Return Policies
            new GetInfoTask(AuctionFormActivity.this).execute(GetInfoTask.SourceType.updateSellerReturnPolicy.toString(), queryStr);
        } else {
            checkForExistingReturnPolicy();
        }

        // Check if item passed in for edit
        ItemID = getIntent().getIntExtra("item_id", 0);
        if(ItemID > 0) {
            startEditTask();
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
        } else {
            // Set Spinners
            ArrayAdapter<Pair> categoryOneAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, Categories);
            categoryOneAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            CategoryOneSpinner.setAdapter(categoryOneAdapter);
            ArrayAdapter<Pair> categoryTwoAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, Categories);
            categoryTwoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            CategoryTwoSpinner.setAdapter(categoryTwoAdapter);
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
        if (categorySpinner != null && categorySpinner.isShowing()) {
            categorySpinner.dismiss();
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
        } else {
            // Set Spinner
            ArrayAdapter<Pair> folderAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, Folders);
            folderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            FolderSpinner.setAdapter(folderAdapter);
        }
    }

    public void onFolderTaskFinish(String data) {
        try {
            //Deserialize
            JSONObject json = new JSONObject(data);
            JSONArray folders = (JSONArray) json.get("folders");

            Folders = new ArrayList<>();
            for (int i = 0; i < folders.length(); i++) {
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
        if (folderSpinner != null && folderSpinner.isShowing()) {
            folderSpinner.dismiss();
        }
    }

    public void checkForExistingReturnPolicy() {
        if (ReturnPolicies == null || ReturnPolicies.size() < 1) {
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
        } else {
            // Set Spinner
            ArrayAdapter<Trio> policyAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, ReturnPolicies);
            policyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            ReturnPolicySpinner.setAdapter(policyAdapter);
        }
    }

    public void onReturnTaskFinish(String data) {
        try {
            //Deserialize
            JSONObject json = new JSONObject(data);
            JSONArray return_policy_list = (JSONArray) json.get("return_policy_list");

            ReturnPolicies = new ArrayList<>();
            for (int i = 0; i < return_policy_list.length(); i++) {
                final JSONObject policy = (JSONObject) return_policy_list.get(i);
                int policy_id = policy.getInt("policy_id");
                String policy_name = policy.getString("policy_name");

                ReturnPolicies.add(new Trio(policy_name, policy_id, ""));
            }

            // Update Cache
            ((Global) getApplication()).getUser().updateReturnPolicies(ReturnPolicies);

            checkForExistingReturnPolicy();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, getResources().getString(R.string.generic_error), Toast.LENGTH_LONG).show();
        }

        //Dismiss Spinner
        if (policySpinner != null && policySpinner.isShowing()) {
            policySpinner.dismiss();
        }
    }

    public void onConsignorTaskFinish(String data) {
        try {
            Toast.makeText(this, getResources().getString(R.string.unimplemented_error), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, getResources().getString(R.string.generic_error), Toast.LENGTH_LONG).show();
        }
    }

    public void onPaymentTaskFinish(String data) {
        try {
            Toast.makeText(this, getResources().getString(R.string.unimplemented_error), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, getResources().getString(R.string.generic_error), Toast.LENGTH_LONG).show();
        }
    }

    public void startEditTask() {
        //Show Spinner
        editSpinner = new ProgressDialog(this);
        editSpinner.setMessage("Loading Details...");
        editSpinner.setCanceledOnTouchOutside(false);
        editSpinner.show();

        // Get Item Info
        User user = ((Global) getApplication()).getUser();
        String queryStr = Utilities.BuildQueryParams(
                new String[][]{
                        new String[]{"user_id", String.valueOf(user.getUserID())},
                        new String[]{"user_guid", user.getUserGuid()},
                        new String[]{"mode", "1"},
                        new String[]{"item_id", String.valueOf(ItemID)}
                });
        new GetInfoTask(this).execute(GetInfoTask.SourceType.editAuctionItem.toString(), queryStr);
    }

    public void onEditTaskFinish(String data) {
        try {
            if (data.startsWith("[")) {
                throw new Exception("Blank Request Return Value");
            }

            //Deserialize
            JSONObject json = new JSONObject(data);

            if (json.has("item_id") && !json.isNull("item_id")) {
                TitleText.setText(json.getString("title"));
                String end_date_pt = json.getString("end_date_pt");
                Date end_date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(end_date_pt);
                String displayEndTime = Utilities.ConvertDateToCountdown(end_date);
                ItemID = json.getInt("item_id");
                ShippingText.setText(String.valueOf(json.getDouble("shipping")));
                ShippingAddText.setText(String.valueOf(json.getDouble("shipping_additional")));
                DescriptionText.setText(android.text.Html.fromHtml(json.getString("description")));
            }
        }
        catch(Exception e) {
            e.printStackTrace();
            Toast.makeText(this, getResources().getString(R.string.generic_error), Toast.LENGTH_LONG).show();
        }

        //Dismiss Spinner if done
        if(editSpinner != null && editSpinner.isShowing()) {
            editSpinner.dismiss();
        }
    }

    public boolean startUpdateTask() {
        // Validate
        if(StartYear == 0 || StartMonth == 0 || StartDay == 0) {
            Toast.makeText(this, "Start Date is required", Toast.LENGTH_LONG).show();
            return false;
        }
        if(StartHour == 0) {
            Toast.makeText(this, "Start Time is required", Toast.LENGTH_LONG).show();
            return false;
        }
        if(EndYear == 0 || EndMonth == 0 || EndDay == 0) {
            Toast.makeText(this, "End Date is required", Toast.LENGTH_LONG).show();
            return false;
        }
        if(EndHour == 0) {
            Toast.makeText(this, "End Time is required", Toast.LENGTH_LONG).show();
            return false;
        }

        // Set Date/Time
        String startMonthStr = (StartMonth < 10) ? "0" + String.valueOf(StartMonth) : String.valueOf(StartMonth);
        String startDayStr = (StartDay < 10) ? "0" + String.valueOf(StartDay) : String.valueOf(StartDay);
        String startDate = StartYear + "-" + startMonthStr + "-" + startDayStr;
        String startHourStr = (StartHour < 13) ? String.valueOf(StartHour) : String.valueOf(StartHour - 12);
        String startAmPm = (StartHour < 13) ? "AM" : "PM";
        String endDate = EndYear + "-" + EndMonth + "-" + EndDay;
        String endHourStr = (EndHour < 13) ? String.valueOf(EndHour) : String.valueOf(EndHour - 12);
        String endAmPm = (EndHour < 13) ? "AM" : "PM";

        if (updateSpinner != null && updateSpinner.isShowing()) {
            updateSpinner.dismiss();
        }
        // Show Spinner
        updateSpinner = new ProgressDialog(AuctionFormActivity.this);
        updateSpinner.setMessage("Saving Auction...");
        updateSpinner.setCanceledOnTouchOutside(false);
        updateSpinner.show();

        // Save Form
        int folderId = Folders.get(FolderSpinner.getSelectedItemPosition()).ID;
        int categoryOneId = Categories.get(CategoryOneSpinner.getSelectedItemPosition()).ID;
        int categoryTwoId = Categories.get(CategoryTwoSpinner.getSelectedItemPosition()).ID;
        int returnPolicyId = ReturnPolicies.get(ReturnPolicySpinner.getSelectedItemPosition()).ID;
        User user = ((Global) getApplication()).getUser();
        String queryStr = Utilities.BuildQueryParams(
                new String[][]{
                        new String[]{"seller_id", String.valueOf(user.getUserID())},
                        new String[]{"seller_guid", user.getUserGuid()},
                        new String[]{"auction_item_id", String.valueOf(ItemID)},
                        new String[]{"inventory_item_id", "0"},
                        new String[]{"update_all", "1"},
                        new String[]{"folder_id", String.valueOf(folderId)},
                        new String[]{"title", TitleText.getText().toString()},
                        new String[]{"inv_qty", InvQuantityText.getText().toString()},
                        new String[]{"sku", SkuText.getText().toString()},
                        new String[]{"consignor_id", ""},
                        new String[]{"description", DescriptionText.getText().toString()},
                        new String[]{"shipping", ShippingText.getText().toString()},
                        new String[]{"shipping_add", ShippingAddText.getText().toString()},
                        new String[]{"return_policy_id", String.valueOf(returnPolicyId)},
                        new String[]{"payment_policy_id", ""},
                        new String[]{"category1", String.valueOf(categoryOneId)},
                        new String[]{"category2", String.valueOf(categoryTwoId)},
                        new String[]{"min_bid", MinBidText.getText().toString()},
                        new String[]{"auction_qty", AucQuantityText.getText().toString()},
                        new String[]{"reserve", AucReserveText.getText().toString()},
                        new String[]{"start_date", startDate},
                        new String[]{"start_time_hour", startHourStr},
                        new String[]{"start_time_min", String.valueOf(StartMinute)},
                        new String[]{"start_time_ampm", startAmPm},
                        new String[]{"end_date", endDate},
                        new String[]{"end_time_hour", endHourStr},
                        new String[]{"end_time_min", String.valueOf(EndMinute)},
                        new String[]{"end_time_ampm", endAmPm},
                        new String[]{"photo1", ""},
                        new String[]{"photo2", ""},
                        new String[]{"photo3", ""},
                        new String[]{"photo4", ""},
                        new String[]{"photo5", ""},
                        new String[]{"photo6", ""},
                        new String[]{"photo7", ""},
                        new String[]{"photo8", ""},
                        new String[]{"photo9", ""},
                        new String[]{"photo10", ""},
                        new String[]{"photo11", ""},
                        new String[]{"photo12", ""},
                        new String[]{"photo13", ""},
                        new String[]{"photo14", ""},
                        new String[]{"photo15", ""}
                }
        );

        new GetInfoTask(AuctionFormActivity.this).execute(GetInfoTask.SourceType.updateAuction.toString(), queryStr);

        return true;
    }

    public void onUpdateTaskFinish(String data) {
        try {
            //Deserialize
            JSONObject json = new JSONObject(data);
            int inventory_item_id = json.getInt("inventory_item_id");

            if (inventory_item_id > 0) {
                Toast.makeText(this, "Successfully Created Auction!", Toast.LENGTH_LONG).show();

                if (updateSpinner != null && updateSpinner.isShowing()) {
                    updateSpinner.dismiss();
                }

                //Redirect to My Auctions
                startActivity(new Intent(this, AuctionListActivity.class));
            } else {
                Toast.makeText(this, "Error Creating Auction", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, getResources().getString(R.string.generic_error), Toast.LENGTH_LONG).show();
        }

        if (updateSpinner != null && updateSpinner.isShowing()) {
            updateSpinner.dismiss();
        }
    }

    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
        switch(datePickerDialog.getTag()) {
            case "start_date":
                StartYear = year;
                StartMonth = month + 1;
                StartDay = day;
                StartDateText.setText((month + 1) + "/" + day + "/" + year);
                break;
            case "end_date":
                EndYear = year;
                EndMonth = month + 1;
                EndDay = day;
                EndDateText.setText((month + 1) + "/" + day + "/" + year);
                break;
        }
    }

    @Override
    public void onTimeSet(RadialPickerLayout radialPickerLayout, int hour, int minute) {
        String displayText = (hour < 13)
            ? (hour + ":" +
                ((minute < 10)
                    ? "0" + minute
                    : minute)
                + " AM")
            : ((hour - 12) + ":" +
                ((minute < 10)
                        ? "0" + minute
                        : minute)
                + " PM");

        switch(lastTimePickerOpened) {
            case "start_time":
                StartHour = hour;
                StartMinute = minute;
                StartTimeText.setText(displayText);
                break;
            case "end_time":
                EndHour = hour;
                EndMinute = minute;
                EndTimeText.setText(displayText);
                break;
        }
    }
}
