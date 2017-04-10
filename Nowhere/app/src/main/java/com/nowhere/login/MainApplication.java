package com.nowhere.login;

import android.app.Application;
import android.content.res.Configuration;
import android.util.Log;

public class MainApplication extends Application {
    private static final String CALL = "call";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(CALL, "MainApplication.onCreate()");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.i(CALL, "MainApplication.onConfigurationChanged()");
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Log.i(CALL, "MainApplication.onLowMemory()");
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        Log.i(CALL, String.format("MainApplication.onTrimMemory(), level: {0}", level));
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        Log.i(CALL, "MainApplication.onTerminate()");
    }
}
