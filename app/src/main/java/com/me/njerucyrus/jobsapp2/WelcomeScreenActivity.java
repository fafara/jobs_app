package com.me.njerucyrus.jobsapp2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;

public class WelcomeScreenActivity extends AppCompatActivity {
    private CountryCodePicker ccp;
    private EditText editTextCarrierNumber;
    private Button mBtnSubmit, mBtnVerify;
    private ImageView appIcon1, appIcon2;
    private TextView txtEnterphone;
    private TextView txtEnterCode;
    private EditText txtVerificationCode;
    private boolean mVerificationInProgress = false;
    private String mVerificationId;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private FirebaseAuth mAuth;
    private ProgressDialog mProgressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_screen);
        ccp = (CountryCodePicker) findViewById(R.id.ccp);
        editTextCarrierNumber = (EditText) findViewById(R.id.editText_carrierNumber);
        ccp.registerCarrierNumberEditText(editTextCarrierNumber);
        mBtnSubmit = (Button)findViewById(R.id.btn_submit_phone);
        //other
        txtEnterCode = (TextView)findViewById(R.id.txt_enter_code);
        txtEnterphone = (TextView) findViewById(R.id.textview_enter_phone);

        txtVerificationCode = (EditText)findViewById(R.id.verification_code);
        mBtnVerify = (Button)findViewById(R.id.btn_verify);
        appIcon1 = (ImageView)findViewById(R.id.logo1);
        appIcon2 = (ImageView)findViewById(R.id.logo2);

        mProgressDialog = new ProgressDialog(WelcomeScreenActivity.this);
        mProgressDialog.setTitle("Submitting");
        mProgressDialog.setTitle("Please wait ...");
        mProgressDialog.setCanceledOnTouchOutside(false);

        mBtnSubmit.setEnabled(false);
        mBtnSubmit.setVisibility(View.INVISIBLE);
        mAuth = FirebaseAuth.getInstance();

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks(){

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                mProgressDialog.dismiss();
                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                    mProgressDialog.dismiss();
            }

            @Override
            public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(verificationId, forceResendingToken);
                mProgressDialog.dismiss();
                mVerificationId = verificationId;
                txtEnterphone.setVisibility(View.GONE);
                ccp.setVisibility(View.GONE);
                editTextCarrierNumber.setVisibility(View.GONE);
                appIcon1.setVisibility(View.GONE);
                mBtnSubmit.setVisibility(View.GONE);

                appIcon2.setVisibility(View.VISIBLE);
                txtEnterCode.setVisibility(View.VISIBLE);
                txtVerificationCode.setVisibility(View.VISIBLE);
                mBtnVerify.setVisibility(View.VISIBLE);

            }

            @Override
            public void onCodeAutoRetrievalTimeOut(String verificationId) {
                super.onCodeAutoRetrievalTimeOut(verificationId);
            }
        };

        editTextCarrierNumber.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);

        ccp.setPhoneNumberValidityChangeListener(new CountryCodePicker.PhoneNumberValidityChangeListener() {
            @Override
            public void onValidityChanged(boolean isValidNumber) {
                // your code
                if (isValidNumber){
                    Drawable icon = getResources().getDrawable(R.drawable.ic_check_green_24dp);
                    editTextCarrierNumber.setCompoundDrawablesWithIntrinsicBounds(null, null, icon, null);
                    mBtnSubmit.setEnabled(true);
                    mBtnSubmit.setVisibility(View.VISIBLE);

                    mBtnSubmit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mProgressDialog.show();
                            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                                    ccp.getFullNumberWithPlus(),
                                    60,
                                    java.util.concurrent.TimeUnit.SECONDS,
                                    WelcomeScreenActivity.this,
                                    mCallbacks);
                        }
                    });

                    Toast.makeText(getApplicationContext(), " "+ccp.getFullNumberWithPlus(), Toast.LENGTH_LONG).show(); ;
                }else{
                    editTextCarrierNumber.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                    mBtnSubmit.setEnabled(false);
                    mBtnSubmit.setVisibility(View.INVISIBLE);
                }
            }
        });

        mBtnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String code = txtVerificationCode.getText().toString().trim();
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
                signInWithPhoneAuthCredential(credential);
            }
        });


    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            startActivity(new Intent(WelcomeScreenActivity.this, MainActivity.class));
                            finish();
                        }else{
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                Toast.makeText(WelcomeScreenActivity.this,"Invalid Verification code",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }
}
