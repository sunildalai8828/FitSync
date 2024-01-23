package com.example.fitsync;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MemberActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    String username;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member);

        username = getIntent().getStringExtra("username");
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, new MemberFragment1()).commit();

        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.member_profile) {
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, new MemberFragment1()).commit();
            } else if (item.getItemId() == R.id.track_workouts) {
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, new MemberFragment2()).commit();
            }

            return true; // Return true to indicate that the event was handled
        });
    }
}