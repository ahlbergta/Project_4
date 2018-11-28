package com.example.ahlbe.android_project_4;

import android.app.Activity;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class DeleteProfileConfirmationDialog extends DialogFragment
{
    private static final String TAG = "DeleteProfileDialog";
    private Button mButtonConfirm, mButtonCancel;
    private Context mContext;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.dialog_delete_profile_confirmation, container, false);

        mContext = getActivity();

        mButtonConfirm = view.findViewById(R.id.dialog_deletion_confirm);
        mButtonCancel = view.findViewById(R.id.cancel_deletion_profile);

        mButtonConfirm.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Log.d(TAG, "Inside the onclick for deleting account");


                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                if(firebaseUser != null)
                {
                    Log.d(TAG, "Inside if firebase user is null:");
                    DocumentReference documentReference = FirebaseFirestore.getInstance().collection("Users").document(firebaseUser.getUid());
                    documentReference.delete().addOnSuccessListener(new OnSuccessListener<Void>()
                    {
                        @Override
                        public void onSuccess(Void aVoid)
                        {
                            Log.d(TAG, "Document successfully deleted");
                        }
                    }).addOnFailureListener(new OnFailureListener()
                    {
                        @Override
                        public void onFailure(@NonNull Exception e)
                        {
                            Log.d(TAG, "Error deleting document");
                        }
                    });
                    firebaseUser.delete().addOnCompleteListener(new OnCompleteListener<Void>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            if(task.isSuccessful())
                            {
                                Toast.makeText(mContext, "Profile Deleted Successfully", Toast.LENGTH_SHORT).show();
                                getDialog().dismiss();
                                Intent loginIntent = new Intent(mContext.getApplicationContext(), LoginActivity.class);
                                loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(loginIntent);


                            }
                        }
                    });
                }
            }
        });
        mButtonCancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                getDialog().dismiss();
            }
        });

        return view;
    }
}
