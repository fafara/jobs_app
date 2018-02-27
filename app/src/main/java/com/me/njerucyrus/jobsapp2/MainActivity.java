package com.me.njerucyrus.jobsapp2;

import android.*;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.me.njerucyrus.models.CustomJobPostComparator;
import com.me.njerucyrus.models.JobPost;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView recyclerView;
    private MyAdapter adapter;
    private List<JobPost> jobPosts = new ArrayList<>();
    private ProgressDialog progressDialog;
    private DatabaseReference mJobsRef;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private DatabaseReference mUsersRef;
    private GoogleSignInClient mGoogleSignInClient;
    private SwipeRefreshLayout mSwipeLayout;
    private LinearLayoutManager mLayoutManager;
    private final static int ITEMS_PER_PAGE = 10;
    private int CURRENT_PAGE = 1;
    private int itemPos = 0;
    private String mLastKey = "";
    private String mPrevKey = "";

    final String TAG = "MainActivityTag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.jobs_swipe_layout);
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        mUsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUser.getUid());

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(MainActivity.this, gso);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Collections.sort(jobPosts, Collections.<JobPost>reverseOrder());

        adapter = new MyAdapter(jobPosts, MainActivity.this);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);

        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        mAuth = FirebaseAuth.getInstance();
        mJobsRef = FirebaseDatabase.getInstance().getReference().child("Jobs");
        mJobsRef.keepSynced(true);

        progressDialog = new ProgressDialog(this);
        recyclerView.setAdapter(adapter);

        progressDialog.setMessage("Loading...");
        progressDialog.show();

        loadJobPosts();

        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                CURRENT_PAGE++;
                itemPos = 0;

                loadMorePosts();
                adapter.notifyDataSetChanged();

            }

        });


    }

    private void loadMorePosts() {

        Query jobsQuery = mJobsRef.orderByKey().endAt(mLastKey).limitToLast(ITEMS_PER_PAGE);
        jobsQuery.keepSynced(true);
        jobsQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                JobPost jobPost = dataSnapshot.getValue(JobPost.class);
                String jobPostKey = dataSnapshot.getKey();

                if (!mPrevKey.equals(jobPostKey)) {

                    jobPosts.add(itemPos++, jobPost);


                } else {

                    mPrevKey = mLastKey;

                }


                if (itemPos == 1) {

                    mLastKey = jobPostKey;

                }

                adapter.notifyDataSetChanged();

                mSwipeLayout.setRefreshing(false);

                mLayoutManager.scrollToPositionWithOffset(10, 0);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
//                jobPosts.remove(dataSnapshot.getValue(JobPost.class));
//                adapter.notifyDataSetChanged();
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
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_post_job) {
            startActivity(new Intent(MainActivity.this, PostJobActivity.class));
            finish();
        } else if (id == R.id.nav_all_job_posts) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();

        } else if (id == R.id.nav_my_posts) {
            startActivity(new Intent(MainActivity.this, MyPostsActivity.class));
            finish();
        } else if (id == R.id.nav_messages) {
            startActivity(new Intent(MainActivity.this, MessagesActivity.class));
            finish();
        } else if (id == R.id.nav_invite_friend) {
            Toast.makeText(getApplicationContext(), "Comming soon", Toast.LENGTH_LONG).show();
        } else if (id == R.id.nav_profile) {
            Toast.makeText(getApplicationContext(), "Comming soon", Toast.LENGTH_LONG).show();

        } else if (id == R.id.nav_logout) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Are you sure you want to logout?");
            builder.setCancelable(false);
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user != null) {
                        FirebaseAuth.getInstance().signOut();
                        signOut();
                        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                        finish();
                    } else {
                        //LOGIN

                    }
                }
            });

            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            builder.show();


        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        } else {

            mUsersRef.child("online").setValue("true");
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        long timestamp = System.currentTimeMillis();
        String serverTime = "" + timestamp;
        mUsersRef.child("online").setValue(serverTime);

    }

    private void loadJobPosts() {
        //do logic here
        Query jobsQuery = mJobsRef.orderByKey().limitToLast(CURRENT_PAGE * ITEMS_PER_PAGE);
        jobsQuery.keepSynced(true);
        jobsQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                JobPost jobPost = dataSnapshot.getValue(JobPost.class);


                itemPos++;

                if (itemPos == 1) {

                    String jobPostKey = dataSnapshot.getKey();

                    mLastKey = jobPostKey;
                    mPrevKey = jobPostKey;

                }

                jobPosts.add(jobPost);


                adapter.notifyDataSetChanged();
                progressDialog.dismiss();

                mSwipeLayout.setRefreshing(false);

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
//                jobPosts.remove(dataSnapshot.getValue(JobPost.class));
//                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void signOut() {
        mAuth.signOut();
        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                });
    }
}