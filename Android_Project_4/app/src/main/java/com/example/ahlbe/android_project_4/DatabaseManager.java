package com.example.ahlbe.android_project_4;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

/**
 * Database Class to handle Adding and Fetching Data in Firestore.
 */
class DatabaseManager
{
    private static final String TAG = "DatabaseManagerClass";
    static FirebaseUser mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();


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
    static void addUser(Map<String, String> user, EditText email, EditText first, EditText last,
                        EditText pPhone, EditText sPhone, EditText pAddress, EditText s_address,
                        EditText notes, final Context context)
    {
        CollectionReference mCollectionReference = FirebaseFirestore.getInstance().collection("Users");
        user.put("email", email.getText().toString());
        user.put("first_name", first.getText().toString());
        user.put("last_name", last.getText().toString());
        user.put("p_phone", pPhone.getText().toString());
        user.put("s_phone", sPhone.getText().toString());
        user.put("p_address", pAddress.getText().toString());
        user.put("s_address",s_address.getText().toString());
        user.put("notes", notes.getText().toString());
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
        DocumentReference mDocumentReference = FirebaseFirestore.getInstance().collection("Users").document(mFirebaseUser.getUid());
        user.put("email", email.getText().toString());
        user.put("first_name", first.getText().toString());
        user.put("last_name", last.getText().toString());
        user.put("p_phone", pPhone.getText().toString());
        user.put("s_phone", sPhone.getText().toString());
        user.put("p_address", pAddress.getText().toString());
        user.put("s_address",s_address.getText().toString());
        user.put("notes", notes.getText().toString());
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
    static void fetchUser(final EditText email, final EditText first, final EditText last,
                          final EditText pPhone, final EditText sPhone, final EditText pAddress, final EditText s_address,
                          final EditText notes)
    {
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

}