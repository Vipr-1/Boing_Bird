package com.gameproject;

import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class Settings extends AppCompatActivity {
    private ToggleButton muteToggle;
    private Switch darkModeSwitch;
    private ConstraintLayout layout;
    private SharedPreferences prefs;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        prefs = getSharedPreferences("settings", MODE_PRIVATE);
        layout = findViewById(R.id.settingsLayout);

        // Back button
        findViewById(R.id.buttonBack).setOnClickListener(v -> finish());

        // Dark mode
        darkModeSwitch = findViewById(R.id.switchDark);
        boolean isDark = prefs.getBoolean("dark_mode", false);
        darkModeSwitch.setChecked(isDark);
        applyDark(isDark);
        darkModeSwitch.setOnCheckedChangeListener((btn, on) -> {
            prefs.edit().putBoolean("dark_mode", on).apply();
            applyDark(on);
        });

        // Music
        mediaPlayer = MediaPlayer.create(this, R.raw.music_q);
        mediaPlayer.setLooping(true);
        muteToggle = findViewById(R.id.toggleMute);
        boolean isMuted = prefs.getBoolean("isMuted", false);
        muteToggle.setChecked(isMuted);
        if (!isMuted) mediaPlayer.start();

        muteToggle.setOnCheckedChangeListener((btn, muted) -> {
            prefs.edit().putBoolean("isMuted", muted).apply();
            if (muted) mediaPlayer.pause();
            else mediaPlayer.start();
        });
    }

    private void applyDark(boolean dark) {
        layout.setBackgroundResource(dark
                ? R.drawable.bg_main_dark
                : R.drawable.bg_main_light);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
