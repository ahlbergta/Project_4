package com.example.ahlbe.android_project_4;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;

public class EditProfileActivity extends AppCompatActivity
{
    private FirebaseAuth mAuth;
    //private EditText;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
    }
}
