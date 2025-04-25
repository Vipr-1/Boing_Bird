package com.gameproject;

import android.os.Bundle;
import android.widget.ImageButton;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class HowToPlay extends AppCompatActivity {

    private ConstraintLayout howToPlayLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.how_to_play);

        ImageButton backButton = findViewById(R.id.buttonBackHow);
        backButton.setOnClickListener(v -> finish());

        howToPlayLayout = findViewById(R.id.how_to_play);
        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        boolean isDark = prefs.getBoolean("dark_mode", false);
        if (isDark) {
            howToPlayLayout.setBackgroundResource(R.drawable.bg_how_dark);
        } else {
            howToPlayLayout.setBackgroundResource(R.drawable.bg_how_light);
        }

        boolean musicMuted = prefs.getBoolean("isMusicMuted", false);
        MusicManager.setMuted(musicMuted, this);
        MusicManager.play(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MusicManager.play(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MusicManager.pause();
    }
}
