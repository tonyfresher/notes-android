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

    private ParametrisedRunnable<Void, Void> mOnPreExecute;
    private ParametrisedRunnable<TArgs[], TResult> mDoInBackground;
    private ParametrisedRunnable<TResult, Void> mOnPostExecute;
    private ParametrisedRunnable<Integer[], Void> mOnProgressUpdate;


    public HandyTask(ParametrisedRunnable<TArgs[], TResult> doInBackground) {
        mDoInBackground = doInBackground;
    }

    public void setOnPreExecute(ParametrisedRunnable<Void, Void> onPreExecute) {
        mOnPreExecute = onPreExecute;
    }

    public void setOnPostExecute(ParametrisedRunnable<TResult, Void> onPostExecute) {
        mOnPostExecute = onPostExecute;
    }

    public void setOnProgressUpdate(ParametrisedRunnable<Integer[], Void> onProgressUpdate) {
        mOnProgressUpdate = onProgressUpdate;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (mOnPreExecute != null) {
            mOnPreExecute.run(null);
        }
        if (mHasNotification) {
            startNotification();
        }
    }

    @Override
    protected TResult doInBackground(TArgs... params) {
        return mDoInBackground.run(params);
    }

    @Override
    protected void onPostExecute(TResult tResult) {
        super.onPostExecute(tResult);
        if (mOnPostExecute != null) {
            mOnPostExecute.run(tResult);
        }
        if (mHasNotification) {
            completeNotification();
        }
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        if (mOnProgressUpdate != null) {
            mOnProgressUpdate.run(values);
        }
        if (mHasNotification) {
            updateNotification(values[0]);
        }
    }

    private boolean mHasNotification = false;
    private Context mContext;
    private NotificationCompat.Builder mBuilder;
    private NotificationManager mNotificationManager;
    private int mId;
    private String mTitle;

    public void setNotification(Context context, int id, String title) {
        mContext = context;
        mId = id;
        mTitle = title;
        mHasNotification = true;
    }

    private void startNotification() {
        mNotificationManager = (NotificationManager) mContext
                .getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(mContext);

        mBuilder.setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(mTitle)
                .setContentText(mContext.getString(R.string.performing));

        mBuilder.setProgress(100, 0, false);
        mNotificationManager.notify(mId, mBuilder.build());
    }

    private void updateNotification(int value) {
        mBuilder.setProgress(100, value, false);
        mNotificationManager.notify(mId, mBuilder.build());
    }

    private void completeNotification() {
        mBuilder.setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(mTitle)
                .setContentText(mContext.getString(R.string.completed));

        mNotificationManager.notify(mId, mBuilder.build());
    }
}
