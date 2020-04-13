package com.example.chatty;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
import com.shashank.sony.fancydialoglib.Animation;
import com.shashank.sony.fancydialoglib.FancyAlertDialog;
import com.shashank.sony.fancydialoglib.Icon;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.concurrent.TimeUnit;

public class PhoneNumberAuthenticationActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "PhoneAuthActivity";

    private String mVerificationId;

    private static final String KEY_VERIFY_IN_PROGRESS = "key_verify_in_progress";

    private boolean mVerificationInProgress = false;

    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    private Button btnSendCode, btnResendCode, btnOTP;
    private EditText phoneNumber, OTP;
    private FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_number_authentication);

        if (savedInstanceState != null)
            onRestoreInstanceState(savedInstanceState);

        mFirebaseAuth = FirebaseAuth.getInstance();

        btnSendCode = findViewById(R.id.btnSendCodeToPhoneNumber);
        btnResendCode = findViewById(R.id.btnResendCodeToPhoneNumber);
        btnOTP = findViewById(R.id.btnSendOTP);

        btnOTP.setOnClickListener(this);
        btnResendCode.setOnClickListener(this);
        btnSendCode.setOnClickListener(this);

        phoneNumber = findViewById(R.id.edtInputPhoneNumber);
        phoneNumber.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                try {
                    startPhoneNumberVerification(phoneNumber.getText().toString());
                } catch (Exception e) {
                    if (phoneNumber.getText().toString().equals("")){
                        FancyToast.makeText(PhoneNumberAuthenticationActivity.this, "Input Phone Number", Toast.LENGTH_SHORT, FancyToast.INFO, true).show();
                    } else {
                        FancyToast.makeText(PhoneNumberAuthenticationActivity.this, "Invalid Phone Number", Toast.LENGTH_SHORT, FancyToast.ERROR, true).show();
                    }
                }
                return false;
            }
        });

        OTP = findViewById(R.id.edtOTP);
        OTP.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                try {
                    verifyPhoneNUmberWithCode(mVerificationId, OTP.getText().toString());
                } catch (Exception e){
                    if (OTP.getText().toString().equals("")){
                        FancyToast.makeText(PhoneNumberAuthenticationActivity.this, "Please Enter Token", Toast.LENGTH_SHORT, FancyToast.INFO, true).show();
                    }else {
                        FancyToast.makeText(PhoneNumberAuthenticationActivity.this, "Invalid Token", Toast.LENGTH_SHORT, FancyToast.ERROR, true).show();
                    }
                }
                return false;
            }
        });

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                Log.d(TAG, "onVerificationCompleted:" + phoneAuthCredential);

                //mVerificationInProgress =false;

                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {

                Log.w(TAG, "onVerification:" + e);

                if (e instanceof FirebaseAuthInvalidCredentialsException){
                    FancyToast.makeText(getApplicationContext(), "Invalid Phone Number", FancyToast.LENGTH_SHORT, FancyToast.ERROR, true).show();
                } else if (e instanceof FirebaseTooManyRequestsException){
                    FancyToast.makeText(getApplicationContext(), "The SMS quota has been exceeded", FancyToast.LENGTH_SHORT, FancyToast.INFO, true).show();
                }

            }

            @Override
            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                super.onCodeSent(verificationId, token);

                Log.d(TAG, "onCodeSent:" + verificationId);
                Log.d(TAG, "Token" + token);
                new FancyAlertDialog.Builder(PhoneNumberAuthenticationActivity.this).setTitle("One Time Password Sent")
                        .setBackgroundColor(Color.parseColor("#000099"))
                        .setMessage("A One Time Password has being sent to " + phoneNumber.getText().toString())
                        .setPositiveBtnText("OK")
                        .setPositiveBtnBackground(Color.parseColor("#000099"))
                        .isCancellable(false)
                        .setAnimation(Animation.POP)
                        .setIcon(R.drawable.ic_info_outline_black_24dp, Icon.Visible)
                        .build();
                mVerificationId = verificationId;
                mResendToken = token;

            }
        };

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mFirebaseAuth.getCurrentUser();
        if (currentUser != null){
            Intent intent = new Intent(PhoneNumberAuthenticationActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        if (mVerificationInProgress && validatePhoneNumber()){
            startPhoneNumberVerification(phoneNumber.getText().toString());
        }

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_VERIFY_IN_PROGRESS, mVerificationInProgress);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mVerificationInProgress = savedInstanceState.getBoolean(KEY_VERIFY_IN_PROGRESS);
    }

    private void startPhoneNumberVerification(String phone_number){

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phone_number,
                120,
                TimeUnit.SECONDS,
                this,
                mCallbacks);

        mVerificationInProgress = true;
    }

    private void resendVerificationCode(String phone_number, PhoneAuthProvider.ForceResendingToken token){

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phone_number,
                120,
                TimeUnit.SECONDS,
                this,
                mCallbacks,
                token
        );
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential){

        mFirebaseAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Log.d(TAG, "signInWithCredential:success");
                    FirebaseUser user = task.getResult().getUser();
                    FancyToast.makeText(PhoneNumberAuthenticationActivity.this, "Welcome " + user.getPhoneNumber(), Toast.LENGTH_SHORT, FancyToast.SUCCESS, true).show();
                    sendToProfileInputActivity();
                } else {

                    Log.w(TAG, "signInWithCredential:failure", task.getException());
                    if (task.getException() instanceof FirebaseAuthInvalidCredentialsException)
                        FancyToast.makeText(PhoneNumberAuthenticationActivity.this, "Invalid Verification Code", Toast.LENGTH_SHORT, FancyToast.WARNING, true);
                }
            }
        });
    }

    private void sendToProfileInputActivity(){
        // Update this method when Profile input Activity is created
        Intent intent = new Intent(PhoneNumberAuthenticationActivity.this, MainActivity.class);
        startActivity(intent);
    }

    private boolean validatePhoneNumber(){

        String phone_number = phoneNumber.getText().toString();
        if (TextUtils.isEmpty(phone_number)){
            phoneNumber.setError("Invalid Phone Number");
            return false;
        }
        return true;
    }

    private void verifyPhoneNUmberWithCode(String verificationId, String code){
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithPhoneAuthCredential(credential);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.btnSendCodeToPhoneNumber:

                try {
                    startPhoneNumberVerification(phoneNumber.getText().toString());
                } catch (Exception e) {
                    if (phoneNumber.getText().toString().equals("")){
                        FancyToast.makeText(PhoneNumberAuthenticationActivity.this, "Input Phone Number", Toast.LENGTH_SHORT, FancyToast.INFO, true).show();
                    } else {
                        FancyToast.makeText(PhoneNumberAuthenticationActivity.this, "Invalid Phone Number", Toast.LENGTH_SHORT, FancyToast.ERROR, true).show();
                    }
                }
                break;

            case R.id.btnResendCodeToPhoneNumber:

                resendVerificationCode(phoneNumber.getText().toString(), mResendToken);
                break;

            case R.id.btnSendOTP:

                try {
                    verifyPhoneNUmberWithCode(mVerificationId, OTP.getText().toString());
                } catch (Exception e){
                    if (OTP.getText().toString().equals("")){
                        FancyToast.makeText(PhoneNumberAuthenticationActivity.this, "Please Enter Token", Toast.LENGTH_SHORT, FancyToast.INFO, true).show();
                    }else {
                        FancyToast.makeText(PhoneNumberAuthenticationActivity.this, "Invalid Token", Toast.LENGTH_SHORT, FancyToast.ERROR, true).show();
                    }
                }
                break;
        }

    }

    public void hide_keyboard(View view){
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
