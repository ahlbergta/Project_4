package com.example.ahlbe.android_project_4;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
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
import com.google.firebase.firestore.GeoPoint;


import static com.example.ahlbe.android_project_4.DatabaseManager.addUser;
import static com.example.ahlbe.android_project_4.DatabaseManager.fetchUser;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class EditProfileActivity extends SecureActivity {
    private static final String TAG = "EditProfileActivity";

    private EditText mEmail, mPAddress, mSAddress, mFirstName, mLastName, mPPhone, mSPhone, mNotes;
    private Button mSubmit;
    private FirebaseUser mFirebaseUser;
    private Context mContext = this;
    private Toolbar mToolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        setContentView(R.layout.activity_create_profile);
        //Set up all the widgets
        mEmail = findViewById(R.id.edit_email);
        mEmail.setText(mFirebaseUser.getEmail());
        mPAddress = findViewById(R.id.edit_p_address);
        mSAddress = findViewById(R.id.edit_s_address);
        mFirstName = findViewById(R.id.edit_first_name);
        mLastName = findViewById(R.id.edit_last_name);
        mPPhone = findViewById(R.id.edit_p_phone);
        mSPhone = findViewById(R.id.edit_s_phone);
        mSubmit = findViewById(R.id.button_submit_edit);
        mNotes = findViewById(R.id.edit_notes);
        mToolbar = findViewById(R.id.toolbar_register_edit);
        setSupportActionBar(mToolbar);
        //Fetch user information and set the fields
        fetchUser(mEmail, mFirstName, mLastName, mPPhone, mSPhone, mPAddress, mSAddress, mNotes);


        if(mFirebaseUser != null) {
            mSubmit.setOnClickListener(new View.OnClickListener()
            {

                //private DocumentReference mDocumentReference = FirebaseFirestore.getInstance().document("users/" + mFirebaseUser.getUid());

                @Override
                public void onClick(View view)
                {
                    Map<String, Object> user = new HashMap<>();

                    // Get a geopoint from the user's address
                    GeoPoint primary_geopoint = null;
                    GeoPoint secondary_geopoint = null;
                    Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
                    if(Geocoder.isPresent()){
                        if(!mPAddress.getText().toString().equals("")) {
                            try {
                                List<Address> primary_address_list = geocoder.getFromLocationName(mPAddress.getText().toString(), 1);
                                Log.d(TAG, primary_address_list.toString());
                                Address primary_address = primary_address_list.get(0);
                                primary_geopoint = new GeoPoint(primary_address.getLatitude(), primary_address.getLongitude());
                            } catch (IOException e) {
                                e.printStackTrace();
                                primary_geopoint = null;
                            }
                        }
                        if(!mSAddress.getText().toString().equals("")){
                            try {
                                List<Address> secondary_address_list = geocoder.getFromLocationName(mSAddress.getText().toString(), 1);
                                Log.d(TAG, secondary_address_list.toString());
                                Address secondary_address = secondary_address_list.get(0);
                                secondary_geopoint = new GeoPoint(secondary_address.getLatitude(), secondary_address.getLongitude());
                            } catch (IOException e) {
                                e.printStackTrace();
                                secondary_geopoint = null;
                            }
                        }
                    }

                    addUser(user, mEmail, mFirstName, mLastName, mPPhone, mSPhone, mPAddress, mSAddress, mNotes, mContext, primary_geopoint, secondary_geopoint);
                    Intent homeIntent = new Intent(EditProfileActivity.this, HomeActivity.class);
                    startActivity(homeIntent);
                    finish();
                }

            });
        }
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
        Intent loginIntent = new Intent(EditProfileActivity.this, LoginActivity.class);
        loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
        Toast.makeText(this, "Logged Out Successfully", Toast.LENGTH_SHORT).show();
        return super.onOptionsItemSelected(item);
    }
}
