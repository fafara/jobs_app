package com.me.njerucyrus.jobsapp2;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by njerucyrus on 2/23/18.
 */

public class JobApp extends MultiDexApplication {

    private FirebaseAuth mAuth;
    private DatabaseReference mUsersRef;
    private FirebaseUser mCurrentUser;

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        if(mCurrentUser !=null) {
            mUsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUser.getUid());

            mUsersRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot != null) {

                        long timestamp = System.currentTimeMillis();
                        String serverTime = ""+timestamp;
                        mUsersRef.child("online").onDisconnect().setValue(serverTime);


                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

}
