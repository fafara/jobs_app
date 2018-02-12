package com.me.njerucyrus.jobsapp2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class LoginActivity extends AppCompatActivity {
    Button btnAuthLogin, btnAuthCreateAccount;
    private FirebaseAuth mAuth;
    EditText txtEmail;
    EditText txtPassword;
    ProgressDialog progressDialog;
    final String TAG = "LoginActivity";
    SignInButton googleSignInButton;
    GoogleSignInClient mGoogleSignInClient;
    private static int RC_SIGN_IN = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        btnAuthLogin = (Button) findViewById(R.id.btnAuthLogin);
        mAuth = FirebaseAuth.getInstance();
        txtEmail = (EditText) findViewById(R.id.txtAuthUsername);
        txtPassword = (EditText) findViewById(R.id.txtAuthPassword);
        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setMessage("Authenticating...");
        progressDialog.setCancelable(false);
        btnAuthCreateAccount = (Button) findViewById(R.id.btnAuthCreateAccount);

        mAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(LoginActivity.this, gso);


        googleSignInButton = (SignInButton)findViewById(R.id.login_with_google);
        googleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

        btnAuthCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                finish();
            }
        });

        btnAuthLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validate()) {
                    progressDialog.show();
                    mAuth.signInWithEmailAndPassword(txtEmail.getText().toString(), txtPassword.getText().toString())
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d(TAG, "signInWithEmail:success");
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        if (progressDialog.isShowing()) {
                                            progressDialog.dismiss();
                                        }

                                        if (user != null) {
                                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                            finish();
                                        }

                                    } else {
                                        if (progressDialog.isShowing()) {
                                            progressDialog.dismiss();
                                        }
                                        // If sign in fails, display a message to the user.
                                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                                        Toast.makeText(LoginActivity.this, "Authentication failed.",
                                                Toast.LENGTH_LONG).show();
                                    }


                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            if (e instanceof FirebaseNetworkException){
                                if (progressDialog.isShowing()){
                                    progressDialog.dismiss();
                                }
                                Toast.makeText(LoginActivity.this, "No internet connection", Toast.LENGTH_LONG).show();
                            }
                            if (e instanceof FirebaseFirestoreException){
                                if (progressDialog.isShowing()){
                                    progressDialog.dismiss();
                                }
                                Toast.makeText(LoginActivity.this, "Internal server error occurred", Toast.LENGTH_LONG).show();
                            }

                        }
                    });

                } else {
                    Toast.makeText(getApplicationContext(), "Please fix the errors above",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {

                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                Toast.makeText(LoginActivity.this, "Authentication failed.",
                        Toast.LENGTH_LONG).show();

            }
        }
    }

    public boolean validate() {
        boolean valid = true;
        if (txtEmail.getText().toString().trim().isEmpty()) {
            txtEmail.setError("This field is required");
            valid = false;
        }else{
            txtEmail.setError(null);
        }
        if (txtPassword.getText().toString().trim().isEmpty()) {
            txtPassword.setError("This field is required");
            valid = false;
        }else{
            txtEmail.setError(null);
        }
        return valid;

    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        progressDialog.setIndeterminate(false);
        progressDialog.show();
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            if(progressDialog !=null && progressDialog.isShowing()){
                                progressDialog.dismiss();
                            }
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(LoginActivity.this, "Login Successful.",
                                    Toast.LENGTH_LONG).show();

                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        } else {
                            if(progressDialog !=null && progressDialog.isShowing()){
                                progressDialog.dismiss();
                            }
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_LONG).show();
                        }

                    }
                });
    }



}
