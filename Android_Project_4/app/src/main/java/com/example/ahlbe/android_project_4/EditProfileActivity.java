package com.example.ahlbe.android_project_4;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import static com.example.ahlbe.android_project_4.DatabaseManager.addUser;
import static com.example.ahlbe.android_project_4.DatabaseManager.fetchUser;
import static com.example.ahlbe.android_project_4.DatabaseManager.updateUser;

import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity
{
    private static final String TAG = "EditProfileActivity";

    private EditText mEmail, mPAddress, mSAddress, mFirstName, mLastName, mPPhone, mSPhone, mNotes;
    private Button mSubmit;
    private FirebaseUser mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private Context mContext = this;
    private Toolbar mToolbar;

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
        mToolbar = findViewById(R.id.toolbar_register_edit);
        setSupportActionBar(mToolbar);
        //Fetch user information and set the fields
        fetchUser(mEmail, mFirstName, mLastName, mPPhone, mSPhone, mPAddress, mSAddress, mNotes);



        mSubmit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Map<String, Object> user = new HashMap<>();

                updateUser(user, mEmail, mFirstName, mLastName, mPPhone, mSPhone, mPAddress, mSAddress,
                        mNotes, mContext);
                Intent homeIntent = new Intent(EditProfileActivity.this, HomeActivity.class);
                startActivity(homeIntent);
                finish();
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
        Intent loginIntent = new Intent(EditProfileActivity.this, LoginActivity.class);
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
    //This Method is called onResume of the all the activities except Login and Register. This method checks whether or not the user is authenticated.
    //This is basically a security check if a user somehow assesses the app without properly authenticating. If the user is not
    //authenticated, it will clear the activity stack to prevent the user from pressing the "back" button to an activity in the
    //app and then redirects them to the Login Activity.
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
