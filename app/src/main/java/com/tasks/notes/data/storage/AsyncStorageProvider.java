package com.tasks.notes.data.storage;

import android.os.AsyncTask;

import com.tasks.notes.data.model.Note;
import com.tasks.notes.utility.AsyncTaskBuilder;

import java.util.List;

public interface AsyncStorageProvider extends StorageProvider {
    AsyncTaskBuilder<Note, Void> getSaveTask();
    AsyncTask<Note, Integer, Void> saveAsync(Note note);

    AsyncTaskBuilder<List<Note>, Void> getSaveManyTask();
    AsyncTask<List<Note>, Integer, Void> saveManyAsync(List<Note> notes);

    AsyncTaskBuilder<Object, Void> getReplaceTask();
    AsyncTask<Object, Integer, Void> replaceAsync(long id, Note note);

    AsyncTaskBuilder<Object, Void> getRefreshViewedDateTask();
    AsyncTask<Object, Integer, Void> refreshViewedDateAsync(long id, String visited);

    AsyncTaskBuilder<Long, Void> getDeleteTask();
    AsyncTask<Long, Integer, Void> deleteAsync(long id);

    AsyncTaskBuilder<Void, Void> getDeleteAllTask();
    AsyncTask<Void, Integer, Void> deleteAllAsync();

    AsyncTaskBuilder<Void, List<Note>> getGetAllTask();
    AsyncTask<Void, Integer, List<Note>> getAllAsync();

    AsyncTaskBuilder<String, List<Note>> getSearchBySubstringTask();
    AsyncTask<String, Integer, List<Note>> searchBySubstringAsync();
}
