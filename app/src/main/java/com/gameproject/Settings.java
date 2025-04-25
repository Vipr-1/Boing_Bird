package com.gameproject;

import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class Settings extends AppCompatActivity {
    private ConstraintLayout layout;
    private SharedPreferences prefs;
    private MediaPlayer mediaPlayer;
    private ImageButton sunButton;
    private ImageButton moonButton;
    private ImageButton musicOnButton;
    private ImageButton musicOffButton;
    private TextView modeLabel;
    private boolean isDarkMode;
    private boolean isMusicMuted;
    private ImageButton btnGameOn, btnGameOff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        prefs = getSharedPreferences("settings", MODE_PRIVATE);
        layout = findViewById(R.id.settingsLayout);

        findViewById(R.id.buttonBack).setOnClickListener(v -> finish());

        sunButton = findViewById(R.id.sunButton);
        moonButton = findViewById(R.id.moonButton);
        modeLabel = findViewById(R.id.modeText);

        isDarkMode = prefs.getBoolean("dark_mode", false);
        applyDark(isDarkMode);

        sunButton.setOnClickListener(v -> {
            isDarkMode = true;
            prefs.edit().putBoolean("dark_mode", true).apply();
            applyDark(true);
        });

        moonButton.setOnClickListener(v -> {
            isDarkMode = false;
            prefs.edit().putBoolean("dark_mode", false).apply();
            applyDark(false);
        });

        // Music setup
        mediaPlayer = MediaPlayer.create(this, R.raw.music_q);
        mediaPlayer.setLooping(true);

        isMusicMuted = prefs.getBoolean("isMusicMuted", false);
        if (!isMusicMuted) {
            mediaPlayer.start();
        }

        musicOnButton = findViewById(R.id.buttonBgMusic);
        musicOffButton = findViewById(R.id.buttonBgNoMusic);
        updateMusicButtons(isMusicMuted);

        musicOnButton.setOnClickListener(v -> {
            isMusicMuted = true;
            prefs.edit().putBoolean("isMusicMuted", true).apply();
            if (mediaPlayer.isPlaying()) mediaPlayer.pause();
            updateMusicButtons(true);
        });

        musicOffButton.setOnClickListener(v -> {
            isMusicMuted = false;
            prefs.edit().putBoolean("isMusicMuted", false).apply();
            if (!mediaPlayer.isPlaying()) mediaPlayer.start();
            updateMusicButtons(false);
        });



        btnGameOn  = findViewById(R.id.buttonGameSound);
        btnGameOff = findViewById(R.id.buttonGameNoSound);

        // Sync UI with stored value
        boolean sfxOn = prefs.getBoolean("game_sound", true);
        flipButtons(sfxOn);

        btnGameOn.setOnClickListener(v -> setGameSound(false));   // turn OFF
        btnGameOff.setOnClickListener(v -> setGameSound(true));   // turn ON
    }

    private void setGameSound(boolean enabled) {
        prefs.edit().putBoolean("game_sound", enabled).apply();
        flipButtons(enabled);
    }

    private void flipButtons(boolean sfxOn) {
        btnGameOn.setVisibility(sfxOn ? View.VISIBLE : View.INVISIBLE);
        btnGameOff.setVisibility(sfxOn ? View.INVISIBLE : View.VISIBLE);
    }

    private void updateMusicButtons(boolean isMuted) {
        musicOnButton.setVisibility(isMuted ? View.GONE : View.VISIBLE);
        musicOffButton.setVisibility(isMuted ? View.VISIBLE : View.GONE);
    }

    private void applyDark(boolean dark) {
        layout.setBackgroundResource(dark
                ? R.drawable.setting_dark
                : R.drawable.setting_light);

        sunButton.setVisibility(dark ? View.GONE : View.VISIBLE);
        moonButton.setVisibility(dark ? View.VISIBLE : View.GONE);
        modeLabel.setText(dark ? "DARK MODE" : "LIGHT MODE");
    }

    @Override
    protected void onPause() {
        super.onPause();
        prefs.edit()
                .putBoolean("dark_mode", isDarkMode)
                .putBoolean("isMusicMuted", isMusicMuted)
                .apply();

        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mediaPlayer != null && !isMusicMuted && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        prefs.edit()
                .putBoolean("dark_mode", isDarkMode)
                .putBoolean("isMusicMuted", isMusicMuted)
                .apply();

        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
