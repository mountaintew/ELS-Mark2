package com.example.els2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
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
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.JsonReader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class Dashboard extends AppCompatActivity {
    public Uri imageUri, uri;
    Context context;
    ToggleButton toggleLoc, toggleNotif, toggledm;
    TextView name, mobile, floodlbl, firelbl, medlbl, otherlbl, notiflbl, loclbl, dmlbl, sendingtv;
    String fullname, bd, sx, bt, he, we, st, age, mc, ar, mn, mobileNumber, emc;
    Button embtn, viewinfo, myplacebtn, contactbtn, settingsbtn, settingsclosebtn, floodbtn, firebtn, medicalbtn, othersbtn, cancel_btn;
    LottieAnimationView location_state_anim, safe_anim, em_anim, waiting, em_load_anim;
    ImageView blurblack, floodic, fireic, medic, otheric, infoic, contactic, locic, settingsic;
    LinearLayout settingsLayout;
    RelativeLayout dashboard, floodLo, fireLo, medLo, otherLo, cancel_layout;
    SharedPreferences user;
    SharedPreferences.Editor editor;
    Animation show_up, show_down, fade_out, fade_in, fade_out_slow, fade_in_slow, floodanim, fireanim, medanim, otheranim, outanim1, outanim2, outanim3, outanim4;
    Handler handler = new Handler();
    Runnable runnable;
    int interval;
    boolean accessedLoc, isDarkTheme = false, hasSurvey = false, cancelled = false;
    Typeface pSans;
    String lat, lng;
    DatabaseReference reference, markerReference;
    Bitmap bmp, notif_bmp;
    String emergency;
    private ImageButton profilepic;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private FusedLocationProviderClient fusedLocationClient;
    private NotificationManagerCompat notificationManager;

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

    @SuppressLint({"CommitPrefEdits", "ClickableViewAccessibility"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        reference = FirebaseDatabase.getInstance().getReference("Users");
        markerReference = FirebaseDatabase.getInstance().getReference("Markers");

        startService(new Intent(this, BackgroundService.class));
        context = getApplicationContext();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(Dashboard.this);

        user = getSharedPreferences("user", MODE_PRIVATE);
        editor = getSharedPreferences("user", MODE_PRIVATE).edit();
        mobileNumber = user.getString("mobileNumber", "");
        emc = user.getString("emc", "");

        checkSurveyStatus();
        //TextView
        firelbl = findViewById(R.id.firelbl);
        floodlbl = findViewById(R.id.floodlbl);
        medlbl = findViewById(R.id.medlbl);
        otherlbl = findViewById(R.id.otherlbl);
        notiflbl = findViewById(R.id.notiflbl);
        loclbl = findViewById(R.id.loclbl);
        dmlbl = findViewById(R.id.dmlbl);
        sendingtv = findViewById(R.id.sendingtv);

        //NotificationManager
        notificationManager = NotificationManagerCompat.from(this);

        //Button
        embtn = findViewById(R.id.embtn);
        viewinfo = findViewById(R.id.viewinfo);
        myplacebtn = findViewById(R.id.myplacebtn);
        contactbtn = findViewById(R.id.contactbtn);
        settingsbtn = findViewById(R.id.settingsbtn);
        settingsclosebtn = findViewById(R.id.settingsclosebtn);

        floodbtn = findViewById(R.id.floodbtn);
        firebtn = findViewById(R.id.firebtn);
        medicalbtn = findViewById(R.id.medicalbtn);
        othersbtn = findViewById(R.id.othersbtn);
        cancel_btn = findViewById(R.id.cancel_btn);

        //ToggleButton
        toggleLoc = findViewById(R.id.toggleLoc);
        toggleNotif = findViewById(R.id.toggleNotif);
        toggledm = findViewById(R.id.toggledm);

        //EditText
        name = findViewById(R.id.name);
        mobile = findViewById(R.id.mobile);

        dashboard = findViewById(R.id.dashboard);

        //Animations
        show_up = AnimationUtils.loadAnimation(context, R.anim.show_up);
        show_down = AnimationUtils.loadAnimation(context, R.anim.show_down);
        fade_out = AnimationUtils.loadAnimation(context, R.anim.fof_alt);
        fade_in = AnimationUtils.loadAnimation(context, R.anim.fif_alt);

        fade_out_slow = AnimationUtils.loadAnimation(context, R.anim.fade_out);
        fade_in_slow = AnimationUtils.loadAnimation(context, R.anim.fade_in);


        floodanim = AnimationUtils.loadAnimation(getApplication(), R.anim.emfade_in);
        floodanim.setStartOffset(100);

        fireanim = AnimationUtils.loadAnimation(getApplication(), R.anim.emfade_in);
        fireanim.setStartOffset(150);

        medanim = AnimationUtils.loadAnimation(getApplication(), R.anim.emfade_in);
        medanim.setStartOffset(200);

        otheranim = AnimationUtils.loadAnimation(getApplication(), R.anim.emfade_in);
        otheranim.setStartOffset(250);

        outanim1 = AnimationUtils.loadAnimation(getApplication(), R.anim.emfade_out);
        outanim1.setStartOffset(100);

        outanim2 = AnimationUtils.loadAnimation(getApplication(), R.anim.emfade_out);
        outanim2.setStartOffset(150);

        outanim3 = AnimationUtils.loadAnimation(getApplication(), R.anim.emfade_out);
        outanim3.setStartOffset(200);

        outanim4 = AnimationUtils.loadAnimation(getApplication(), R.anim.emfade_out);
        outanim4.setStartOffset(250);

        //Layout
        settingsLayout = findViewById(R.id.settingsLayout);

        floodLo = findViewById(R.id.floodLo);
        fireLo = findViewById(R.id.fireLo);
        medLo = findViewById(R.id.medLo);
        otherLo = findViewById(R.id.otherLo);

        cancel_layout = findViewById(R.id.cancel_layout);

        //Imageview
        blurblack = findViewById(R.id.blurblack);
        //icons
        floodic = findViewById(R.id.floodic);
        fireic = findViewById(R.id.fireic);
        medic = findViewById(R.id.medic);
        otheric = findViewById(R.id.otheric);

        infoic = findViewById(R.id.infoic);
        contactic = findViewById(R.id.contactic);
        locic = findViewById(R.id.locic);
        settingsic = findViewById(R.id.settingsic);

   
        // Lottie
        safe_anim = findViewById(R.id.safe_anim);
        safe_anim.playAnimation();
        em_anim = findViewById(R.id.em_anim);
        waiting = findViewById(R.id.waiting);
        em_load_anim = findViewById(R.id.em_load_anim);

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
        name.setText(fullname);
        mobile.setText(mobileNumber);
        profilepic = findViewById(R.id.profilepic);

        pSans = ResourcesCompat.getFont(context, R.font.productsans);


        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!cancelled) {
                    cancelled = true;
                    em_load_anim.pauseAnimation();
                    cancel_layout.startAnimation(fade_out);
                    cancel_layout.setVisibility(View.GONE);
                    embtn.startAnimation(fade_in);
                    embtn.setVisibility(View.VISIBLE);
                    Toast.makeText(context, "Report cancelled.", Toast.LENGTH_SHORT).show();
                }
            }
        });


        reference.child(mobileNumber + "/info").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("em_contact").exists()) {
                    String[] split = snapshot.child("em_contact").getValue(String.class).split("@");
                    emc = split[1] + " " + split[0];
                    editor.putString("emc", split[1] + " " + split[0]);
                    editor.apply();
                    sendOnChannel1(embtn.getRootView());
                } else {
                    editor.putString("emc", "");
                    editor.apply();
                    sendOnChannel1(embtn.getRootView());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        LocationRequest mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(30000);
        mLocationRequest.setFastestInterval(3000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationCallback mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    Toast.makeText(context, "Initialization failed.", Toast.LENGTH_SHORT).show();
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }
                        fusedLocationClient.getLastLocation()
                                .addOnSuccessListener(Dashboard.this, new OnSuccessListener<Location>() {
                                    @Override
                                    public void onSuccess(Location location) {
                                        lat = String.valueOf(location.getLatitude());
                                        lng = String.valueOf(location.getLongitude());
                                        Geocoder geocoder = new Geocoder(Dashboard.this, Locale.getDefault());
                                        try {
                                            List<Address> addresses = geocoder.getFromLocation(Double.parseDouble(lat), Double.parseDouble(lng), 1);
                                            String cityName = addresses.get(0).getLocality();
                                            Task<Void> setLocality = reference.child(mobileNumber + "/info").child("locality").setValue(cityName);
                                            setLocality.addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                }
                                            });
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                    }
                }
            }
        };
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.getFusedLocationProviderClient(context).requestLocationUpdates(mLocationRequest, mLocationCallback, null);

        StorageReference photoReference = storageReference.child("images/" + "+639566189375.jpg");
        final long ONE_MEGABYTE = 1024 * 1024;
        photoReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);


                notif_bmp = bmp;
                int w = notif_bmp.getWidth();
                int h = notif_bmp.getHeight();

                int radius = Math.min(h / 2, w / 2);
                Bitmap output = Bitmap.createBitmap(w + 8, h + 8, Bitmap.Config.ARGB_8888);

                Paint p = new Paint();
                p.setAntiAlias(true);

                Canvas c = new Canvas(output);
                c.drawARGB(0, 0, 0, 0);
                p.setStyle(Paint.Style.FILL);

                c.drawCircle((w / 2) + 4, (h / 2) + 4, radius, p);

                p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

                c.drawBitmap(notif_bmp, 4, 4, p);
                p.setXfermode(null);
                p.setStyle(Paint.Style.STROKE);
                p.setColor(Color.WHITE);
                p.setStrokeWidth(3);
                c.drawCircle((w / 2) + 4, (h / 2) + 4, radius, p);
                notif_bmp = output;
                profilepic.setImageBitmap(notif_bmp);
                sendOnChannel1(name.getRootView());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
            }
        });


        String displaymode = user.getString("displaymode", "");
        int nightModeFlags =
                getBaseContext().getResources().getConfiguration().uiMode &
                        Configuration.UI_MODE_NIGHT_MASK;
        switch (nightModeFlags) {
            case Configuration.UI_MODE_NIGHT_YES:
                //night mode
                if (displaymode.equals("dark")) {
                    darkMode();
                    isDarkTheme = true;
                    editor.putString("displaymode", "dark");
                    editor.apply();
                    toggledm.setChecked(true);
                } else if (displaymode.equals("dark")) {
                    lightMode();
                    isDarkTheme = false;
                    editor.putString("displaymode", "light");
                    editor.apply();
                    toggledm.setChecked(false);
                } else {
                    darkMode();
                    isDarkTheme = true;
                    editor.putString("displaymode", "dark");
                    editor.apply();
                    toggledm.setChecked(true);
                }
                break;
            case Configuration.UI_MODE_NIGHT_UNDEFINED:
            case Configuration.UI_MODE_NIGHT_NO:
                //light mode
                if (displaymode.equals("dark")) {
                    darkMode();
                    isDarkTheme = true;
                    editor.putString("displaymode", "dark");
                    editor.apply();
                    toggledm.setChecked(true);
                } else if (displaymode.equals("dark")) {
                    lightMode();
                    isDarkTheme = false;
                    editor.putString("displaymode", "light");
                    editor.apply();
                    toggledm.setChecked(false);
                } else {
                    lightMode();
                    isDarkTheme = false;
                    editor.putString("displaymode", "light");
                    editor.apply();
                    toggledm.setChecked(false);
                }
                break;
        }

        toggledm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (toggledm.isChecked()) {
                    darkMode();
                    editor.putString("displaymode", "dark");
                    editor.apply();
                    isDarkTheme = true;
                } else {
                    lightMode();
                    editor.putString("displaymode", "light");
                    editor.apply();
                    isDarkTheme = false;
                }
            }
        });

        embtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openEmergency("General");
            }
        });
        embtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (floodLo.getVisibility() == View.GONE || floodLo.getVisibility() == View.INVISIBLE) {
                    floodLo.startAnimation(floodanim);
                    floodLo.setVisibility(View.VISIBLE);

                    fireLo.startAnimation(fireanim);
                    fireLo.setVisibility(View.VISIBLE);

                    medLo.startAnimation(medanim);
                    medLo.setVisibility(View.VISIBLE);

                    otherLo.startAnimation(otheranim);
                    otherLo.setVisibility(View.VISIBLE);
                }

                return true;
            }
        });
        dashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (floodLo.getVisibility() == View.VISIBLE) {
                    floodLo.startAnimation(outanim4);
                    floodLo.setVisibility(View.INVISIBLE);
                    fireLo.startAnimation(outanim3);
                    fireLo.setVisibility(View.INVISIBLE);
                    medLo.startAnimation(outanim2);
                    medLo.setVisibility(View.INVISIBLE);
                    otherLo.startAnimation(outanim1);
                    otherLo.setVisibility(View.INVISIBLE);
                }
            }
        });
        floodbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openEmergency("Flood");
            }
        });
        firebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openEmergency("Fire");
            }
        });
        medicalbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openEmergency("Medical");
            }
        });
        othersbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openEmergency("Others");
            }
        });
        profilepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askPermission();
                choosePicture();
            }
        });
        toggleLoc.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (toggleLoc.isChecked()) {

                    if (toggleNotif.isChecked()) {
                        sendOnChannel2(name.getRootView());
                    }
                    Task<Void> q = reference.child(mobileNumber + "/info").child("toggled").setValue(true);
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
                    embtn.setBackgroundResource(isDarkTheme ? R.drawable.dm_ripple_embtn : R.drawable.ripple_embtn);

                    if (toggleNotif.isChecked()) {
                        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                        notificationManager.cancel(2);
                    }

                    Task<Void> q = markerReference.child(mobileNumber).setValue(null);
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
                    Task<Void> qstate = reference.child(mobileNumber + "/info").child("state").setValue("Safe");
                    qstate.addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            safe_anim.playAnimation();
                            em_anim.startAnimation(fade_out);
                            em_anim.setVisibility(View.GONE);
                        }
                    });

                    Task<Void> toggleState = reference.child(mobileNumber + "/info").child("toggled").setValue(false);
                    toggleState.addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                        }
                    });
                }
                if (ContextCompat.checkSelfPermission(Dashboard.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(Dashboard.this, new String[]{
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
                if (toggleNotif.isChecked()) {
                    sendOnChannel1(name.getRootView());
                    if (toggleLoc.isChecked()) {
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

                Intent i = new Intent(Dashboard.this, Contacts.class);
                i.putExtra("mobilenumber", mobileNumber);
                startActivity(i);
                finish();
                overridePendingTransition(R.anim.show_up, R.anim.show_down);
            }
        });

        //GENERAL NOTIFICATIONS ###################################################################################################
        reference.child(mobileNumber + "/services").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("notifications").exists()) {
                    PendingIntent contentIntent =
                            PendingIntent.getActivity(context, 0, new Intent(context, Dashboard.class), 0);

                    String[] result = snapshot.child("notifications").getValue(String.class).split("/");

                    Intent i = new Intent(Dashboard.this, NotificationHandler.class);
                    i.putExtra("mobilenumber", mobileNumber);
                    i.putExtra("notification", result[0]);
                    i.putExtra("from", result[2]);
                    i.putExtra("type", result[1]);
                    startActivity(i);
                    finish();
                    overridePendingTransition(R.anim.show_up, R.anim.show_down);
                    NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    if (Build.VERSION.SDK_INT >= 26) {
                        //When sdk version is larger than26
                        String id = "channel_1";
                        String description = "145";
                        int importance = NotificationManager.IMPORTANCE_HIGH;
                        NotificationChannel channel = new NotificationChannel(id, description, importance);
//                     channel.enableLights(true);
//                     channel.enableVibration(true);
                        manager.createNotificationChannel(channel);
                        Notification notification = new Notification.Builder(Dashboard.this, id)
                                .setCategory(Notification.CATEGORY_MESSAGE)
                                .setSmallIcon(R.drawable.placeicon)
                                .setContentTitle("ELS")
                                .setContentText(result[0])
                                .setContentIntent(contentIntent)
                                .setAutoCancel(true)
                                .build();
                        manager.notify(1, notification);
                    } else {
                        //When sdk version is less than26
                        Notification notification = new NotificationCompat.Builder(Dashboard.this)
                                .setContentTitle("ELS")
                                .setContentText(result[0])
                                .setSmallIcon(R.drawable.placeicon)
                                .setContentIntent(contentIntent)
                                .build();
                        manager.notify(1, notification);
                    }
                } else {
                    // no notifications
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //GENERAL NOTIFICATIONS ###################################################################################################

        //MESSAGE NOTIFICATIONS ###################################################################################################
        reference.child(mobileNumber + "/services").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String message = snapshot.child("message").getValue(String.class);
                    String admin = snapshot.child("reporter").getValue(String.class);
                    PendingIntent contentIntent =
                            PendingIntent.getActivity(context, 0, new Intent(context, Dashboard.class), 0);

                    Intent i = new Intent(Dashboard.this, Feedback.class);
                    i.putExtra("mobilenumber", mobileNumber);
                    i.putExtra("feedback", message);
                    i.putExtra("admin", admin);
                    startActivity(i);
                    finish();
                    overridePendingTransition(R.anim.show_up, R.anim.show_down);

                    NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    if (Build.VERSION.SDK_INT >= 26) {
                        //When sdk version is larger than26
                        String id = "channel_1";
                        String description = "143";
                        int importance = NotificationManager.IMPORTANCE_HIGH;
                        NotificationChannel channel = new NotificationChannel(id, description, importance);
//                     channel.enableLights(true);
//                     channel.enableVibration(true);//
                        manager.createNotificationChannel(channel);
                        Notification notification = new Notification.Builder(Dashboard.this, id)
                                .setCategory(Notification.CATEGORY_MESSAGE)
                                .setSmallIcon(R.drawable.placeicon)
                                .setContentTitle("Barangay Response Team")
                                .setContentText("A message from your barangay.")
                                .setContentIntent(contentIntent)
                                .setAutoCancel(true)
                                .build();
                        manager.notify(1, notification);
                        vibrate();
                    } else {
                        //When sdk version is less than26
                        Notification notification = new NotificationCompat.Builder(Dashboard.this)
                                .setContentTitle("Barangay Response Team")
                                .setContentText("A message from your barangay.")
                                .setSmallIcon(R.drawable.placeicon)
                                .setContentIntent(contentIntent)
                                .build();
                        manager.notify(1, notification);
                        vibrate();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //MESSAGE NOTIFICATIONS ###################################################################################################
        boolean userstate = user.getBoolean("inEmergency", false);
        if (userstate) {
            inEmergency();
        } else {
            inSafe();
        }
    }

    private void lightMode() {
        setTheme(R.style.LightTheme);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        settingsLayout.setBackgroundResource(R.drawable.coordbg);
        notiflbl.setTextColor(getColor(R.color.dark));
        loclbl.setTextColor(getColor(R.color.dark));
        dmlbl.setTextColor(getColor(R.color.dark));
        settingsclosebtn.setTextColor(getColor(R.color.dark));


        dashboard.setBackgroundColor(getColor(R.color.light));
        name.setTextColor(getColor(R.color.dark));
        mobile.setTextColor(getColor(R.color.dark));

        notiflbl.setTextColor(getColor(R.color.dark));
        loclbl.setTextColor(getColor(R.color.dark));

        embtn.setBackgroundResource(R.drawable.ripple_embtn);

        floodbtn.setBackgroundResource(R.drawable.ripple_floodbtn);
        floodic.setBackgroundResource(R.drawable.ic_flood);
        floodlbl.setTextColor(getColor(R.color.dark));

        firebtn.setBackgroundResource(R.drawable.ripple_firebtn);
        fireic.setBackgroundResource(R.drawable.ic_fire);
        firelbl.setTextColor(getColor(R.color.dark));

        medicalbtn.setBackgroundResource(R.drawable.ripple_medbtn);
        medic.setBackgroundResource(R.drawable.ic_medical);
        medlbl.setTextColor(getColor(R.color.dark));

        othersbtn.setBackgroundResource(R.drawable.ripple_othersbtn);
        otheric.setBackgroundResource(R.drawable.ic_others);
        otherlbl.setTextColor(getColor(R.color.dark));

        viewinfo.setBackgroundResource(R.drawable.ripple);
        contactbtn.setBackgroundResource(R.drawable.ripple);
        myplacebtn.setBackgroundResource(R.drawable.ripple);
        settingsbtn.setBackgroundResource(R.drawable.ripple);

        infoic.setBackgroundResource(R.drawable.ic_info);
        locic.setBackgroundResource(R.drawable.placeicon);
        contactic.setBackgroundResource(R.drawable.ic_contact);
        settingsic.setBackgroundResource(R.drawable.ic_settings);
    }

    private void darkMode() {

        setTheme(R.style.DarkTheme);
        getWindow().setStatusBarColor(getColor(R.color.dark));
        settingsLayout.setBackgroundResource(R.drawable.dm_coordbg);
        notiflbl.setTextColor(getColor(R.color.light));
        loclbl.setTextColor(getColor(R.color.light));
        dmlbl.setTextColor(getColor(R.color.light));
        settingsclosebtn.setTextColor(getColor(R.color.light));

        dashboard.setBackgroundColor(getColor(R.color.dark));
        name.setTextColor(getColor(R.color.light));
        mobile.setTextColor(getColor(R.color.light));
        embtn.setBackgroundResource(R.drawable.dm_ripple_embtn);

        floodbtn.setBackgroundResource(R.drawable.dm_ripple_floodbtn);
        floodic.setBackgroundResource(R.drawable.dm_ic_flood);
        floodlbl.setTextColor(getColor(R.color.light));

        firebtn.setBackgroundResource(R.drawable.dm_ripple_firebtn);
        fireic.setBackgroundResource(R.drawable.dm_ic_fire);
        firelbl.setTextColor(getColor(R.color.light));

        medicalbtn.setBackgroundResource(R.drawable.dm_ripple_medbtn);
        medic.setBackgroundResource(R.drawable.dm_ic_medical);
        medlbl.setTextColor(getColor(R.color.light));

        othersbtn.setBackgroundResource(R.drawable.dm_ripple_othersbtn);
        otheric.setBackgroundResource(R.drawable.dm_ic_others);
        otherlbl.setTextColor(getColor(R.color.light));

        viewinfo.setBackgroundResource(R.drawable.dm_ripple);
        contactbtn.setBackgroundResource(R.drawable.dm_ripple);
        myplacebtn.setBackgroundResource(R.drawable.dm_ripple);
        settingsbtn.setBackgroundResource(R.drawable.dm_ripple);

        infoic.setBackgroundResource(R.drawable.dm_ic_info);
        locic.setBackgroundResource(R.drawable.dm_placeicon);
        contactic.setBackgroundResource(R.drawable.dm_ic_contact);
        settingsic.setBackgroundResource(R.drawable.dm_ic_settings);
    }

    public void checkSurveyStatus() {
        reference.child(mobileNumber + "/info").child("surveytaken").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    if (!snapshot.getValue().toString().equals("true")) {
                        showSurvey();
                    } else {
                        hasSurvey = true;
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void showSurvey() {
        Button continuebtn;

        AlertDialog.Builder surveyDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(isDarkTheme ? R.layout.covid_survey : R.layout.covid_survey, null);

        continuebtn = view.findViewById(R.id.continuebtn);

        surveyDialog.setView(view);
        surveyDialog.setCancelable(false);
        AlertDialog alertDialog = surveyDialog.create();

        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        alertDialog.show();

        continuebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                Intent i = new Intent(Dashboard.this, Survey.class);
                i.putExtra("mobilenumber", mobileNumber);
                startActivity(i);
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });
    }

    private void openMyPlace() {
        Intent i = new Intent(Dashboard.this, MyPlace.class);
        i.putExtra("displaymode", isDarkTheme ? "dark" : "light");
        startActivity(i);
        finish();
        overridePendingTransition(R.anim.show_up, R.anim.show_down);
    }

    private void openEmergency(String emergency) {
        TextView emd, emtitle, req, helpertext;
        EditText message;
        ImageView icon;
        Button sendbtn;
        AutoCompleteTextView emdm;
        CheckBox cb;
        RelativeLayout em_layout, em_layout_alt;
        AlertDialog.Builder emergencyDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view;


        switch (emergency) {
            case "Flood":
                view = inflater.inflate(R.layout.emergency, null);
                emd = view.findViewById(R.id.emd);
                message = view.findViewById(R.id.message);
                icon = view.findViewById(R.id.icon);
                sendbtn = view.findViewById(R.id.sendbtn);
                em_layout = view.findViewById(R.id.em_layout);
                emtitle = view.findViewById(R.id.emtitle);
                emd.setText("Flood");
                helpertext = view.findViewById(R.id.helpertext);
                if (isDarkTheme) {
                    em_layout.setBackgroundResource(R.drawable.dm_em_bg);
                    icon.setBackgroundResource(R.drawable.dm_ic_flood);
                    emtitle.setTextColor(getColor(R.color.light));
                    emd.setTextColor(Color.parseColor("#42A5F5"));
                    message.setBackgroundResource(R.drawable.dm_bordered);
                    message.setTextColor(getColor(R.color.light));
                    sendbtn.setBackgroundResource(R.drawable.dm_ripple_sendbtn);
                    sendbtn.setTextColor(getColor(R.color.light));
                    helpertext.setTextColor(getColor(R.color.light));

                } else {
                    em_layout.setBackgroundResource(R.drawable.em_bg);
                    icon.setBackgroundResource(R.drawable.ic_flood);
                    emtitle.setTextColor(getColor(R.color.dark));
                    emd.setTextColor(getColor(R.color.dark));
                    message.setBackgroundResource(R.drawable.bordered);
                    message.setTextColor(getColor(R.color.dark));
                    sendbtn.setBackgroundResource(R.drawable.ripple_sendbtn);
                    sendbtn.setTextColor(getColor(R.color.dark));
                    helpertext.setTextColor(getColor(R.color.dark));
                }

                emergencyDialog.setView(view);
                emergencyDialog.setCancelable(true);
                AlertDialog floodDialog = emergencyDialog.create();
                floodDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                floodDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                floodDialog.show();

                sendbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!toggleLoc.isChecked()) {
                            Toast.makeText(context, "Report failed. Please turn on your location", Toast.LENGTH_SHORT).show();
                            floodDialog.dismiss();
                        } else {
                            cancelled = false;
                            floodDialog.dismiss();
                            embtn.startAnimation(fade_out);
                            embtn.setVisibility(View.GONE);
                            cancel_layout.setVisibility(View.VISIBLE);
                            em_load_anim.playAnimation();
                            if (floodLo.getVisibility() == View.VISIBLE) {
                                floodLo.startAnimation(outanim4);
                                floodLo.setVisibility(View.INVISIBLE);
                                fireLo.startAnimation(outanim3);
                                fireLo.setVisibility(View.INVISIBLE);
                                medLo.startAnimation(outanim2);
                                medLo.setVisibility(View.INVISIBLE);
                                otherLo.startAnimation(outanim1);
                                otherLo.setVisibility(View.INVISIBLE);
                            }
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (!cancelled) {
                                        reference.child(mobileNumber + "/info").addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if (snapshot.exists()) {
                                                    String repLat = snapshot.child("lat").getValue(String.class);
                                                    String repLng = snapshot.child("lng").getValue(String.class);
                                                    String repLocality = snapshot.child("locality").getValue(String.class);
                                                    String repState = "Emergency";
                                                    String repDetails = emd.getText().toString();
                                                    String repMessage = message.getText().toString();
                                                    boolean repToggled = toggleLoc.isChecked();

                                                    Report report = new Report(repLat, repLng, repLocality, repState, repDetails, repMessage, mobileNumber, fullname, repToggled);
                                                    // Report(String lat, String lng, String locality, String state, String details, String message, boolean toggled)
                                                    markerReference.child(mobileNumber).setValue(report, new DatabaseReference.CompletionListener() {
                                                        @Override
                                                        public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                                            Task<Void> sendTimestamp = markerReference.child(mobileNumber).child("timestamp").setValue(ServerValue.TIMESTAMP);
                                                            Task<Void> sendState = reference.child(mobileNumber + "/info").child("state").setValue("Emergency");

                                                            sendTimestamp.addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    sendState.addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            Toast.makeText(context, "Report Successful.", Toast.LENGTH_SHORT).show();
                                                                            cancel_layout.startAnimation(fade_out);
                                                                            cancel_layout.setVisibility(View.GONE);
                                                                            embtn.setVisibility(View.VISIBLE);
                                                                            em_anim.startAnimation(fade_in);
                                                                            em_anim.setVisibility(View.VISIBLE);
                                                                            em_anim.playAnimation();
                                                                            safe_anim.startAnimation(fade_out);
                                                                            safe_anim.setVisibility(View.GONE);
                                                                            inEmergency();
                                                                        }
                                                                    });
                                                                }
                                                            });
                                                        }
                                                    });
                                                } else {
                                                    Toast.makeText(context, "Report Failed.", Toast.LENGTH_SHORT).show();
                                                    floodDialog.dismiss();
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                            }
                                        });
                                    } else {
                                    }
                                }
                            }, 5000);


                        }
                    }
                });
                break;
            case "Fire":
                view = inflater.inflate(R.layout.emergency, null);
                emd = view.findViewById(R.id.emd);
                message = view.findViewById(R.id.message);
                icon = view.findViewById(R.id.icon);
                sendbtn = view.findViewById(R.id.sendbtn);
                em_layout = view.findViewById(R.id.em_layout);
                emtitle = view.findViewById(R.id.emtitle);
                emd.setText("Fire");
                helpertext = view.findViewById(R.id.helpertext);

                if (isDarkTheme) {
                    em_layout.setBackgroundResource(R.drawable.dm_em_bg);
                    icon.setBackgroundResource(R.drawable.dm_ic_fire);
                    emtitle.setTextColor(getColor(R.color.light));
                    emd.setTextColor(Color.parseColor("#FF5722"));
                    message.setBackgroundResource(R.drawable.dm_bordered);
                    message.setTextColor(getColor(R.color.light));
                    sendbtn.setBackgroundResource(R.drawable.dm_ripple_sendbtn);
                    sendbtn.setTextColor(getColor(R.color.light));

                    helpertext.setTextColor(getColor(R.color.light));
                } else {
                    em_layout.setBackgroundResource(R.drawable.em_bg);
                    icon.setBackgroundResource(R.drawable.ic_fire);
                    emtitle.setTextColor(getColor(R.color.dark));
                    emd.setTextColor(getColor(R.color.dark));
                    message.setBackgroundResource(R.drawable.bordered);
                    message.setTextColor(getColor(R.color.dark));
                    sendbtn.setBackgroundResource(R.drawable.ripple_sendbtn);
                    sendbtn.setTextColor(getColor(R.color.dark));
                    helpertext.setTextColor(getColor(R.color.dark));
                }


                emergencyDialog.setView(view);
                emergencyDialog.setCancelable(true);
                AlertDialog fireDialog = emergencyDialog.create();
                fireDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                fireDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                fireDialog.show();

                sendbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!toggleLoc.isChecked()) {
                            Toast.makeText(context, "Report failed. Please turn on your location", Toast.LENGTH_SHORT).show();
                            fireDialog.dismiss();
                        } else {
                            cancelled = false;
                            fireDialog.dismiss();
                            embtn.startAnimation(fade_out);
                            embtn.setVisibility(View.GONE);
                            cancel_layout.setVisibility(View.VISIBLE);
                            em_load_anim.playAnimation();
                            if (floodLo.getVisibility() == View.VISIBLE) {
                                floodLo.startAnimation(outanim4);
                                floodLo.setVisibility(View.INVISIBLE);
                                fireLo.startAnimation(outanim3);
                                fireLo.setVisibility(View.INVISIBLE);
                                medLo.startAnimation(outanim2);
                                medLo.setVisibility(View.INVISIBLE);
                                otherLo.startAnimation(outanim1);
                                otherLo.setVisibility(View.INVISIBLE);
                            }


                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (!cancelled) {
                                        reference.child(mobileNumber + "/info").addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if (snapshot.exists()) {
                                                    String repLat = snapshot.child("lat").getValue(String.class);
                                                    String repLng = snapshot.child("lng").getValue(String.class);
                                                    String repLocality = snapshot.child("locality").getValue(String.class);
                                                    String repState = "Emergency";
                                                    String repDetails = emd.getText().toString();
                                                    String repMessage = message.getText().toString();
                                                    boolean repToggled = toggleLoc.isChecked();

                                                    Report report = new Report(repLat, repLng, repLocality, repState, repDetails, repMessage, mobileNumber, fullname, repToggled);
                                                    // Report(String lat, String lng, String locality, String state, String details, String message, boolean toggled)

                                                    markerReference.child(mobileNumber).setValue(report, new DatabaseReference.CompletionListener() {
                                                        @Override
                                                        public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                                            Task<Void> sendTimestamp = markerReference.child(mobileNumber).child("timestamp").setValue(ServerValue.TIMESTAMP);
                                                            Task<Void> sendState = reference.child(mobileNumber + "/info").child("state").setValue("Emergency");

                                                            sendTimestamp.addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    sendState.addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            Toast.makeText(context, "Report Successful.", Toast.LENGTH_SHORT).show();
                                                                            cancel_layout.startAnimation(fade_out);
                                                                            cancel_layout.setVisibility(View.GONE);
                                                                            embtn.setVisibility(View.VISIBLE);
                                                                            em_anim.startAnimation(fade_in);
                                                                            em_anim.setVisibility(View.VISIBLE);
                                                                            em_anim.playAnimation();
                                                                            safe_anim.startAnimation(fade_out);
                                                                            safe_anim.setVisibility(View.GONE);
                                                                            fireDialog.dismiss();
                                                                            inEmergency();
                                                                        }
                                                                    });
                                                                }
                                                            });
                                                        }
                                                    });
                                                } else {
                                                    Toast.makeText(context, "Report Failed.", Toast.LENGTH_SHORT).show();
                                                    fireDialog.dismiss();
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                            }
                                        });
                                    } else {

                                    }
                                }
                            }, 5000);
                        }
                    }
                });
                break;
            case "Medical":
                view = inflater.inflate(R.layout.emergency, null);
                emd = view.findViewById(R.id.emd);
                message = view.findViewById(R.id.message);
                icon = view.findViewById(R.id.icon);
                sendbtn = view.findViewById(R.id.sendbtn);

                em_layout = view.findViewById(R.id.em_layout);
                emtitle = view.findViewById(R.id.emtitle);
                helpertext = view.findViewById(R.id.helpertext);

                if (isDarkTheme) {
                    em_layout.setBackgroundResource(R.drawable.dm_em_bg);
                    icon.setBackgroundResource(R.drawable.dm_ic_medical);
                    emtitle.setTextColor(getColor(R.color.light));
                    emd.setTextColor(Color.parseColor("#AED581"));
                    message.setBackgroundResource(R.drawable.dm_bordered);
                    message.setTextColor(getColor(R.color.light));
                    sendbtn.setBackgroundResource(R.drawable.dm_ripple_sendbtn);
                    sendbtn.setTextColor(getColor(R.color.light));
                    helpertext.setTextColor(getColor(R.color.light));
                } else {
                    em_layout.setBackgroundResource(R.drawable.em_bg);
                    icon.setBackgroundResource(R.drawable.ic_medical);
                    emtitle.setTextColor(getColor(R.color.dark));
                    emd.setTextColor(getColor(R.color.dark));
                    message.setBackgroundResource(R.drawable.bordered);
                    message.setTextColor(getColor(R.color.dark));
                    sendbtn.setBackgroundResource(R.drawable.ripple_sendbtn);
                    sendbtn.setTextColor(getColor(R.color.dark));
                    helpertext.setTextColor(getColor(R.color.dark));
                }

                emd.setText("Medical");
                message.setHint("Medical concerns (required)");
                emergencyDialog.setView(view);
                emergencyDialog.setCancelable(true);
                AlertDialog medDialog = emergencyDialog.create();
                medDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                medDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                medDialog.show();
                sendbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!toggleLoc.isChecked()) {
                            Toast.makeText(context, "Report failed. Please turn on your location", Toast.LENGTH_SHORT).show();
                            medDialog.dismiss();
                        } else {
                            if (message.getText().toString().equals("")) {
                                Toast.makeText(context, "Please type your medical emergency concerns", Toast.LENGTH_LONG).show();
                            } else {
                                cancelled = false;
                                medDialog.dismiss();
                                embtn.startAnimation(fade_out);
                                embtn.setVisibility(View.GONE);
                                cancel_layout.setVisibility(View.VISIBLE);
                                em_load_anim.playAnimation();
                                if (floodLo.getVisibility() == View.VISIBLE) {
                                    floodLo.startAnimation(outanim4);
                                    floodLo.setVisibility(View.INVISIBLE);
                                    fireLo.startAnimation(outanim3);
                                    fireLo.setVisibility(View.INVISIBLE);
                                    medLo.startAnimation(outanim2);
                                    medLo.setVisibility(View.INVISIBLE);
                                    otherLo.startAnimation(outanim1);
                                    otherLo.setVisibility(View.INVISIBLE);
                                }
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (!cancelled) {
                                            reference.child(mobileNumber + "/info").addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    if (snapshot.exists()) {
                                                        String repLat = snapshot.child("lat").getValue(String.class);
                                                        String repLng = snapshot.child("lng").getValue(String.class);
                                                        String repLocality = snapshot.child("locality").getValue(String.class);
                                                        String repState = "Emergency";
                                                        String repDetails = emd.getText().toString();
                                                        String repMessage = message.getText().toString();
                                                        boolean repToggled = toggleLoc.isChecked();
                                                        //(String lat, String lng, String locality, String state, String details, String message, String mobile, String fullname, boolean toggled)
                                                        Report report = new Report(repLat, repLng, repLocality, repState, repDetails, repMessage, mobileNumber, fullname, repToggled);
                                                        // Report(String lat, String lng, String locality, String state, String details, String message, boolean toggled)
                                                        markerReference.child(mobileNumber).setValue(report, new DatabaseReference.CompletionListener() {
                                                            @Override
                                                            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                                                Task<Void> sendTimestamp = markerReference.child(mobileNumber).child("timestamp").setValue(ServerValue.TIMESTAMP);
                                                                Task<Void> sendState = reference.child(mobileNumber + "/info").child("state").setValue("Emergency");
                                                                sendTimestamp.addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        sendState.addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                Toast.makeText(context, "Report Successful.", Toast.LENGTH_SHORT).show();
                                                                                cancel_layout.startAnimation(fade_out);
                                                                                cancel_layout.setVisibility(View.GONE);
                                                                                embtn.setVisibility(View.VISIBLE);
                                                                                em_anim.startAnimation(fade_in);
                                                                                em_anim.setVisibility(View.VISIBLE);
                                                                                em_anim.playAnimation();
                                                                                safe_anim.startAnimation(fade_out);
                                                                                safe_anim.setVisibility(View.GONE);
                                                                                medDialog.dismiss();
                                                                                inEmergency();
                                                                            }
                                                                        });
                                                                    }
                                                                });
                                                            }
                                                        });
                                                    } else {
                                                        Toast.makeText(context, "Report Failed.", Toast.LENGTH_SHORT).show();
                                                        medDialog.dismiss();
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {
                                                }
                                            });

                                        }
                                    }
                                }, 5000);
                            }
                        }
                    }
                });
                break;
            default:
                view = inflater.inflate(R.layout.emergency_alt, null);

                TextInputLayout tilem = view.findViewById(R.id.tilem);
                message = view.findViewById(R.id.message);
                sendbtn = view.findViewById(R.id.sendbtn);
                emtitle = view.findViewById(R.id.emtitle);
                cb = view.findViewById(R.id.cb);
                emdm = view.findViewById(R.id.manualemd);
                em_layout_alt = view.findViewById(R.id.em_layout_alt);
                req = view.findViewById(R.id.req);
                helpertext = view.findViewById(R.id.helpertext);

                if (isDarkTheme) {
                    em_layout_alt.setBackgroundResource(R.drawable.dm_em_bg);
                    emtitle.setTextColor(getColor(R.color.light));
                    req.setTextColor(getColor(R.color.metatext));

                    tilem.setBoxBackgroundColor(getColor(R.color.dark));
                    tilem.setDefaultHintTextColor(null);
                    emdm.setTextColor(getColor(R.color.light));
                    emdm.setHintTextColor(getColor(R.color.metatext));

                    message.setBackgroundResource(R.drawable.dm_bordered);
                    message.setTextColor(getColor(R.color.light));
                    sendbtn.setBackgroundResource(R.drawable.dm_ripple_sendbtn);
                    helpertext.setTextColor(getColor(R.color.light));
                    sendbtn.setTextColor(getColor(R.color.light));
                    cb.setTextColor(getColor(R.color.light));
                } else {
                    em_layout_alt.setBackgroundResource(R.drawable.em_bg);
                    emtitle.setTextColor(getColor(R.color.dark));
                    req.setTextColor(getColor(R.color.metatext));

                    tilem.setBoxBackgroundColor(getColor(R.color.light));
                    tilem.setDefaultHintTextColor(null);
                    emdm.setTextColor(getColor(R.color.dark));
                    emdm.setHintTextColor(getColor(R.color.metatext));

                    message.setBackgroundResource(R.drawable.bordered);
                    message.setTextColor(getColor(R.color.dark));
                    sendbtn.setBackgroundResource(R.drawable.ripple_sendbtn);
                    sendbtn.setTextColor(getColor(R.color.dark));
                    helpertext.setTextColor(getColor(R.color.dark));
                    cb.setTextColor(getColor(R.color.dark));
                }

                ArrayAdapter<CharSequence> emergencies = ArrayAdapter.createFromResource(getBaseContext(),
                        R.array.emergencies, android.R.layout.simple_spinner_item);
                emdm.setThreshold(1);
                emdm.setAdapter(emergencies);


                emergencyDialog.setView(view);
                emergencyDialog.setCancelable(true);
                AlertDialog alertDialog = emergencyDialog.create();
                alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                alertDialog.show();

                emdm.setText(user.getString("defem", ""));
                message.setText(user.getString("defmsg", ""));
                cb.setChecked(user.getBoolean("hasDefault", false));
                cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        boolean hasdefault = user.getBoolean("hasDefault", false);
                        if (cb.isChecked()) {
                            if (emdm.getText().toString().length() == 0) {
                                Toast.makeText(context, "Emergency details cannot be empty.", Toast.LENGTH_SHORT).show();
                                cb.setChecked(false);
                            } else {
                                editor.putBoolean("hasDefault", true);
                                editor.putString("defem", emdm.getText().toString());
                                editor.putString("defmsg", message.getText().toString());
                                editor.apply();
                                Toast.makeText(context, "Default saved.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            //save
                            if (hasdefault) {
                                emdm.setText("");
                                message.setText("");
                                editor.putBoolean("hasDefault", false);
                                editor.putString("defem", "");
                                editor.putString("defmsg", "");
                                editor.apply();
                                Toast.makeText(context, "Default removed.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

                message.requestFocus();
                sendbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (emdm.getText().toString().equals("")) {
                            Toast.makeText(context, "Please input your emergency", Toast.LENGTH_LONG).show();
                        } else {
                            if (!toggleLoc.isChecked()) {
                                Toast.makeText(context, "Report failed. Please turn on your location", Toast.LENGTH_SHORT).show();
                                alertDialog.dismiss();
                            } else {
                                cancelled = false;
                                alertDialog.dismiss();
                                embtn.startAnimation(fade_out);
                                embtn.setVisibility(View.GONE);
                                cancel_layout.setVisibility(View.VISIBLE);
                                em_load_anim.playAnimation();
                                if (floodLo.getVisibility() == View.VISIBLE) {
                                    floodLo.startAnimation(outanim4);
                                    floodLo.setVisibility(View.INVISIBLE);
                                    fireLo.startAnimation(outanim3);
                                    fireLo.setVisibility(View.INVISIBLE);
                                    medLo.startAnimation(outanim2);
                                    medLo.setVisibility(View.INVISIBLE);
                                    otherLo.startAnimation(outanim1);
                                    otherLo.setVisibility(View.INVISIBLE);
                                }
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (!cancelled) {

                                            reference.child(mobileNumber + "/info").addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    if (snapshot.exists()) {
                                                        String repLat = snapshot.child("lat").getValue(String.class);
                                                        String repLng = snapshot.child("lng").getValue(String.class);
                                                        String repLocality = snapshot.child("locality").getValue(String.class);
                                                        String repState = "Emergency";
                                                        String repDetails = emdm.getText().toString();
                                                        String repMessage = message.getText().toString();
                                                        boolean repToggled = toggleLoc.isChecked();

                                                        Report report = new Report(repLat, repLng, repLocality, repState, repDetails, repMessage, mobileNumber, fullname, repToggled);
                                                        // Report(String lat, String lng, String locality, String state, String details, String message, boolean toggled)


                                                        markerReference.child(mobileNumber).setValue(report, new DatabaseReference.CompletionListener() {
                                                            @Override
                                                            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                                                Task<Void> sendTimestamp = markerReference.child(mobileNumber).child("timestamp").setValue(ServerValue.TIMESTAMP);
                                                                Task<Void> sendState = reference.child(mobileNumber + "/info").child("state").setValue("Emergency");


                                                                sendTimestamp.addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        sendState.addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                Toast.makeText(context, "Report Successful.", Toast.LENGTH_SHORT).show();
                                                                                cancel_layout.startAnimation(fade_out);
                                                                                cancel_layout.setVisibility(View.GONE);
                                                                                embtn.setVisibility(View.VISIBLE);
                                                                                em_anim.startAnimation(fade_in);
                                                                                em_anim.setVisibility(View.VISIBLE);
                                                                                em_anim.playAnimation();
                                                                                safe_anim.startAnimation(fade_out);
                                                                                safe_anim.setVisibility(View.GONE);
                                                                                alertDialog.dismiss();
                                                                                inEmergency();
                                                                            }
                                                                        });
                                                                    }
                                                                });
                                                            }
                                                        });
                                                    } else {
                                                        Toast.makeText(context, "Report Failed.", Toast.LENGTH_SHORT).show();
                                                        alertDialog.dismiss();
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {
                                                }
                                            });
                                        }
                                    }
                                }, 5000);
                            }
                        }
                    }
                });
        }
    }

    private void inEmergency() {
        editor.putBoolean("inEmergency", true);
        editor.apply();
        embtn.setText("Waiting for Response");
        embtn.setEnabled(false);

        safe_anim.setVisibility(View.GONE);
        em_anim.startAnimation(fade_in);
        em_anim.playAnimation();
        em_anim.setVisibility(View.VISIBLE);
        waiting.setVisibility(View.VISIBLE);

        if (floodLo.getVisibility() == View.VISIBLE) {
            floodLo.startAnimation(outanim4);
            floodLo.setVisibility(View.INVISIBLE);
            fireLo.startAnimation(outanim3);
            fireLo.setVisibility(View.INVISIBLE);
            medLo.startAnimation(outanim2);
            medLo.setVisibility(View.INVISIBLE);
            otherLo.startAnimation(outanim1);
            otherLo.setVisibility(View.INVISIBLE);
        }
    }

    private void inSafe() {
        editor.putBoolean("inEmergency", false);
        editor.apply();

        embtn.setText("Emergency");
        embtn.setEnabled(true);

        em_anim.setVisibility(View.GONE);
        safe_anim.startAnimation(fade_in);
        safe_anim.playAnimation();
        safe_anim.setVisibility(View.VISIBLE);
        waiting.setVisibility(View.GONE);
    }

    private void openInfo() {
        TextView info_name, info_mobile, info_bday, info_sex, info_btype, info_height, info_weight, info_age, info_medcon, info_ar, info_mednote;
        LinearLayout viewinfo;
        Button editbtn;

        AlertDialog.Builder infoDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(isDarkTheme ? R.layout.dm_viewinfo : R.layout.viewinfo, null);


        viewinfo = view.findViewById(R.id.viewinfo);
        editbtn = view.findViewById(R.id.editbtn);
        info_name = view.findViewById(R.id.info_name);
        info_bday = view.findViewById(R.id.info_bday);
        info_sex = view.findViewById(R.id.info_sex);
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


        viewinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        editbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                        notificationManager.cancelAll();
                        Intent i = new Intent(Dashboard.this, Register.class);
                        i.putExtra("hasSurvey", hasSurvey);
                        i.putExtra("number", mobileNumber);
                        i.putExtra("fname", user.getString("firstname", ""));
                        i.putExtra("lname", user.getString("lastname", ""));
                        i.putExtra("bday", bd.equals("n/a") ? "" : bd);
                        i.putExtra("sex", sx);
                        i.putExtra("btype", bt);
                        i.putExtra("height", he.equals("n/a") ? "" : he);
                        i.putExtra("weight", we.equals("n/a") ? "" : we);
                        i.putExtra("medcon", mc.equals("n/a") ? "" : mc);
                        i.putExtra("ar", ar.equals("n/a") ? "" : ar);
                        i.putExtra("mednote", mn.equals("n/a") ? "" : mn);
                        i.putExtra("todo", "edit");
                        startActivity(i);
                        finish();
                        overridePendingTransition(R.anim.fade_in_slow, R.anim.fade_out_slow);
                        alertDialog.dismiss();
                    }
                }, 1000);
            }
        });
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        alertDialog.show();
    }

    @Override
    protected void onResume() {
        interval = 10000;
        handler.postDelayed(runnable = new Runnable() {
            public void run() {
                handler.postDelayed(runnable, interval);
                if (toggleLoc.isChecked()) {
                    //send location
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
                                            Task<Void> setLocality = reference.child(mobileNumber + "/info").child("locality").setValue(cityName);
                                            setLocality.addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                }
                                            });
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }

                                        Task<Void> sendLongitude = reference.child(mobileNumber + "/info").child("lat").setValue(lat);
                                        sendLongitude.addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                            }
                                        });
                                        Task<Void> sendLatitude = reference.child(mobileNumber + "/info").child("lng").setValue(lng);
                                        sendLatitude.addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                            }
                                        });
                                        Task<Void> sendTimestamp = reference.child(mobileNumber + "/info").child("timestamp").setValue(ServerValue.TIMESTAMP);
                                        sendTimestamp.addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                            }
                                        });

                                    } else {
                                        Toast.makeText(context, "Location not found.", Toast.LENGTH_SHORT).show();
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
        CropImage.startPickImageActivity(Dashboard.this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE
                && resultCode == Activity.RESULT_OK) {
            Uri imageuri = CropImage.getPickImageResultUri(this, data);
            if (CropImage.isReadExternalStoragePermissionsRequired(this, imageuri)) {
                uri = imageuri;
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}
                        , 0);
            } else {
                startCrop(imageuri);
            }
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Picasso.with(this).load(result.getUri()).transform(new CircleTransform()).into(profilepic);
                sendOnChannel1(name.getRootView());
                uploadPicture(result.getUri());
            }
        }
    }

    private void startCrop(Uri imageuri) {
        CropImage.activity(imageuri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMultiTouchEnabled(true)
                .setAspectRatio(1, 1)
                .start(this);
    }

    private void uploadPicture(Uri iuri) {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("Uploading Image...");
        pd.show();

        StorageReference profileRef = storageReference.child("images/" + mobileNumber);
        profileRef.putFile(iuri)
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

    private void vibrate() {
        if (Build.VERSION.SDK_INT >= 26) {
            ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(VibrationEffect.createOneShot(150, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(500);
        }
    }

    public void sendOnChannel1(View v) {

        RemoteViews collapsedView, expandedView;


        if (isDarkTheme) {
            collapsedView = new RemoteViews(getPackageName(),
                    R.layout.dm_notification_collapsed);
            expandedView = new RemoteViews(getPackageName(),
                    R.layout.dm_notification_expanded);
        } else {
            collapsedView = new RemoteViews(getPackageName(),
                    R.layout.notification_collapsed);
            expandedView = new RemoteViews(getPackageName(),
                    R.layout.notification_expanded);
        }


        String mn_short = "";

        if (mn.length() > 70) {
            mn_short = mn.substring(0, 70) + "...";
        }

        expandedView.setImageViewBitmap(R.id.notif_profile, notif_bmp);
        expandedView.setTextViewText(R.id.notif_cont, emc.equals("") ? "Contact not set" : emc);
        expandedView.setImageViewUri(R.id.notif_profile, uri);
        expandedView.setTextViewText(R.id.notif_name, fullname);
        expandedView.setTextViewText(R.id.notif_bday, bd);
        expandedView.setTextViewText(R.id.notif_age, age);
        expandedView.setTextViewText(R.id.notif_sex, sx);
        expandedView.setTextViewText(R.id.notif_height, he);
        expandedView.setTextViewText(R.id.notif_weight, we);
        expandedView.setTextViewText(R.id.notif_state, st);
        expandedView.setTextViewText(R.id.notif_medcon, mc);
        expandedView.setTextViewText(R.id.notif_ar, ar);
        expandedView.setTextViewText(R.id.notif_mednote, mn);

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

    public void askPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, 100);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, 400);
        } else {

            return;
        }
    }

    @Override
    public void onBackPressed() {
        this.onPause();
    }
}