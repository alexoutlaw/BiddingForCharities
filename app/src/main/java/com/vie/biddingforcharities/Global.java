package com.vie.biddingforcharities;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

    NavDrawerListAdapter navAdapter;

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
        ArrayList<NavDrawerItem> navRows = new ArrayList<>();

        if(user.HasPermission(User.UserTypes.STANDARD)) {
            navRows.add(new NavDrawerItem(getString(R.string.account_text),R.drawable.ic_action_user, User.UserTypes.STANDARD, AccountActivity.class));
            navRows.add(new NavDrawerItem(getString(R.string.watchlist_text),R.drawable.ic_visibility_white_48dp, User.UserTypes.STANDARD, WatchListActivity.class));
            navRows.add(new NavDrawerItem(getString(R.string.bids_text),R.drawable.ic_shopping_cart_white_48dp, User.UserTypes.STANDARD, BidListActivity.class));

            navRows.add(new NavDrawerItem(getString(R.string.search_text),R.drawable.ic_search_white_48dp, User.UserTypes.STANDARD, AuctionSearchActivity.class));
        }

        if(user.HasPermission(User.UserTypes.SELLER)) {
            navRows.add(new NavDrawerItem(getString(R.string.create_text),R.drawable.ic_note_add_white_48dp, User.UserTypes.SELLER, AuctionFormActivity.class));
            navRows.add(new NavDrawerItem(getString(R.string.auctions_text),R.drawable.ic_description_white_48dp, User.UserTypes.SELLER, AuctionListActivity.class));
            navRows.add(new NavDrawerItem(getString(R.string.categories_text),R.drawable.ic_label_outline_white_48dp, User.UserTypes.SELLER, SellerCategoriesActivity.class));
            navRows.add(new NavDrawerItem(getString(R.string.folder_text),R.drawable.ic_perm_media_white_48dp, User.UserTypes.SELLER, SellerFolderActivity.class));
            navRows.add(new NavDrawerItem(getString(R.string.returnpolicy_text),R.drawable.ic_assignment_late_white_48dp, User.UserTypes.SELLER, SellerReturnPolicyActivity.class));
        }

        if(!user.HasPermission(User.UserTypes.SELLER)) {
            navRows.add(new NavDrawerItem(getString(R.string.request_text), R.drawable.ic_action_mail, User.UserTypes.STANDARD, CharityRequestActivity.class));
        }

        navAdapter = new NavDrawerListAdapter(this, navRows);
        mDrawerList.setAdapter(navAdapter);
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Navigate(parent.getContext(), (NavDrawerItem)navAdapter.getItem(position));
        }
    }

    private void Navigate(Context cxt, NavDrawerItem item) {
        if(user.HasPermission(item.PermissionLevel)) {
            Class navigateTo = item.getLinkedActivity();
            cxt.startActivity(new Intent(cxt, navigateTo));

            if(!(cxt instanceof HomeActivity)) {
                ((Activity)cxt).finish();
            }
        } else {
            Toast.makeText(this, getString(R.string.permission_error), Toast.LENGTH_LONG).show();
        }

    }
}


