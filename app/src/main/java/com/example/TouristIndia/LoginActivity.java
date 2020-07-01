package com.example.TouristIndia;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {

    protected LinearLayout progress;
    String type = "";
    String codeSent;
    FirebaseAuth mAuth;
    FirebaseDatabase mDatabase;
    DatabaseReference mref;
    String TAG = "phoneauth";
    boolean flag = false;
    List<String> list = new ArrayList<>();
    private EditText code, phone, otp;
    private Dialog otpDialog = new Dialog(this);
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            String getotp = phoneAuthCredential.getSmsCode();
            if (getotp != null) {
                otp.setText(getotp);
                verifyCode(getotp);
            }
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {

        }

        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
//            Toast.makeText(LoginActivity.this,"code received",Toast.LENGTH_SHORT).show();
            codeSent = s;

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        type = getIntent().getStringExtra("type");
        progress = findViewById(R.id.progress);

        mAuth = FirebaseAuth.getInstance();

        mDatabase = FirebaseDatabase.getInstance();
        mref = mDatabase.getReference("Admins");

        code = findViewById(R.id.countrycode);
        phone = findViewById(R.id.phone);
        LinearLayout verify = findViewById(R.id.verify);

        otpDialog.setContentView(R.layout.otp_dialog);

        otp = otpDialog.findViewById(R.id.otp);
        LinearLayout otpVerify;
        otpVerify = otpDialog.findViewById(R.id.otpverify);

        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendVerificationCode();
            }
        });

        otpVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verifyCode();
            }
        });
    }

    private void verifyCode() {
        String otpcode = otp.getText().toString();
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codeSent, otpcode);
        signInWithPhoneAuthCredential(credential);
        otpDialog.dismiss();
    }

    private void verifyCode(String smscode) {

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codeSent, smscode);

        signInWithPhoneAuthCredential(credential);

        otpDialog.dismiss();
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String num = "+" + code.getText().toString() + phone.getText().toString();
                            storeMobile(num);
                            Intent i = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(i);

                        } else {
                            Toast.makeText(LoginActivity.this, "OTP incorrect!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void storeMobile(String res) {

        SharedPreferences ref = getApplicationContext().getSharedPreferences("Covid", MODE_PRIVATE);
        SharedPreferences.Editor editor = ref.edit();
        editor.putString("mobile", res);
        editor.apply();

    }

    private void sendVerificationCode() {


        String phoneNumber = "+" + code.getText().toString() + phone.getText().toString();

        if (phoneNumber.length() < 10) {
            phone.setError("Enter a invalid phone number!");
            phone.requestFocus();
            return;
        }


        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                LoginActivity.this,               // Activity (for callback binding)
                mCallbacks);

        otpDialog.show();

//        Toast.makeText(LoginActivity.this,"code sent",Toast.LENGTH_SHORT).show();

    }

    private boolean isEligible(final String phoneNumber) {

        mref = FirebaseDatabase.getInstance().getReference();
        mref.child("Admins").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    String comment = ds.getValue(String.class);
                    Log.d(TAG, comment);
                    if (comment.equals(phoneNumber)) {
                        flag = true;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        return flag;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
}
