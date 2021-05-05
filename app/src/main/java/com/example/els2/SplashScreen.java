package com.example.els2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SplashScreen extends AppCompatActivity {
    Context context;
    ConnectivityManager connectivityManager;
    NetworkInfo networkInfo;
    boolean isConnected, hasMobile, hasInstructed, hasRegistered, hasLocation, permitted;
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    TextView version;
    String theme;
    RelativeLayout splashscreen;
    Animation fade_out, fade_in;
    ImageView logo;
    ProgressBar progress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setTheme(R.style.DarkTheme);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        int nightModeFlags =
                getBaseContext().getResources().getConfiguration().uiMode &
                        Configuration.UI_MODE_NIGHT_MASK;
        switch (nightModeFlags) {
            case Configuration.UI_MODE_NIGHT_YES:
                //night mode
                //Toast.makeText(this, "night mode", Toast.LENGTH_SHORT).show();
                theme = "night";
                setTheme(R.style.DarkTheme);
                break;
            case Configuration.UI_MODE_NIGHT_NO:
                //light mode
                //Toast.makeText(this, "light mode", Toast.LENGTH_SHORT).show();
                theme = "light";
                setTheme(R.style.LightTheme);
                break;

            case Configuration.UI_MODE_NIGHT_UNDEFINED:
                //default
                //Toast.makeText(this, "default", Toast.LENGTH_SHORT).show();
                theme = "default";
                setTheme(R.style.LightTheme);
                break;
        }
        context = getApplicationContext();
        connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = connectivityManager.getActiveNetworkInfo();
        isConnected = networkInfo != null && networkInfo.isConnectedOrConnecting();


        //Logo
        logo = (ImageView) findViewById(R.id.logo);

        //Progressbar
        progress = findViewById(R.id.progress);

        //version
        version = findViewById(R.id.version);

        //Animations
        fade_out = AnimationUtils.loadAnimation(context, R.anim.fade_out);

        sp = getSharedPreferences("user", MODE_PRIVATE);
        editor = getSharedPreferences("user", MODE_PRIVATE).edit();

        //hasMobile, hasInstructed, hasRegistered, hasLocation;
        permitted = sp.getBoolean("permitted", false);
        hasMobile = sp.getBoolean("hasMobile", false);
        hasRegistered = sp.getBoolean("hasRegistered", false);

        if (!permitted){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    logo.startAnimation(fade_out);
                    progress.startAnimation(fade_out);
                    version.startAnimation(fade_out);
                    Intent i=new Intent(SplashScreen.this, Permissions.class);
                    startActivity(i);
                    finish();
                    overridePendingTransition(R.anim.fade_in_slow,R.anim.fade_out_slow);
                }
            }, 1000);
        }
        if (permitted && !hasMobile && !hasRegistered){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    logo.startAnimation(fade_out);
                    progress.startAnimation(fade_out);
                    version.startAnimation(fade_out);
                    Intent i=new Intent(SplashScreen.this, SetMobile.class);
                    startActivity(i);
                    finish();
                    overridePendingTransition(R.anim.fade_in_slow,R.anim.fade_out_slow);
                }
            }, 1000);
        }
        if (permitted && hasMobile && !hasRegistered){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    logo.startAnimation(fade_out);
                    progress.startAnimation(fade_out);
                    version.startAnimation(fade_out);
                    Intent i=new Intent(SplashScreen.this, Register.class);
                    startActivity(i);
                    finish();
                    overridePendingTransition(R.anim.fade_in_slow,R.anim.fade_out_slow);
                }
            }, 1000);
        }
        if (permitted && hasMobile && hasRegistered){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    logo.startAnimation(fade_out);
                    progress.startAnimation(fade_out);
                    version.startAnimation(fade_out);
                    Intent i=new Intent(SplashScreen.this, Dashboard.class);
                    startActivity(i);
                    finish();
                    overridePendingTransition(R.anim.fade_in_slow,R.anim.fade_out_slow);
                }
            }, 1000);
        }
    }
}