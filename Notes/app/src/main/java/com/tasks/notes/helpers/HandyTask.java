package com.tasks.notes.helpers;

import android.app.NotificationManager;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;

import com.tasks.notes.R;

public class HandyTask<TArgs, TResult>
        extends AsyncTask<TArgs, Integer, TResult> {
    public interface ParametrisedRunnable<TA, TR> {
        TR run(TA params);
    }

    public interface ParametrisedRunnableWithTask<TA, TR> {
        TR run(HandyTask task, TA params);
    }

    private final ParametrisedRunnableWithTask<TArgs[], TResult> doInBackground;
    private ParametrisedRunnable<Void, Void> onPreExecute;
    private ParametrisedRunnable<TResult, Void> onPostExecute;
    private ParametrisedRunnable<Integer[], Void> onProgressUpdate;

    public HandyTask(ParametrisedRunnableWithTask<TArgs[], TResult> doInBackground) {
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
        if (hasNotification) {
            startNotification();
        }
    }

    @Override
    protected TResult doInBackground(TArgs... params) {
        return doInBackground.run(this, params);
    }

    @Override
    protected void onPostExecute(TResult tResult) {
        super.onPostExecute(tResult);
        if (onPostExecute != null) {
            onPostExecute.run(tResult);
        }
        if (hasNotification) {
            closeNotification();
        }
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        if (onProgressUpdate != null) {
            onProgressUpdate.run(values);
        }
        if (hasNotification) {
            updateNotification(values[0]);
        }
    }

    private boolean hasNotification = false;
    private Context context;
    private NotificationCompat.Builder notifyBuilder;
    private NotificationManager notifyManager;
    private int notifyId;
    private String notifyTitle;
    private boolean indeterminate;

    public void setNotification(Context context, int id, String title, boolean indeterminate) {
        this.context = context;
        this.notifyId = id;
        this.notifyTitle = title;
        this.indeterminate = indeterminate;
        hasNotification = true;
    }

    public void startNotification() {
        notifyManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        notifyBuilder = new NotificationCompat.Builder(context);

        notifyBuilder.setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(notifyTitle)
                .setContentText(context.getString(R.string.performing))
                .setOngoing(true);

        notifyBuilder.setProgress(100, 0, indeterminate);
        notifyManager.notify(notifyId, notifyBuilder.build());
    }

    public void updateNotification(int value) {
        notifyBuilder.setProgress(100, value, false);
        notifyManager.notify(notifyId, notifyBuilder.build());
    }

    public void closeNotification() {
        notifyManager.cancel(notifyId);
    }
}
