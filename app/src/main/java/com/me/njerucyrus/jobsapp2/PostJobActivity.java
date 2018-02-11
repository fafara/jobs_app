package com.me.njerucyrus.jobsapp2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.me.njerucyrus.models.JobPost;

import java.util.Date;

public class PostJobActivity extends AppCompatActivity {
    Button btnSubmitJobPost;
    EditText txtTitle, txtDescription, txtSalary, txtDeadline, txtLocation;
    String category;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    ProgressDialog progressDialog;
    private JobPost jobPost;
    Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_job);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        spinner = (Spinner) findViewById(R.id.category_spinner);
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.caterory_array, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner.setAdapter(adapter);


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent,
                                       View view, int pos, long id) {
                category = parent.getItemAtPosition(pos).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });
        txtTitle = (EditText) findViewById(R.id.txtTitle);
        txtDescription = (EditText) findViewById(R.id.txtDescription);

        txtLocation = (EditText) findViewById(R.id.txtLocation);
        txtDeadline = (EditText) findViewById(R.id.txtDeadline);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Submitting please wait...");


        db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        db.setFirestoreSettings(settings);

        btnSubmitJobPost = (Button) findViewById(R.id.btnSubmitJobPost);
        btnSubmitJobPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validate()) {

                    double lat = 0.00;
                    double lng = 0.00;
                    Date postedOn = new Date();
                    FirebaseUser currentUser = mAuth.getCurrentUser();
                    String user = currentUser.getEmail();

                    jobPost = new JobPost(
                            category,
                            txtDescription.getText().toString().trim(),
                            txtTitle.getText().toString().trim(),
                            txtLocation.getText().toString().trim(),
                            lat,
                            lng,
                            postedOn,
                            user,
                            txtDeadline.getText().toString()
                    );


                    progressDialog.show();

                    db.collection("jobs").add(jobPost)

                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    startActivity(new Intent(PostJobActivity.this, MainActivity.class));
                                    Toast.makeText(getApplicationContext(), "Job posted successfully.", Toast.LENGTH_LONG).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    Toast.makeText(getApplicationContext(), "Error occurred while posting.", Toast.LENGTH_LONG).show();
                                }
                            });

                }
            }
        });
    }

    public boolean validate() {
        boolean valid = true;
        if (txtTitle.getText().toString().trim().isEmpty()) {
            txtTitle.setError("This field is required");
            valid = false;
        } else {
            txtTitle.setError(null);
        }

        if (txtDescription.getText().toString().trim().isEmpty()) {
            txtDescription.setError("This field is required");
            valid = false;
        } else {
            txtDescription.setError(null);
        }

        if (spinner.getSelectedItem().equals("Select Category")) {
            valid = false;
            Toast.makeText(getApplicationContext(), "Select Category",
                    Toast.LENGTH_SHORT).show();
        }

        if (txtLocation.getText().toString().trim().isEmpty()) {
            txtLocation.setError("This field is required");
            valid = false;
        } else {
            txtLocation.setError(null);
        }

        if (txtDeadline.getText().toString().trim().isEmpty()) {
            txtDeadline.setError("This field is required");
            valid = false;
        } else {
            txtDeadline.setError(null);
        }
        return valid;
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        }
    }
}