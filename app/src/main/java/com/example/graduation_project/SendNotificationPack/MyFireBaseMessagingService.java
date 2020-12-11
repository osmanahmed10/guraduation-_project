package com.example.graduation_project.SendNotificationPack;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.example.graduation_project.R;
import com.example.graduation_project.home;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class MyFireBaseMessagingService extends FirebaseMessagingService {

    private String title, message, receiver;

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            sendOAndAboveNotification(remoteMessage);
        } else {
            sendNormalNotification(remoteMessage);
        }

        /*Intent intent = new Intent(this, home.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,10,intent,PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,AppChannel.requestChannelId)
                .setContentTitle(title).setContentText(Message).setSmallIcon(R.drawable.applogo)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT).setContentIntent(pendingIntent)
                .setAutoCancel(true);

        int id = (int) System.currentTimeMillis();

        manager.notify(id, builder.build());*/
    }

    private void sendOAndAboveNotification(RemoteMessage remoteMessage) {
        title = remoteMessage.getData().get("title");
        message = remoteMessage.getData().get("Message");
        int id = (int) System.currentTimeMillis();

        RemoteMessage.Notification notification = remoteMessage.getNotification();
        Intent intent = new Intent(this,home.class);
        PendingIntent pIntent = PendingIntent.getActivity(this,id,intent,PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setContentTitle(title).setContentText(message).setSmallIcon(R.drawable.applogo)
                .setContentIntent(pIntent).setAutoCancel(true);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(id,builder.build());

    }

    private void sendNormalNotification(RemoteMessage remoteMessage) {
        title = remoteMessage.getData().get("title");
        message = remoteMessage.getData().get("Message");
        int id = (int) System.currentTimeMillis();

        RemoteMessage.Notification notification = remoteMessage.getNotification();
        Intent intent = new Intent(this,home.class);
        PendingIntent pIntent = PendingIntent.getActivity(this,id,intent,PendingIntent.FLAG_ONE_SHOT);

        AppChannel channel = new AppChannel(this);

        Notification.Builder builder = channel.getNotifications(title,message,pIntent,R.drawable.applogo);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        channel.getManager().notify(id,builder.build());
    }
}
