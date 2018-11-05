package com.example.ahlbe.android_project_4;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class HomeActivity extends Activity
{
    private Button mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mButton = findViewById(R.id.button_pets);
        mButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent petsActivity = new Intent(HomeActivity.this, PetsActivity.class);
                startActivity(petsActivity);
            }
        });

    }
}
