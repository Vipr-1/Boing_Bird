package com.gameproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    private MediaPlayer mediaPlayer;
    private SharedPreferences preferences;
    private ConstraintLayout mainLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main),
                (v, insets) -> {
                    Insets sb = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                    v.setPadding(sb.left, sb.top, sb.right, sb.bottom);
                    return insets;
                }
        );

        // Nav buttons
        findViewById(R.id.imagePlay).setOnClickListener(v -> startActivity(new Intent(this, GameLogic.class)));
        findViewById(R.id.imageSkins).setOnClickListener(v -> startActivity(new Intent(this, Skins.class)));
        findViewById(R.id.buttonHardLevel).setOnClickListener(v -> startActivity(new Intent(this, HardLevel.class)));
        findViewById(R.id.image3).setOnClickListener(v -> startActivity(new Intent(this, HowToPlay.class)));
        findViewById(R.id.buttonSettings).setOnClickListener(v -> startActivity(new Intent(this, Settings.class)));

        // Preference
        preferences = getSharedPreferences("settings", MODE_PRIVATE);

        // Background (Dark Mode)
        mainLayout = findViewById(R.id.main);
        applyDarkMode();

        // Music (respect mute)
        mediaPlayer = MediaPlayer.create(this, R.raw.music_q);
        mediaPlayer.setLooping(true);
        if (!preferences.getBoolean("isMuted", false)) {
            mediaPlayer.start();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        applyDarkMode();
        boolean muted = preferences.getBoolean("isMuted", false);
        if (muted) {
            if (mediaPlayer.isPlaying()) mediaPlayer.pause();
        } else {
            if (!mediaPlayer.isPlaying()) mediaPlayer.start();
        }
    }

    private void applyDarkMode() {
        boolean isDark = preferences.getBoolean("dark_mode", false);
        mainLayout.setBackgroundResource(isDark
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
