package com.me.njerucyrus.jobsapp2;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.me.njerucyrus.models.JobPost;

import java.util.ArrayList;
import java.util.List;

public class MyPostsActivity extends AppCompatActivity {
    final String TAG = "MyPostsActivity";
    private RecyclerView recyclerView;
    private MyAdapter adapter;
    private List<JobPost> jobPosts;
    private ProgressDialog progressDialog;
    private FirebaseUser mCurrentUser;
    private DatabaseReference mJobsRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_posts);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewMyposts);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        jobPosts = new ArrayList<>();
        adapter = new MyAdapter(jobPosts, MyPostsActivity.this);

        progressDialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();

        progressDialog.setMessage("Loading...");
        mJobsRef = FirebaseDatabase.getInstance().getReference().child("Jobs");
        mJobsRef.keepSynced(true);

        recyclerView.setAdapter(adapter);
        progressDialog.show();
        loadMyPosts();


    }

    private void loadMyPosts() {
        mJobsRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot != null) {
                    JobPost jobPost = dataSnapshot.getValue(JobPost.class);
                    String key = jobPost.getPostedByUid();
                    if (key.equals(mCurrentUser.getUid())) {
                        jobPosts.add(jobPost);
                    }
                    adapter.notifyDataSetChanged();
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
//                jobPosts.remove(dataSnapshot.getValue(JobPost.class));
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
        mRootRef.child("Users")
                .child(mAuth.getCurrentUser().getUid())
                .child("online")
                .setValue("true");
    }
}
