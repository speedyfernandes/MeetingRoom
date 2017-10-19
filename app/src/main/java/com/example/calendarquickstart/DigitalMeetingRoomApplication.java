package com.example.calendarquickstart;

import android.app.Application;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by Jerry on 20/10/17.
 */

public class DigitalMeetingRoomApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        //TODO: Change the App Package name
        //TODO: Use the new Support Library stuff for Custom Fonts
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Rubik-Bold.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
    }
}
