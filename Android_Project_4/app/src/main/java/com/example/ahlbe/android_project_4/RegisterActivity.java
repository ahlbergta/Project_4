package com.example.ahlbe.android_project_4;

import android.app.Activity;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends Activity
{
    private  static final String TAG = "RegisterActivity";

    //Declaring the Widgets
    private Button mRegister;
    private EditText mEmail, mPassword, mPasswordConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //Get all the Widgets
        mRegister = findViewById(R.id.button_register);
        mEmail = findViewById(R.id.edit_email_register);
        mPassword = findViewById(R.id.edit_pass_register);
        mPasswordConfirm = findViewById(R.id.edit_confirm_pass);
        mRegister.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Log.d(TAG, "click the button: Attempting to register user");

                //Checking if EditText fields have been filled
                if(!InputValidator.isEmpty(mEmail.getText().toString())
                        && !InputValidator.isEmpty(mPassword.getText().toString())
                        && !InputValidator.isEmpty(mPasswordConfirm.getText().toString()))
                {
                    if(InputValidator.doPasswordMatch(mPassword.getText().toString(),
                            mPasswordConfirm.getText().toString()))
                    {
                        if(InputValidator.isPasswordStrong(mPassword.getText().toString()))
                        {
                            createAccount(mEmail.getText().toString(), mPassword.getText().toString());
                        }
                        else
                        {
                            Toast.makeText(RegisterActivity.this, "Password not strong enough. Make sure the password is 8" +
                                    "characters long and contains 1 number, 1 lower case letter, 1 upper case letter", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else
                    {
                        Toast.makeText(RegisterActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(RegisterActivity.this, "You must fill out all fields", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void createAccount(String email, String password)
    {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>()
        {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task)
            {
                Log.d(TAG, "inside onComplete: " + task.isSuccessful());

                if(task.isSuccessful())
                {
                    Log.d(TAG, "inside isSuccessful " + FirebaseAuth.getInstance().getCurrentUser().getUid());
                }
                else
                {
                    Toast.makeText(RegisterActivity.this, "Unable to create account", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


}
