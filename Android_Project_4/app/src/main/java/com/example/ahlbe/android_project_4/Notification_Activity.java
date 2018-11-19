package com.example.ahlbe.android_project_4;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import javax.annotation.Nullable;

public class Notification_Activity extends AppCompatActivity
{
    public static final String FIRST_NAME = "first";
    public static final String LAST_NAME = "last";
    public static final String TAG = "Notification_Activity";

    TextView mNameTextview;
    TextView mNameRealTime;
    String first;
    String last;

    private DocumentReference mDocumentReference = FirebaseFirestore.getInstance().document("Users/johnconnor");

    public void fetchData(View view)
    {
        mDocumentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>()
        {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot)
            {
                if(documentSnapshot.exists())
                {
                    first = documentSnapshot.getString(FIRST_NAME);
                    last = documentSnapshot.getString(LAST_NAME);
                    //mNameTextview.setText(first + " " + last);
                }
            }
        });

    }



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        mNameTextview = findViewById(R.id.name_first);
        mNameRealTime = findViewById(R.id.name_first_real_time);
        mDocumentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>()
        {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e)
            {
                if(documentSnapshot.exists())
                {
                    String first = documentSnapshot.getString(FIRST_NAME);
                    String last = documentSnapshot.getString(LAST_NAME);
                    mNameRealTime.setText(first + " " + last);

                }
                else if(e != null)
                {
                    Log.e(TAG, "Uh oh. You got an exception!", e);
                }
            }
        });



    }

    @Override
    protected void onStart()
    {
        super.onStart();
        mDocumentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>()
        {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e)
            {
                if(documentSnapshot.exists())
                {
                    String first = documentSnapshot.getString(FIRST_NAME);
                    String last = documentSnapshot.getString(LAST_NAME);
                    mNameRealTime.setText(first + " " + last);

                }
                else if(e != null)
                {
                    Log.e(TAG, "Uh oh. You got an exception!", e);
                }
            }
        });
    }

    public void goToSaveData(View view)
    {
        Intent intent = new Intent(this, SaveDataActivity.class);
        startActivity(intent);
    }

    @SuppressWarnings("deprecation")
    private void Notify(String notificationTitle, String notificationMessage)
    {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        @SuppressWarnings("deprecation")
        Notification notification = new Notification(R.drawable.ic_launcher_foreground, "New Message", System.currentTimeMillis());

        Intent notificationIntent = new Intent(Notification_Activity.this, Notification_Activity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        Notification.Builder builder = new Notification.Builder(Notification_Activity.this);
        builder.setContentIntent(pendingIntent);
        builder.setContentTitle("New Notification");
        builder.setContentText(first + " " + last);
        builder.setSmallIcon(R.drawable.ic_launcher_background);
        builder.setOngoing(true);
        builder.build();
        notification = builder.getNotification();

        //notification.setLatestEventInfo(Notification_Activity.this, notificationTitle, notificationMessage, pendingIntent);
        notificationManager.notify(9999, notification);
    }
    public void onClickNotify(View view)
    {
        Log.d(TAG, "in the onCLick");
        Notify("Title: First Name Fetched", "Msg: Returning Name");
    }


}
