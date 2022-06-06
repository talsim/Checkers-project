package com.example.checkers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

/**
 * This class manages the LoginActivity in the application.
 *
 * @author Tal Simhayev
 * @version 1.0
 */
public class LoginActivity extends AppCompatActivity {

    public Button login;
    public TextView createAccount;
    public EditText mEmail;
    public EditText mPassword;
    public ProgressBar progressBar;
    private FirebaseAuth fAuth; // used to authenticate the user

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        login = findViewById(R.id.auth);
        createAccount = findViewById(R.id.textViewCreateAccount);
        mEmail = findViewById(R.id.editTextEmail);
        mPassword = findViewById(R.id.editTextPassword);

        fAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progressBar);
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

    /**
     * Handle the login button press, and check in the database if the given input is valid.
     * If the input is valid then log in, else show error on text boxes.
     */
    public void loginHandler() {
        String email = mEmail.getText().toString().trim(); // remove spaces
        String password = mPassword.getText().toString().trim();

        // check user input (e.g make sure that the user entered a password)
        if (!validateFields(email, password))
            return;

        progressBar.setVisibility(View.VISIBLE);

        // authenticate the user
        login.setEnabled(false);

        fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Context appContext = getApplicationContext();
                if (task.isSuccessful()) {
                    Toast.makeText(appContext, "Logged in Successfully.", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(appContext, LobbyActivity.class));
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

    /**
     * Check if user input in different fields such as email and password are valid or not (e.g password length must be atleast 6 characters).
     *
     * @param email    The string representation of the email entered.
     * @param password The string representation of the password entered.
     * @return true if user input is OK, else otherwise.
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
        } else if (password.length() < 6) {
            mPassword.setError("Password Must be >= 6 Characters");
            isValid = false;
        }
        return isValid;
    }

}