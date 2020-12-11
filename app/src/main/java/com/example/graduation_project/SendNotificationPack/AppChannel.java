package com.example.graduation_project.SendNotificationPack;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Build;

import androidx.annotation.RequiresApi;

public class AppChannel extends ContextWrapper {
    private static final String requestChannelId = "Request";
    private static final String requestChannelName = "graduation_project";

    private NotificationManager manager;

    public AppChannel(Context base) {
        super(base);

        if(Build.VERSION.SDK_INT >=Build.VERSION_CODES.O) {
            creatChannel();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void creatChannel() {
        NotificationChannel requestChannel = new NotificationChannel(requestChannelId,requestChannelName, NotificationManager.IMPORTANCE_DEFAULT);
        requestChannel.enableLights(true);
        requestChannel.enableVibration(true);
        requestChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);


        getManager().createNotificationChannel(requestChannel);
    }

    public NotificationManager getManager() {
        if (manager == null) {
            manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return manager;
    }

    @TargetApi(Build.VERSION_CODES.O)
    public Notification.Builder getNotifications (String title, String message, PendingIntent pIntent, int icon) {
        return new Notification.Builder(getApplicationContext(),requestChannelId).setContentIntent(pIntent)
                .setContentTitle(title).setContentText(message).setAutoCancel(true).setSmallIcon(icon);
    }
}
