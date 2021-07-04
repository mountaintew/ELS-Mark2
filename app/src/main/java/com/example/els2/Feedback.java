package com.example.els2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class Feedback extends AppCompatActivity {
    TextView fbMessage, bar_admin;
    Context context;
    Button safebtn;
    DatabaseReference reference, userreference;
    String mobilenumber;

    SharedPreferences user;
    SharedPreferences.Editor editor;

    String admin;

    boolean isDarkTheme;

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

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        user = getSharedPreferences("user", MODE_PRIVATE);
        editor = getSharedPreferences("user", MODE_PRIVATE).edit();

        String displaymode = user.getString("displaymode", "light");
        int nightModeFlags =
                getBaseContext().getResources().getConfiguration().uiMode &
                        Configuration.UI_MODE_NIGHT_MASK;
        switch (nightModeFlags) {
            case Configuration.UI_MODE_NIGHT_YES:
                //night mode
                if (displaymode.equals("dark")) {
                    isDarkTheme = true;
                    setContentView(R.layout.dm_activity_feedback);
                    editor.putString("displaymode", "dark");
                    editor.apply();
                } else {
                    isDarkTheme = false;
                    setContentView(R.layout.activity_feedback);
                    editor.putString("displaymode", "light");
                    editor.apply();
                }
                break;
            case Configuration.UI_MODE_NIGHT_UNDEFINED:
            case Configuration.UI_MODE_NIGHT_NO:
                //light mode
                if (displaymode.equals("light")) {
                    isDarkTheme = false;
                    setContentView(R.layout.activity_feedback);
                    editor.putString("displaymode", "light");
                    editor.apply();
                } else {
                    isDarkTheme = true;
                    setContentView(R.layout.dm_activity_feedback);
                    editor.putString("displaymode", "dark");
                    editor.apply();
                }
                break;
        }

        if (Build.VERSION.SDK_INT < 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
            getWindow().setStatusBarColor(isDarkTheme ? getColor(R.color.dark) : Color.TRANSPARENT);
        }
        context = getApplicationContext();
        fbMessage = findViewById(R.id.fbMessage);
        bar_admin = findViewById(R.id.bar_admin);
        fbMessage.setText(getIntent().getStringExtra("feedback"));
        bar_admin.setText("- Barangay Admin " + getIntent().getStringExtra("admin"));
        safebtn = findViewById(R.id.safebtn);
        mobilenumber = getIntent().getStringExtra("mobilenumber");

        reference = FirebaseDatabase.getInstance().getReference("Markers");
        userreference = FirebaseDatabase.getInstance().getReference("Users");

        safebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reference.child(mobilenumber).setValue(null, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                        userreference.child(mobilenumber + "/services").setValue(null, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                Task<Void> toggled =userreference.child(mobilenumber + "/info").child("toggled").setValue(false);
                                Task<Void> state =userreference.child(mobilenumber + "/info").child("state").setValue("Safe");


                                toggled.addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        state.addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                inSafe();

                                                Intent i = new Intent(Feedback.this, Dashboard.class);
                                                startActivity(i);
                                                finish();
                                                overridePendingTransition(R.anim.show_up, R.anim.show_down);
                                            }
                                        });
                                    }
                                });
                            }
                        });
                    }
                });
            }
        });
    }

    private void inSafe() {
        editor.putBoolean("inEmergency", false);
        editor.apply();
    }
}