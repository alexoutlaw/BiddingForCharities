package com.vie.biddingforcharities.ui;


import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.vie.biddingforcharities.AccountAddressFragment;
import com.vie.biddingforcharities.AccountEmailFragment;
import com.vie.biddingforcharities.AccountNameFragment;
import com.vie.biddingforcharities.AccountUsernameFragment;
import com.vie.biddingforcharities.R;

public class AccountPageAdapter extends FragmentPagerAdapter {
    Context mCxt;

    String[] tabTitles;

    public AccountPageAdapter(FragmentManager fm, Context cxt) {
        super(fm);
        mCxt = cxt;

        tabTitles = mCxt.getResources().getStringArray(R.array.account_tab_items);
    }

    @Override
    public Fragment getItem(int position) {
        switch(position) {
            case 0:
                return new AccountNameFragment();
            case 1:
                return new AccountUsernameFragment();
            case 2:
                return new AccountEmailFragment();
            case 3:
                return new AccountAddressFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (position <tabTitles.length) {
            return tabTitles[position];
        }
        else {
            return "Unknown";
        }
    }
}
