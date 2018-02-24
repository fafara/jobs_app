package com.me.njerucyrus.jobsapp2;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class JobPostDetailActivity extends AppCompatActivity {
    private TextView mTitle;
    private TextView mDescription;
    private TextView mLocation;
    private TextView mDeadline;
    private TextView mPostedBy;
    private TextView mCategory;
    private TextView mStartChat;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private  String postedByUid;
    private String postedBy;
    private DatabaseReference mUsersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_post_detail);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        mTitle = (TextView) findViewById(R.id.txtTitleSingle);
        mDescription = (TextView) findViewById(R.id.txtItemDescriptionSingle);
        mLocation = (TextView) findViewById(R.id.txtLocationSingle);
        mDeadline = (TextView) findViewById(R.id.txtDeadlineSingle);
        mCategory = (TextView) findViewById(R.id.txtCategorySingle);
        mPostedBy = (TextView) findViewById(R.id.txtPostedBySingle);
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        mUsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUser.getUid());


        mTitle.setText(getIntent().getStringExtra("title"));
        mDescription.setText(getIntent().getStringExtra("description"));
        String location = "Job Location: " + getIntent().getStringExtra("location");
        mLocation.setText(location);
        String deadline = "Deadline: " + getIntent().getStringExtra("deadline");
        mDeadline.setText(deadline);
        String category = "Category: " + getIntent().getStringExtra("category");
        mCategory.setText(category);
        postedBy = "Posted By: " + getIntent().getStringExtra("postedBy");
        postedByUid = getIntent().getStringExtra("postedByUid");
        mStartChat = (TextView) findViewById(R.id.txtStartChat);
        if (mCurrentUser.getUid().equals(postedByUid)) {
            //mStartChat.setEnabled(false);
            //mStartChat.setVisibility(View.INVISIBLE);
            postedBy = "Posted By Me";
            mPostedBy.setText(postedBy);
        } else {
           // mStartChat.setEnabled(true);
            //mStartChat.setVisibility(View.VISIBLE);
            mPostedBy.setText(postedBy);
        }

        mStartChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(JobPostDetailActivity.this, ChatActivity.class)
                .putExtra("fullName",getIntent().getStringExtra("postedBy"))
                        .putExtra("postedByUid", postedByUid)
                );
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
        FirebaseUser mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        mRootRef.child("Users").child(mCurrentUser.getUid()).child("online").setValue("true");
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
