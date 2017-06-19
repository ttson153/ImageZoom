package com.tts.libimagezoomanim;

import android.app.Application;
import android.content.Context;

/**
 * Created by tts on 11/28/16.
 */

public class MyApplication extends Application {
    private static Context context;

    public void onCreate() {
        super.onCreate();
        MyApplication.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return MyApplication.context;
    }
}
