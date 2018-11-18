package com.example.ahlbe.android_project_4;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class PetsActivity extends AppCompatActivity
{
    private static final String TAG = "PetsActivity";
    private Button mButtonAddPet;
    private android.support.v7.widget.Toolbar mToolbar;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private ArrayList<Pet> pets = new ArrayList<>();
    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pets);
        mButtonAddPet = findViewById(R.id.button_add_pet);
        mToolbar = findViewById(R.id.toolbar_pet);
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        setSupportActionBar(mToolbar);
        mButtonAddPet.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
//                CreatePetsDialog createPetsDialog = new CreatePetsDialog();
//                createPetsDialog.show(getFragmentManager(), "dialog_create_pet_profile");

                Context context = PetsActivity.this;
                Intent createPetIntent = new Intent(PetsActivity.this, CreatePetProfileActivity.class);
                startActivity(createPetIntent);
            }
        });
    }
    @Override
    //Creates the Option Menu at the top of the Home Activity
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home_menu, menu);
        return true;
    }

    /** This method is called when the user selects the Sign Out option from the options menu. This will sign the user out and
     * destroy the activity stack to prevent assess to previous activities.
     *
     * @param item that the user has clicked on
     * @return the user back to the login screen
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        FirebaseAuth.getInstance().signOut();
        Intent loginIntent = new Intent(PetsActivity.this, LoginActivity.class);
        loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
        Toast.makeText(this, "Logged Out Successfully", Toast.LENGTH_SHORT).show();
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onResume()
    {
        super.onResume();
        authenticationStateCheck();
        if(firebaseUser != null)
        {
            Log.d(TAG, "Inside the onResume, firebase is null");
            db.collection("Pets").whereArrayContains("owners", firebaseUser.getUid()).get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task)
                        {
                            if (task.isSuccessful())
                            {
                                for (QueryDocumentSnapshot documentSnapshot : task.getResult())
                                {
                                    Pet pet = new Pet(
                                            documentSnapshot.getString(getString(R.string.pet_name)),
                                            documentSnapshot.getString(getString(R.string.pet_notes)),
                                            documentSnapshot.getLong(getString(R.string.pet_status)),
                                            documentSnapshot.getTimestamp(getString(R.string.pet_last_safe)),
                                            documentSnapshot.getString(getString(R.string.pet_conan_id)),
                                            documentSnapshot.getBoolean(getString(R.string.pet_notify)),
                                            (ArrayList)documentSnapshot.get(getString(R.string.pet_owners)));
                                    pets.add(pet);
                                    Log.d(TAG, pet.getOwners().toString());
                                    Log.d(TAG, "This is the pets name " + documentSnapshot.get("pName"));
                                }
                            }
                            else
                            {
                                Log.d(TAG, "Something fragging happened");
                            }

                        }


                    });

        }
        ArrayAdapter<Pet> mPetArrayAdapter = new ArrayAdapter<Pet>(this, android.R.layout.simple_list_item_1, pets);
    }
    private void authenticationStateCheck()
    {
        Log.d(TAG, "Inside checkauthenticationState method");
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser == null)
        {
            Log.d(TAG, "user is null. Navigating back to login screen");
            Intent loginIntent = new Intent(PetsActivity.this, LoginActivity.class);
            loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(loginIntent);
            finish();
        }
        else
        {
            Log.d(TAG, "checked Authentication state: user is authenticated");
        }


    }
}
