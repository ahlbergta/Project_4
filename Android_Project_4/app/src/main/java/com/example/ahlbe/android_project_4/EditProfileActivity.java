package com.example.ahlbe.android_project_4;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import static com.example.ahlbe.android_project_4.DatabaseManager.addUser;

import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity
{
    private static final String TAG = "EditProfileActivity";

    private EditText mEmail, mPAddress, mSAddress, mFirstName, mLastName, mPPhone, mSPhone, mNotes;
    private Button mSubmit;
    private FirebaseUser mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private Context mContext = this;

    private DocumentReference mDocumentReference = FirebaseFirestore.getInstance().document("users/" + mFirebaseUser.getUid());


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);
        //Set up all the widgets
        mEmail = findViewById(R.id.edit_email);
        mPAddress = findViewById(R.id.edit_p_address);
        mSAddress = findViewById(R.id.edit_s_address);
        mFirstName = findViewById(R.id.edit_first_name);
        mLastName = findViewById(R.id.edit_last_name);
        mPPhone = findViewById(R.id.edit_p_phone);
        mSPhone = findViewById(R.id.edit_s_phone);
        mSubmit = findViewById(R.id.button_submit);
        mNotes = findViewById(R.id.edit_notes);


        mSubmit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Map<String, String> user = new HashMap<>();

                addUser(user, mEmail, mFirstName, mLastName, mPPhone, mSPhone, mPAddress, mSAddress,
                        mNotes, mContext);
            }
        });
    }
    @Override
    protected void onResume()
    {
        super.onResume();
        authenticationStateCheck();
    }
    private void authenticationStateCheck()
    {
        Log.d(TAG, "Inside checkauthenticationState method");
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser == null)
        {
            Log.d(TAG, "user is null. Navigating back to login screen");
            Intent loginIntent = new Intent(EditProfileActivity.this, LoginActivity.class);
            loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(loginIntent);
            finish();
        }
        else
        {
            Log.d(TAG, "checked Authentication state: user is authenticated");
        }


    }
}
