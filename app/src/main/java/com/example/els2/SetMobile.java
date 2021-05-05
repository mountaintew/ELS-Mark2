package com.example.els2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class SetMobile extends AppCompatActivity {
    LottieAnimationView smsanim;
    EditText mobileEt;
    Button submitbtn;
    ProgressBar progressbar;
    String mobile_input;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_mobile);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        if (Build.VERSION.SDK_INT < 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        context = getApplicationContext();
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        smsanim = findViewById(R.id.smsicon);
        smsanim.setProgress((float) 1.0);


        mobileEt = findViewById(R.id.mobile);
        submitbtn = findViewById(R.id.submitbtn);
        progressbar = findViewById(R.id.progressbar);

        submitbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressbar.setVisibility(View.VISIBLE);
                submitbtn.setVisibility(View.GONE);
                if (mobileEt.getText().toString().equals("")){
                    progressbar.setVisibility(View.GONE);
                    submitbtn.setVisibility(View.VISIBLE);
                    Toast.makeText(getBaseContext(), "Invalid mobile number", Toast.LENGTH_SHORT).show();

                } else if (mobileEt.getText().charAt(0) != '9'){
                    progressbar.setVisibility(View.GONE);
                    submitbtn.setVisibility(View.VISIBLE);
                    mobileEt.requestFocus();
                    Toast.makeText(getBaseContext(), "Invalid mobile number\nPlease use the format: 9123456789", Toast.LENGTH_LONG).show();
                } else {
                    //begin otp
                    progressbar.setVisibility(View.VISIBLE);
                    submitbtn.setVisibility(View.GONE);
                    mobile_input = mobileEt.getText().toString();

                    if (isConnected){
                        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                                "+63" + mobile_input,
                                60,
                                TimeUnit.SECONDS,
                                SetMobile.this,
                                new PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
                                    @Override
                                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                    }

                                    @Override
                                    public void onVerificationFailed(@NonNull FirebaseException e) {
                                    }

                                    @Override
                                    public void onCodeSent(@NonNull String verId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                        smsanim.playAnimation();
                                        progressbar.setVisibility(View.GONE);
                                        submitbtn.setVisibility(View.VISIBLE);

                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                Intent intent = new Intent(getApplicationContext(), CheckOtp.class);
                                                intent.putExtra("mobile", mobile_input);
                                                intent.putExtra("verId", verId);

                                                startActivity(intent);
                                                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                                finish();
                                            }
                                        }, 1500);
                                    }
                                }
                        );
                    }else {
                        Toast.makeText(context, R.string.connect, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });




    }
    public static void setWindowFlag(Activity activity, final int bits, boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }
}