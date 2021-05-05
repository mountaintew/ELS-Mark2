package com.example.els2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class CheckOtp extends AppCompatActivity {

    TextView texthelper;
    EditText et1, et2, et3, et4 ,et5, et6;
    Button submitbtn;
    boolean etc1 = false, etc2 = false, etc3 = false, etc4 = false, etc5 = false , etc6 = false;
    String verId, mobile, acc;
    Animation fade_in, fade_out;
    Context context;
    ProgressBar progressBar;
    NetworkInfo activeNetwork;
    ConnectivityManager cm;
    boolean isConnected;
    String number;
    SharedPreferences.Editor editor;

    DatabaseReference accounts = FirebaseDatabase.getInstance().getReference("Residents");
    @SuppressLint({"SetTextI18n", "CommitPrefEdits"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_otp);
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
        texthelper = findViewById(R.id.texthelper);
        texthelper.setText("Please type the verification code\nsent to +63" + getIntent().getStringExtra("mobile"));
        editor = getSharedPreferences("user", MODE_PRIVATE).edit();

        et1 = findViewById(R.id.et1);
        et2 = findViewById(R.id.et2);
        et3 = findViewById(R.id.et3);
        et4 = findViewById(R.id.et4);
        et5 = findViewById(R.id.et5);
        et6 = findViewById(R.id.et6);

        fade_in = AnimationUtils.loadAnimation(context, R.anim.fif_alt);
        fade_out = AnimationUtils.loadAnimation(context, R.anim.fof_alt);


        submitbtn = findViewById(R.id.submitbtn);
        progressBar = findViewById(R.id.progressbar);

        cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        activeNetwork = cm.getActiveNetworkInfo();
        isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        verId = getIntent().getStringExtra("verId");
        mobile = getIntent().getStringExtra("mobile");

        et1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                et2.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable s) {
                et2.requestFocus();
            }
        });
        et2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                et3.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable s) {
                et3.requestFocus();
            }
        });
        et3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                et4.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable s) {
                et4.requestFocus();
            }
        });
        et4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                et5.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable s) {
                et5.requestFocus();
            }
        });
        et5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                et6.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable s) {
                et6.requestFocus();
            }
        });

        submitbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.startAnimation(fade_in);
                progressBar.setVisibility(View.VISIBLE);
                submitbtn.startAnimation(fade_out);
                submitbtn.setVisibility(View.GONE);

                etc1 = checkfield(et1.getText().toString());
                etc2 = checkfield(et2.getText().toString());
                etc3 = checkfield(et3.getText().toString());
                etc4 = checkfield(et4.getText().toString());
                etc5 = checkfield(et5.getText().toString());
                etc6 = checkfield(et6.getText().toString());

                if (etc1 && etc2 && etc3 && etc4 && etc5 && etc6){
                    String code = et1.getText().toString() +
                            et2.getText().toString() +
                            et3.getText().toString() +
                            et4.getText().toString() +
                            et5.getText().toString() +
                            et6.getText().toString();

                    if (verId != null){

                        PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(verId,code);
                        FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()){
                                            Query q = accounts.orderByChild("number").equalTo("+63" + mobile);
                                            q.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    if (snapshot.exists()){
                                                        //has Account
                                                        editor.putBoolean("hasMobile",true);
                                                        editor.putBoolean("hasInstructed",true);
                                                        editor.putBoolean("hasRegistered",true);
                                                        editor.putBoolean("hasLocation",true);
                                                        editor.apply();
                                                        Intent i=new Intent(CheckOtp.this, Dashboard.class);
                                                        startActivity(i);
                                                        finish();
                                                        overridePendingTransition(R.anim.fade_in_slow,R.anim.fade_out_slow);
                                                    } else {
                                                        //new Account
                                                        editor.putBoolean("hasMobile",true);
                                                        editor.putString("mobileNumber", "+63" + mobile);
                                                        editor.apply();
                                                        Intent i=new Intent(CheckOtp.this, Register.class);
                                                        startActivity(i);
                                                        finish();
                                                        overridePendingTransition(R.anim.fade_in_slow,R.anim.fade_out_slow);
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });
                                        } else {
                                            progressBar.startAnimation(fade_out);
                                            progressBar.setVisibility(View.GONE);
                                            submitbtn.startAnimation(fade_in);
                                            submitbtn.setVisibility(View.VISIBLE);
                                            Toast.makeText(context, R.string.invalidcode, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                } else {
                    Toast.makeText(CheckOtp.this, R.string.invalidcode, Toast.LENGTH_SHORT).show();
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
    public boolean checkfield(String text){
        String num = "[0-9]";
        if (text.equals("")){
            return false;
        }
        if (text.matches(num)){
            return true;
        }
        return false;
    }
}