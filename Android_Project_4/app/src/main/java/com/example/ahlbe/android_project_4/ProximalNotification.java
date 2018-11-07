package com.example.ahlbe.android_project_4;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.media.Image;
import android.util.Log;

import com.google.firebase.firestore.DocumentSnapshot;

public class ProximalNotification {
    private Context context;
    private String pet_name;
    private String pet_notes;
    private Image pet_image;

    public ProximalNotification(Context context, DocumentSnapshot pet) {
        Log.d("ProximalNotification", "Notification constructor");
        this.context = context;
        pet_name = (String) pet.get("pName");
        pet_notes = (String) pet.get("pNotes");
        pet_image = null;
        Notify();
    }

    private void Notify(){
        Log.d("ProximalNotification", "Nofify begin");
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification proximalNotification = new Notification(R.drawable.ic_launcher_foreground, pet_name, System.currentTimeMillis());

        Notification.Builder builder = new Notification.Builder(context);
        builder.setContentTitle(pet_name);
        builder.setContentText(pet_notes);
        builder.setSmallIcon(R.drawable.ic_launcher_foreground);
        builder.setOngoing(true);
        builder.build();
        proximalNotification = builder.getNotification();
        notificationManager.notify(9999, proximalNotification);
        Log.d("ProximalNotification", "Notify end");
    }
}
