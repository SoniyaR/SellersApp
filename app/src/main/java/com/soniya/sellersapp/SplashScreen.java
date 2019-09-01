package com.soniya.sellersapp;

import android.content.Intent;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent splashIntent = new Intent(SplashScreen.this, MainActivity.class);
                startActivity(splashIntent);
                finish();
            }
        }, 2500);

        ImageView steering = findViewById(R.id.splashicon);
        steering.animate().scaleX(0.1f).scaleY(0.1f);
        steering.animate().scaleX(1.5f).scaleY(1.5f).setDuration(2000);
        steering.animate().rotation(45f).setDuration(1000);
    }
}
