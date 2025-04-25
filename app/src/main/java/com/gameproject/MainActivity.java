package com.gameproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

// Global music controller
import com.gameproject.MusicManager;

public class MainActivity extends AppCompatActivity {
    private SharedPreferences preferences;
    private ConstraintLayout mainLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);


        findViewById(R.id.imagePlay)
                .setOnClickListener(v -> startActivity(new Intent(this, GameLogic.class)));
        findViewById(R.id.imageSkins)
                .setOnClickListener(v -> startActivity(new Intent(this, Skins.class)));
        findViewById(R.id.buttonHardLevel)
                .setOnClickListener(v -> startActivity(new Intent(this, HardLevel.class)));
        findViewById(R.id.image3)
                .setOnClickListener(v -> startActivity(new Intent(this, HowToPlay.class)));
        findViewById(R.id.buttonSettings)
                .setOnClickListener(v -> startActivity(new Intent(this, Settings.class)));

        preferences = getSharedPreferences("settings", MODE_PRIVATE);
        mainLayout = findViewById(R.id.main);
        applyDarkMode();

        boolean musicMuted = preferences.getBoolean("isMusicMuted", false);
        MusicManager.setMuted(musicMuted, this);
        MusicManager.play(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean musicMuted = preferences.getBoolean("isMusicMuted", false);
        MusicManager.setMuted(musicMuted, this);
        MusicManager.play(this);

        applyDarkMode();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MusicManager.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MusicManager.pause();
    }

    private void applyDarkMode() {
        boolean isDark = preferences.getBoolean("dark_mode", false);
        if (isDark) {
            mainLayout.setBackgroundResource(R.drawable.final_bg_dark);
        } else {
            mainLayout.setBackgroundResource(R.drawable.final_bg_light);
        }
    }
}
