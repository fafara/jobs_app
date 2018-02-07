package com.me.njerucyrus.jobsapp2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    Button btnAuthLogin, btnAuthCreateAccount;
    private FirebaseAuth mAuth;
    EditText txtEmail;
    EditText txtPassword;
    ProgressDialog progressDialog;
    final String TAG = "LoginActivity";

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
        btnAuthCreateAccount = (Button) findViewById(R.id.btnAuthCreateAccount);

        btnAuthCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
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
                                        }

                                    } else {
                                        if (progressDialog.isShowing()) {
                                            progressDialog.dismiss();
                                        }
                                        // If sign in fails, display a message to the user.
                                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                                        Toast.makeText(LoginActivity.this, "Invalid email or password.",
                                                Toast.LENGTH_LONG).show();
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
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
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
}
