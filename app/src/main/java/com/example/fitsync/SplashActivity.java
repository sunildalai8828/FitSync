package com.example.fitsync;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {
    private static final int SPLASH_SCREEN_TIMEOUT = 1500;
//    SharedPreferences sharedPreferences=getSharedPreferences("mypreferences",MODE_PRIVATE);
//    String username=sharedPreferences.getString("username",null);
//    String control=sharedPreferences.getString("control",null);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(() -> {
//            if (username!=null){
//                switch (control){
//                    case "admin":
//                        Intent AdloginIntent = new Intent(SplashActivity.this, AdminActivity.class);
//                        startActivity(AdloginIntent);
//                        break;
//                    case "trainer":
//                        Intent TrloginIntent = new Intent(SplashActivity.this, StaffActivity.class);
//                        startActivity(TrloginIntent);
//                        break;
//                    case "member":
//                        Intent MeloginIntent = new Intent(SplashActivity.this, MemberActivity.class);
//                        startActivity(MeloginIntent);
//                        break;
//                    default:
//                        break;
//                }
//            }else {
                Intent loginIntent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(loginIntent);
//            }
            finish();
        }, SPLASH_SCREEN_TIMEOUT);
    }
}