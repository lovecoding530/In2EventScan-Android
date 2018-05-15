package com.in2event.in2eventscan;

import android.app.Application;
import android.content.Context;

/**
 * Created by Kangtle_R on 2/1/2018.
 */

public class MyApp extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    public static Context getContext(){
        return context;
    }
}
