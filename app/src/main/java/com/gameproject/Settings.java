package com.gameproject;

import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import org.json.JSONObject;

import java.io.FileInputStream;

public class Settings extends AppCompatActivity {
    private ToggleButton muteToggle;
    private Switch darkModeSwitch;
    private ConstraintLayout layout;
    private SharedPreferences prefs;
    private MediaPlayer mediaPlayer;

    private static final String best_score_file = "best_score.json";
    private static final String best_score_key = "bestScore";
    private static final String odometer_file = "odometer.json";
    private static final String odometer_key = "odometer";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        prefs = getSharedPreferences("settings", MODE_PRIVATE);
        layout = findViewById(R.id.settingsLayout);

        // Back button
        findViewById(R.id.buttonBack).setOnClickListener(v -> finish());

        TextView bestScoreField = findViewById(R.id.bestScoreField);
        String bestScoreString = "Best Score: " + loadBestScore();
        bestScoreField.setText(bestScoreString);

        TextView odometerField = findViewById(R.id.odometerField);
        String odometerString = "Total Distance: " + loadOdometer();
        odometerField.setText(odometerString);

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

    private int loadOdometer(){
        try (FileInputStream fis = openFileInput(odometer_file)) {
            byte[] data = new byte[fis.available()];
            fis.read(data);
            JSONObject json = new JSONObject(new String(data));
            return json.optInt(odometer_key, 0);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    private int loadBestScore() {
        try (FileInputStream fis = openFileInput(best_score_file)) {
            byte[] data = new byte[fis.available()];
            fis.read(data);
            JSONObject json = new JSONObject(new String(data));
            return json.optInt(best_score_key, 0);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
