package com.me.njerucyrus.jobsapp2;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.me.njerucyrus.models.User;

/**
 * Created by njerucyrus on 2/23/18.
 */

public class JobApp extends MultiDexApplication {

    private FirebaseAuth mAuth;
    private DatabaseReference mUsersRef;
    private DatabaseReference profileRef;
    private FirebaseUser mCurrentUser;

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        if(mCurrentUser !=null) {
            mUsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUser.getUid());
            profileRef = FirebaseDatabase.getInstance().getReference().child("Profiles").child(mCurrentUser.getUid());

            profileRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    SharedPreferences settings = getSharedPreferences("PROFILE_DATA",
                            Context.MODE_PRIVATE);

                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("phoneNumber", user.getPhoneNumber());
                    editor.putString("fullName", user.getFullName());
                    editor.putString("email", user.getEmail());
                    editor.putString("userId", user.getUserUid());
                    editor.apply();
                    editor.commit();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
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
