package com.me.njerucyrus.jobsapp2;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class JobPostDetailActivity extends AppCompatActivity {
    TextView mTitle, mDescription, mLocation, mDeadline,mPostedBy, mCategory, mStartChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_post_detail);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        mTitle = (TextView)findViewById(R.id.txtTitleSingle);
        mDescription = (TextView)findViewById(R.id.txtItemDescriptionSingle);
        mLocation = (TextView)findViewById(R.id.txtLocationSingle);
        mDeadline = (TextView)findViewById(R.id.txtDeadlineSingle);
        mCategory = (TextView)findViewById(R.id.txtCategorySingle);
        mPostedBy = (TextView)findViewById(R.id.txtPostedBySingle);


        mTitle.setText(getIntent().getStringExtra("title"));
        mDescription.setText(getIntent().getStringExtra("description"));
        String location = "Job Location: "+getIntent().getStringExtra("location");
        mLocation.setText(location);
        String deadline = "Deadline: "+getIntent().getStringExtra("deadline");
        mDeadline.setText(deadline);
        String category = "Category: "+getIntent().getStringExtra("category");
        mCategory.setText(category);
        String postedBy = "Posted By: "+getIntent().getStringExtra("postedBy");
        mPostedBy.setText(postedBy);

        mStartChat = (TextView)findViewById(R.id.txtStartChat);
        mStartChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(JobPostDetailActivity.this, ChatActivity.class));
                finish();
            }
        });
    }
}
