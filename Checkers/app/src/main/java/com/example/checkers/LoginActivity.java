package com.example.checkers;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    protected Button login;
    protected TextView createAccount;
    protected EditText mEmail;
    protected EditText mPassword;
    protected ProgressBar progressBar;
    private FirebaseAuth fAuth; // used to authenticate the user
    private FirebaseFirestore fStore; // database

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        login = (Button) findViewById(R.id.auth);
        createAccount = (TextView) findViewById(R.id.textViewCreateAccount);
        mEmail = (EditText) findViewById(R.id.editTextEmail);
        mPassword = (EditText) findViewById(R.id.editTextPassword);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginHandler();
            }
        });

        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Just switch to the Register activity for creating a new account
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    protected void loginHandler() {
        String email = mEmail.getText().toString().trim(); // remove spaces
        String password = mPassword.getText().toString().trim();

        Task<QuerySnapshot> getUsersCollection = fStore.collection("users").get();

        // check user input (e.g make sure that the user entered a password)
        if (!validateFields(email, password))
            return;

        /*         *** BETA - solution for not allowing a player to login from different devices if he's online already
        while (!getUsersCollection.isComplete()) // waiting for task to finish
        {
            System.out.println("waiting for task to finish");
        }

        if (getUsersCollection.isSuccessful()) {
            if (isUserOnline(email, getUsersCollection))
            {
                Toast.makeText(getApplicationContext(), "Error! User is already online in another device.", Toast.LENGTH_SHORT).show();
                return;
            }
        } else {
            Log.d(TAG, "Error getting documents: ", getUsersCollection.getException());
        }*/

        progressBar.setVisibility(View.VISIBLE);

        // authenticate the user
        login.setEnabled(false);

        fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Context appContext = getApplicationContext();
                if (task.isSuccessful()) {
                    Toast.makeText(appContext, "Logged in Successfully.", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(appContext, WaitingRoomActivity.class));
                    finish();
                } else {
                    Exception exception = task.getException();
                    if (exception != null)
                        Toast.makeText(appContext, "Error! " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(appContext, "Error: Couldn't log in.", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.INVISIBLE);
                }
                login.setEnabled(true);
            }
        });
    }

    private boolean isUserOnline(String email, @NonNull Task<QuerySnapshot> task) {

        for (QueryDocumentSnapshot doc : task.getResult()) {
            String currUserEmail = (String) doc.get("email");
            if (currUserEmail != null)
                if (currUserEmail.equals(email)) // found the correct user
                {
                    Boolean online = (Boolean) doc.get("isOnline");
                    if (online != null)
                        return online;
                }
        }
        return false;
    }

    /*
        Check if user input in different fields such as email and password are valid or not.
        Returns: true if user input is OK, else otherwise.
    */
    private boolean validateFields(String email, String password) {
        boolean isValid = true;

        if (email.isEmpty()) {
            mEmail.setError("Email is Required.");
            isValid = false;
        }
        if (password.isEmpty()) {
            mPassword.setError("Password is Required.");
            isValid = false;
        }
        else if(password.length() < 6){
            mPassword.setError("Password Must be >= 6 Characters");
            isValid = false;
        }
        return isValid;
    }

}