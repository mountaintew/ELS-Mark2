package com.example.els2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class Dashboard extends AppCompatActivity {
    Context context;
    ToggleButton toggleLoc, toggleNotif;
    TextView name, mobile, bday, sex, btype, height, weight, state;
    String fullname, bd, sx, bt, he, we, st, age;
    private ImageView profilepic;
    public Uri imageUri;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    String mobileNumber;
    SharedPreferences user;
    SharedPreferences.Editor editor;

    Handler handler = new Handler();
    Runnable runnable;
    int interval;
    boolean accessedLoc;


    private FusedLocationProviderClient fusedLocationClient;
    String lat, lng;

    private NotificationManagerCompat notificationManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
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


        startService(new Intent(this, BackgroundService.class));
        context = getApplicationContext();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();


        user = getSharedPreferences("user", MODE_PRIVATE);
        editor = getSharedPreferences("user", MODE_PRIVATE).edit();
        mobileNumber = user.getString("mobileNumber", "");

        //NotificationManager
        notificationManager = NotificationManagerCompat.from(this);


        //ToggleButton
        toggleLoc = findViewById(R.id.toggleLoc);
        toggleNotif = findViewById(R.id.toggleNotif);

        //EditText
        name = findViewById(R.id.name);
        mobile = findViewById(R.id.mobile);
        bday = findViewById(R.id.bday);
        sex = findViewById(R.id.sex);
        btype = findViewById(R.id.btype);
        height = findViewById(R.id.height);
        weight = findViewById(R.id.weight);
        state = findViewById(R.id.state);

        fullname = user.getString("firstname", "") + " " + user.getString("lastname", "");
        bd = user.getString("bday", "n/a");
        age = user.getString("age", "n/a");
        sx = user.getString("sex", "");
        bt = user.getString("bloodtype", "");
        we = user.getString("weight", "n/a");
        he = user.getString("height", "n/a");
        st = "Safe";

        name.setText(fullname);
        mobile.setText(mobileNumber);
        bday.setText(bd);
        sex.setText(sx);
        btype.setText(bt);
        height.setText(he);
        weight.setText(we);
        state.setText(st);
        profilepic = findViewById(R.id.profilepic);

        sendOnChannel1(name.getRootView());
        vibrate();


        profilepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosePicture();
            }
        });
        
        toggleLoc.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (toggleLoc.isChecked()){
                    vibrate();
                } else {
                }
                if(ContextCompat.checkSelfPermission(Dashboard.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(Dashboard.this,new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION
                    }, 100);
                } else {
                    accessedLoc = true;
                }
            }
        });
        
        toggleNotif.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (toggleNotif.isChecked()){
                    sendOnChannel1(name.getRootView());
                    vibrate();
                } else {
                    NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.cancel(1);
                }
            }
        });


    }

    @Override
    protected void onResume() {
        interval = 10000;
        handler.postDelayed(runnable = new Runnable() {
            public void run() {
                handler.postDelayed(runnable, interval);
                if (toggleLoc.isChecked()){
                    //send location
                    fusedLocationClient = LocationServices.getFusedLocationProviderClient(Dashboard.this);
                    if (ActivityCompat.checkSelfPermission(Dashboard.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Dashboard.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        askPermission();
                    }
                    fusedLocationClient.getLastLocation()
                            .addOnSuccessListener(Dashboard.this, new OnSuccessListener<Location>() {
                                @Override
                                public void onSuccess(Location location) {
                                   
                                    if (location != null) {
                                        lat = String.valueOf(location.getLatitude());
                                        lng = String.valueOf(location.getLongitude());

                                        Toast.makeText(Dashboard.this, "lat: " + lat + "\nlng: " + lng, Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(context, "no location", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {
                    //stop sending and state safe
                }
            }
        }, interval);
        super.onResume();
    }
    private void choosePicture() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,1);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK && data != null){
            imageUri = data.getData();
            profilepic.setImageURI(imageUri);
            uploadPicture();
        }
    }
    private void uploadPicture() {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("Uploading Image...");
        pd.show();

        StorageReference profileRef = storageReference.child("images/"+mobileNumber);
        profileRef.putFile(imageUri)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        pd.dismiss();
                        Toast.makeText(context, "Failed to upload", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        pd.dismiss();
                        Snackbar.make(findViewById(android.R.id.content), "Image Uploaded.", Snackbar.LENGTH_LONG).show();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        double progressPercent = (100.00 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                        pd.setMessage("Percentage: " + (int) progressPercent + "%");
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
    private void vibrate() {
        if (Build.VERSION.SDK_INT >= 26) {
            ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(VibrationEffect.createOneShot(150, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(500);
        }
    }

    public void sendOnChannel1(View v) {
        RemoteViews collapsedView = new RemoteViews(getPackageName(),
                R.layout.notification_collapsed);
        RemoteViews expandedView = new RemoteViews(getPackageName(),
                R.layout.notification_expanded);

        expandedView.setTextViewText(R.id.notif_name, fullname);
        expandedView.setTextViewText(R.id.notif_cont, "testcontact");
        expandedView.setTextViewText(R.id.notif_bday, bd);
        expandedView.setTextViewText(R.id.notif_age, age);
        expandedView.setTextViewText(R.id.notif_sex, sx);
        expandedView.setTextViewText(R.id.notif_height, he);
        expandedView.setTextViewText(R.id.notif_weight, we);
        expandedView.setTextViewText(R.id.notif_state, st);
        //set the profile pic here


        Notification notification = new NotificationCompat.Builder(this, App.CHANNEL_1_ID)
                .setSmallIcon(R.drawable.ic_baseline_location_on_24)
                .setCustomContentView(collapsedView)
                .setCustomBigContentView(expandedView)
                //.setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .build();

        notificationManager.notify(1, notification);
    }
    public void sendOnChannel2(View v) {
        Notification notification = new NotificationCompat.Builder(this, App.CHANNEL_2_ID)
                .setSmallIcon(R.drawable.ic_baseline_location_on_24)
                .setContentTitle("ELS")
                .setOngoing(true)
                .setContentText("Running...")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build();
        notificationManager.notify(2, notification);
    }
    public void askPermission(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, 100);
        } else {
            return;
        }
    }
}