package com.example.els2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Contacts extends AppCompatActivity implements MainAdapter.OnContactListener {

    RecyclerView contact_rv;
    ArrayList<ContactModel> arrayList = new ArrayList<ContactModel>();
    MainAdapter mainAdapter;
    LinearLayout contactlayout;
    Button submitcontact;
    EditText conNameEt, conNumEt;
    String mobilenumber;
    DatabaseReference reference;
    String contactlist;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        contactlayout = findViewById(R.id.contactlayout);
        contactlayout.requestFocus();

        //Recycler View
        contact_rv = findViewById(R.id.contact_rv);

        //EditText
        conNameEt = findViewById(R.id.conNameEt);
        conNumEt = findViewById(R.id.conNumEt);

        //Button
        submitcontact = findViewById(R.id.submitcontact);

        //Reference
        reference = FirebaseDatabase.getInstance().getReference("Users");
        
        mobilenumber = getIntent().getStringExtra("mobilenumber");

        //check permission
        checkPermission();

        Query q = reference.orderByChild("number").equalTo(mobilenumber);
        q.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    contactlist = snapshot.child(mobilenumber).child("contacts").getValue(String.class);
                    if (contactlist == null){
                        contactlist = "";
                    }

                    Toast.makeText(Contacts.this, "exist" + contactlist, Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(Contacts.this, "!exist", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        submitcontact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = validatename(conNameEt.getText().toString());
                String num = validatemobile(conNumEt.getText().toString());

                if (!name.equals("") && !num.equals("")){
                    Task<Void> q = reference.child(mobilenumber).child("contacts").setValue(contactlist + name + '%' + "+63" + num + ",");
                    q.addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(Contacts.this, "Contact successfully added", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    private String validatemobile(String num) {
        String numbers = "[0-9]+";
        if (!num.matches(numbers)){
            Toast.makeText(this, "Invalid mobile number", Toast.LENGTH_SHORT).show();
            return "";
        }else {
            return num;
        }
        
    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(Contacts.this,
                Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(Contacts.this,
                    new String[]{Manifest.permission.READ_CONTACTS}, 300);
        } else {
            getContactList();
        }
    }

    private void getContactList() {
        //Initialize uri
        Uri uri = ContactsContract.Contacts.CONTENT_URI;
        //Sort by asc
        String sort = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME+" ASC";
        //Init cursor
        Cursor cursor = getContentResolver().query(uri,null, null, null, sort);
        //check condition
        if (cursor.getCount() > 0){
            while (cursor.moveToNext()){
                //get contact id
                String id = cursor.getString(cursor.getColumnIndex(
                        ContactsContract.Contacts._ID
                ));
                String name = cursor.getString(cursor.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME
                ));

                //init phone uri
                Uri phoneUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
                //init selection
                String selection = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " =?";
                //init phone cursor
                Cursor phoneCursor = getContentResolver().query(
                        phoneUri,null,selection,new String[]{id},null
                );

                if (phoneCursor.moveToNext()){
                    String number = phoneCursor.getString(phoneCursor.getColumnIndex(
                            ContactsContract.CommonDataKinds.Phone.NUMBER
                    ));

                    ContactModel model = new ContactModel();

                    model.setName(name);
                    model.setNumber(number);
                    arrayList.add(model);

                    phoneCursor.close();
                }
            }
            cursor.close();
        }
        contact_rv.setLayoutManager(new LinearLayoutManager(this));

        mainAdapter = new MainAdapter(this,arrayList, this);
        contact_rv.setAdapter(mainAdapter);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 300 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            getContactList();
        } else {
            Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onBackPressed() {
        Intent i=new Intent(Contacts.this, Dashboard.class);
        startActivity(i);
        finish();
        overridePendingTransition(R.anim.show_up,R.anim.show_down);
    }

    @Override
    public void onContactClick(int pos) {
        conNameEt.setText(arrayList.get(pos).name);
        conNumEt.setText(validatenum(arrayList.get(pos).number.replace(" ", "")));
    }

    private String validatename(String name){
        String alpha = "[a-zA-ZñÑ ]+";
        if (!name.matches(alpha)){
            conNameEt.setText("");
            conNameEt.requestFocus();
            Toast.makeText(this, "Name must contain alpha characters only.", Toast.LENGTH_SHORT).show();
            return "";
        } else {
            name = name.substring(0, 1).toUpperCase() + name.substring(1);
            return name;
        }
    }

    private String validatenum(String num) {
        if (num.length() > 13){
            return "";
        }
        String x = num.charAt(0) + "" + num.charAt(1) + "" + num.charAt(2);
        if (x.equals("+63")){
            return num.substring(3);
        } else if (num.charAt(0) == '0'){
            return num.substring(1);
        } else {
            return "";
        }
    }
}