package com.vie.biddingforcharities.logic;

import java.net.URLEncoder;

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

            str += URLEncoder.encode(jsonTerms[i][0]);
            str += "=";
            str += URLEncoder.encode(jsonTerms[i][1]);
        }

        return str;
    }
}
