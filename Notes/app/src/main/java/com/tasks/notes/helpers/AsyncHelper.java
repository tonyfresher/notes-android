package com.tasks.notes.helpers;

import android.app.NotificationManager;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;

import com.tasks.notes.R;

public class AsyncHelper {
    public interface ParametrisedRunnable<TArgs, TResult> {
        TResult run(TArgs params);
    }

    private static AsyncHelper instance = new AsyncHelper();
    public static AsyncHelper getInstance() {
        return instance;
    }

    public class Task<TArgs, TResult>
            extends AsyncTask<TArgs, Integer, TResult>{
        private ParametrisedRunnable<Void, Void> mOnPreExecute;
        private ParametrisedRunnable<TArgs[], TResult> mDoInBackground;
        private ParametrisedRunnable<TResult, Void> mOnPostExecute;
        private ParametrisedRunnable<Integer[], Void> mOnProgressUpdate;


        public Task(ParametrisedRunnable<TArgs[], TResult> doInBackground) {
            this.mDoInBackground = doInBackground;
        }

        public void setOnPreExecute(ParametrisedRunnable<Void, Void> onPreExecute) {
            this.mOnPreExecute = onPreExecute;
        }

        public void setOnPostExecute(ParametrisedRunnable<TResult, Void> onPostExecute) {
            this.mOnPostExecute = onPostExecute;
        }

        public void setOnProgressUpdate(ParametrisedRunnable<Integer[], Void> onProgressUpdate) {
            this.mOnProgressUpdate = onProgressUpdate;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (mOnPreExecute != null) {
                mOnPreExecute.run(null);
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
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            if (mOnProgressUpdate != null) {
                mOnProgressUpdate.run(values);
            }
        }
    }


    public class NotificationTask<TArgs, TResult>
            extends Task<TArgs, TResult> {
        private NotificationCompat.Builder mBuilder;
        private NotificationManager mNotificationManager;
        private final Context mContext;

        private final int mId;
        private final String mTitle;

        public NotificationTask(Context context, int id, String title,
                                ParametrisedRunnable<TArgs[], TResult> doInBackground) {
            super(doInBackground);
            mContext = context;
            mId = id;
            mTitle = title;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mNotificationManager = (NotificationManager) mContext
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            mBuilder = new NotificationCompat.Builder(mContext);

            mBuilder.setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(mTitle)
                    .setContentText(mContext.getString(R.string.performing));

            mBuilder.setProgress(100, 0, false);
            mNotificationManager.notify(mId, mBuilder.build());
        }

        @Override
        protected void onPostExecute(TResult result) {
            super.onPostExecute(result);
            mBuilder.setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(mTitle)
                    .setContentText(mContext.getString(R.string.completed));

            mNotificationManager.notify(mId, mBuilder.build());
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            mBuilder.setProgress(100, values[0], false);
            mNotificationManager.notify(mId, mBuilder.build());
        }
    }
}
