package com.nowhere.login;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.BindView;

public class MainActivity extends Activity {
    private static final String CALL = "call";

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.main);

        Log.i(CALL, "MainActivity.onCreate())");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Log.i(CALL, "MainActivity.onSaveInstanceState()");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        Log.i(CALL, "MainActivity.onRestoreInstanceState()");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(CALL, "MainActivity.onStart()");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(CALL, "MainActivity.onRestart()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(CALL, "MainActivity.onResume()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(CALL, "MainActivity.onPause()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(CALL, "MainActivity.onStop()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(CALL, "MainActivity.onDestroy()");
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
        Log.i(CALL, "MainActivity.onBackPressed()");
    }
}