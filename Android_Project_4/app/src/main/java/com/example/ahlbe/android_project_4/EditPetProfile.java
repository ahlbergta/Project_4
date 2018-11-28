package com.example.ahlbe.android_project_4;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import static com.example.ahlbe.android_project_4.DatabaseManager.updatePet;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EditPetProfile extends AppCompatActivity
{
    private static final int RESULT_LOAD_IMG = 7264;

    private static final long ONE_GIG = 1024 * 1024 * 1024;
    private static final String TAG = "EditPetProfile";
    private Boolean NOTIFY_USER = true;
    private Context mContext;
    private Button mButtonSubmit;
    private ToggleButton mToggleButtonNotify;
    private ImageButton mImageButton;
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
        mImageButton = findViewById(R.id.imageButton);
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

        // Get the pet image
        final String conanID = mEditTextConan.getText().toString();

        FirebaseStorage storage = FirebaseStorage.getInstance();
        // Create a storage reference from our app
        StorageReference storageRef = storage.getReference();
        // Create a reference to 'images/pets/<conanID>.jpg'
        StorageReference petImageRef = storageRef.child("images/pets/" + conanID + ".jpg");
        petImageRef.getBytes(ONE_GIG).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Log.d(TAG, "Success");
                Bitmap petImage = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                mImageButton.setImageBitmap(petImage);
            }
        });

        mImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "In onClick for image button");
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG);
            }
        });

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
                UploadPetPicture();
                Intent petIntent = new Intent(EditPetProfile.this, HomeActivity.class);
                startActivity(petIntent);
            }
        });
    }

    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);
        Log.d(TAG, "In activity result callback");
        if (resultCode == RESULT_OK) {
            Log.d(TAG, "Result code OK");
            try {
                Log.d(TAG, "Getting image");
                final Uri imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                mImageButton.setImageBitmap(selectedImage);
                Log.d(TAG, "Image updated");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void UploadPetPicture(){
        FirebaseStorage storage = FirebaseStorage.getInstance();

        // Create a storage reference from our app
        StorageReference storageRef = storage.getReference();

        // Create a reference to 'images/pets/<conanID>.jpg'
        StorageReference petImageRef = storageRef.child("images/pets/" + mEditTextConan.getText().toString() + ".jpg");

        Bitmap petPic = ((BitmapDrawable) mImageButton.getDrawable()).getBitmap();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        petPic.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = petImageRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Log.d(TAG, "Image upload failed");
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                Log.d(TAG, "Image upload successful");
            }
        });
    }
}
