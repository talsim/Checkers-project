package com.example.checkers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {
    public static final String TAG = "Register";
    public TextView login;
    public Button register;
    public EditText mEmail;
    public EditText mPassword;
    public EditText mConfirmPassword;
    public EditText mUsername;
    public ProgressBar progressBar;
    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore; // database

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        login = (TextView) findViewById(R.id.textViewLogin);
        register = (Button) findViewById(R.id.ButtonRegister);
        mEmail = (EditText) findViewById(R.id.editTextEmail);
        mPassword = (EditText) findViewById(R.id.editTextPassword);
        mConfirmPassword = (EditText) findViewById(R.id.editTextConfirmPassword);
        mUsername = (EditText) findViewById(R.id.editTextName);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirect to Login Page
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerHandler();
            }
        });
    }

    // handle the onClickListener for the button
    public void registerHandler() {
        String email = mEmail.getText().toString().trim(); // remove spaces
        String password = mPassword.getText().toString().trim();
        String confirmPassword = mConfirmPassword.getText().toString().trim();
        String username = mUsername.getText().toString().trim();

        // check user input (e.g make sure that the user entered a password)
        if (!validateFields(email, password, confirmPassword))
            return;

        progressBar.setVisibility(View.VISIBLE); // show the loading state

        // validation checks passed, now register the user in the database

        register.setEnabled(false); // disable any button presses when registering user
        fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Context appContext = getApplicationContext();
                if (task.isSuccessful()) {
                    Toast.makeText(appContext, "User created.", Toast.LENGTH_SHORT).show();
                    addUserdataToCloud(username, email);
                    startActivity(new Intent(appContext, LobbyActivity.class));
                    finish();
                } else {
                    Exception exception = task.getException();
                    if (exception != null)
                        Toast.makeText(appContext, "Error! " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(appContext, "Error: Couldn't create user.", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.INVISIBLE);
                }
                register.setEnabled(true); // enable the button once finished registering
            }
        });
    }

    // add username and email to db
    public void addUserdataToCloud(String username, String email) {
        // Create a new user with its username and email
        Map<String, Object> userData = new HashMap<>();
        userData.put("username", username);
        userData.put("email", email);

        String uid = Objects.requireNonNull(fAuth.getCurrentUser()).getUid(); // impossible to get nullptr exception because this code snippet will only be run if the user is successfully created in fAuth
        DocumentReference documentReference = fStore.collection("users").document(uid);

        // Add a new document with the unique UID of the user
        documentReference.set(userData).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d(TAG, "OnSuccess: user profile is created for uid: " + uid);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Error adding document", e);
            }
        });
    }

    // all validations
    private boolean validateFields(String email, String password, String confirmPassword) {
        boolean isValid = true;
        if (email.isEmpty()) {
            mEmail.setError("Email is Required.");
            isValid = false;
        }
        if (password.isEmpty()) {
            mPassword.setError("Password is Required.");
            isValid = false;
        } else if (password.length() < 6) {
            mPassword.setError("Password Must be >= 6 Characters");
            isValid = false;
        }
        if (!confirmPassword.equals(password)) {
            if (confirmPassword.isEmpty())
                mConfirmPassword.setError("Please Confirm your Password.");
            else
                mConfirmPassword.setError("Passwords Don't Match");
            isValid = false;
        }
        return isValid;
    }


}