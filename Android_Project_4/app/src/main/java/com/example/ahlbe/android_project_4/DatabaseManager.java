package com.example.ahlbe.android_project_4;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Map;

/**
 * Database Class to handle Adding and Fetching Data in Firestore.
 */


public class DatabaseManager {
    private static final String TAG = "DatabaseManagerClass";
    private static FirebaseUser mFirebaseUser;

//    --------------- Test Code
    private static int PET_STATUS = 0;
    private static final Timestamp PET_LAST_SAFE = new Timestamp(12, 12);
    private static final boolean NOTIFY_PING_USER = false;
    private static final ArrayList<String> OWNERS = new ArrayList<>();
//    --------------- End Test Code




    /**
     * addUser Method takes the text from all the EditText from the CreateProfileActivity and add the data to
     * Firebase using a HashMap
     *
     * @param user  HashMap to add the key-value pairs to add to Firebase
     * @param email text input from user in the EditText View. Same for the rest below.
     * @param first
     * @param last
     * @param pPhone
     * @param sPhone
     * @param pAddress
     * @param s_address
     * @param notes
     * @param context Context passed from the Activity to create a Toast
     */
    static void addUser(Map<String, Object> user, EditText email, EditText first, EditText last,
                        EditText pPhone, EditText sPhone, EditText pAddress, EditText s_address,
                        EditText notes, final Context context, GeoPoint geoPAddress, GeoPoint geoSAddress)
    {
        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        CollectionReference mCollectionReference = FirebaseFirestore.getInstance().collection("Users");
        user.put("email", email.getText().toString());
        user.put("first_name", first.getText().toString());
        user.put("last_name", last.getText().toString());
        user.put("p_phone", pPhone.getText().toString());
        user.put("s_phone", sPhone.getText().toString());
        user.put("p_address", pAddress.getText().toString());
        user.put("s_address",s_address.getText().toString());
        user.put("notes", notes.getText().toString());
        user.put(context.getString(R.string.user_primary_address_geopoint),geoPAddress);
        user.put(context.getString(R.string.user_secondary_address_geopoint), geoSAddress);
        mCollectionReference.document(mFirebaseUser.getUid()).set(user).addOnCompleteListener(new OnCompleteListener<Void>()
        {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                Log.d(TAG, "Document Sucessfully Updated!");
                Toast.makeText(context, "Document was uploaded Successfully", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener()
        {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                Log.d(TAG, "Document was not uploaded", e);
                Toast.makeText(context, "Document was not uploaded", Toast.LENGTH_SHORT).show();
            }
        });
    }
    /**
     * updateUser Method takes the text from all the EditText from the EditProfileActivity and updates the user in
     * Firebase using a HashMap
     *
     * @param user  HashMap to add the key-value pairs to add to Firebase
     * @param email text input from user in the EditText View. Same for the rest below.
     * @param first
     * @param last
     * @param pPhone
     * @param sPhone
     * @param pAddress
     * @param s_address
     * @param notes
     * @param context Context passed from the Activity to create a Toast
     */
    static void updateUser(Map<String, Object> user, EditText email, EditText first, EditText last,
                           EditText pPhone, EditText sPhone, EditText pAddress, EditText s_address,
                           EditText notes, final Context context)
    {
        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DocumentReference mDocumentReference = FirebaseFirestore.getInstance().collection("Users").document(mFirebaseUser.getUid());
        user.put(context.getString(R.string.user_first_name), email.getText().toString());
        user.put(context.getString(R.string.user_last_name), first.getText().toString());
        user.put(context.getString(R.string.user_email), last.getText().toString());
        user.put(context.getString(R.string.user_primary_phone), pPhone.getText().toString());
        user.put(context.getString(R.string.user_secondary_address), sPhone.getText().toString());
        user.put(context.getString(R.string.user_primary_phone), pAddress.getText().toString());
        user.put(context.getString(R.string.user_secondary_address),s_address.getText().toString());
        user.put(context.getString(R.string.user_notes), notes.getText().toString());
        mDocumentReference.update(user).addOnCompleteListener(new OnCompleteListener<Void>()
        {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                Log.d(TAG, "Profile was updated successfully!");
                Toast.makeText(context, "Profile was updated successfully", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener()
        {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                Log.d(TAG, "an exception was encountered");
                Toast.makeText(context, "Profile was not updated!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    /**
     * fetchUser Method takes the data from the profile in Firestore and sets the text in the EditText view in
     * the EditProfileActivity. This is to indicate what the user's data has stored in the database.
     *
     * @param email text input from user in the EditText View. Same for the rest below.
     * @param first
     * @param last
     * @param pPhone
     * @param sPhone
     * @param pAddress
     * @param s_address
     * @param notes
     */
    static void fetchUser(final EditText email, final EditText first, final EditText last,
                          final EditText pPhone, final EditText sPhone, final EditText pAddress, final EditText s_address,
                          final EditText notes)
    {
        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final DocumentReference documentReference = FirebaseFirestore.getInstance().collection("Users").document(mFirebaseUser.getUid());
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>()
        {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot)
            {
                if(documentSnapshot.exists())
                {
                    email.setText(documentSnapshot.getString("email"));
                    first.setText(documentSnapshot.getString("first_name"));
                    last.setText(documentSnapshot.getString("last_name"));
                    pPhone.setText(documentSnapshot.getString("p_phone"));
                    sPhone.setText(documentSnapshot.getString("s_phone"));
                    pAddress.setText(documentSnapshot.getString("p_address"));
                    s_address.setText(documentSnapshot.getString("s_address"));
                    notes.setText(documentSnapshot.getString("notes"));

                }
            }
        });

    }
    static void addPet(Map<String, Object> pet, final EditText petName, final EditText petNotes, final Context context,
                       boolean petSafe, final EditText conanID)
    {
        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        OWNERS.add(mFirebaseUser.getUid());

        CollectionReference collectionReference = FirebaseFirestore.getInstance().collection("Pets");
        pet.put(context.getString(R.string.pet_name), petName.getText().toString());
        pet.put(context.getString(R.string.pet_notes), petNotes.getText().toString());
        pet.put(context.getString(R.string.pet_conan_id), conanID.getText().toString());
        if(petSafe)
        {
            pet.put(context.getString(R.string.pet_status), PET_STATUS);
        }
        else
        {
            PET_STATUS = 1;
            pet.put(context.getString(R.string.pet_status), PET_STATUS);
        }
        pet.put(context.getString(R.string.pet_status), PET_STATUS);
        pet.put(context.getString(R.string.pet_last_safe), PET_LAST_SAFE);
        pet.put(context.getString(R.string.pet_notify), NOTIFY_PING_USER);
        pet.put(context.getString(R.string.pet_owners), OWNERS);

        collectionReference.add(pet).addOnCompleteListener(new OnCompleteListener<DocumentReference>()
        {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task)
            {
                Log.d(TAG, "Inside on complete");
                Toast.makeText(context, "Pet added Successfully", Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener()
        {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                Log.d(TAG, "Inside onFailure: Failed to add pet");
            }
        });



    }
    static void updatePet(final Map<String, Object> pet, EditText pName, EditText pNotes, EditText conanID, String documentID, Boolean notify, Context context)
    {
        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        pet.put(context.getString(R.string.pet_name), pName.getText().toString());
        pet.put(context.getString(R.string.pet_notes), pNotes.getText().toString());
        pet.put(context.getString(R.string.pet_conan_id),conanID.getText().toString());
        pet.put(context.getString(R.string.pet_notify),notify);
        final DocumentReference documentReference = FirebaseFirestore.getInstance().collection("Pets").document(documentID);
        documentReference.update(pet).addOnSuccessListener(new OnSuccessListener<Void>()
        {
            @Override
            public void onSuccess(Void aVoid)
            {
               Log.d(TAG, "updated pet successfully");
            }
        }).addOnFailureListener(new OnFailureListener()
        {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                Log.d(TAG, "Something happened" + e.getMessage());
            }
        });




    }

}
