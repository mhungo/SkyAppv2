package com.example.skyapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class ContactActivity extends AppCompatActivity {

    BottomNavigationView navView;
    RecyclerView myContactList;
    ImageView findPeopleBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

         navView = findViewById(R.id.nav_view);
         navView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

         findPeopleBtn = findViewById(R.id.find_people_btn);
         myContactList = findViewById(R.id.contact_list);
         myContactList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

         findPeopleBtn.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 Intent findpeopleIntent = new Intent(ContactActivity.this, FindPeopleActivity.class);
                 startActivity(findpeopleIntent);
             }
         });

    }

    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

            switch (menuItem.getItemId())
            {
                case R.id.navication_home:
                    Intent mainIntent = new Intent(ContactActivity.this, ContactActivity.class);
                    startActivity(mainIntent);
                    break;
                case R.id.navication_setting:
                    Intent settingIntent = new Intent(ContactActivity.this, SettingActivity.class);
                    startActivity(settingIntent);
                    break;
                case R.id.navication_notification:
                    Intent notificationIntent = new Intent(ContactActivity.this,NotificationActivity.class);
                    startActivity(notificationIntent);
                    break;
                case R.id.navication_logout:
                    FirebaseAuth.getInstance().signOut();
                    Intent logoutIntent = new Intent(ContactActivity.this, RegisterActivity.class);
                    startActivity(logoutIntent);
                    finish();
                    break;
            }

            return false;
        }
    };
}