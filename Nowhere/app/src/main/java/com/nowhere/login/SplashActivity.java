package com.nowhere.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class SplashActivity extends Activity {
    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        Intent intent = new Intent(this, MainActivity.class);

        startActivity(intent);
        finish();
    }
}