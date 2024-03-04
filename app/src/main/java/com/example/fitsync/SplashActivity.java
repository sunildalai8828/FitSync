package com.example.fitsync;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {
    // Timeout duration for splash screen in milliseconds
    private static final int SPLASH_SCREEN_TIMEOUT = 1500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash); // Set the layout for the splash screen

        // Delayed execution to show the splash screen for a certain duration
        new Handler().postDelayed(() -> {
            // Create an intent to start LoginActivity
            Intent loginIntent = new Intent(SplashActivity.this, LoginActivity.class);
            // Start LoginActivity
            startActivity(loginIntent);
            // Close the current activity (splash screen) to prevent going back to it
            finish();
        }, SPLASH_SCREEN_TIMEOUT); // Delay duration before executing the Runnable
    }
}
