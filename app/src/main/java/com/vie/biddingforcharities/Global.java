package com.vie.biddingforcharities;
import android.app.Application;

public class Global extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        //Set app-wide standard font
        //stolen from http://stackoverflow.com/questions/2711858/is-it-possible-to-set-font-for-entire-application/16883281#16883281
        FontsOverride.setDefaultFont(this, "DEFAULT", "fonts/myriadpro_cond.otf");
        FontsOverride.setDefaultFont(this, "SERIF", "fonts/myriadpro_cond.otf");
    }
}
