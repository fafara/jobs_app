package com.me.njerucyrus.jobsapp2;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.me.njerucyrus.models.JobPost;
import com.me.njerucyrus.models.User;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class PostJobActivity extends AppCompatActivity {
    private Button btnSubmitJobPost;
    private EditText txtTitle, txtDescription, txtLocation;
    private TextView txtDeadline;
    private String category;
    private DatabaseReference mJobsRef;
    private DatabaseReference mUsersRef;
    private DatabaseReference mProfilesRef;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private ProgressDialog progressDialog;
    private JobPost jobPost;
    private Spinner spinner;
    private SimpleDateFormat mSimpleDateFormat;
    private Calendar mCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_job);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


        spinner = (Spinner) findViewById(R.id.category_spinner);
        // Create an ArrayAdapter using the string array and a default spinner auto_complete_place_item
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.caterory_array, android.R.layout.simple_spinner_item);

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
        txtDeadline = (TextView) findViewById(R.id.txtDeadline);

        mSimpleDateFormat = new SimpleDateFormat("MM/dd/yyyy h:mm a", Locale.getDefault());

        txtDeadline.setOnClickListener(textListener);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Submitting please wait...");
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();


        mJobsRef = FirebaseDatabase.getInstance().getReference().child("Jobs");
        mUsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mProfilesRef = FirebaseDatabase.getInstance().getReference().child("Profiles");


        btnSubmitJobPost = (Button) findViewById(R.id.btnSubmitJobPost);
        btnSubmitJobPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validate()) {

                    double lat = 0.00;
                    double lng = 0.00;
                    Date postedOn = new Date();

                    SharedPreferences settings = getSharedPreferences("PROFILE_DATA",
                            Context.MODE_PRIVATE);
                    String fullName = settings.getString("fullName", "");
                    String phoneNumber = settings.getString("phoneNumber", "");
                    String postedBy = fullName + " || " + phoneNumber;

                    jobPost = new JobPost(
                            category,
                            txtDescription.getText().toString().trim(),
                            txtTitle.getText().toString().trim(),
                            txtLocation.getText().toString().trim(),
                            lat,
                            lng,
                            postedOn,
                            postedBy,
                            mCurrentUser.getUid(),
                            txtDeadline.getText().toString()
                    );


                    progressDialog.show();

                    if (!isNetworkAvailable()) {
                        mJobsRef.push().setValue(jobPost);
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Job posted successfully", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(PostJobActivity.this, MainActivity.class));
                    } else {
                        postWithNet(jobPost);
                    }


                } else {
                    progressDialog.dismiss();

                    Toast.makeText(getApplicationContext(), "fix errors above", Toast.LENGTH_LONG).show();

                }
            }
        });
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void postWithNet(JobPost jobPost) {
        mJobsRef.push().setValue(jobPost)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressDialog.dismiss();
                        // Exception e = jobTask.getException();
                        Toast.makeText(getApplicationContext(), "Job posted successfully", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(PostJobActivity.this, MainActivity.class));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Error occurred while posting job", Toast.LENGTH_LONG).show();
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

        if (txtDeadline.getText().toString().trim().equals(getResources().getString(R.string.post_job_deadline_hint))) {
            Toast.makeText(PostJobActivity.this, "Please select a deadline", Toast.LENGTH_SHORT).show();
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
            finish();
        } else {
            DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
            mRootRef.child("Users")
                    .child(mAuth.getCurrentUser().getUid())
                    .child("online")
                    .setValue("true");
        }
    }

    /* Define the onClickListener, and start the DatePickerDialog with users current time */
    private final View.OnClickListener textListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mCalendar = Calendar.getInstance();
            new DatePickerDialog(PostJobActivity.this, mDateDataSet, mCalendar.get(Calendar.YEAR),
                    mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH)).show();
        }
    };

    /* After user decided on a date, store those in our calendar variable and then start the TimePickerDialog immediately */
    private final DatePickerDialog.OnDateSetListener mDateDataSet = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            mCalendar.set(Calendar.YEAR, year);
            mCalendar.set(Calendar.MONTH, monthOfYear);
            mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            new TimePickerDialog(PostJobActivity.this, mTimeDataSet, mCalendar.get(Calendar.HOUR_OF_DAY), mCalendar.get(Calendar.MINUTE), false).show();
        }
    };

    /* After user decided on a time, save them into our calendar instance, and now parse what our calendar has into the TextView */
    private final TimePickerDialog.OnTimeSetListener mTimeDataSet = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            mCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            mCalendar.set(Calendar.MINUTE, minute);
            txtDeadline.setText(mSimpleDateFormat.format(mCalendar.getTime()));
        }
    };


}
