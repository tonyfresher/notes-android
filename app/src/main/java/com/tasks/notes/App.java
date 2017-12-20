package com.tasks.notes;

import android.app.Application;
import android.content.Context;

import com.tasks.notes.data.storage.StorageProvider;

import javax.inject.Inject;

public class App extends Application {

    protected AppComponent appComponent;

    @Inject
    StorageProvider databaseProvider;

    public static App get(Context context) {
        return (App) context.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        appComponent = DaggerApplicationComponent
                .builder()
                .applicationModule(new AppModule(this))
                .build();
        appComponent.inject(this);
    }

    public AppComponent getComponent(){
        return appComponent;
    }
}