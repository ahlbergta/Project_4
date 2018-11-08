package com.example.ahlbe.android_project_4;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class DeleteProfileConfirmationDialog extends DialogFragment
{
    private static final String TAG = "DeleteProfileConfirmationDialog";

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
                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                if(firebaseUser != null)
                {
                    firebaseUser.delete().addOnCompleteListener(new OnCompleteListener<Void>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            if(task.isSuccessful())
                            {
                                Toast.makeText(mContext, "Profile Deleted Successfully", Toast.LENGTH_SHORT).show();
                                getDialog().dismiss();
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
