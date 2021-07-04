package com.example.els2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Transformation;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class Survey extends AppCompatActivity {
    CardView question1, question2, question_sub, question3a, question3b, question4, question5, thanks;
    Animation fi, fo, su, sd;
    Context context;
    ProgressBar progress;
    Button ac14, ac15, ac25, ac55, ac65, q2yes, q2no, q3ayes, q3ano, q3b_contbtn, q4yes, q4no, q5yes, q5no, qsub_btn;
    String mobilenumber, ageCategory, vaccinated, dosestaken, frontliner, hasRelative, vacc_avail;
    CheckBox q3b_cb1, q3b_cb2, q3b_cb3, q3b_cb4, q3b_cb5, q3b_cb6, q3b_cb7, q3b_cb8, q3b_cb9, q3b_cb10;
    ArrayList<String> symp = new ArrayList<String>();
    DatabaseReference userRef;
    LottieAnimationView comp_anim;
    Button btn_add, btn_min, gtd;
    TextView doses;
    public class ProgressBarAnimation extends Animation{
        private ProgressBar progressBar;
        private float from;
        private float  to;

        public ProgressBarAnimation(ProgressBar progressBar, float from, float to) {
            super();
            this.progressBar = progressBar;
            this.from = from;
            this.to = to;
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            super.applyTransformation(interpolatedTime, t);
            float value = from + (to - from) * interpolatedTime;
            progressBar.setProgress((int) value);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_survey);
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
        comp_anim = findViewById(R.id.comp_anim);
        // Firebase Ref
        userRef = FirebaseDatabase.getInstance().getReference("Users");

        // Intent Extra
        mobilenumber = getIntent().getStringExtra("mobilenumber");

        //TextView
        doses = findViewById(R.id.doses);

        //Buttons
        btn_add = findViewById(R.id.btn_add);
        btn_min = findViewById(R.id.btn_min);
        gtd = findViewById(R.id.gtd);
        //ProgressBar
        progress = findViewById(R.id.progress);

        //Animations
        fi = AnimationUtils.loadAnimation(context, R.anim.fade_in);
        fo = AnimationUtils.loadAnimation(context, R.anim.fade_out);
        su = AnimationUtils.loadAnimation(context, R.anim.show_up);
        sd = AnimationUtils.loadAnimation(context, R.anim.show_down);

        //CardViews
        question1 = findViewById(R.id.question1);
        question2 = findViewById(R.id.question2);
        question3a = findViewById(R.id.question3a);
        question3b = findViewById(R.id.question3b);
        question4 = findViewById(R.id.question4);
        question5 = findViewById(R.id.question5);
        question_sub = findViewById(R.id.question_sub);
        thanks = findViewById(R.id.thanks);

        //Buttons
        ac14 = findViewById(R.id.ac14);
        ac15 = findViewById(R.id.ac15);
        ac25 = findViewById(R.id.ac25);
        ac55 = findViewById(R.id.ac55);
        ac65 = findViewById(R.id.ac65);


        qsub_btn = findViewById(R.id.qsub_btn);

        q2yes = findViewById(R.id.q2yes);
        q2no = findViewById(R.id.q2no);

        q3ayes = findViewById(R.id.q3yes);
        q3ano = findViewById(R.id.q3no);

        q3b_contbtn = findViewById(R.id.q3b_contbtn);

        q4yes = findViewById(R.id.q4yes);
        q4no = findViewById(R.id.q4no);

        q5yes = findViewById(R.id.q5yes);
        q5no = findViewById(R.id.q5no);

        //CheckBox
        //q3b_cb1, q3b_cb2, q3b_cb3, q3b_cb4, q3b_cb5, q3b_cb6, q3b_cb7, q3b_cb8, q3b_cb9, q3b_cb10
        q3b_cb1 = findViewById(R.id.q3b_cb1);
        q3b_cb2 = findViewById(R.id.q3b_cb2);
        q3b_cb3 = findViewById(R.id.q3b_cb3);
        q3b_cb4 = findViewById(R.id.q3b_cb4);
        q3b_cb5 = findViewById(R.id.q3b_cb5);
        q3b_cb6 = findViewById(R.id.q3b_cb6);
        q3b_cb7 = findViewById(R.id.q3b_cb7);
        q3b_cb8 = findViewById(R.id.q3b_cb8);
        q3b_cb9 = findViewById(R.id.q3b_cb9);
        q3b_cb10 = findViewById(R.id.q3b_cb10);

        question1.startAnimation(fi);
        question1.setVisibility(View.VISIBLE);

        ProgressBarAnimation add10 = new ProgressBarAnimation(progress, progress.getProgress(), progress.getProgress() + 10);

        ProgressBarAnimation min10 = new ProgressBarAnimation(progress, progress.getProgress(), progress.getProgress() - 10);
        ProgressBarAnimation min20 = new ProgressBarAnimation(progress, progress.getProgress(), progress.getProgress() - 20);

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doses.setText(String.valueOf(Integer.parseInt(doses.getText().toString()) + 1));
                if (Integer.parseInt(doses.getText().toString()) == 1){
                    qsub_btn.setEnabled(true);
                    qsub_btn.startAnimation(fi);
                    qsub_btn.setBackgroundResource(R.drawable.ripple_cbtn_enabled);
                }
            }
        });
        btn_min.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!doses.getText().toString().equals("0")){
                    doses.setText(String.valueOf(Integer.parseInt(doses.getText().toString()) - 1));
                }
                if (doses.getText().toString().equals("0")){
                    qsub_btn.setEnabled(false);
                    qsub_btn.startAnimation(fi);
                    qsub_btn.setBackgroundResource(R.drawable.ripple_cbtn_disabled);
                }
            }
        });
        qsub_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dosestaken = doses.getText().toString();
                question_sub.setVisibility(View.GONE);
                question3a.startAnimation(fi);
                question3a.setVisibility(View.VISIBLE);
                progress.startAnimation( createProgressAnimation(20));
            }
        });

        ac14.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ageCategory = "14 and below";
                question1.setVisibility(View.GONE);
                question2.startAnimation(fi);
                question2.setVisibility(View.VISIBLE);
                progress.startAnimation( createProgressAnimation(20));
            }
        });
        ac15.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ageCategory = "15 to 24 years old";
                question1.setVisibility(View.GONE);
                question2.startAnimation(fi);
                question2.setVisibility(View.VISIBLE);
                progress.startAnimation( createProgressAnimation(20));
            }
        });
        ac25.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ageCategory = "25 to 54 years old";
                question1.setVisibility(View.GONE);
                question2.startAnimation(fi);
                question2.setVisibility(View.VISIBLE);
                progress.startAnimation( createProgressAnimation(20));
            }
        });
        ac55.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ageCategory = "55 to 64 years old";
                question1.setVisibility(View.GONE);
                question2.startAnimation(fi);
                question2.setVisibility(View.VISIBLE);
                progress.startAnimation( createProgressAnimation(20));
            }
        });
        ac65.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ageCategory = "65 and above";
                question1.setVisibility(View.GONE);
                question2.startAnimation(fi);
                question2.setVisibility(View.VISIBLE);
                progress.startAnimation( createProgressAnimation(20));
            }
        });

        q2yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vaccinated = "yes";
                question2.setVisibility(View.GONE);
                question_sub.startAnimation(fi);
                question_sub.setVisibility(View.VISIBLE);
                progress.startAnimation( createProgressAnimation(20));
            }
        });
        q2no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vaccinated = "no";
                question2.setVisibility(View.GONE);
                question3b.startAnimation(fi);
                question3b.setVisibility(View.VISIBLE);
                progress.startAnimation( createProgressAnimation(20));
            }
        });
        q3b_cb1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (q3b_cb1.isChecked()){
                    symp.add(q3b_cb1.getText().toString());
                }else {
                    symp.remove(q3b_cb1.getText().toString());
                }
                checkAll();
            }
        });
        q3b_cb2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (q3b_cb2.isChecked()){
                    symp.add(q3b_cb2.getText().toString());
                }else {
                    symp.remove(q3b_cb2.getText().toString());
                }
                checkAll();
            }
        });
        q3b_cb3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (q3b_cb3.isChecked()){
                    symp.add(q3b_cb3.getText().toString());
                }else {
                    symp.remove(q3b_cb3.getText().toString());
                }
                checkAll();
            }
        });
        q3b_cb4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (q3b_cb4.isChecked()){
                    symp.add(q3b_cb4.getText().toString());
                }else {
                    symp.remove(q3b_cb4.getText().toString());
                }
                checkAll();
            }
        });
        q3b_cb5.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (q3b_cb5.isChecked()){
                    symp.add(q3b_cb5.getText().toString());
                }else {
                    symp.remove(q3b_cb5.getText().toString());
                }
                checkAll();
            }
        });
        q3b_cb6.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (q3b_cb6.isChecked()){
                    symp.add(q3b_cb6.getText().toString());
                }else {
                    symp.remove(q3b_cb6.getText().toString());
                }
                checkAll();
            }
        });
        q3b_cb7.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (q3b_cb7.isChecked()){
                    symp.add(q3b_cb7.getText().toString());
                }else {
                    symp.remove(q3b_cb7.getText().toString());
                }
                checkAll();
            }
        });
        q3b_cb8.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (q3b_cb8.isChecked()){
                    symp.add(q3b_cb8.getText().toString());
                }else {
                    symp.remove(q3b_cb8.getText().toString());
                }
                checkAll();
            }
        });
        q3b_cb9.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (q3b_cb9.isChecked()){
                    symp.add(q3b_cb9.getText().toString());
                }else {
                    symp.remove(q3b_cb9.getText().toString());
                }
                checkAll();
            }
        });
        q3b_cb10.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(q3b_cb10.isChecked()){
                    q3b_contbtn.setEnabled(true);
                    q3b_contbtn.startAnimation(fi);
                    q3b_contbtn.setBackgroundResource(R.drawable.ripple_cbtn_enabled);
                    q3b_contbtn.setTextColor(getColor(R.color.light));
                }else{
                    q3b_contbtn.setEnabled(false);
                    q3b_contbtn.startAnimation(fi);
                    q3b_contbtn.setBackgroundResource(R.drawable.ripple_cbtn_disabled);
                    q3b_contbtn.setTextColor(getColor(R.color.dark));
                }
            }
        });
        q3b_contbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                question3b.setVisibility(View.GONE);
                question3a.startAnimation(fi);
                question3a.setVisibility(View.VISIBLE);
                progress.startAnimation( createProgressAnimation(vaccinated.equals("yes") ?  20 : 15));
            }
        });

        q3ayes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                frontliner = "yes";
                question3a.setVisibility(View.GONE);
                question4.startAnimation(fi);
                question4.setVisibility(View.VISIBLE);
                progress.startAnimation( createProgressAnimation(20));
            }
        });
        q3ano.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                frontliner = "no";
                question3a.setVisibility(View.GONE);
                question4.startAnimation(fi);
                question4.setVisibility(View.VISIBLE);
                progress.startAnimation( createProgressAnimation(10));
            }
        });
        q4yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hasRelative = "yes";
                if (vaccinated.equals("yes")){
                    progress.startAnimation( createProgressAnimation(20));
                    surveydone("v");
                }else{
                    question4.setVisibility(View.GONE);
                    question5.startAnimation(fi);
                    question5.setVisibility(View.VISIBLE);
                    progress.startAnimation( createProgressAnimation(15));
                }

            }
        });
        q4no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hasRelative = "no";
                if (vaccinated.equals("yes")){
                    progress.startAnimation( createProgressAnimation(20));
                    surveydone("v");
                }else{
                    question4.setVisibility(View.GONE);
                    question5.startAnimation(fi);
                    question5.setVisibility(View.VISIBLE);
                    progress.startAnimation( createProgressAnimation(15));
                }
            }
        });

        q5yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vacc_avail = "yes";
                progress.startAnimation(createProgressAnimation(100 - progress.getProgress()));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        surveydone("nv");
                    }
                }, 800);
            }
        });

        q5no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vacc_avail = "no";
                progress.startAnimation(createProgressAnimation(100 - progress.getProgress()));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        surveydone("nv");
                    }
                }, 800);
            }
        });
        gtd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Survey.this, Dashboard.class);
                startActivity(i);
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

    }

    private void surveydone(String type) {
        if (type.equals("v")){
            VUserHelper vUserHelper = new VUserHelper(ageCategory, vaccinated, doses.getText().toString(), frontliner, hasRelative);
            userRef.child(mobilenumber + "/info_covid_related").setValue(vUserHelper, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                    Task<Void> q = userRef.child(mobilenumber + "/info").child("surveytaken").setValue(true);
                    q.addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            question4.startAnimation(fo);
                            question4.setVisibility(View.GONE);
                            progress.startAnimation(fo);
                            progress.setVisibility(View.GONE);
                            thanks.startAnimation(fi);
                            thanks.setVisibility(View.VISIBLE);
                            comp_anim.playAnimation();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //error
                            Toast.makeText(context, "Please connect to the internet.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }
        if (type.equals("nv")){
            NVUserHelper nvUserHelper = new NVUserHelper(ageCategory, vaccinated, symp.toString(), frontliner, hasRelative, vacc_avail);
            userRef.child(mobilenumber + "/info_covid_related").setValue(nvUserHelper, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                    Task<Void> q = userRef.child(mobilenumber + "/info").child("surveytaken").setValue(true);
                    q.addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            question5.startAnimation(fo);
                            question5.setVisibility(View.GONE);
                            progress.startAnimation(fo);
                            progress.setVisibility(View.GONE);
                            thanks.startAnimation(fi);
                            thanks.setVisibility(View.VISIBLE);
                            comp_anim.playAnimation();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //error
                            Toast.makeText(context, "Please connect to the internet.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
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

    private ProgressBarAnimation createProgressAnimation(int i) {
        ProgressBarAnimation add20 = new ProgressBarAnimation(progress, progress.getProgress(), progress.getProgress() + i);
        add20.setDuration(800);
        return add20;
    }
    private void checkAll() {
        if (symp.size() == 1){
            q3b_contbtn.setEnabled(true);
            q3b_contbtn.startAnimation(fi);
            q3b_contbtn.setBackgroundResource(R.drawable.ripple_cbtn_enabled);
            q3b_contbtn.setTextColor(getColor(R.color.light));
            Log.d("size", String.valueOf(symp.size()));
        }
        if (symp.size() == 0){
            q3b_contbtn.setEnabled(false);
            q3b_contbtn.startAnimation(fi);
            q3b_contbtn.setBackgroundResource(R.drawable.ripple_cbtn_disabled);
            q3b_contbtn.setTextColor(getColor(R.color.dark));
            Log.d("size", String.valueOf(symp.size()));
        }


    }
}