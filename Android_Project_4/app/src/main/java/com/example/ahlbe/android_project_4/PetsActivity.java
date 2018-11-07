package com.example.ahlbe.android_project_4;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class PetsActivity extends AppCompatActivity
{
    private static final String TAG = "PetsActivity";
    Button mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pets);
        mButton = findViewById(R.id.button_add_pet);
        mButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent createPetIntent = new Intent(PetsActivity.this, CreatePetProfileActivity.class);
                startActivity(createPetIntent);
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
            Intent loginIntent = new Intent(PetsActivity.this, LoginActivity.class);
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
