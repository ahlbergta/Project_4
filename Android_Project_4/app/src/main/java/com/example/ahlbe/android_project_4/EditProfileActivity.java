package com.example.ahlbe.android_project_4;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;



import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity
{
    private static final String TAG = "EditProfileActivity";
    public static final String EMAIL = "email";
    public static final String FIRSTNAME = "first_name";
    public static final String LASTNAME = "last_name";
    public static final String PRIMARYPHONE = "p_phone";
    public static final String SECONDARYPHONE = "s_phone";
    public static final String PRIMARYADDRESS = "p_address";
    public static final String SECONDARYADDRESS = "s_address";
    public static final String NOTES = "notes";

    //Set up Widgets
    private FirebaseAuth mAuth;
    private EditText mEmail, mPAddress, mSAddress, mFirstName, mLastName, mPPhone, mSPhone, mNotes;
    private Button mSubmit;
    //Firestore Reference
    private CollectionReference mCollectionReference = FirebaseFirestore.getInstance().collection("users");

    //Setup data entries
    //String email, first_name, last_name, notes, p_address, s_address, p_phone, s_phone;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
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


        //Get User
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user != null)
        {
            mEmail.setText(user.getEmail());
        }

        mSubmit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Map<String, String> updateUser = new HashMap<>();
                addData(updateUser);

                Log.d(TAG, "Inside the onCLick");
                mCollectionReference.document(user.getUid()).set(updateUser);

            }
        });

    }
    protected Map<String, String> addData(Map<String, String> updateUser)
    {

        updateUser.put(EMAIL, mEmail.getText().toString());
        updateUser.put(FIRSTNAME, mFirstName.getText().toString());
        updateUser.put(LASTNAME, mLastName.getText().toString());
        updateUser.put(PRIMARYPHONE, mPPhone.getText().toString());
        updateUser.put(SECONDARYPHONE, mPPhone.getText().toString());
        updateUser.put(PRIMARYADDRESS, mPAddress.getText().toString());
        updateUser.put(SECONDARYADDRESS,mSAddress.getText().toString());
        updateUser.put(NOTES, mNotes.getText().toString());
        return updateUser;
    }


}
