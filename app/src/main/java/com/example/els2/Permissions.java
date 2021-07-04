package com.example.els2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;

public class Permissions extends AppCompatActivity {
    LinearLayout ll1, ll2;
    Button ab1, ab2;
    LottieAnimationView anim1, anim2;
    Animation fifalt, fofalt;
    Context context;
    int dencount;
    SharedPreferences user;
    SharedPreferences.Editor editor;
    RelativeLayout permissions;
    TextView locationtitle, locationdetails, notiftitle, notifdetails1, notifdetails2, notifdetails3, notifdetails4, notifdetails5, notifdetails6;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permissions);
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
        permissions = findViewById(R.id.permissions);
        user = getSharedPreferences("user", MODE_PRIVATE);
        locationtitle = findViewById(R.id.locationtitle);
        locationdetails = findViewById(R.id.locationdetails);

        notiftitle = findViewById(R.id.notiftitle);
        notifdetails1 = findViewById(R.id.notifdetails1);
        notifdetails2 = findViewById(R.id.notifdetails2);
        notifdetails3 = findViewById(R.id.notifdetails3);
        notifdetails4 = findViewById(R.id.notifdetails4);
        notifdetails5 = findViewById(R.id.notifdetails5);
        notifdetails6 = findViewById(R.id.notifdetails6);

        context = getApplicationContext();
        editor = getSharedPreferences("user", MODE_PRIVATE).edit();
        dencount = 0;
        ll1 = findViewById(R.id.ll1);
        ll2 = findViewById(R.id.ll2);
        ab1 = findViewById(R.id.ab1);
        ab2 = findViewById(R.id.ab2);


        fifalt = AnimationUtils.loadAnimation(context, R.anim.fif_alt);
        fofalt = AnimationUtils.loadAnimation(context, R.anim.fof_alt);

        anim1 = findViewById(R.id.anim1);
        anim2 = findViewById(R.id.anim2);
        anim1.startAnimation(fifalt);
        anim1.setVisibility(View.VISIBLE);


        ab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askPermission();
            }
        });
        ab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ab2.getText().equals("Done")) {
                    editor.putBoolean("permitted", true);
                    editor.apply();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            ll2.startAnimation(fofalt);
                            ll2.setVisibility(View.GONE);
                            ab2.startAnimation(fofalt);
                            ab2.setVisibility(View.GONE);

                            Intent i = new Intent(Permissions.this, SetMobile.class);
                            startActivity(i);
                            finish();
                            overridePendingTransition(R.anim.fade_in_slow, R.anim.fade_out_slow);
                        }
                    }, 1000);
                    return;
                } else {
                    ll1.setVisibility(View.GONE);
                    ab1.setVisibility(View.GONE);
                    ab2.startAnimation(fifalt);
                    ab2.setText("Done");
                    ab2.setBackgroundResource(R.drawable.allowbuttons_done);
                    startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
                }
            }
        });


        int nightModeFlags =
                getBaseContext().getResources().getConfiguration().uiMode &
                        Configuration.UI_MODE_NIGHT_MASK;
        switch (nightModeFlags) {
            case Configuration.UI_MODE_NIGHT_YES:
                //night mode
                //Toast.makeText(this, "night mode", Toast.LENGTH_SHORT).show();
                setTheme(R.style.DarkTheme);
                getWindow().setStatusBarColor(Color.parseColor("#222222"));
                permissions.setBackgroundColor(Color.parseColor("#222222"));
                locationtitle.setTextColor(Color.parseColor("#fcbc20"));
                locationdetails.setTextColor(Color.parseColor("#fafafa"));

                notiftitle.setTextColor(Color.parseColor("#fcbc20"));


                ab1.setTextColor(Color.parseColor("#fafafa"));
                anim2.setBackgroundResource(R.drawable.dark_notif_bg);
                notifdetails1.setTextColor(Color.parseColor("#fafafa"));
                notifdetails2.setTextColor(Color.parseColor("#ebebeb"));
                notifdetails3.setTextColor(Color.parseColor("#ebebeb"));
                notifdetails4.setTextColor(Color.parseColor("#ebebeb"));
                notifdetails5.setTextColor(Color.parseColor("#ebebeb"));
                notifdetails6.setTextColor(Color.parseColor("#fafafa"));
                ab2.setTextColor(Color.parseColor("#222222"));
                break;
            case Configuration.UI_MODE_NIGHT_NO:
            case Configuration.UI_MODE_NIGHT_UNDEFINED:
                //light mode
                //Toast.makeText(this, "light mode", Toast.LENGTH_SHORT).show();
                setTheme(R.style.LightTheme);
                getWindow().setStatusBarColor(Color.TRANSPARENT);
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                permissions.setBackgroundColor(Color.parseColor("#FAFAFA"));
                locationtitle.setTextColor(Color.parseColor("#222222"));
                locationdetails.setTextColor(Color.parseColor("#8f8f8f"));

                notiftitle.setTextColor(Color.parseColor("#222222"));


                ab1.setTextColor(Color.parseColor("#222222"));
                anim2.setBackgroundColor(Color.parseColor("#FAFAFA"));
                notifdetails1.setTextColor(Color.parseColor("#222222"));
                notifdetails2.setTextColor(Color.parseColor("#8f8f8f"));
                notifdetails3.setTextColor(Color.parseColor("#8f8f8f"));
                notifdetails4.setTextColor(Color.parseColor("#8f8f8f"));
                notifdetails5.setTextColor(Color.parseColor("#8f8f8f"));
                notifdetails6.setTextColor(Color.parseColor("#222222"));
                ab2.setTextColor(Color.parseColor("#222222"));
                break;


        }
    }

    public void askPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, 100);
        } else {
            ll1.startAnimation(fofalt);
            ll1.setVisibility(View.GONE);
            ab1.startAnimation(fofalt);
            ab1.setVisibility(View.GONE);

            ll2.startAnimation(fifalt);
            ll2.setVisibility(View.VISIBLE);
            ab2.startAnimation(fifalt);
            ab2.setVisibility(View.VISIBLE);

            anim2.playAnimation();
        }
    }

    public void askPermissionFs() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.FOREGROUND_SERVICE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.FOREGROUND_SERVICE
            }, 101);
        } else {
            //permitted foreground service
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 100: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    ll1.startAnimation(fofalt);
                    ll1.setVisibility(View.GONE);
                    ab1.startAnimation(fofalt);
                    ab1.setVisibility(View.GONE);

                    ll2.startAnimation(fifalt);
                    ll2.setVisibility(View.VISIBLE);
                    ab2.startAnimation(fifalt);
                    ab2.setVisibility(View.VISIBLE);

                    anim2.playAnimation();

                    askPermissionFs();
                } else {
                    dencount++;
                    if (dencount >= 1) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(Permissions.this);
                        builder.setMessage(R.string.locdeny)
                                .setCancelable(false)
                                .setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        askPermission();
                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.setTitle("Permission Denied");
                        alert.show();
                    }
                }
                return;
            }
        }
    }


}