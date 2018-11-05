package com.example.ahlbe.android_project_4;

import android.app.Activity;
import android.content.Intent;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends Activity
{
    private static final String TAG = "LogicActivity";

    private FirebaseAuth mAuth;
    private Button mRegister, mLogin;
    private EditText mEmail, mPassword;
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
        if(mFirebaseUser == null)
        {
            Log.d(TAG, "User is null");
        }
        else
        {
            Log.d(TAG, "user is not null");
        }

        mRegister.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(registerIntent);

            }
        });
        mLogin.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(!InputValidator.isEmpty(mPassword.getText().toString())
                        && !InputValidator.isEmpty(mEmail.getText().toString()))
                {
                    signIn(mEmail.getText().toString(),mPassword.getText().toString());
                    Intent homeIntent = new Intent(LoginActivity.this, HomeActivity.class);
                    startActivity(homeIntent);

                }
            }
        });

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




}
