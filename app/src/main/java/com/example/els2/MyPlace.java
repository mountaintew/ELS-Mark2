package com.example.els2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

public class MyPlace extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    SupportMapFragment mapFragment;
    private FusedLocationProviderClient fusedLocationClient;
    Context context;
    String city;
    String TAG = "My Place";
    TextView citytv, temptv, fltv, cloudtv, windtv, desctv, pressuretv, humiditytv, celsius;
    ImageView weathericon;
    private final String url = "https://api.openweathermap.org/data/2.5/weather";
    private final String appId = "48496257879e286be2cd02aecd2cefd4";
    DecimalFormat df = new DecimalFormat("#.#");
    CardView card_icon;
    ScrollView content;
    RelativeLayout err_content;
    Animation fade_in, fade_out;
    boolean isDarkTheme;
    SharedPreferences user;

    String displaymode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String dm = getIntent().getStringExtra("displaymode");
        user = getSharedPreferences("user", MODE_PRIVATE);
        displaymode = user.getString("displaymode", "light");


        getWindow().setStatusBarColor(displaymode.equals("dark") ? Color.parseColor("#212121"): Color.TRANSPARENT);
        setContentView(displaymode.equals("dark") ? R.layout.dm_activity_my_place : R.layout.activity_my_place);

        isDarkTheme = displaymode.equals("dark");

        context = getApplicationContext();
        mapFragment  = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        citytv = findViewById(R.id.citytv);
        temptv = findViewById(R.id.temptv);
        fltv = findViewById(R.id.fltv);
        cloudtv = findViewById(R.id.cloudtv);
        windtv =  findViewById(R.id.windtv);
        desctv = findViewById(R.id.desctv);
        pressuretv = findViewById(R.id.pressuretv);
        humiditytv = findViewById(R.id.humiditytv);
        celsius = findViewById(R.id.celsius);

        weathericon = findViewById(R.id.weathericon);
        card_icon = findViewById(R.id.card_icon);

        content = findViewById(R.id.content);
        err_content = findViewById(R.id.error);

        fade_in = AnimationUtils.loadAnimation(context, R.anim.fade_in);
        fade_out = AnimationUtils.loadAnimation(context, R.anim.fade_out);


        LocationRequest mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(60000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationCallback mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        //TODO: UI updates.
                    }
                }
            }
        };
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.getFusedLocationProviderClient(context).requestLocationUpdates(mLocationRequest, mLocationCallback, null);

    }


    private void changeBgColor(String descmain, String ic) {
        switch (descmain){
            case "Clouds":
            case "Clear":
                if (ic.contains("n")){
                    card_icon.setCardBackgroundColor(ContextCompat.getColor(this, R.color.dark));
                }else {
                    card_icon.setCardBackgroundColor(ContextCompat.getColor(this, R.color.day));
                }
                break;
            case "Rain":
                if (ic.contains("n")){
                    card_icon.setCardBackgroundColor(ContextCompat.getColor(this, R.color.dark));
                }else {
                    card_icon.setCardBackgroundColor(ContextCompat.getColor(this, R.color.raind));
                }
                break;
            case "Drizzle":
                card_icon.setCardBackgroundColor(ContextCompat.getColor(this, R.color.drizzle));
                break;
            case "Thunderstorm":
                card_icon.setCardBackgroundColor(ContextCompat.getColor(this, R.color.storm));
                break;
            default:
                card_icon.setCardBackgroundColor(ContextCompat.getColor(this, R.color.metatext));

        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Add a marker in Sydney and move the camera

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(MyPlace.this);
        if (ActivityCompat.checkSelfPermission(MyPlace.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MyPlace.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            askPermission();
        }



        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(MyPlace.this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {

                        if (location != null) {
                            LatLng currentlocation = new LatLng(location.getLatitude(), location.getLongitude());
                            mMap.addMarker(new MarkerOptions().position(currentlocation).title("Your Location"));
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(currentlocation));
                            Geocoder geocoder = new Geocoder(MyPlace.this, Locale.getDefault());
                            try {
                                List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                                city = addresses.get(0).getLocality();
                                getWeatherDetails(temptv.getRootView(), city);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Intent i=new Intent(MyPlace.this, Dashboard.class);
                            startActivity(i);
                            finish();
                            overridePendingTransition(R.anim.show_up,R.anim.show_down);
                        }
                    }
                });

        if (isDarkTheme){
            try {
                // Customise the styling of the base map using a JSON object defined
                // in a raw resource file.
                boolean success = googleMap.setMapStyle(
                        MapStyleOptions.loadRawResourceStyle(
                                this, R.raw.dm_style));

                if (!success) {
                    Log.e(TAG, "Style parsing failed.");
                }
            } catch (Resources.NotFoundException e) {
                Log.e(TAG, "Can't find style. Error: ", e);
            }
        }else{
            try {
                // Customise the styling of the base map using a JSON object defined
                // in a raw resource file.
                boolean success = googleMap.setMapStyle(
                        MapStyleOptions.loadRawResourceStyle(
                                this, R.raw.style));

                if (!success) {
                    Log.e(TAG, "Style parsing failed.");
                }
            } catch (Resources.NotFoundException e) {
                Log.e(TAG, "Can't find style. Error: ", e);
            }
            mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID); // set the maps by sattelite
        }
        mMap.moveCamera(CameraUpdateFactory.zoomTo(15.0f)); //set the zoom on map by 13
    }
    private void getWeatherDetails(View view, String city) {
        String tempUrl  = "";
        String country = "Philippines";
        tempUrl = url + "?q=" + city + "," + country + "&appid=" + appId;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, tempUrl, new Response.Listener<String>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(String response) {
                if (err_content.getVisibility() == View.VISIBLE){
                    err_content.setVisibility(View.GONE);
                }
                content.startAnimation(fade_in);
                content.setVisibility(View.VISIBLE);
                String output = "";
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    JSONArray jsonArray = jsonResponse.getJSONArray("weather");
                    JSONObject jsonObjectWeather = jsonArray.getJSONObject(0);
                    String description = jsonObjectWeather.getString("description");
                    String descmain = jsonObjectWeather.getString("main");
                    JSONObject jsonObjectMain  = jsonResponse.getJSONObject("main");
                    double temp = jsonObjectMain.getDouble("temp") - 273.15;
                    double feelslike = jsonObjectMain.getDouble("feels_like") - 273.15;
                    float pressure =jsonObjectMain.getInt("pressure");
                    int humidity  = jsonObjectMain.getInt("humidity");
                    String icon = jsonObjectWeather.getString("icon");

                    changeBgColor(descmain, icon);

                    String iconUrl = "https://openweathermap.org/img/wn/" + icon + "@4x.png";
                    Picasso.with(context).load(iconUrl).into(weathericon);

                    JSONObject jsonObjectWind = jsonResponse.getJSONObject("wind");
                    String wind = jsonObjectWind.getString("speed");

                    JSONObject jsonObjectClouds = jsonResponse.getJSONObject("clouds");
                    String clouds = jsonObjectClouds.getString("all");
                    description = description.substring(0, 1).toUpperCase() + description.substring(1);
                    desctv.setText(description);
                    temptv.setText(df.format(temp));
                    fltv.setText(df.format(feelslike) + "°C");
                    cloudtv.setText(clouds + "%");
                    windtv.setText(wind + " m/s");
                    pressuretv.setText(df.format(pressure) + " mb");
                    humiditytv.setText(humidity + "%");
                    celsius.setText("°C");
                    citytv.setText(city);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                err_content.startAnimation(fade_in);
                err_content.setVisibility(View.VISIBLE);
                Log.d("TaG", error.getMessage());
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
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
        Intent i=new Intent(MyPlace.this, Dashboard.class);
        startActivity(i);
        finish();
        overridePendingTransition(R.anim.show_up,R.anim.show_down);
    }
}