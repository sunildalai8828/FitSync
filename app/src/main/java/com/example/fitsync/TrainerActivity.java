package com.example.fitsync;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class TrainerActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    String username;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trainer);

        username = getIntent().getStringExtra("username");
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, new TrainerFragment1()).commit();

        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.staff_overview) {
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, new TrainerFragment1()).commit();
            } else if (item.getItemId() == R.id.staff_profile) {
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_container,new TrainerFragment2()).commit();
            }
            return true; // Return true to indicate that the event was handled
        });
    }
}