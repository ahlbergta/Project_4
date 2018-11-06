package com.example.ahlbe.android_project_4;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class PetsActivity extends Activity
{
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
}
