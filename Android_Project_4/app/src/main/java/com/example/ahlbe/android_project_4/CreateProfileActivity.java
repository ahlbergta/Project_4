package com.example.ahlbe.android_project_4;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import static com.example.ahlbe.android_project_4.DatabaseManager.addUser;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class CreateProfileActivity extends SecureActivity {
    private static final String TAG = "CreateProfileActivity";

    //Set up the data entries

    //Set up Widgets
    private FirebaseAuth mAuth;
    private EditText mEmail, mPAddress, mSAddress, mFirstName, mLastName, mPPhone, mSPhone, mNotes;
    private Button mSubmit;
    private Context mContext = this;

    //Firestore Reference
    private CollectionReference mCollectionReference = FirebaseFirestore.getInstance().collection("users/");

    //Setup data entries
    //String email, first_name, last_name, notes, p_address, s_address, p_phone, s_phone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
        mSubmit = findViewById(R.id.button_submit_edit);
        mNotes = findViewById(R.id.edit_notes);


        mSubmit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Log.d(TAG, "Inside the onCLick");

                Map<String, String> updateUser = new HashMap<>();
//                addUser(updateUser, mEmail, mFirstName, mLastName, mPPhone, mSPhone, mPAddress, mSAddress,
//                        mNotes, mContext);

                Log.d(TAG, "Inside the onCLick");
                Intent homeIntent = new Intent(CreateProfileActivity.this, HomeActivity.class);
                startActivity(homeIntent);
                finish();
            }
        });
    }
}
