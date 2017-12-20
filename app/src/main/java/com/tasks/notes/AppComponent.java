package com.tasks.notes;

import android.content.Context;

import com.tasks.notes.data.storage.StorageProvider;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = AppModule.class)
public interface AppComponent {
    void inject(App application);

    Context getContext();
    StorageProvider getDatabasePrvider();
}
