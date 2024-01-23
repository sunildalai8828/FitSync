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

        getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, new AdminFragment1()).commit();

        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.admin_overview) {
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, new AdminFragment1()).commit();
            } else if (item.getItemId() == R.id.add) {
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, new AdminFragment2()).commit();
            } else if (item.getItemId() == R.id.admin_profile) {
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, new AdminFragment3()).commit();
            }
            return true; // Return true to indicate that the event was handled
        });
    }


}
