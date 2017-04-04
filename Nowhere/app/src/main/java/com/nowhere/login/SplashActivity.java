package com.nowhere.login;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

public class SplashActivity extends Activity {
    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        Intent intent = new Intent(this, LoginActivity.class);

        startActivity(intent);
        finish();
    }
}