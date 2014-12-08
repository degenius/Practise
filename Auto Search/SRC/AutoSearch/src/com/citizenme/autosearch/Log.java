
package com.citizenme.autosearch;

public class Log {

    public static String DEFAULT_LOG_TAG = "CITIZENME";
    public static final boolean ENABLE_DEBUG = true;

    private Log() {
    }

    public static void d(String msg) {
        if (ENABLE_DEBUG)
            android.util.Log.d(DEFAULT_LOG_TAG, msg);
    }

    public static void i(String msg) {
        if (ENABLE_DEBUG)
            android.util.Log.i(DEFAULT_LOG_TAG, msg);
    }

    public static void w(String msg) {
        if (ENABLE_DEBUG)
            android.util.Log.w(DEFAULT_LOG_TAG, msg);
    }

    public static void e(String msg) {
        if (ENABLE_DEBUG)
            android.util.Log.e(DEFAULT_LOG_TAG, msg);
    }

    public static void v(String msg) {
        if (ENABLE_DEBUG)
            android.util.Log.v(DEFAULT_LOG_TAG, msg);
    }

}
