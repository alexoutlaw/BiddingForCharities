package com.vie.biddingforcharities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.vie.biddingforcharities.logic.AuctionItem;
import com.vie.biddingforcharities.logic.GetBitmapTask;
import com.vie.biddingforcharities.logic.GetInfoTask;
import com.vie.biddingforcharities.logic.Utilities;
import com.vie.biddingforcharities.ui.AuctionGridAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class AuctionListActivity extends Activity {
    DrawerLayout NavLayout;
    ListView NavList;
    Button HomeButton;
    ImageButton NavDrawerButton;

    GridView ItemGrid;
    TextView EmptyWarningText;
    ProgressDialog spinner;

    ArrayList<GetInfoTask> jsonTasks = new ArrayList<GetInfoTask>();
    ArrayList<GetBitmapTask> downloadTasks = new ArrayList<GetBitmapTask>();
    ArrayList<AuctionItem> itemList = new ArrayList<AuctionItem>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auctionlist);

        NavLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavList = (ListView) findViewById(R.id.navList);
        NavDrawerButton = (ImageButton) findViewById(R.id.nav_drawer_expand);
        ItemGrid = (GridView) findViewById(R.id.item_grid);
        EmptyWarningText = (TextView) findViewById(R.id.empty_warning_text);

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
    protected void onResume() {
        super.onResume();

        // Clear Previous
        itemList.clear();

        //Show Spinner
        spinner = new ProgressDialog(this);
        spinner.setMessage("Loading Auctions...");
        spinner.setCanceledOnTouchOutside(false);
        spinner.show();

        // Get Bids
        int userID = ((Global) getApplication()).getUser().getUserNameId();
        String queryStr = Utilities.BuildQueryParams(
                new String[][]{
                        new String[]{"search_seller_id", String.valueOf(userID)},
                        new String[]{"search_high_bidder_id", "0"},
                        new String[]{"search_title", ""},
                        new String[]{"search_category_id", "0"},
                        new String[]{"search_consignor_id", "0"},
                        new String[]{"search_start_row", "0"},
                        new String[]{"search_items_per_page", "100"},
                        new String[]{"search_sort_by", "0"},

                });
        jsonTasks.add((GetInfoTask) new GetInfoTask(this).execute("getUserAuctions", queryStr));
    }

    @Override
    protected void onPause() {
        //Cancel threads while reference is valid
        for(GetInfoTask t: jsonTasks) {t.cancel(true);}
        for(GetBitmapTask t: downloadTasks) {t.cancel(true);}
        jsonTasks.clear();
        downloadTasks.clear();

        super.onPause();
    }

    public void onTaskFinish(GetInfoTask task ,String data) {
        jsonTasks.remove(task);
        itemList.clear();

        try {
            //Deserialize
            JSONObject json = new JSONObject(data);
            JSONArray itemArray = (JSONArray) json.get("items");

            // Show warning if no items returned
            EmptyWarningText.setVisibility(itemArray.length() > 0
                    ? View.GONE
                    : View.VISIBLE);

            for(int i = 0; i < itemArray.length(); i++) {
                final JSONObject item = (JSONObject) itemArray.get(i);

                // All items on the Bids page will have a bidder user id, works as a null item check
                if(item.has("item_id") && !item.isNull("item_id")) {
                    final String image_url = item.getString("img");
                    String title = item.getString("title");

                    int item_id = item.getInt("item_id");
                    int seller_id = item.getInt("seller_id");
                    int seller_user_name_id = item.getInt("seller_user_name_id");
                    double current_high_bid = item.getDouble("current_high_bid");
                    double current_price_plus_shipping = item.getDouble("current_price_plus_shipping");
                    int total_bids = item.getInt("total_bids");

                    String start_date_str = item.getString("start_date_pt");
                    String end_date_str = item.getString("end_date_pt");
                    String state_date_timestamp = item.getString("start_date_time_stamp_pt");
                    Date state_date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(state_date_timestamp);
                    String end_date_timestamp = item.getString("end_date_time_stamp_pt");
                    Date end_date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(end_date_timestamp);
                    String displayEndTime = Utilities.ConvertDateToCountdown(end_date);

                    itemList.add(new AuctionItem (image_url, title, displayEndTime, current_high_bid, item_id));
                }
            }
        }
        catch(Exception e) {
            e.printStackTrace();
            Toast.makeText(this, getResources().getString(R.string.generic_error), Toast.LENGTH_LONG).show();
        }

        //Dismiss Spinner if done
        if(jsonTasks.isEmpty() && spinner != null && spinner.isShowing()) {
            spinner.dismiss();

            // Display tiles
            ItemGrid.setAdapter(new AuctionGridAdapter(this, itemList));
        }
    }
}
