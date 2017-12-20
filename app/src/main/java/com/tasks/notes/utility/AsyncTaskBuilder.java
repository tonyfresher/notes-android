package com.tasks.notes.utility;

import android.os.AsyncTask;

public class AsyncTaskBuilder<TArgs, TResult>
        extends AsyncTask<TArgs, Integer, TResult> {

    public interface ParametrisedRunnable<TA, TR> {
        TR run(TA params);
    }

    private final ParametrisedRunnable<TArgs[], TResult> doInBackground;
    private ParametrisedRunnable<Void, Void> onPreExecute;
    private ParametrisedRunnable<TResult, Void> onPostExecute;
    private ParametrisedRunnable<Integer[], Void> onProgressUpdate;

    public AsyncTaskBuilder(ParametrisedRunnable<TArgs[], TResult> doInBackground) {
        this.doInBackground = doInBackground;
    }

    public AsyncTaskBuilder<TArgs, TResult> setOnPreExecute(ParametrisedRunnable<Void, Void> onPreExecute) {
        this.onPreExecute = onPreExecute;
        return this;
    }

    public AsyncTaskBuilder<TArgs, TResult> setOnPostExecute(ParametrisedRunnable<TResult, Void> onPostExecute) {
        this.onPostExecute = onPostExecute;
        return this;
    }

    public AsyncTaskBuilder<TArgs, TResult> setOnProgressUpdate(ParametrisedRunnable<Integer[], Void> onProgressUpdate) {
        this.onProgressUpdate = onProgressUpdate;
        return this;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (onPreExecute != null) {
            onPreExecute.run(null);
        }
    }

    @Override
    protected TResult doInBackground(TArgs... params) {
        return doInBackground.run(params);
    }

    @Override
    protected void onPostExecute(TResult tResult) {
        super.onPostExecute(tResult);
        if (onPostExecute != null) {
            onPostExecute.run(tResult);
        }
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        if (onProgressUpdate != null) {
            onProgressUpdate.run(values);
        }
    }
}
