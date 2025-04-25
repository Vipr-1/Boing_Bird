package com.gameproject;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class Settings extends AppCompatActivity {
    private ConstraintLayout layout;
    private SharedPreferences prefs;
    private ImageButton sunButton, moonButton;
    private TextView modeLabel;
    private boolean isDarkMode;

    private ImageButton musicOnButton, musicOffButton;
    private boolean isMusicMuted;

    private ImageButton btnGameOn, btnGameOff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        prefs = getSharedPreferences("settings", MODE_PRIVATE);
        layout = findViewById(R.id.settingsLayout);

        findViewById(R.id.buttonBack).setOnClickListener(v -> finish());

        sunButton   = findViewById(R.id.sunButton);
        moonButton  = findViewById(R.id.moonButton);
        modeLabel   = findViewById(R.id.modeText);
        isDarkMode  = prefs.getBoolean("dark_mode", false);
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

        musicOnButton  = findViewById(R.id.buttonBgMusic);
        musicOffButton = findViewById(R.id.buttonBgNoMusic);
        isMusicMuted   = prefs.getBoolean("isMusicMuted", false);

        MusicManager.setMuted(isMusicMuted, this);
        updateMusicButtons(isMusicMuted);

        musicOnButton.setOnClickListener(v -> {
            isMusicMuted = true;
            prefs.edit().putBoolean("isMusicMuted", true).apply();
            MusicManager.setMuted(true, this);
            updateMusicButtons(true);
        });
        musicOffButton.setOnClickListener(v -> {
            isMusicMuted = false;
            prefs.edit().putBoolean("isMusicMuted", false).apply();
            MusicManager.setMuted(false, this);
            updateMusicButtons(false);
        });

        btnGameOn  = findViewById(R.id.buttonGameSound);
        btnGameOff = findViewById(R.id.buttonGameNoSound);
        boolean sfxOn = prefs.getBoolean("game_sound", true);
        flipButtons(sfxOn);

        btnGameOn.setOnClickListener(v -> setGameSound(false));
        btnGameOff.setOnClickListener(v -> setGameSound(true));
    }

    private void setGameSound(boolean enabled) {
        prefs.edit().putBoolean("game_sound", enabled).apply();
        flipButtons(enabled);
    }

    private void flipButtons(boolean sfxOn) {
        if (sfxOn) {
            btnGameOn.setVisibility(View.VISIBLE);
            btnGameOff.setVisibility(View.INVISIBLE);
        } else {
            btnGameOn.setVisibility(View.INVISIBLE);
            btnGameOff.setVisibility(View.VISIBLE);
        }
    }

    private void updateMusicButtons(boolean isMuted) {
        if (isMuted) {
            musicOnButton.setVisibility(View.GONE);
            musicOffButton.setVisibility(View.VISIBLE);
        } else {
            musicOnButton.setVisibility(View.VISIBLE);
            musicOffButton.setVisibility(View.GONE);
        }
    }


    private void applyDark(boolean dark) {
        if (dark) {
            layout.setBackgroundResource(R.drawable.setting_dark);
            sunButton.setVisibility(View.GONE);
            moonButton.setVisibility(View.VISIBLE);
            modeLabel.setText("DARK MODE");
        } else {
            layout.setBackgroundResource(R.drawable.setting_light);
            sunButton.setVisibility(View.VISIBLE);
            moonButton.setVisibility(View.GONE);
            modeLabel.setText("LIGHT MODE");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MusicManager.setMuted(isMusicMuted, this);
    }
}
