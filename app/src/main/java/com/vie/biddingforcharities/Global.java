package com.vie.biddingforcharities;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.vie.biddingforcharities.netcode.User;
import com.vie.biddingforcharities.ui.FontsOverride;
import com.vie.biddingforcharities.ui.NavDrawerItem;
import com.vie.biddingforcharities.ui.NavDrawerListAdapter;

import java.util.ArrayList;

public class Global extends Application {
    public User user;

    @Override
    public void onCreate() {
        super.onCreate();

        //Set app-wide standard font
        //stolen from http://stackoverflow.com/questions/2711858/is-it-possible-to-set-font-for-entire-application/16883281#16883281
        FontsOverride.setDefaultFont(this, "DEFAULT", "fonts/myriadpro_cond.otf");
        FontsOverride.setDefaultFont(this, "SERIF", "fonts/myriadpro_cond.otf");
    }

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
                Toast.makeText(this, "My Info, Coming Soon!", Toast.LENGTH_LONG).show();
                //cxt.startActivity(new Intent(cxt, AccountActivity.class));
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


