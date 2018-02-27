package com.me.njerucyrus.jobsapp2;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.glomadrian.codeinputlib.CodeInput;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;

import java.util.Arrays;

public class PhoneAuthActivity extends AppCompatActivity {
    private CountryCodePicker ccp;
    private EditText editTextCarrierNumber;
    private Button mBtnSubmit, mBtnVerify, mBtnResend;
    private ImageView appIcon1;
    private TextView txtEnterphone;
    private TextView txtResendCodeTimer;
    private CodeInput txtVerificationCode;
    private boolean mVerificationInProgress = false;
    private String mVerificationId;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private FirebaseAuth mAuth;
    private ProgressDialog mProgressDialog;
    private final static int STATE_CODE_SENT = 0;
    private final static int STATE_VERIFICATION_SUCCESSFUL = 1;
    private final static int STATE_VERIFICATION_FAILED = 3;
    private final static int STATE_QUOTA_EXCEEDED = 4;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_auth);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        ccp = (CountryCodePicker) findViewById(R.id.ccp);
        editTextCarrierNumber = (EditText) findViewById(R.id.editText_carrierNumber);
        ccp.registerCarrierNumberEditText(editTextCarrierNumber);
        mBtnSubmit = (Button)findViewById(R.id.btn_submit_phone);

        txtEnterphone = (TextView) findViewById(R.id.textview_enter_phone);

        txtVerificationCode = (CodeInput)findViewById(R.id.verification_code_input);

        mBtnVerify = (Button)findViewById(R.id.btn_verify);
        appIcon1 = (ImageView)findViewById(R.id.logo1);
        txtResendCodeTimer = (TextView)findViewById(R.id.txt_timer);

        mBtnResend = (Button)findViewById(R.id.btn_resend);

        mProgressDialog = new ProgressDialog(PhoneAuthActivity.this);
        mProgressDialog.setTitle("Submitting");
        mProgressDialog.setTitle("Please wait ...");
        mProgressDialog.setCanceledOnTouchOutside(false);

        mBtnSubmit.setEnabled(false);
        mBtnSubmit.setVisibility(View.INVISIBLE);
        mAuth = FirebaseAuth.getInstance();


        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks(){

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                if (mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }

                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                if (mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }
                updateUI(STATE_VERIFICATION_FAILED);
            }

            @Override
            public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(verificationId, forceResendingToken);
                mResendToken = forceResendingToken;
                mVerificationId = verificationId;
                updateUI(STATE_CODE_SENT);


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
                    editTextCarrierNumber.setCompoundDrawablesWithIntrinsicBounds(
                            null, null, icon, null);

                    mBtnSubmit.setEnabled(true);
                    mBtnSubmit.setVisibility(View.VISIBLE);

                    mBtnSubmit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mProgressDialog.show();

                            confirmPhoneNumber(ccp.getFullNumberWithPlus());
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
                mProgressDialog.setTitle("Validating code.");
                mProgressDialog.setMessage("Please wait");
                mProgressDialog.show();
                String code = Arrays.toString(txtVerificationCode.getCode());
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
                signInWithPhoneAuthCredential(credential);
            }
        });

        mBtnResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences settings = getSharedPreferences("AUTH_DATA",
                        Context.MODE_PRIVATE);

                String phoneNumber = settings.getString("phoneNumber", "");
                if(!phoneNumber.equals("")){
                    mProgressDialog.setTitle("Resending");
                    mProgressDialog.setMessage("Please wait..");
                    long seconds = 60000;
                    showResendCodeCountDown(seconds);
                    resendVerificationCode(phoneNumber, mResendToken);
                }
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser !=null){
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }

    }

    private void resendVerificationCode(String phoneNumber, PhoneAuthProvider.ForceResendingToken mResendToken) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                java.util.concurrent.TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks,         // OnVerificationStateChangedCallbacks
                mResendToken);             // ForceResendingToken from callbacks
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(STATE_VERIFICATION_SUCCESSFUL);
                        }else{

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                updateUI(STATE_VERIFICATION_FAILED);
                            }
                            else if (task.getException() instanceof FirebaseTooManyRequestsException){
                                updateUI(STATE_QUOTA_EXCEEDED);
                            }
                        }
                    }
                });
    }


    private void updateUI(int state){
        switch (state){
            case STATE_CODE_SENT:
                if (mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }
                txtEnterphone.setVisibility(View.GONE);
                ccp.setVisibility(View.GONE);
                editTextCarrierNumber.setVisibility(View.GONE);
                appIcon1.setVisibility(View.GONE);
                mBtnSubmit.setVisibility(View.GONE);

                txtVerificationCode.setVisibility(View.VISIBLE);
                txtResendCodeTimer.setVisibility(View.VISIBLE);
                mBtnVerify.setVisibility(View.VISIBLE);
                mBtnResend.setVisibility(View.VISIBLE);
                long seconds = 60000;
                showResendCodeCountDown(seconds);
                break;
            case STATE_VERIFICATION_SUCCESSFUL:
                if (mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }
                startActivity(new Intent(PhoneAuthActivity.this, ProfileActivity.class));
                finish();
                break;
            case STATE_VERIFICATION_FAILED:
                if (mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }
                Toast.makeText(PhoneAuthActivity.this,"Invalid Verification code",Toast.LENGTH_SHORT).show();
                break;
            case STATE_QUOTA_EXCEEDED:
                if (mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }

                Toast.makeText(PhoneAuthActivity.this,"Firebase Quota exceeded for this project",Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void showResendCodeCountDown(long seconds) {
        new CountDownTimer(seconds, 1000){

            @Override
            public void onTick(long millisUntilFinished) {
                long timeLeft = millisUntilFinished/1000;
                String countDownText = "Resend Code After "+timeLeft+" Seconds";
                txtResendCodeTimer.setText(countDownText);
                mBtnResend.setEnabled(false);
            }

            @Override
            public void onFinish() {
                txtResendCodeTimer.setVisibility(View.GONE);
                mBtnResend.setEnabled(true);
            }
        }.start();
    }

    private void confirmPhoneNumber(final String phoneNumber){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Phone Number ");
        builder.setMessage("Proceed with "+phoneNumber);
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                                phoneNumber,
                                60,
                                java.util.concurrent.TimeUnit.SECONDS,
                                PhoneAuthActivity.this,
                                mCallbacks);

                SharedPreferences settings = getSharedPreferences("AUTH_DATA",
                        Context.MODE_PRIVATE);

                SharedPreferences.Editor editor = settings.edit();
                editor.putString("phoneNumber", phoneNumber);
                editor.apply();
                editor.commit();


            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.show();
    }


}
