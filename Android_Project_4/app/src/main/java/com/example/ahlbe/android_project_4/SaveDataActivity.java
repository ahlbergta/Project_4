package com.example.ahlbe.android_project_4;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class SaveDataActivity extends AppCompatActivity
{
    private static final String TAG = "SaveDataActivity";
    private static final String FIRSTNAME = "first";
    private static final String LASTNAME = "last";
    EditText mFirstEditView;
    EditText mLastEditView;


    DocumentReference mDocumentReference = FirebaseFirestore.getInstance().document("Users/johnconnor");



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_data);
    }
    public void saveData(View view)
    {
        mFirstEditView = findViewById(R.id.first_edit_view);
        mLastEditView = findViewById(R.id.last_edit_view);
        String first = mFirstEditView.getText().toString();
        String last = mLastEditView.getText().toString();
        if(first.isEmpty() || last.isEmpty())
        {
            Toast toast = Toast.makeText(getApplicationContext(), "Please Fill Out Both Fields", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }


        mDocumentReference.update(FIRSTNAME, first).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid)
            {
                Log.d(TAG, "DocumentSnapshot successfully updated!");
                Toast toast = Toast.makeText(getApplicationContext(), "Document Updated Successfully!", Toast.LENGTH_SHORT);
                toast.show();
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                    }
                });
        mDocumentReference.update(LASTNAME, last).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "DocumentSnapshot successfully updated!");
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                    }
                });
    }
    public void goToFetchData(View view)
    {
        Intent intent = new Intent(this, Notification_Activity.class);
        startActivity(intent);
    }
}
