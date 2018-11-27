package com.example.ahlbe.android_project_4;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ToggleButton;

import java.util.HashMap;
import java.util.Map;

import static com.example.ahlbe.android_project_4.DatabaseManager.addPet;

public class CreatePetProfileActivity extends SecureActivity
{
    private static final String CONAN_ID = "0xd38dd9b09451";
    private static boolean PET_SAFE = true;
    private static final String TAG = "CreatePetActivity";
    private Button mSubmit;
    private ToggleButton mPetStatus;
    private LayoutInflater mLayoutInflater;
    private LinearLayout mLinearLayout;
    private EditText mPetName, mPetNotes, mConanID;
    private Context mContext = this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_pet_profile);
//        mLayoutInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View mView = mLayoutInflater.inflate(R.layout.activity_pets, null);
//        mLinearLayout = mView.findViewById(R.id.layout_activity_pets);
//        mLinearLayout.setOrientation(LinearLayout.VERTICAL);
//        final Button mButton = new Button(this);
        mSubmit = findViewById(R.id.button_submit_pet_profile);
        mPetName = findViewById(R.id.edit_pet_name_edit);
        mPetNotes = findViewById(R.id.edit_pet_notes);
        mPetStatus = findViewById(R.id.toggle_pet_status);
        mConanID = findViewById(R.id.edit_conanID);
        mConanID.setText(CONAN_ID);

        mSubmit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
//                mButton.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER_HORIZONTAL));
//                mButton.setText(mPetName.getText().toString());
//                mLinearLayout.addView(mButton);

                // Add conan id to the list of owned pets
                PawPrints_Application app = (PawPrints_Application) getApplication();
                app.AddPet(mConanID.getText().toString());

                if(mPetStatus.isChecked()) {
                    PET_SAFE = false;
                }
                Map<String, Object> pet = new HashMap<>();
                addPet(pet, mPetName, mPetNotes, mContext,PET_SAFE, mConanID);
                Log.d(TAG, "This is the Linear Layout thingy " + mLinearLayout);
                Intent petIntent = new Intent(CreatePetProfileActivity.this, HomeActivity.class);
                startActivity(petIntent);

            }
        });
    }
}


