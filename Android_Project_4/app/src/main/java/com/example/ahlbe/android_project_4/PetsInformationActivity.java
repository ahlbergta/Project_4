package com.example.ahlbe.android_project_4;

import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

public class PetsInformationActivity extends AppCompatActivity
{
    private Button mButtonEdit, mButtonLost;
    private TextView mTextViewPName, mTextViewPNotes, mTextViewLastSafe, mTextViewStatus, mTextViewPConanID;
    private int petPostion;
    private ArrayList<Pet> mPets;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pets_information);
        mTextViewPName = findViewById(R.id.text_information_name);
        mTextViewPNotes = findViewById(R.id.text_information_notes);
        mTextViewLastSafe = findViewById(R.id.text_information_last_safe);
        mTextViewStatus = findViewById(R.id.text_information_status);
        mTextViewPConanID = findViewById(R.id.text_information_conanID);
        petPostion = getIntent().getExtras().getInt("petPosition");
        mPets = PetsActivity.getPets();
        mTextViewPName.setText(mPets.get(petPostion).getpName());
        mTextViewPNotes.setText(mPets.get(petPostion).getpNotes());
        mTextViewLastSafe.setText(mPets.get(petPostion).getTimestamp().toDate().toString());
        if(mPets.get(petPostion).getpStatus() == 0)
        {
            mTextViewStatus.setText("Safe");
        }
        else
        {
            mTextViewStatus.setText("Lost");
        }
        mTextViewPConanID.setText(mPets.get(petPostion).getConanID());

    }
}
