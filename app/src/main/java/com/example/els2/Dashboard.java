package com.example.els2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import android.Manifest;
import android.annotation.SuppressLint;
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
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class Dashboard extends AppCompatActivity {
    Context context;
    ToggleButton toggleLoc, toggleNotif;
    TextView name, mobile;
    String fullname, bd, sx, bt, he, we, st, age, mc, ar, mn;
    Button viewinfo, myplacebtn, contactbtn, settingsbtn, settingsclosebtn;
    LottieAnimationView redanim;
    ImageView blurblack;
    LinearLayout settingsLayout;
    private ImageView profilepic;
    public Uri imageUri;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    String mobileNumber;
    SharedPreferences user;
    SharedPreferences.Editor editor;
    Animation show_up, show_down, fade_out, fade_in;
    Handler handler = new Handler();
    Runnable runnable;
    int interval;
    boolean accessedLoc;
    Typeface pSans;
    private FusedLocationProviderClient fusedLocationClient;
    String lat, lng;
    DatabaseReference reference;
    private NotificationManagerCompat notificationManager;
    @SuppressLint("CommitPrefEdits")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        reference = FirebaseDatabase.getInstance().getReference("Users");

        startService(new Intent(this, BackgroundService.class));
        context = getApplicationContext();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        user = getSharedPreferences("user", MODE_PRIVATE);
        editor = getSharedPreferences("user", MODE_PRIVATE).edit();
        mobileNumber = user.getString("mobileNumber", "");

        redanim = findViewById(R.id.redanim);

        //NotificationManager
        notificationManager = NotificationManagerCompat.from(this);

        //Button
        viewinfo = findViewById(R.id.viewinfo);
        myplacebtn = findViewById(R.id.myplacebtn);
        contactbtn = findViewById(R.id.contactbtn);
        settingsbtn = findViewById(R.id.settingsbtn);
        settingsclosebtn = findViewById(R.id.settingsclosebtn);

        //ToggleButton
        toggleLoc = findViewById(R.id.toggleLoc);
        toggleNotif = findViewById(R.id.toggleNotif);

        //EditText
        name = findViewById(R.id.name);
        mobile = findViewById(R.id.mobile);

        //Animations
        show_up = AnimationUtils.loadAnimation(context, R.anim.show_up);
        show_down = AnimationUtils.loadAnimation(context, R.anim.show_down);
        fade_out = AnimationUtils.loadAnimation(context, R.anim.fof_alt);
        fade_in = AnimationUtils.loadAnimation(context, R.anim.fif_alt);

        //Layout
        settingsLayout = findViewById(R.id.settingsLayout);

        //Imageview
        blurblack = findViewById(R.id.blurblack);

        fullname = user.getString("firstname", "") + " " + user.getString("lastname", "");
        bd = user.getString("bday", "n/a");
        age = user.getString("age", "n/a");
        sx = user.getString("sex", "");
        bt = user.getString("bloodtype", "");
        we = user.getString("weight", "n/a");
        he = user.getString("height", "n/a");
        mc = user.getString("medcon", "n/a");
        ar = user.getString("allergy", "n/a");
        mn = user.getString("notes", "n/a");
        st = "Safe";
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        Log.d("Token", "onCreate: " + refreshedToken);
        name.setText(fullname);
        mobile.setText(mobileNumber);
        profilepic = findViewById(R.id.profilepic);

        sendOnChannel1(name.getRootView());

        pSans = ResourcesCompat.getFont(context, R.font.productsans);

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
                    redanim.playAnimation();
                    if (toggleNotif.isChecked()){
                        sendOnChannel2(name.getRootView());
                    }
                    Task<Void> q = reference.child(mobileNumber).child("toggled").setValue(true);
                    q.addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(context, "Location turned on", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context, "Please check internet connection", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    redanim.pauseAnimation();
                    if (toggleNotif.isChecked()){
                        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                        notificationManager.cancel(2);
                    }

                    Task<Void> q = reference.child(mobileNumber).child("toggled").setValue(false);
                    q.addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(context, "Location turned off", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context, "Please check internet connection", Toast.LENGTH_SHORT).show();
                        }
                    });
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
                    if (toggleLoc.isChecked()){
                        sendOnChannel2(name.getRootView());
                    }
                } else {
                    NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.cancelAll();

                }
            }
        });

        viewinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openInfo();
            }
        });

        myplacebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMyPlace();
            }
        });

        settingsbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingsLayout.startAnimation(show_up);
                settingsLayout.setVisibility(View.VISIBLE);
                blurblack.startAnimation(fade_in);
                blurblack.setVisibility(View.VISIBLE);
                settingsbtn.setEnabled(false);

            }
        });

        settingsclosebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingsLayout.startAnimation(show_down);
                settingsLayout.setVisibility(View.GONE);
                blurblack.startAnimation(fade_out);
                blurblack.setVisibility(View.GONE);
                settingsbtn.setEnabled(true);


            }
        });

        blurblack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingsLayout.startAnimation(show_down);
                settingsLayout.setVisibility(View.GONE);
                blurblack.startAnimation(fade_out);
                blurblack.setVisibility(View.GONE);
                settingsbtn.setEnabled(true);

            }
        });

        contactbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i=new Intent(Dashboard.this, Contacts.class);
                i.putExtra("mobilenumber", mobileNumber);
                startActivity(i);
                finish();
                overridePendingTransition(R.anim.show_up,R.anim.show_down);
            }
        });



    }

    private void openMyPlace() {
        Intent i=new Intent(Dashboard.this, MyPlace.class);
        startActivity(i);
        finish();
        overridePendingTransition(R.anim.show_up,R.anim.show_down);
    }

    private void openInfo() {
        TextView info_name, info_mobile, info_bday, info_sex, info_btype, info_height, info_weight, info_age, info_medcon, info_ar, info_mednote;
        LinearLayout viewinfo;
        Button editbtn;
        AlertDialog.Builder infoDialog  = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.viewinfo, null);

        viewinfo = view.findViewById(R.id.viewinfo);
        editbtn = view.findViewById(R.id.editbtn);
        info_name = view.findViewById(R.id.info_name);
        info_bday = view.findViewById(R.id.info_bday);
        info_sex  = view.findViewById(R.id.info_sex);
        info_btype = view.findViewById(R.id.info_btype);
        info_height = view.findViewById(R.id.info_height);
        info_weight = view.findViewById(R.id.info_weight);
        info_age = view.findViewById(R.id.info_age);
        info_medcon = view.findViewById(R.id.info_medcon);
        info_ar = view.findViewById(R.id.info_ar);
        info_mednote = view.findViewById(R.id.info_mednote);
        info_mobile = view.findViewById(R.id.info_mobile);

        info_name.setText(fullname);
        info_bday.setText(bd);
        info_sex.setText(sx);
        info_btype.setText(bt);
        info_height.setText(he);
        info_weight.setText(we);
        info_age.setText(age);
        info_medcon.setText(mc);
        info_ar.setText(ar);
        info_mednote.setText(mn);
        info_mobile.setText(mobileNumber);

        infoDialog.setView(view);
        infoDialog.setCancelable(true);
        AlertDialog alertDialog = infoDialog.create();
        alertDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        alertDialog.show();

        viewinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        editbtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                        notificationManager.cancelAll();
                        Intent i=new Intent(Dashboard.this, Register.class);
                        i.putExtra("number" , mobileNumber);
                        i.putExtra("fname" , user.getString("firstname", ""));
                        i.putExtra("lname" , user.getString("lastname", ""));
                        i.putExtra("bday" , bd);
                        i.putExtra("sex" , sx);
                        i.putExtra("btype" , bt);
                        i.putExtra("height" , he.equals("n/a") ? "" : he);
                        i.putExtra("weight" , we.equals("n/a") ? "" : we);
                        i.putExtra("medcon" , mc);
                        i.putExtra("ar" , ar);
                        i.putExtra("mednote" , mn);
                        i.putExtra("todo" , "edit");
                        startActivity(i);
                        finish();
                        overridePendingTransition(R.anim.fade_in_slow,R.anim.fade_out_slow);
                    }
                }, 1000);
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

                                        Geocoder geocoder = new Geocoder(Dashboard.this, Locale.getDefault());
                                        try {
                                            List<Address> addresses = geocoder.getFromLocation(Double.parseDouble(lat), Double.parseDouble(lng), 1);
                                            String cityName = addresses.get(0).getLocality();
                                            Toast.makeText(context, cityName, Toast.LENGTH_SHORT).show();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }


                                        Task<Void> sendLongitude = reference.child(mobileNumber).child("lat").setValue(lat);
                                        sendLongitude.addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                            }
                                        });
                                        Task<Void> sendLatitude = reference.child(mobileNumber).child("lng").setValue(lng);
                                        sendLatitude.addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                            }
                                        });
                                        Task<Void> sendTimestamp = reference.child(mobileNumber).child("timestamp").setValue(ServerValue.TIMESTAMP);
                                        sendLatitude.addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                            }
                                        });

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
        String mn_short = "";
        if (mn.length() > 70){
            mn_short = mn.substring(0,70) + "...";
        }

        expandedView.setTextViewText(R.id.notif_name, fullname);
        expandedView.setTextViewText(R.id.notif_cont, "ice contact");
        expandedView.setTextViewText(R.id.notif_bday, bd);
        expandedView.setTextViewText(R.id.notif_age, age);
        expandedView.setTextViewText(R.id.notif_sex, sx);
        expandedView.setTextViewText(R.id.notif_height, he);
        expandedView.setTextViewText(R.id.notif_weight, we);
        expandedView.setTextViewText(R.id.notif_state, st);
        expandedView.setTextViewText(R.id.notif_medcon, mc);
        expandedView.setTextViewText(R.id.notif_ar, ar);
        expandedView.setTextViewText(R.id.notif_mednote, mn_short);




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
    @Override
    public void onBackPressed() {
        this.onPause();
    }
}