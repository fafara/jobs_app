package com.me.njerucyrus.jobsapp2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.me.njerucyrus.models.User;

public class ProfileActivity extends AppCompatActivity {
    private final String TAG = "ProfileActivity";
    private EditText txtFullName, txtEmail, txtPhoneNumber;

    private Button btnRegister;
    DatabaseReference mUsersRef;
    DatabaseReference mProfilesRef;
    private FirebaseAuth mAuth;
    private User user;
    private boolean userExists;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        txtFullName = (EditText) findViewById(R.id.txtFullName);
        txtEmail = (EditText) findViewById(R.id.txtEmailAddress);
        txtPhoneNumber = (EditText) findViewById(R.id.txtPhoneNumber);

        mUsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mProfilesRef = FirebaseDatabase.getInstance().getReference().child("Profiles");
        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(ProfileActivity.this);


        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validate()) {

                    progressDialog.setMessage("Updating account profile ...");
                    String fullName = txtFullName.getText().toString();
                    String email = txtEmail.getText().toString();
                    String phoneNumber = txtPhoneNumber.getText().toString();
                    String imageThumbnail = "default";
                    final String userUid = mAuth.getCurrentUser().getUid();
                    String status = "default";
                    String image = "default";
                    String deviceTokenId = FirebaseInstanceId.getInstance().getToken();
                    String online = "false";

                    user = new User(fullName, email, phoneNumber, userUid, status, image, imageThumbnail, deviceTokenId, online);

                    mUsersRef.child(userUid).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> userTask) {
                            if (userTask.isSuccessful()) {
                                progressDialog.dismiss();
                                mProfilesRef.child(userUid).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> profileTask) {
                                        if (profileTask.isSuccessful()){
                                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                            Toast.makeText(getApplicationContext(), "User profile updated successfully", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });

                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "Error occurred while updating user profile", Toast.LENGTH_LONG).show();

                            }
                        }
                    });
                }

            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        } else {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Profiles");
            ref.child(currentUser.getUid())
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot == null) {
//                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            } else {
                                if (dataSnapshot.hasChild("fullName")) {
                                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                }

                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
        }

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

        return valid;
    }


}
