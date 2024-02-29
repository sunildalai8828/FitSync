package com.example.fitsync;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AdminActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);



        bottomNavigationView = findViewById(R.id.bottom_navigation);

        getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, new AdminOverviewFragment()).commit();

        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.admin_overview) {
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, new AdminOverviewFragment()).commit();
            } else if (item.getItemId() == R.id.add) {
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, new AdminAddFragment()).commit();
            } else if (item.getItemId() == R.id.admin_profile) {
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, new AdminProfileFragment()).commit();
            } else if (item.getItemId() == R.id.assign_trainer) {
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, new AdminAssignFragment()).commit();
            }
            return true; // Return true to indicate that the event was handled
        });
    }


}
