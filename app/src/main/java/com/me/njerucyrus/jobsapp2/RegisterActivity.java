package com.me.njerucyrus.jobsapp2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
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
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.me.njerucyrus.models.User;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    final String TAG = "RegisterActivity";
    private EditText txtFullName, txtEmail, txtPhoneNumber, txtPassword,
            txtConfirmPassword;

    private Button btnRegister;

    FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private User user;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        txtFullName = (EditText) findViewById(R.id.txtFullName);
        txtEmail = (EditText) findViewById(R.id.txtEmailAddress);
        txtPhoneNumber = (EditText) findViewById(R.id.txtPhoneNumber);
        txtPassword = (EditText) findViewById(R.id.txtPassword);
        txtConfirmPassword = (EditText) findViewById(R.id.txtConfirmPassword);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(RegisterActivity.this);


        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validate()) {
                    final String fullName = txtFullName.getText().toString();
                    String email = txtEmail.getText().toString();
                    String phoneNumber = txtPhoneNumber.getText().toString();
                    String password = txtConfirmPassword.getText().toString();
                    user = new User(fullName, email,phoneNumber);


                    progressDialog.setMessage("Creating account ...");
                    progressDialog.show();
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

                                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                .setDisplayName(fullName).build();

                                        currentUser.updateProfile(profileUpdates);

                                        db.collection("users").add(user)
                                                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentReference> task) {
                                                        if (task.isSuccessful()) {
                                                            if (progressDialog.isShowing()) {
                                                                progressDialog.dismiss();
                                                            }
                                                            Toast.makeText(RegisterActivity.this, "Account Created Successfully", Toast.LENGTH_LONG).show();
                                                            finish();
                                                            startActivity(new Intent(RegisterActivity.this, MainActivity.class));

                                                        } else {
                                                            if (progressDialog.isShowing()) {
                                                                progressDialog.dismiss();
                                                            }
                                                            Toast.makeText(RegisterActivity.this, "Failed to setup your account please try again later",
                                                                    Toast.LENGTH_LONG).show();
                                                        }
                                                    }
                                                });

                                    } else {
                                        if (progressDialog.isShowing()) {
                                            progressDialog.dismiss();
                                        }
                                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                        Toast.makeText(getApplicationContext(), "Failed to create account.",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {
                    Toast.makeText(getApplicationContext(), "Please fix the errors above",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    public boolean validate() {
        boolean valid = true;
        if (txtFullName.getText().toString().trim().isEmpty()) {
            txtFullName.setError("This field is required");
            valid = false;
        } else {
            txtFullName.setError(null);
        }

        if (txtPhoneNumber.getText().toString().trim().isEmpty()) {
            txtPhoneNumber.setError("This field is required");
            valid = false;
        } else {
            txtPhoneNumber.setError(null);
        }
        if (txtEmail.getText().toString().trim().isEmpty()) {
            txtEmail.setError("This field is required");
            valid = false;
        } else {
            txtEmail.setError(null);
        }
        if (txtPassword.getText().toString().trim().isEmpty()) {
            txtPassword.setError("This field is required");
            valid = false;
        } else {
            txtPassword.setError(null);
        }
        if (txtConfirmPassword.getText().toString().trim().isEmpty()) {
            txtConfirmPassword.setError("This field is required");
            valid = false;
        } else {
            txtConfirmPassword.setError(null);
        }

        if (!txtConfirmPassword.getText().toString().trim().equals(txtPassword.getText().toString().trim())) {
            txtConfirmPassword.setError("Password didi not match");
            Toast.makeText(getApplicationContext(), "Password do not match", Toast.LENGTH_SHORT).show();
            valid = false;
        }
        return valid;
    }
}
