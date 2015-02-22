package com.vie.biddingforcharities;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.vie.biddingforcharities.logic.User;
import com.vie.biddingforcharities.ui.FontsOverride;
import com.vie.biddingforcharities.ui.NavDrawerItem;
import com.vie.biddingforcharities.ui.NavDrawerListAdapter;

import java.util.ArrayList;

public class Global extends Application {
    public static final String PREFS_NAME = "General_Prefs";
    private User user;

    @Override
    public void onCreate() {
        super.onCreate();

        //Set app-wide standard font
        //stolen from http://stackoverflow.com/questions/2711858/is-it-possible-to-set-font-for-entire-application/16883281#16883281
        FontsOverride.setDefaultFont(this, "DEFAULT", "fonts/myriadpro_cond.otf");
        FontsOverride.setDefaultFont(this, "SANS_SERIF", "fonts/myriadpro_cond.otf");
        FontsOverride.setDefaultFont(this, "SERIF", "fonts/myriadpro_reg.otf");
    }

    // Chained up from HomeActivity
    public void onDestroy() {
        saveUserState(user);
    }

    public User getUser() {
        if(user == null) {
            restoreUserState();
        }

        return user;
    }

    public void setUser(User newUser) {
        user = newUser;
        saveUserState(user);
    }

    public void saveUserState(User user) {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("User_Email", user.getEmail());
        editor.putInt("User_UserID", user.getUserID());
        editor.putString("User_UserGuid", user.getUserGuid());
        editor.putInt("User_UserType", user.getUserType().ordinal());
        editor.putInt("User_UserNameId", user.getUserNameId());
        editor.putString("User_UserName", user.getUserName());
        editor.putString("User_FirstName", user.getFirstName());
        editor.putString("User_LastName", user.getLastName());
        editor.putInt("User_AddressId", user.getAddressId());
        editor.putBoolean("User_HasSellerInvoiceDefaults", user.getHasInvoiceDefaults());
        editor.commit();
    }

    public void restoreUserState() {
        try {
            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
            String email = settings.getString("User_Email", "");
            int userId = settings.getInt("User_UserID", 0);
            String userGuid = settings.getString("User_UserGuid", "");
            int userType = settings.getInt("User_UserType", 0);
            int userNameId = settings.getInt("User_UserNameId", 0);
            String userName = settings.getString("User_UserName", "");
            String firstName = settings.getString("User_FirstName", "");
            String lastName = settings.getString("User_LastName", "");
            int addressId = settings.getInt("User_AddressId", 0);
            boolean invoidDefaults = settings.getBoolean("User_HasSellerInvoiceDefaults", false);

            user = new User(email, userId, userGuid, userType, userNameId, userName, addressId, invoidDefaults);
            user.updateFullName(firstName, lastName);
        } catch (Exception e) {
            try {
                user = new User("", 0, "", 0, 0, "", 0, false);
            } catch (Exception e1) {
                // really bad
                Toast.makeText(this, "FATAL ERROR: User Data Corrupted! Reinstall Required.", Toast.LENGTH_LONG).show();
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
    }

    public String getPrefsName() { return PREFS_NAME; }

    public void BuildNavigationMenu(ListView mDrawerList) {
        String[] navTitles = getResources().getStringArray(R.array.nav_menu_items);
        TypedArray navIcons = getResources().obtainTypedArray(R.array.nav_menu_icons);

        ArrayList<NavDrawerItem> navItems = new ArrayList<>();
        for(int i = 0; i < navTitles.length; i++) {
            navItems.add(new NavDrawerItem(navTitles[i], navIcons.getResourceId(i, -1)));
        }

        navIcons.recycle();

        mDrawerList.setAdapter(new NavDrawerListAdapter(this, navItems));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Navigate(parent.getContext(), position);
        }
    }

    private void Navigate(Context cxt, int position) {
        //TODO: switch back as implemented
        switch(position) {
            case 0:
                cxt.startActivity(new Intent(cxt, AccountActivity.class));
                break;
            case 1:
                Toast.makeText(this, "My Watchlist, Coming Soon!", Toast.LENGTH_LONG).show();
                //cxt.startActivity(new Intent(cxt, WatchListActivity.class));
                break;
            case 2:
                Toast.makeText(this, "My Bids, Coming Soon!", Toast.LENGTH_LONG).show();
                //cxt.startActivity(new Intent(cxt, BidListActivity.class));
                break;
            case 3:
                Toast.makeText(this, "My Auctions, Coming Soon!", Toast.LENGTH_LONG).show();
                //cxt.startActivity(new Intent(cxt, AuctionListActivity.class));
                break;
            case 4:
                Toast.makeText(this, "Create Auction, Coming Soon!", Toast.LENGTH_LONG).show();
                //cxt.startActivity(new Intent(cxt, AuctionFormActivity.class));
                break;
            case 5:
                Toast.makeText(this, "Search Auctions, Coming Soon!", Toast.LENGTH_LONG).show();
                //cxt.startActivity(new Intent(cxt, AuctionSearchActivity.class));
                break;
            case 6:
                Toast.makeText(this, "Request Fundraiser, Coming Soon!", Toast.LENGTH_LONG).show();
                //cxt.startActivity(new Intent(cxt, CharityRequestActivity.class));
                break;
        }

        if(!(cxt instanceof HomeActivity)) {
            ((Activity)cxt).finish();
        }
    }
}


