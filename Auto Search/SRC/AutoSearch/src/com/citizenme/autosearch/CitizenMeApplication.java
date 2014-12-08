package com.citizenme.autosearch;

import android.app.Application;
import android.content.Context;
import android.os.SystemClock;

/**
 * Application class to provide application context through out the application
 * as well as It can also handle initial operations in the future
 * 
 * 
 */
public class CitizenMeApplication extends Application {

    public static final boolean ENABLE_DEBUG = true;

    private static Context sAppContext;

    @Override
    public void onCreate() {

        sAppContext = this.getApplicationContext();

        if (ENABLE_DEBUG) {
            Log.i("CitizenMeApplication.onCreate() uptime: "
                    + SystemClock.uptimeMillis() + " elapsedRealtime: "
                    + SystemClock.elapsedRealtime());
        }
        super.onCreate();
    }

    public static Context getAppContext() {
        return sAppContext;
    }

}
