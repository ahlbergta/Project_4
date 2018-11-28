package com.example.ahlbe.android_project_4;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static com.example.ahlbe.android_project_4.DatabaseManager.addPet;

public class CreatePetProfileActivity extends SecureActivity
{
    private static final int RESULT_LOAD_IMG = 7264;

    private static final String CONAN_ID = "0xd38dd9b09451";    // Autofill ConanID for testing\

    private static boolean PET_SAFE = true;
    private static final String TAG = "CreatePetActivity";
    private ImageButton mImageButton;
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
        mImageButton = findViewById(R.id.imageButton);
        mSubmit = findViewById(R.id.button_submit_pet_profile);
        mPetName = findViewById(R.id.edit_pet_name_edit);
        mPetNotes = findViewById(R.id.edit_pet_notes);
        mPetStatus = findViewById(R.id.toggle_pet_status);
        mConanID = findViewById(R.id.edit_conanID);
        mConanID.setText(CONAN_ID);

        mImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "In onClick for image button");
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG);
            }
        });

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

                // Upload the image
                UploadPetPicture();

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

    private void UploadPetPicture(){
        FirebaseStorage storage = FirebaseStorage.getInstance();

        // Create a storage reference from our app
        StorageReference storageRef = storage.getReference();

        // Create a reference to 'images/pets/<conanID>.jpg'
        StorageReference petImageRef = storageRef.child("images/pets/" + mConanID.getText().toString() + ".jpg");

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
}


