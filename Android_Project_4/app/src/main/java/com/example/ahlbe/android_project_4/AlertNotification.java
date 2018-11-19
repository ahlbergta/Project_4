package com.example.ahlbe.android_project_4;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import java.util.Random;

public class AlertNotification {
    private static final CharSequence CHANNEL_NAME = "PawPrints_Alert_Channel";
    private static final String CHANNEL_ID = "C12345";
    private static final int SMALL_ICON = R.drawable.ic_launcher_background;
    private Context context;
    private NotificationManager notificationManager;
    private int id;

    public AlertNotification(Context context){
        this.context = context;
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        createNotificationChannel();
    }

    public int Notify(String title, String text){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(SMALL_ICON)
                .setContentTitle(title)
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        id = new Random().nextInt(1000);
        notificationManager.notify(id, builder.build());
        return id;
    }

    private void createNotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Channel for PawPrints alert notifications");
            notificationManager.createNotificationChannel(channel);
        }
    }
}
