package com.tasks.notes.helpers;

import android.os.AsyncTask;

public class HandyTask<TArgs, TResult>
        extends AsyncTask<TArgs, Integer, TResult> {
    public interface ParametrisedRunnable<TA, TR> {
        TR run(TA params);
    }

    private final ParametrisedRunnable<TArgs[], TResult> doInBackground;
    private ParametrisedRunnable<Void, Void> onPreExecute;
    private ParametrisedRunnable<TResult, Void> onPostExecute;
    private ParametrisedRunnable<Integer[], Void> onProgressUpdate;

    public HandyTask(ParametrisedRunnable<TArgs[], TResult> doInBackground) {
        this.doInBackground = doInBackground;
    }

    public void setOnPreExecute(ParametrisedRunnable<Void, Void> onPreExecute) {
        this.onPreExecute = onPreExecute;
    }

    public void setOnPostExecute(ParametrisedRunnable<TResult, Void> onPostExecute) {
        this.onPostExecute = onPostExecute;
    }

    public void setOnProgressUpdate(ParametrisedRunnable<Integer[], Void> onProgressUpdate) {
        this.onProgressUpdate = onProgressUpdate;
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
