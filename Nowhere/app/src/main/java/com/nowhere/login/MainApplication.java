package com.nowhere.login;

import android.app.Application;
import android.content.res.Configuration;
import android.widget.Toast;

public class MainApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(this, "MainApplication.onCreate() called", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Toast.makeText(this, "MainApplication.onConfigurationChanged() called", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Toast.makeText(this, "MainApplication.onLowMemory() called", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        Toast.makeText(
                this,
                String.format("MainApplication.onTrimMemory() called, level: {0}", level),
                Toast.LENGTH_SHORT
        ).show();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        Toast.makeText(this, "MainApplication.onTerminate() called", Toast.LENGTH_SHORT).show();
    }
}
