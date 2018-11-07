package com.example.ahlbe.android_project_4;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity
{
    private static final String TAG = "LogicActivity";

    //Firebase Objects
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    //Setting up Widgets
    private Button mRegister, mLogin;
    private EditText mEmail, mPassword;
    private TextView mTextView;
    private FirebaseUser mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        //Set the widgets
        mRegister = findViewById(R.id.button_login_register);
        mLogin = findViewById(R.id.button_login_login);
        mEmail = findViewById(R.id.edit_email_login);
        mPassword = findViewById(R.id.edit_pass_login);
        mTextView = findViewById(R.id.text_resend);
        //Function to set up Authentication Listener
        settingFirebaseAuthListener();

        mRegister.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(registerIntent);

            }
        });
        //Checks to see if fields are not empty when user clicks login button
        mLogin.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(!InputValidator.isEmpty(mPassword.getText().toString())
                        && !InputValidator.isEmpty(mEmail.getText().toString()))
                {
                    signIn(mEmail.getText().toString(),mPassword.getText().toString());


                }
            }
        });
        mTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                ResendVerificationEmailDialog dialog = new ResendVerificationEmailDialog();
                dialog.show(getFragmentManager(), "dialog_resend_email_verification");
            }
        });


    }

    @Override
    protected void onStart()
    {
        //Initialize the Authentication State Listener
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        if(mAuthStateListener != null)
        {
            FirebaseAuth.getInstance().removeAuthStateListener(mAuthStateListener);
        }
    }

    private void signIn(String email, String password)
    {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>()
        {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task)
            {
                if(task.isSuccessful())
                {
                    Log.d(TAG, "sign in was successful");
                    //FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                }
                else
                {
                    Log.e(TAG, "sign in failed", task.getException());
                    Toast.makeText(LoginActivity.this, "Login Failure", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void settingFirebaseAuthListener()
    {
        mAuthStateListener = new FirebaseAuth.AuthStateListener()
        {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth)
            {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

                if(firebaseUser != null)
                {

                    if(firebaseUser.isEmailVerified())
                    {
                        Log.d(TAG, "The authentication state has changed: user is signed in: " + firebaseUser.getUid());
                        Toast.makeText(LoginActivity.this, "You are Authenticated with: " + firebaseUser.getEmail(),Toast.LENGTH_SHORT).show();
                        Intent homeIntent = new Intent(LoginActivity.this, CreateProfileActivity.class);
                        startActivity(homeIntent);
                        finish();

                    }
                    else
                    {
                        Toast.makeText(LoginActivity.this, "Check your email for a verification link", Toast.LENGTH_SHORT).show();
                        FirebaseAuth.getInstance().signOut();
                    }


                }
                else
                {
                    Log.d(TAG, "The authenticate state has changed: user has signed out");
                }
            }
        };
    }




}
