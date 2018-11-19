package com.example.ahlbe.android_project_4;

import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static com.example.ahlbe.android_project_4.InputValidator.isEmpty;

public class ResendVerificationEmailDialog extends DialogFragment
{
    private static final String TAG = "ResendVerificationEmail";


    private EditText mEmail, mPassword;


    private Context mContext;

    @Nullable
    @Override
    //Creates the Dialog Box when the user clicks on the Resend Verification Button
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.dialog_resend, container, false);

        mPassword = view.findViewById(R.id.dialog_password);
        mEmail = view.findViewById(R.id.dialog_email);
        mContext = getActivity();

        TextView confirmDialog = view.findViewById(R.id.dialogConfirm);
        confirmDialog.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Log.d(TAG, "inside onClick: attemtping to resend verification");
                Log.d(TAG, mEmail.getText().toString() + " " + mPassword.getText().toString());
                if(!isEmpty(mEmail.getText().toString())
                        && !isEmpty(mPassword.getText().toString()))
                {
                    authenticateAndResendVerification(mEmail.getText().toString(), mPassword.getText().toString());
                }
                else
                {
                    Toast.makeText(mContext, "All fields must be filled out", Toast.LENGTH_SHORT).show();
                }




            }
        });

        TextView cancelDialog = view.findViewById(R.id.dialogCancel);
        cancelDialog.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                getDialog().dismiss();
            }
        });
        return view;
    }

    //Firebase Method to authenticate credentials the user has inputted. This will give the user
    //Authenticated State in order to send verification email again. Sign them out afterwords.
    private void authenticateAndResendVerification(String email, String password)
    {
        Log.d(TAG, "inside authenticateandresendverification");
        AuthCredential credential = EmailAuthProvider.getCredential(email, password);
        FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>()
        {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task)
            {
                Log.d(TAG, "Before Task is sucessful");
                if(task.isSuccessful())
                {
                    Log.d(TAG, "inside onComplete: authentication complete");
                    sendVerificationEmail();
                    FirebaseAuth.getInstance().signOut();
                    getDialog().dismiss();

                }
            }
        }).addOnFailureListener(new OnFailureListener()
        {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                Log.d(TAG, "inside on failure");
                Toast.makeText(mContext, "Check your credentials", Toast.LENGTH_SHORT).show();
                getDialog().dismiss();
            }
        });
    }
    //Method to send verification email to the user.
    public void sendVerificationEmail()
    {
        Log.d(TAG, "inside sendVerificationemail");
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser != null)
        {
            Log.d(TAG, "inside FirebaseUser conditional");
            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>()
            {
                @Override
                public void onComplete(@NonNull Task<Void> task)
                {
                    if(task.isSuccessful())
                    {
                        Toast.makeText(mContext, "Resent Verification", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(mContext, "Couldn't send Verification", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
