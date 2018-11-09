package com.example.ahlbe.android_project_4;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class PetsActivity extends AppCompatActivity
{
    private static final String TAG = "PetsActivity";
    Button mButton;
    private android.support.v7.widget.Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pets);
        mButton = findViewById(R.id.button_add_pet);
        mToolbar = findViewById(R.id.toolbar_pet);
        setSupportActionBar(mToolbar);
        mButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                CreatePetsDialog createPetsDialog = new CreatePetsDialog();
                createPetsDialog.show(getFragmentManager(), "dialog_create_pet_profile");
//                Context context = PetsActivity.this;
//                Intent createPetIntent = new Intent(PetsActivity.this, CreatePetProfileActivity.class);
//                startActivity(createPetIntent);
            }
        });
    }
    @Override
    //Creates the Option Menu at the top of the Home Activity
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home_menu, menu);
        return true;
    }

    /** This method is called when the user selects the Sign Out option from the options menu. This will sign the user out and
     * destroy the activity stack to prevent assess to previous activities.
     *
     * @param item that the user has clicked on
     * @return the user back to the login screen
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        FirebaseAuth.getInstance().signOut();
        Intent loginIntent = new Intent(PetsActivity.this, LoginActivity.class);
        loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
        Toast.makeText(this, "Logged Out Successfully", Toast.LENGTH_SHORT).show();
        return super.onOptionsItemSelected(item);
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