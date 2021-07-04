package com.example.els2;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class SplashScreen extends AppCompatActivity {
    Context context;
    ConnectivityManager connectivityManager;
    NetworkInfo networkInfo;
    boolean isConnected, hasMobile, hasInstructed, hasRegistered, hasLocation, permitted;
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    TextView version, title;
    String theme;
    RelativeLayout splashscreen;
    Animation fade_out, fade_in;
    ImageView logo;
    LottieAnimationView progress;
    int delay = 5000;
    RelativeLayout mainLo;
    LinearLayout errormock;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splashscreen);
       setTheme(R.style.DarkTheme);
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
        splashscreen = findViewById(R.id.splashscreen);
        //version
        version = findViewById(R.id.version);
        title = findViewById(R.id.title);

        int nightModeFlags =
                getBaseContext().getResources().getConfiguration().uiMode &
                        Configuration.UI_MODE_NIGHT_MASK;
        switch (nightModeFlags) {
            case Configuration.UI_MODE_NIGHT_YES:
                //night mode
                //Toast.makeText(this, "night mode", Toast.LENGTH_SHORT).show();
                theme = "night";
                getWindow().setStatusBarColor(Color.parseColor("#212121"));
                setTheme(R.style.DarkTheme);
                title.setTextColor(getColor(R.color.main));
                version.setTextColor(getColor(R.color.light));
                splashscreen.setBackgroundColor(getColor(R.color.dark));
                break;
            case Configuration.UI_MODE_NIGHT_NO:
            case Configuration.UI_MODE_NIGHT_UNDEFINED:
                //default
                //Toast.makeText(this, "default", Toast.LENGTH_SHORT).show();
                //light mode
                //Toast.makeText(this, "light mode", Toast.LENGTH_SHORT).show();
                theme = "light";
                title.setTextColor(getColor(R.color.dark));
                version.setTextColor(getColor(R.color.light));
                splashscreen.setBackgroundColor(getColor(R.color.light));
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



        //Animations
        fade_in = AnimationUtils.loadAnimation(context, R.anim.fade_in);
        fade_out = AnimationUtils.loadAnimation(context, R.anim.fade_out);

        title.startAnimation(fade_in);
        title.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                title.startAnimation(fade_out);
                title.setVisibility(View.GONE);
                title.setText("ELS");
                title.startAnimation(fade_in);
                title.setVisibility(View.VISIBLE);
            }
        }, 4000);


        mainLo = findViewById(R.id.mainLo);
        errormock = findViewById(R.id.errormock);

//      isMockSettingsON(context)
//      areThereMockPermissionApps(context))
//      finishAndRemoveTask();

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
                    title.startAnimation(fade_out);
                    Intent i=new Intent(SplashScreen.this, Permissions.class);
                    startActivity(i);
                    finish();
                    overridePendingTransition(R.anim.fade_in_slow,R.anim.fade_out_slow);
                }
            }, delay);
        }
        if (permitted && !hasMobile && !hasRegistered){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    logo.startAnimation(fade_out);
                    progress.startAnimation(fade_out);
                    version.startAnimation(fade_out);
                    title.startAnimation(fade_out);
                    Intent i=new Intent(SplashScreen.this, SetMobile.class);
                    startActivity(i);
                    finish();
                    overridePendingTransition(R.anim.fade_in_slow,R.anim.fade_out_slow);
                }
            }, delay);
        }
        if (permitted && hasMobile && !hasRegistered){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    logo.startAnimation(fade_out);
                    progress.startAnimation(fade_out);
                    version.startAnimation(fade_out);
                    title.startAnimation(fade_out);
                    Intent i=new Intent(SplashScreen.this, Register.class);
                    i.putExtra("todo" , "register");
                    startActivity(i);
                    finish();
                    overridePendingTransition(R.anim.fade_in_slow,R.anim.fade_out_slow);
                }
            }, delay);
        }
        if (permitted && hasMobile && hasRegistered){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    logo.startAnimation(fade_out);
                    progress.startAnimation(fade_out);
                    version.startAnimation(fade_out);
                    title.startAnimation(fade_out);
                    Intent i=new Intent(SplashScreen.this, Dashboard.class);
                    startActivity(i);
                    finish();
                    overridePendingTransition(R.anim.fade_in_slow,R.anim.fade_out_slow);
                }
            }, delay);
        }
    }


    //check if other apps is using mock location
    private boolean areThereMockPermissionApps(Context context) {
        int count = 0;

        PackageManager pm = context.getPackageManager();
        List<ApplicationInfo> packages =
                pm.getInstalledApplications(PackageManager.GET_META_DATA);

        for (ApplicationInfo applicationInfo : packages) {
            try {
                PackageInfo packageInfo = pm.getPackageInfo(applicationInfo.packageName,
                        PackageManager.GET_PERMISSIONS);

                // Get Permissions
                String[] requestedPermissions = packageInfo.requestedPermissions;

                if (requestedPermissions != null) {
                    for (int i = 0; i < requestedPermissions.length; i++) {
                        if (requestedPermissions[i]
                                .equals("android.permission.ACCESS_MOCK_LOCATION")
                                && !applicationInfo.packageName.equals(context.getPackageName())) {
                            count++;
                        }
                    }
                }
            } catch (PackageManager.NameNotFoundException e) {
                Log.e("Got exception " , e.getMessage());
            }
        }

        if (count > 0)
            return true;
        return false;
    }

    //check if mock settings is on
    private boolean isMockSettingsON(Context context) {
        // returns true if mock location enabled, false if not enabled.
        if (Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ALLOW_MOCK_LOCATION).equals("0"))
            return false;
        else
            return true;
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