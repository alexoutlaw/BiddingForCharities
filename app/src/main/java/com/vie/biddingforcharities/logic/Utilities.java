package com.vie.biddingforcharities.logic;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.Date;

/**
 * Created by Alex Outlaw on 2/21/2015.
 */
public final class Utilities {
    public static String BuildQueryParams(String[][] jsonTerms) {
        String str = "";

        for(int i = 0; i < jsonTerms.length; i++) {
            if(i == 0) {
                str += "?";
            } else {
                str += "&";
            }

            str += jsonTerms[i][0];
            str += "=";
            str += jsonTerms[i][1];
        }

        return str;
    }

    public static String ConvertDateToCountdown(Date countdownEnd) {
        Date now = new Date();

        if(now.after(countdownEnd)) {
            return "Expired";
        }

        long diff = countdownEnd.getTime() - now.getTime();

        long diffSeconds = diff / 1000 % 60;
        long diffMinutes = diff / (60 * 1000) % 60;
        long diffHours = diff / (60 * 60 * 1000) % 24;
        long diffDays = diff / (24 * 60 * 60 * 1000);

        String result = "";

        if(diffDays > 0) {
            result += diffDays + "D ";
        }
        if(diffHours > 0) {
            result += diffHours + "H ";
        }
        if(diffMinutes > 0) {
            result += diffMinutes + "H ";
        }
//        if(diffSeconds > 0) {
//            result += diffSeconds + "S ";
//        }

        return result;
    }

    public static int MeasureGridCell(Context cxt, View cell) {
        // We need a fake parent
        FrameLayout buffer = new FrameLayout( cxt );
        android.widget.AbsListView.LayoutParams layoutParams = new  android.widget.AbsListView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        buffer.addView( cell, layoutParams);

        cell.forceLayout();
        cell.measure(1000, 1000);

        int width = cell.getMeasuredWidth();

        buffer.removeAllViews();

        return width;
    }

}
