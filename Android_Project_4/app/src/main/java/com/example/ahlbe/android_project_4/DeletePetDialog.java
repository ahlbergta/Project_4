package com.example.ahlbe.android_project_4;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class DeletePetDialog extends DialogFragment
{
    private static final String TAG = "DeletePetDialog";

    private Button mButtonCancel, mButtonConfirm;
    private Context mContext;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private int petPosition;
    private ArrayList<Pet> mPetArrayList = PetsActivity.getPets();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.dialog_delete_pet, container, false);

        mContext = getActivity();

        petPosition = PetsInformationActivity.getPetPosition();
        mButtonCancel = view.findViewById(R.id.cancel_deletion_pet);
        mButtonConfirm = view.findViewById(R.id.dialog_deletion_confirm_pet);

        mButtonCancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                getDialog().dismiss();
            }
        });
        mButtonConfirm.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                db.collection(mContext.getResources().getString(R.string.pet_collection))
                        .document(mPetArrayList.get(petPosition).getDocumentID()).delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>()
                        {
                            @Override
                            public void onSuccess(Void aVoid)
                            {
                                Log.d(TAG, "inside the onsuccess for delete pet");
                                getDialog().dismiss();
                                Toast.makeText(mContext, "Pet Profile Successfully Deleted!", Toast.LENGTH_SHORT).show();
                                Intent intentHome = new Intent(mContext.getApplicationContext(), HomeActivity.class);
                                intentHome.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intentHome);
                            }
                        }).addOnFailureListener(new OnFailureListener()
                {
                    @Override
                    public void onFailure(@NonNull Exception e)
                    {
                        Log.d(TAG, "inside the onfailure for delete pet" + e.getMessage());
                        Toast.makeText(mContext, "Error occur. Try again later", Toast.LENGTH_SHORT).show();
                        getDialog().dismiss();
                    }
                });
            }
        });
        return view;
    }
}
