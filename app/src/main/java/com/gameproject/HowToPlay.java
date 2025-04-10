package com.gameproject;

import android.os.Bundle;
import android.widget.ImageButton;

import android.widget.FrameLayout;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class HowToPlay extends AppCompatActivity {

    ConstraintLayout howToPlayLayout;

    //Back Button that goes back to the main page
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.how_to_play);

        ImageButton backButton = findViewById(R.id.buttonBackHow);
        backButton.setOnClickListener(v -> finish());


        // Apply dark or light background
        howToPlayLayout = findViewById(R.id.how_to_play);

        SharedPreferences preferences = getSharedPreferences("settings", MODE_PRIVATE);
        boolean isDark = preferences.getBoolean("dark_mode", false);

        if (isDark) {
            howToPlayLayout.setBackgroundResource(R.drawable.bg_how_dark); // dark mode bg
        } else {
            howToPlayLayout.setBackgroundResource(R.drawable.bg_how_light); // light mode bg
        }

    }
}
