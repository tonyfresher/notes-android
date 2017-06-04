package com.tasks.notes.helpers;


import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;

import com.tasks.notes.R;

public class NotificationWrapper {

    private NotificationCompat.Builder notifyBuilder;
    private NotificationManager notifyManager;
    private int notifyId;
    private String notifyTitle;
    private boolean indeterminate;

    private String performingText;


    public NotificationWrapper(Context context, int id, String title, boolean indeterminate) {
        this.notifyId = id;
        this.notifyTitle = title;
        this.indeterminate = indeterminate;

        notifyManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        notifyBuilder = new NotificationCompat.Builder(context);

        performingText = context.getString(R.string.performing);
    }

    public void start() {
        notifyBuilder.setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(notifyTitle)
                .setContentText(performingText)
                .setOngoing(true);

        notifyBuilder.setProgress(100, 0, indeterminate);
        notifyManager.notify(notifyId, notifyBuilder.build());
    }

    public void update(int value) {
        notifyBuilder.setProgress(100, value, false);
        notifyManager.notify(notifyId, notifyBuilder.build());
    }

    public void complete(String completedText) {
        notifyBuilder.setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(notifyTitle)
                .setContentText(completedText)
                .setOngoing(false);

        notifyManager.notify(notifyId, notifyBuilder.build());
    }

    public void close() {
        notifyManager.cancel(notifyId);
    }
}
