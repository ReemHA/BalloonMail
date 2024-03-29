package com.balloonmail.app.balloonmailapp.activities;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.balloonmail.app.balloonmailapp.R;

public class SplashScreen extends AppCompatActivity {
    private final int SPLASH_SCREEN_TIMEOUT = 1500;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(), LoginTabbedActivity.class);
                startActivity(intent);
                SplashScreen.this.finish();
            }
        }, SPLASH_SCREEN_TIMEOUT);
    }
}
