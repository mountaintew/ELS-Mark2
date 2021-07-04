package com.example.els2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class NotificationHandler extends AppCompatActivity {
    TextView notification, helpertext;

    String mobilenumber, message, from, type, c_fullname;
    DatabaseReference ref;

    Button confirm_btn, deny_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_handler);
        //Database Reference
        ref = FirebaseDatabase.getInstance().getReference("Users");
        //Buttons
        confirm_btn = findViewById(R.id.confirm_btn);
        deny_btn = findViewById(R.id.deny_btn);


        notification = findViewById(R.id.notification);
        helpertext = findViewById(R.id.helpertext);


        mobilenumber = getIntent().getStringExtra("mobilenumber");
        message = getIntent().getStringExtra("notification");
        from = getIntent().getStringExtra("from");
        type = getIntent().getStringExtra("type");

        if (type.equals("accept")){
            confirm_btn.setText("Confirm");
            deny_btn.setText("I'm not related to this person");
            helpertext.setText("By tapping confirm, you will receive a notification when this person has an emergency.");

            ref.child(from + "/info/fullname").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        c_fullname = snapshot.getValue(String.class);
                        notification.setText(c_fullname + " set your number as their emergency contact.");
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            confirm_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ref.child(mobilenumber + "/services/notifications").setValue(null, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                        }
                    });
                    ref.child(mobilenumber + "/services/accepted_contact").setValue(true, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                            Intent i=new Intent(NotificationHandler.this, Dashboard.class);
                            startActivity(i);
                            finish();
                            overridePendingTransition(R.anim.show_up,R.anim.show_down);
                        }
                    });
                }
            });
            deny_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ref.child(mobilenumber + "/services/notifications").setValue(null, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {

                        }
                    });
                    ref.child(mobilenumber + "/services/accepted_contact").setValue(false, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                            Intent i=new Intent(NotificationHandler.this, Dashboard.class);
                            startActivity(i);
                            finish();
                            overridePendingTransition(R.anim.show_up,R.anim.show_down);
                        }
                    });
                }
            });
        }



    }
}