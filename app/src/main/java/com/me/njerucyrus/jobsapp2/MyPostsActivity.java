package com.me.njerucyrus.jobsapp2;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.me.njerucyrus.models.JobPost;

import java.util.ArrayList;
import java.util.List;

public class MyPostsActivity extends AppCompatActivity {
    final String TAG = "MyPostsActivity";
    private RecyclerView recyclerView;
    private MyAdapter adapter;
    private List<JobPost> jobPosts;
    private ProgressDialog progressDialog;
    private FirebaseFirestore db;
    private ListenerRegistration firestoreListener;
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

        progressDialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();

        progressDialog.setMessage("Loading...");


        db = FirebaseFirestore.getInstance();

        loadMyPosts();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        firestoreListener = db.collection("jobs").whereEqualTo("postedBy", currentUser.getEmail())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                        if (e != null) {

                            Log.e(TAG, "Listen failed!", e);
                            return;
                        }
                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }

                        List<JobPost> updatedList = new ArrayList<>();
                        for (DocumentSnapshot doc : documentSnapshots) {

                            JobPost post = doc.toObject(JobPost.class);
                            updatedList.add(post);

                        }
                        jobPosts.clear();
                        adapter = new MyAdapter(updatedList, MyPostsActivity.this, db);
                        recyclerView.setAdapter(adapter);

                    }
                });
    }


    private void loadMyPosts() {
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        progressDialog.show();

        db.collection("jobs").whereEqualTo("postedBy", user.getEmail())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (progressDialog != null && progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                            if (task.getResult().size() == 0) {
                                Toast.makeText(getApplicationContext(), "You have no posts yet", Toast.LENGTH_SHORT).show();
                            }
                            for (DocumentSnapshot document : task.getResult()) {
                                JobPost jobPost = document.toObject(JobPost.class);
                                jobPosts.add(jobPost);
                            }
                            adapter = new MyAdapter(jobPosts, MyPostsActivity.this, db);
                            recyclerView.setAdapter(adapter);
                        } else {
                            if (progressDialog != null && progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                            Toast.makeText(getApplicationContext(), "You have no posts yet", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }
}
