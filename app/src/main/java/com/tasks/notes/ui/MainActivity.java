package com.tasks.notes.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.tasks.notes.R;
import com.tasks.notes.data.storage.AsyncStorageProvider;
import com.tasks.notes.ui.fragments.ListFragment;
import com.tasks.notes.ui.infrastructure.OnBackPressedListener;

import javax.inject.Inject;



public class MainActivity extends AppCompatActivity {

    private static final String WORKER_THREAD_NAME = "WORKER_THREAD";

    private static final int MAIN_FRAGMENT_CONTAINER = R.id.main_fragment_container;
    private static final int OPEN_TRANSACTION = FragmentTransaction.TRANSIT_FRAGMENT_OPEN;
    private static final int CLOSE_TRANSACTION = FragmentTransaction.TRANSIT_FRAGMENT_CLOSE;

    @Inject
    AsyncStorageProvider DatabaseProvider;

    private HandlerThread workerThread;
    private Handler workerHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        workerThread = new HandlerThread(WORKER_THREAD_NAME);
        workerThread.start();
        workerHandler = new Handler(workerThread.getLooper());

        if (savedInstanceState == null) {
            ListFragment listFragment = ListFragment.newInstance();
            addFragment(listFragment, ListFragment.TAG);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        workerThread.quit();
    }

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.getBackStackEntryCount() > 0) {
            String fragmentName = fragmentManager
                    .getBackStackEntryAt(fragmentManager.getBackStackEntryCount() - 1)
                    .getName();
            Fragment fragment = fragmentManager.findFragmentByTag(fragmentName);

            if (fragment instanceof OnBackPressedListener &&
                    ((OnBackPressedListener) fragment).onBackPressed()) {
                return;
            }
        }

        if (fragmentManager.getBackStackEntryCount() > 1) {
            fragmentManager.popBackStack();
        } else {
            finish();
        }
    }

    public void addFragment(Fragment fragment, String tag) {
        getSupportFragmentManager().beginTransaction()
                .add(MAIN_FRAGMENT_CONTAINER, fragment, tag)
                .setTransition(OPEN_TRANSACTION)
                .addToBackStack(tag)
                .commit();
    }

    public void removeFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .remove(fragment)
                .setTransition(CLOSE_TRANSACTION)
                .commit();
    }
}