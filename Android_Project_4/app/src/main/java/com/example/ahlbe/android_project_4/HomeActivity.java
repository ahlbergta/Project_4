package com.example.ahlbe.android_project_4;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class HomeActivity extends Activity
{
    private Button mButtonPets;
    private Button mButtonEditProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mButtonPets = findViewById(R.id.button_pets);
        mButtonEditProfile = findViewById(R.id.button_edit_profile);
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

    }
}
