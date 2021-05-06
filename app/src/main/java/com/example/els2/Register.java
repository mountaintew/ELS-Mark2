package com.example.els2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
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
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;

public class Register extends AppCompatActivity implements View.OnClickListener  {
    AutoCompleteTextView medcon, ar;
    Context context;
    Button nextbtn, backbtn;

    LottieAnimationView check;
    TextView title, fname_lbl, lname_lbl, bday_lbl;
    EditText fname, lname, bday, weight, height, mednote;
    String age;
    Spinner sex, bloodtype, weight_unit, height_unit;
    LinearLayout reg1, reg2, buttons;
    ScrollView scrollView;
    Animation fif, fof, slide_up, slide_down;

    String fn = "", ln = "", bd = "", sx = "", bt = "", we ="", he = "", mc = "", al = "", mn = "", mobileNumber;
    private int mYear, mMonth, mDay;
    int registerstate = 0;
    boolean fn_chk, ln_chk, isConnected;
    DatabaseReference databaseReference;
    SharedPreferences.Editor editor;
    SharedPreferences user;
    DatePickerDialog datePickerDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        if (Build.VERSION.SDK_INT < 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
            getWindow().setStatusBarColor(Color.WHITE);
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        context = getApplicationContext();
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        editor = getSharedPreferences("user", MODE_PRIVATE).edit();
        user = getSharedPreferences("user", MODE_PRIVATE);
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        mobileNumber = user.getString("mobileNumber", "");
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        ArrayAdapter<CharSequence> medconadapter = ArrayAdapter.createFromResource(getBaseContext(),
                R.array.conditions, android.R.layout.simple_spinner_item);
        medcon = findViewById(R.id.medcon);
        medcon.setThreshold(1);
        medcon.setAdapter(medconadapter);

        ArrayAdapter<CharSequence> alleradapter = ArrayAdapter.createFromResource(getBaseContext(),
                R.array.aller, android.R.layout.simple_spinner_item);
        ar = findViewById(R.id.ar);
        ar.setThreshold(1);
        ar.setAdapter(alleradapter);

        fn_chk = false;
        ln_chk = false;

        //EditText
        fname = findViewById(R.id.fnameEt);
        lname = findViewById(R.id.lnameEt);
        bday = findViewById(R.id.bday);
        weight = findViewById(R.id.weightEt);
        height = findViewById(R.id.heightEt);
        mednote = findViewById(R.id.mednote);

        //Spinner
        sex = findViewById(R.id.sex);
        bloodtype = findViewById(R.id.bloodtype);
        weight_unit = findViewById(R.id.weight_unit);
        height_unit = findViewById(R.id.height_unit);

        //Labels
        title = findViewById(R.id.title);
        fname_lbl = findViewById(R.id.fname_lbl);
        lname_lbl = findViewById(R.id.lname_lbl);
        bday_lbl = findViewById(R.id.bday_lbl);
        age = "";

        //Buttons
        nextbtn = findViewById(R.id.nextbtn);
        backbtn = findViewById(R.id.backbtn);

        //Animations
        fif = AnimationUtils.loadAnimation(context, R.anim.fif_alt);
        fof = AnimationUtils.loadAnimation(context, R.anim.fof_alt);
        slide_up = AnimationUtils.loadAnimation(context, R.anim.show_up);
        slide_down = AnimationUtils.loadAnimation(context, R.anim.show_down);

        //Lottie Animation
        check = findViewById(R.id.check);

        //Layouts
        reg1 = findViewById(R.id.reg1);
        reg2 = findViewById(R.id.reg2);
        scrollView = findViewById(R.id.scrollView);
        buttons = findViewById(R.id.buttons);

        title.requestFocus();

        //Listeners
        bday.setOnClickListener(this);
        nextbtn.setOnClickListener(this);
        backbtn.setOnClickListener(this);
        bday.setText("");

        fname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                fname_lbl.setTextColor(Color.parseColor("#222222"));
                fname.setBackgroundResource(R.drawable.bordered);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        lname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                lname.setBackgroundResource(R.drawable.bordered);
                lname_lbl.setTextColor(Color.parseColor("#222222"));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }
    @Override
    public void onClick(View view) {
        if (view == bday) {
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);
            bday.setBackgroundResource(R.drawable.bordered);
            bday_lbl.setTextColor(Color.parseColor("#222222"));

            datePickerDialog = new DatePickerDialog(this,
                    new DatePickerDialog.OnDateSetListener() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {
                            bday.setText((((monthOfYear + 1) <= 9) ? "0" + (monthOfYear + 1) : (monthOfYear + 1))  + "/" +
                                    ((dayOfMonth <= 9) ? "0" + dayOfMonth : dayOfMonth) + "/" + year);
                        }
                    }, mYear, mMonth, mDay);
            datePickerDialog.show();
        }
        if (view == nextbtn){
            if (registerstate == 0){
                if (fname.getText().toString().equals("")){
                    fname.setBackgroundResource(R.drawable.error);
                    fname_lbl.setTextColor(Color.parseColor("#ef5350"));
                    Toast.makeText(context, "Firstname is required", Toast.LENGTH_SHORT).show();
                } else {
                    fn_chk = validatename(fname.getText().toString(), "firstname");
                }

                if (lname.getText().toString().equals("")){
                    lname.setBackgroundResource(R.drawable.error);
                    lname_lbl.setTextColor(Color.parseColor("#ef5350"));
                    Toast.makeText(context, "Lastname is required", Toast.LENGTH_SHORT).show();
                }else {
                    ln_chk = validatename(lname.getText().toString(), "lastname");
                }

                if (fn_chk && ln_chk){
                    fn_chk = false;
                    ln_chk = false;


                    if (bday.getText().toString().equals("")){
                        registerstate = 1;
                        reg1.startAnimation(slide_down);
                        reg1.setVisibility(View.GONE);
                        reg2.startAnimation(slide_up);
                        reg2.setVisibility(View.VISIBLE);
                        backbtn.startAnimation(fif);
                        backbtn.setVisibility(View.VISIBLE);
                        backbtn.setEnabled(true);
                        nextbtn.startAnimation(fif);
                        nextbtn.setText("Submit");
                    } else {
                        String[] strbday = bday.getText().toString().split("/");
                        final Calendar c = Calendar.getInstance();
                        mYear = c.get(Calendar.YEAR);
                        mMonth = c.get(Calendar.MONTH);
                        mDay = c.get(Calendar.DAY_OF_MONTH);

                        age = getAge(Integer.parseInt(strbday[2]), Integer.parseInt(strbday[0]), Integer.parseInt(strbday[1]));

                        if (Integer.parseInt(age) < 0) {
                            if (mDay < Integer.parseInt(strbday[1])){
                                bday.setBackgroundResource(R.drawable.error);
                                bday_lbl.setTextColor(Color.parseColor("#ef5350"));
                                Toast.makeText(context, "Invalid Birthdate", Toast.LENGTH_SHORT).show();
                            } else if (mMonth + 1 < Integer.parseInt(strbday[0])) {
                                bday.setBackgroundResource(R.drawable.error);
                                bday_lbl.setTextColor(Color.parseColor("#ef5350"));
                                Toast.makeText(context, "Invalid Birthdate", Toast.LENGTH_SHORT).show();
                            } else  {
                                registerstate = 1;
                                reg1.startAnimation(slide_down);
                                reg1.setVisibility(View.GONE);
                                reg2.startAnimation(slide_up);
                                reg2.setVisibility(View.VISIBLE);
                                backbtn.startAnimation(fif);
                                backbtn.setVisibility(View.VISIBLE);
                                backbtn.setEnabled(true);
                                nextbtn.startAnimation(fif);
                                nextbtn.setText("Submit");
                            }
                        } else {
                            registerstate = 1;
                            reg1.startAnimation(slide_down);
                            reg1.setVisibility(View.GONE);
                            reg2.startAnimation(slide_up);
                            reg2.setVisibility(View.VISIBLE);
                            backbtn.startAnimation(fif);
                            backbtn.setVisibility(View.VISIBLE);
                            backbtn.setEnabled(true);
                            nextbtn.startAnimation(fif);
                            nextbtn.setText("Submit");
                        }
                    }

                }
            } else if (registerstate == 1){
                //fn, ln, bd, sx, bt, we, he, mc, al, mn;

                String strfn = fname.getText().toString();
                fn = strfn.substring(0, 1).toUpperCase() + strfn.substring(1);

                String strln = lname.getText().toString();
                ln = strln.substring(0, 1).toUpperCase() + strln.substring(1);

                if (!bday.getText().toString().equals("")){
                    bd = bday.getText().toString();
                } else {
                    bd = "n/a";
                }
                sx = sex.getSelectedItem().toString();
                bt = bloodtype.getSelectedItem().toString();

                if (!weight.getText().toString().equals("")){
                    we = weight.getText().toString() + " " + weight_unit.getSelectedItem().toString();
                }else {
                    we = "n/a";
                }
                if (!height.getText().toString().equals("")){
                    he = height.getText().toString() + " " + height_unit.getSelectedItem().toString();
                } else {
                    he = "n/a";
                }

                if (!medcon.getText().toString().equals("")){
                    mc = medcon.getText().toString();
                } else {
                    mc = "n/a";
                }
                if (!ar.getText().toString().equals("")){
                    al = ar.getText().toString();
                } else {
                    al = "n/a";
                }
                if (!mednote.getText().toString().equals("")){
                    mn = mednote.getText().toString();
                } else {
                    mn = "n/a";
                }
                int ag = 0;
                if (!age.equals("")){
                    ag = Integer.parseInt(age);
                }
                //fn, ln, bd, sx, bt, we, he, mc, al, mn;
                // firstname,  lastname,  birthdate,  sex,  bloodtype,  weight,  height,  conditions,  allergies,  mednotes,  number,  state, boolean toggled
                UserHelper userHelper = new UserHelper(ag, fn, ln, bd, sx, bt, we, he, mc, al, mn, mobileNumber, "Safe", false);

                //SENDS THE DATA TO THE DATABASE IF CONNECTED TO THE INTERNET ELSE STORE THE DATA IN TO LOCAL STORAGE
                if (isConnected){
                    databaseReference.child(mobileNumber).setValue(userHelper, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                            scrollView.startAnimation(fof);
                            scrollView.setVisibility(View.GONE);
                            buttons.startAnimation(fof);
                            buttons.setVisibility(View.GONE);
                            check.startAnimation(fif);
                            check.setVisibility(View.VISIBLE);
                            check.setSpeed((float) 2.0);
                            check.playAnimation();;
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Intent i=new Intent(Register.this, Dashboard.class);
                                    startActivity(i);
                                    finish();
                                    overridePendingTransition(R.anim.fade_in_slow,R.anim.fade_out_slow);
                                }
                            }, 1900);
                        }
                    });
                }
                editor.putString("age", age);
                editor.putString("firstname", fn);
                editor.putString("lastname", ln);
                editor.putString("bday", bd);
                editor.putString("sex", sx);
                editor.putString("bloodtype", bt);
                editor.putString("weight", we);
                editor.putString("height", he);
                editor.putString("medcon", mc);
                editor.putString("allergy", al);
                editor.putString("notes", mn);
                editor.putBoolean("hasRegistered", true);
                editor.apply();

            }
        }
        if (view == backbtn){
            registerstate = 0;
            nextbtn.startAnimation(fif);
            nextbtn.setText("Next");
            backbtn.startAnimation(fof);
            backbtn.setVisibility(View.INVISIBLE);
            backbtn.setEnabled(false);
            reg1.startAnimation(slide_up);
            reg1.setVisibility(View.VISIBLE);
            reg2.startAnimation(slide_down);
            reg2.setVisibility(View.GONE);
        }
    }

    public boolean validatename(String name, String source){
        String nonumbers = "[a-zA-Z- ',-ñÑ]+";
        String invalid = source.equals("firstname") ? "Invalid Firstname" : "Invalid Lastname";

        if (name.equals("")){
            showerrors(source);
            return false;
        } else if (!name.matches(nonumbers)){
            showerrors(source);
            Toast.makeText(context, invalid, Toast.LENGTH_SHORT).show();
            return false;
        } else if (name.length() <= 1){
            showerrors(source);
            Toast.makeText(context, invalid, Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }
    public  void showerrors(String source){
        if (source.equals("firstname")){
            fname.setBackgroundResource(R.drawable.error);
            fname_lbl.setTextColor(Color.parseColor("#ef5350"));
        }
        if (source.equals("lastname")){
            lname.setBackgroundResource(R.drawable.error);
            lname_lbl.setTextColor(Color.parseColor("#ef5350"));
        }
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
    private String getAge(int year, int month, int day){
        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();
        dob.set(year, month, day);
        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)){
            age--;
        }
        Integer ageInt = new Integer(age);
        String ageS = ageInt.toString();
        return ageS;
    }
    @Override
    public void onBackPressed() {}
}