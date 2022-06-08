package com.example.equationsolver;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;


public class SplashScreen extends AppCompatActivity {
    private ImageView icon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

            getSupportActionBar().hide();

            icon = findViewById(R.id.app_icon);
            TextView mainText = findViewById(R.id.mainText);

            Animation anim;
            anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);
            icon.startAnimation(anim);
            anim = AnimationUtils.loadAnimation(getApplicationContext(),  R.anim.side_slide);
            mainText.startAnimation(anim);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(getBaseContext(),MainActivity.class));
                    finish();
                    }
                },3000);

            }
        }