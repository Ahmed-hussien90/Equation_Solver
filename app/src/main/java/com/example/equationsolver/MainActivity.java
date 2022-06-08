package com.example.equationsolver;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.ramotion.paperonboarding.PaperOnboardingEngine;
import com.ramotion.paperonboarding.PaperOnboardingFragment;
import com.ramotion.paperonboarding.PaperOnboardingPage;
import com.ramotion.paperonboarding.listeners.PaperOnboardingOnChangeListener;
import com.ramotion.paperonboarding.listeners.PaperOnboardingOnRightOutListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();

        Button start = findViewById(R.id.start1);

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getBaseContext(), Home.class);
                startActivity(intent);
                finish();

            }

        });


        PaperOnboardingEngine engine = new PaperOnboardingEngine(findViewById(R.id.onboardingRootView), getDataForOnboarding(), getApplicationContext());

        engine.setOnChangeListener(new PaperOnboardingOnChangeListener() {
            @Override
            public void onPageChanged(int oldElementIndex, int newElementIndex) {
                if(newElementIndex == 2){
                    start.setVisibility(View.VISIBLE);
                }else{
                    start.setVisibility(View.GONE);
                }
            }
        });

        engine.setOnRightOutListener(new PaperOnboardingOnRightOutListener() {
            @Override
            public void onRightOut() {
                // Probably here will be your exit action
                Intent intent = new Intent(getBaseContext(), Home.class);
                startActivity(intent);
                finish();
            }
        });


    }

    private ArrayList<PaperOnboardingPage> getDataForOnboarding() {
        // prepare data
        PaperOnboardingPage scr1 = new PaperOnboardingPage("Scan",
                "Use MathSolver app to Scan a tricky problem. You can also manually input problems using our smart calculator",
                Color.parseColor("#3D1D66"), R.mipmap.ic_scan, R.drawable.ic_baseline_add_a_photo_24);
        PaperOnboardingPage scr2 = new PaperOnboardingPage("Solve It",
                "get solutions for many problems",
                Color.parseColor("#3D1D66"), R.mipmap.ic_solve, R.drawable.ic_baseline_done_24);
        PaperOnboardingPage scr3 = new PaperOnboardingPage("Save & share",
                "Save the solution and share it as a text",
                Color.parseColor("#3D1D66"), R.mipmap.ic_share, R.drawable.ic_baseline_share_24);

        ArrayList<PaperOnboardingPage> elements = new ArrayList<>();
        elements.add(scr1);
        elements.add(scr2);
        elements.add(scr3);
        return elements;

    }

    protected void onResume() {
        super.onResume();
        SharedPreferences sharedpreferences = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
        if (!sharedpreferences.getBoolean("yes", false)) {
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putBoolean("yes", Boolean.TRUE);
            editor.apply();
        } else {
            Intent intent = new Intent(this,Home.class);
            startActivity(intent);
            finish();
        }

    }

}