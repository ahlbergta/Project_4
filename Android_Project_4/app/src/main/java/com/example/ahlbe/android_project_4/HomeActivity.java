package com.example.ahlbe.android_project_4;

import android.app.Activity;
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

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class HomeActivity extends SecureActivity {
    private static final String TAG = "HomeActivity";
    private Button mButtonPets, mButtonEditProfile, mButtonDeleteProfile, mButtonViewMap;
    private android.support.v7.widget.Toolbar mToolbar;
    private boolean isNewUser = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mButtonPets = findViewById(R.id.button_pets);
        mButtonEditProfile = findViewById(R.id.button_edit_profile);
        mButtonDeleteProfile = findViewById(R.id.button_delete_profile);
        mToolbar = findViewById(R.id.toolbar_home);
        mButtonViewMap = findViewById(R.id.button_map);
        setSupportActionBar(mToolbar);
        mButtonPets.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent petsActivity = new Intent(HomeActivity.this, PetsActivity.class);
                startActivity(petsActivity);
            }
        });
        mButtonEditProfile.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent editProfileActivity = new Intent(HomeActivity.this, EditProfileActivity.class);
                startActivity(editProfileActivity);
            }
        });
        mButtonDeleteProfile.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                DeleteProfileConfirmationDialog deleteProfileConfirmationDialog = new DeleteProfileConfirmationDialog();
                deleteProfileConfirmationDialog.show(getFragmentManager(), "delete_profile_dialog");
            }
        });
        mButtonViewMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent viewMapActivity = new Intent(HomeActivity.this, GoogleMaps.class);
                startActivity(viewMapActivity);
            }
        });
//
//        if(!isNewUser)
//        {
//            if(FirebaseAuth.getInstance().getCurrentUser() != null)
//            {
//                DocumentReference documentReference = FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
//
//                Log.d(TAG, "Inside isNewUser conditional");
//
//                documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>()
//                {
//                    @Override
//                    public void onSuccess(DocumentSnapshot documentSnapshot)
//                    {
//                        if (!documentSnapshot.exists())
//                        {
//                            Log.d(TAG, "inside documentSnapshot conditional");
//                            Intent createProfileIntent = new Intent(HomeActivity.this, CreateProfileActivity.class);
//                            startActivity(createProfileIntent);
//                            isNewUser = true;
//
//                        }

//                    }
//                });
//            }
//        }

    }

    @Override
    //Creates the Option Menu at the top of the Home Activity
    public boolean onCreateOptionsMenu(Menu menu) {
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
    public boolean onOptionsItemSelected(MenuItem item) {
        FirebaseAuth.getInstance().signOut();
        Intent loginIntent = new Intent(HomeActivity.this, LoginActivity.class);
        loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
        Toast.makeText(this, "Logged Out Successfully", Toast.LENGTH_SHORT).show();
        return super.onOptionsItemSelected(item);
    }
}
