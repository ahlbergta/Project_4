package com.example.ahlbe.android_project_4;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class PetsInformationActivity extends AppCompatActivity
{
    private static final String TAG = "PetsInformationActivity";
    private Button mButtonEdit, mButtonLost;
    private TextView mTextViewPName, mTextViewPNotes, mTextViewLastSafe, mTextViewStatus, mTextViewPConanID;
    private int petPosition;
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
        mButtonEdit = findViewById(R.id.button_edit_information);
        petPosition = getIntent().getExtras().getInt("petPosition");
        mPets = PetsActivity.getPets();
        mTextViewPName.setText("Pet Name: " + mPets.get(petPosition).getpName());
        mTextViewPNotes.setText("Pet Notes: " + mPets.get(petPosition).getpNotes());
        mTextViewLastSafe.setText(mPets.get(petPosition).getTimestamp().toDate().toString());
        if(mPets.get(petPosition).getpStatus() == 0)
        {
            mTextViewStatus.setText("Safe");
        }
        else
        {
            mTextViewStatus.setText("Lost");
        }
        mTextViewPConanID.setText(mPets.get(petPosition).getConanID());
        mButtonEdit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intentEditPetInformation = new Intent(PetsInformationActivity.this, EditPetProfile.class);
                intentEditPetInformation.putExtra("documentID", mPets.get(petPosition).getDocumentID());
                intentEditPetInformation.putExtra("petPosition", petPosition);
                startActivity(intentEditPetInformation);
                finish();
            }
        });


    }
}
