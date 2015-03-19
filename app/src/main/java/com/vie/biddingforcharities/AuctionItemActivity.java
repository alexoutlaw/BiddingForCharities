package com.vie.biddingforcharities;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.vie.biddingforcharities.logic.GetBitmapTask;
import com.vie.biddingforcharities.logic.GetInfoTask;
import com.vie.biddingforcharities.logic.User;
import com.vie.biddingforcharities.logic.Utilities;
import com.vie.biddingforcharities.ui.BidFormDialog;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AuctionItemActivity extends Activity {
    DrawerLayout NavLayout;
    ListView NavList;
    Button HomeButton;
    ImageButton NavDrawerButton;

    ImageView ItemImage;
    TextView SellerNameText, ItemDescriptionText, RemainingTimeText, EndTimestampText, CurrentBidText, TotalBidsText, ShippingPrice;
    Button BidButton, WatchlistButton, ShareButton, ContactButton;

    BidFormDialog BidDialog;
    ProgressDialog spinner;

    Integer ItemID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auctionitem);

        NavLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavList = (ListView) findViewById(R.id.navList);
        NavDrawerButton = (ImageButton) findViewById(R.id.nav_drawer_expand);
        ItemImage = (ImageView) findViewById(R.id.item_image);
        SellerNameText = (TextView) findViewById(R.id.seller_name);
        ItemDescriptionText = (TextView) findViewById(R.id.item_description);
        RemainingTimeText = (TextView) findViewById(R.id.remaining_time);
        EndTimestampText = (TextView) findViewById(R.id.end_timestamp);
        CurrentBidText = (TextView) findViewById(R.id.current_bid);
        TotalBidsText = (TextView) findViewById(R.id.total_bids);
        ShippingPrice = (TextView) findViewById(R.id.shipping_price);

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

        // Build Search Dialog
        BidDialog = new BidFormDialog();
        BidDialog.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.DialogTheme);

        // Place Bid on Item
        BidButton = (Button) findViewById(R.id.place_bid_button);
        BidButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BidDialog.show(getFragmentManager(), "bid");
            }
        });

        // Add to Watchlist
        WatchlistButton = (Button) findViewById(R.id.add_watchlist_button);
        WatchlistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO
                Toast.makeText(AuctionItemActivity.this, getResources().getString(R.string.unimplemented_error), Toast.LENGTH_LONG).show();
            }
        });

        // Share via Email
        ShareButton = (Button) findViewById(R.id.share_button);
        ShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringBuilder emailBody = new StringBuilder()
                    .append("<p>Someone has shared an auction item with you.")
                    .append("<br /><br />")
                    .append("To view this item please go to: ")
                    .append("<a href=\"http://biddingforcharities.com/auc_item.php?id=" + ItemID + "\">")
                    .append("Item Link</a></p>");

                final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
                emailIntent.setType("message/rfc822");
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Bidding For Charities Shared Item");
                emailIntent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(emailBody.toString()));
                startActivity(Intent.createChooser(emailIntent, "Send email..."));
            }
        });

        // Email Seller
        ContactButton = (Button) findViewById(R.id.contact_button);
        ContactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO
                Toast.makeText(AuctionItemActivity.this, getResources().getString(R.string.unimplemented_error), Toast.LENGTH_LONG).show();
            }
        });

        // Get Item ID from caller
        Bundle extras = getIntent().getExtras();
        if(extras != null && extras.containsKey("item_id")) {
            ItemID = extras.getInt("item_id");
        }
        else {
            ItemID = 0;
        }

        //Show Spinner
        spinner = new ProgressDialog(this);
        spinner.setMessage("Loading Details...");
        spinner.setCanceledOnTouchOutside(false);
        spinner.show();

        // Get Item Info
        User user =  ((Global) getApplication()).getUser();
        String queryStr = Utilities.BuildQueryParams(
                new String[][]{
                        new String[]{"user_id", String.valueOf(user.getUserID())},
                        new String[]{"user_guid", user.getUserGuid()},
                        new String[]{"mode", "1"},
                        new String[]{"item_id", String.valueOf(ItemID)}
                });
        new GetInfoTask(this).execute("getAuctionItem", queryStr);
    }

    public void onItemTaskFinish(String data) {
        try {

            if (data.startsWith("[")) {
                throw new Exception("Blank Request Return Value");
            }

            //Deserialize
            JSONObject json = new JSONObject(data);

            if (json.has("item_id") && !json.isNull("item_id")) {
                double current_price = json.getDouble("current_price");
                boolean is_featured = Boolean.getBoolean(json.getString("is_featured"));
                int sellers_user_id = json.getInt("sellers_user_id");
                boolean item_belongs_to_viewer = Boolean.getBoolean(json.getString("item_belongs_to_viewer"));
                boolean auction_has_ended = Boolean.getBoolean(json.getString("auction_has_ended"));
                double next_mandatory_bid = json.getDouble("next_mandatory_bid");
                boolean viewer_is_high_bidder = Boolean.getBoolean(json.getString("viewer_is_high_bidder"));
                double current_max_bid = json.getDouble("current_max_bid");
                String photo1 = json.getString("photo1");
                String photo2 = json.getString("photo2");
                String photo3 = json.getString("photo3");
                String photo4 = json.getString("photo4");
                String photo5 = json.getString("photo5");
                String photo6 = json.getString("photo6");
                String photo7 = json.getString("photo7");
                String photo8 = json.getString("photo8");
                String photo9 = json.getString("photo9");
                String photo10 = json.getString("photo10");
                String photo11 = json.getString("photo11");
                String photo12 = json.getString("photo12");
                String photo13 = json.getString("photo13");
                String photo14 = json.getString("photo14");
                String photo15 = json.getString("photo15");
                String title = json.getString("title");
                String time_left = json.getString("time_left");
                String end_date_pt = json.getString("end_date_pt");
                Date end_date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(end_date_pt);
                String displayEndTime = Utilities.ConvertDateToCountdown(end_date);
                int total_bids = json.getInt("total_bids");
                int item_id = json.getInt("item_id");
                String item_guid = json.getString("item_guid");
                double shipping = json.getDouble("shipping");
                double shipping_additional = json.getDouble("shipping_additional");
                String description = json.getString("description");

                // Set Item Image
                new GetBitmapTask(this, ItemImage, 0, 0).execute(photo1);

                // Set Item Text
                SellerNameText.setText(String.valueOf(sellers_user_id));
                ItemDescriptionText.setText(android.text.Html.fromHtml(description));
                RemainingTimeText.setText(displayEndTime);
                EndTimestampText.setText(end_date_pt);
                CurrentBidText.setText("$" + String.valueOf(current_price));
                TotalBidsText.setText(String.valueOf(total_bids));
                ShippingPrice.setText("$" + String.valueOf(shipping) + (shipping_additional > 0 ? " Additional: $" + shipping_additional : ""));
            }
        }
        catch(Exception e) {
            e.printStackTrace();
            Toast.makeText(this, getResources().getString(R.string.generic_error), Toast.LENGTH_LONG).show();
        }

        //Dismiss Spinner if done
        if(spinner != null && spinner.isShowing()) {
            spinner.dismiss();
        }
    }

    public void startBidTask(String bidAmountText) {
        BidDialog.dismiss();

        User user =  ((Global) getApplication()).getUser();
        String queryStr = Utilities.BuildQueryParams(
                new String[][]{
                        new String[]{"user_id", String.valueOf(user.getUserID())},
                        new String[]{"user_guid", user.getUserGuid()},
                        new String[]{"mode", "2"},
                        new String[]{"item_id", String.valueOf(ItemID)},
                        new String[]{"user_name_id", String.valueOf(user.getUserNameId())},
                        new String[]{"bid_value",bidAmountText}
                });
        new GetInfoTask(AuctionItemActivity.this).execute("bidAuctionItem", queryStr);
    }

    public void onBidTaskFinish(String data) {
        try {
            //Deserialize
            JSONObject json = new JSONObject(data);

            if (json.has("return_bid_message") && !json.isNull("return_bid_message")) {
                Toast.makeText(this, json.getString("return_bid_message"), Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(this, getResources().getString(R.string.place_bid_error), Toast.LENGTH_LONG).show();
            }
        }
        catch(Exception e) {
            e.printStackTrace();
            Toast.makeText(this, getResources().getString(R.string.generic_error), Toast.LENGTH_LONG).show();
        }
    }
}
