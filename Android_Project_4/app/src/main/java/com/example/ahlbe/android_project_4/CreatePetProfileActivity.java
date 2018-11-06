package com.example.ahlbe.android_project_4;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

public class CreatePetProfileActivity extends Activity
{
    private static final String TAG = "CreatePetActivity";
    private Button mSubmit;
    private LayoutInflater mLayoutInflater;
    private LinearLayout mLinearLayout;
    private EditText mPetName;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_pet_profile);
        mLayoutInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View mView = mLayoutInflater.inflate(R.layout.activity_pets, null);
        mLinearLayout = mView.findViewById(R.id.layout_activity_pets);
        final Button mButton = new Button(this);
        mSubmit = findViewById(R.id.button_submit_pet_profile);
        mPetName = findViewById(R.id.edit_pet_name);
        mSubmit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                mButton.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                mButton.setText(mPetName.getText().toString());
                mLinearLayout.addView(mButton);
                Log.d(TAG, "This is the Linear Layout thingy " + mLinearLayout);
                Intent petIntent = new Intent(CreatePetProfileActivity.this, PetsActivity.class);
                startActivity(petIntent);

            }
        });


    }
}
