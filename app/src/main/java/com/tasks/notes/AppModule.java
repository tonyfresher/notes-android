package com.tasks.notes;

import android.app.Application;
import android.content.Context;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {

    private final Application application;

    public AppModule(Application app) {
        application = app;
    }

    @Provides
    Context provideContext() {
        return application;
    }
}
