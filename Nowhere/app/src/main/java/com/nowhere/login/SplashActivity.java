package com.nowhere.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class SplashActivity extends Activity {
    public static final String CALL = "call";

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Log.i(CALL, "SplashActivity.onCreate()");

        Intent intent = new Intent(this, MainActivity.class);

        startActivity(intent);
        finish();
    }
}