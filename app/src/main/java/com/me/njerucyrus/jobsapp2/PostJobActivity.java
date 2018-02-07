package com.me.njerucyrus.jobsapp2;

import android.app.ProgressDialog;
import android.renderscript.Script;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import java.util.Date;
import java.util.HashMap;

public class PostJobActivity extends AppCompatActivity {
    Button btnSubmitJobPost;
    EditText txtTitle, txtDescription, txtSalary, txtDeadline, txtLocation;
    String category;
    FirebaseFirestore db;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_job);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        Spinner spinner = (Spinner) findViewById(R.id.category_spinner);
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
        txtSalary = (EditText) findViewById(R.id.txtSalary);
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
                    HashMap<String, Object> data = new HashMap<>();
                    data.put("category", category);
                    data.put("title", txtTitle.getText().toString());
                    data.put("description", txtDescription.getText().toString());
                    data.put("deadline", txtDeadline.getText().toString());
                    data.put("lat", 0.0);
                    data.put("lng", 0.0);
                    data.put("posted_by", "default user");
                    data.put("posted_on", new Date().toString());

                    progressDialog.show();
                    db.collection("jobs").add(data)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    if (progressDialog.isShowing()) {
                                        progressDialog.dismiss();
                                    }
                                    Toast.makeText(getApplicationContext(), "Added Document " + documentReference.getId(),
                                            Toast.LENGTH_LONG).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    if (progressDialog.isShowing()) {
                                        progressDialog.dismiss();
                                    }

                                    Toast.makeText(getApplicationContext(), "ERROR OCCURED " + e.getLocalizedMessage(),
                                            Toast.LENGTH_LONG).show();
                                }
                            });

                }else{
                    Toast.makeText(getApplicationContext(), "Please fix the errors above",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public boolean validate(){
        boolean valid = true;
        if (txtTitle.getText().toString().trim().isEmpty()){
            txtTitle.setError("This field is required");
            valid = false;
        }else{
            txtTitle.setError(null);
        }

        if (txtDescription.getText().toString().trim().isEmpty()){
            txtDescription.setError("This field is required");
            valid = false;
        }else{
            txtDescription.setError(null);
        }

        if (txtSalary.getText().toString().trim().isEmpty()){
            txtSalary.setError("This field is required");
            valid = false;
        }else{
            txtSalary.setError(null);
        }
        if(txtDeadline.getText().toString().trim().isEmpty()){
            txtDeadline.setError("This field is required");
            valid = false;
        }else{
            txtDeadline.setError(null);
        }
        return valid;
    }
}
