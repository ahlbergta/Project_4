package com.example.ahlbe.android_project_4;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import static com.example.ahlbe.android_project_4.DatabaseManager.updatePet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EditPetProfile extends AppCompatActivity
{
    private static final String TAG = "EditPetProfile";
    private Boolean NOTIFY_USER = true;
    private Context mContext;
    private Button mButtonSubmit;
    private ToggleButton mToggleButtonNotify;
    private TextView mTextViewPName, mTextViewPNotes, mTextViewStatus, mTextViewStatusShow, mTextViewConan,
            mTextViewNotify, mTextViewLastSafe, mTextViewDateShow;
    private EditText mEditTextPName, mEditTextConan, mEditTextPNotes;
    private int petPosition;
    private ArrayList<Pet> mPets = PetsActivity.getPets();
    private String documentID;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_pet_profile);
        mButtonSubmit = findViewById(R.id.button_submit_edit);
        mToggleButtonNotify = findViewById(R.id.toggle_notify_edit);
        mTextViewPName = findViewById(R.id.text_pet_name_edit);
        mTextViewPNotes = findViewById(R.id.text_pet_notes_edit);
        mTextViewStatus = findViewById(R.id.text_pet_status_edit);
        mTextViewStatusShow = findViewById(R.id.text_view_status_show_edit);
        mTextViewConan = findViewById(R.id.text_pet_conan_edit);
        mTextViewNotify = findViewById(R.id.text_pet_notify_user_edit);
        mTextViewLastSafe = findViewById(R.id.text_pet_last_seen_edit);
        mTextViewDateShow = findViewById(R.id.text_pet_date_edit);

        mEditTextPName = findViewById(R.id.edit_pet_name_edit);
        mEditTextConan = findViewById(R.id.edit_pet_conan_edit);
        mEditTextPNotes = findViewById(R.id.edit_pet_notes_edit);

        petPosition = getIntent().getExtras().getInt("petPosition");

        mEditTextPName.setText(mPets.get(petPosition).getpName());
        mEditTextPNotes.setText(mPets.get(petPosition).getpNotes());
        mEditTextConan.setText(mPets.get(petPosition).getConanID());

        mContext = this;

        mTextViewDateShow.setText(mPets.get(petPosition).getTimestamp().toDate().toString());
        if(mPets.get(petPosition).getpStatus() == 0)
        {
            mTextViewStatusShow.setText("Safe");
        }
        else
        {
            mTextViewStatusShow.setText("Lost");
        }
        mButtonSubmit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(mToggleButtonNotify.isChecked())
                {
                    NOTIFY_USER = true;
                }
                else
                {
                    NOTIFY_USER = false;
                }
                Map<String, Object> pet = new HashMap<>();
                documentID = mPets.get(petPosition).getDocumentID();
                Log.d(TAG, "this is document id " + documentID);
                updatePet(pet, mEditTextPName, mEditTextPNotes, mEditTextConan, documentID, NOTIFY_USER, mContext);
            }
        });





    }
}
