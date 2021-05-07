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
import android.view.WindowManager;
import android.widget.Toast;

import java.util.ArrayList;

public class Contacts extends AppCompatActivity implements MainAdapter.OnContactListener {

    RecyclerView contact_rv;
    ArrayList<ContactModel> arrayList = new ArrayList<ContactModel>();
    MainAdapter mainAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //Recycler View
        contact_rv = findViewById(R.id.contact_rv);

        //check permission
        checkPermission();
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
            checkPermission();
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
        Log.d("Clicked", "onContactClick: clicked" + pos);
    }
}