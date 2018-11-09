package com.example.ahlbe.android_project_4;

import android.app.Activity;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static com.example.ahlbe.android_project_4.InputValidator.doPasswordMatch;
import static com.example.ahlbe.android_project_4.InputValidator.isEmpty;
import static com.example.ahlbe.android_project_4.InputValidator.isPasswordStrong;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";

    //Declaring the Widgets
    private Button mRegister;
    private EditText mEmail, mPassword, mPasswordConfirm;
    private Toolbar mToolbar;
    //private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //Get all the Widgets
        mRegister = findViewById(R.id.button_register);
        mEmail = findViewById(R.id.edit_email_register);
        mPassword = findViewById(R.id.edit_pass_register);
        mPasswordConfirm = findViewById(R.id.edit_confirm_pass);
        mToolbar = findViewById(R.id.toolbar_register_edit);
        setSupportActionBar(mToolbar);
        //mProgressBar = findViewById(R.id.progress_register);
        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "click the button: Attempting to register user");

                //Checking if EditText fields have been filled

                if (!isEmpty(mEmail.getText().toString())
                        && !isEmpty(mPassword.getText().toString())
                        && !isEmpty(mPasswordConfirm.getText().toString())) {
                    if (doPasswordMatch(mPassword.getText().toString(),
                            mPasswordConfirm.getText().toString())) {
                        if (isPasswordStrong(mPassword.getText().toString())) {
                            //mProgressBar.setIndeterminate(true);
                            createAccount(mEmail.getText().toString(), mPassword.getText().toString());
                        } else {
                            Toast.makeText(RegisterActivity.this, "Password not strong enough. Make sure the password is 8" +
                                    "characters long and contains 1 number, 1 lower case letter, 1 upper case letter, and 1 special character", Toast.LENGTH_LONG).show();

                        }
                    } else {
                        Toast.makeText(RegisterActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(RegisterActivity.this, "You must fill out all fields", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    /**
     * This method takes the text input by the user in the EditText fields and call the Firebase createUser method in order
     * to create the user. This method automatically authenticates a user, so the the Firebase
     * method signOut must be called the end. If the method was successfully, clear the activity stack and redirect user to
     * Login
     *
     * @param email    The text from the EditText in LoginActivity inputed by user.
     * @param password Text from the EditText in LoginActivity inputed by user.
     */
    private void createAccount(String email, String password)
    {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d(TAG, "inside onComplete: " + task.isSuccessful());

                if (task.isSuccessful())
                {
                    //mProgressBar.setIndeterminate(false);
                    Log.d(TAG, "inside isSuccessful " + FirebaseAuth.getInstance().getCurrentUser().getUid());
                    sendVerificationEmail();
                    FirebaseAuth.getInstance().signOut();
                    Intent registerActivityIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(registerActivityIntent);
                    finish();

                }
                else
                    {
                    // mProgressBar.setIndeterminate(false);
                    Toast.makeText(RegisterActivity.this, "Unable to create account", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //Firebase Method to send an verification to the user with the email they have registered.
    private void sendVerificationEmail() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(RegisterActivity.this, "Sent Verification Email", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(RegisterActivity.this, "Couldn't send Verification Email", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}


