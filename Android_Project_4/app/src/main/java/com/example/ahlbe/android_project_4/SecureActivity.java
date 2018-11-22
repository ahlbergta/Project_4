package com.example.ahlbe.android_project_4;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SecureActivity extends AppCompatActivity {
    //This Method is called onResume of the all the activities except Login and Register. This method checks whether or not the user is authenticated.
    //This is basically a security check if a user somehow assesses the app without properly authenticating. If the user is not
    //authenticated, it will clear the activity stack to prevent the user from pressing the "back" button to an activity in the
    //app and then redirects them to the Login Activity.
    private void authenticationStateCheck() {
        //Log.d(TAG, "Inside checkauthenticationState method");
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser == null) {
            //Log.d(TAG, "user is null. Navigating back to login screen");
            Intent loginIntent = new Intent(this, LoginActivity.class);
            loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(loginIntent);
            finish();
        } else {
            //Log.d(TAG, "checked Authentication state: user is authenticated");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        authenticationStateCheck();
    }
}
