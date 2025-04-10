package com.gameproject;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.widget.Button;
import android.view.View;
import android.content.Intent;
import android.widget.ImageButton;

import android.media.MediaPlayer;
import android.widget.ToggleButton;

import android.widget.Switch;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.content.SharedPreferences;




public class MainActivity extends AppCompatActivity {

    //Music player and toggle
    MediaPlayer mediaPlayer;
    ToggleButton muteToggle;

    Switch darkModeSwitch;
    ConstraintLayout mainLayout;
    SharedPreferences preferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageButton playClassicButton = findViewById(R.id.imagePlay);
        playClassicButton.setOnClickListener(v -> mainGame());

        ImageButton skinsButton = findViewById(R.id.imageSkins);
        skinsButton.setOnClickListener(v -> skinScreen());

        // clicking the How To Play Button
        ImageButton howToPlayButton = findViewById(R.id.image3);
        howToPlayButton.setOnClickListener(v -> openHowToPlay());

        //music toggle button setup
        mediaPlayer = MediaPlayer.create(this, R.raw.music_q);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();

        muteToggle = findViewById(R.id.Mute);
        muteToggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                mediaPlayer.pause();
            } else {
                mediaPlayer.start();
            }
        });

        mainLayout = findViewById(R.id.main);
        darkModeSwitch = findViewById(R.id.switch1);
        preferences = getSharedPreferences("settings", MODE_PRIVATE);

        // Restore saved mode
        boolean isDark = preferences.getBoolean("dark_mode", false);
        mainLayout.setBackgroundResource(isDark ? R.drawable.bg_night : R.drawable.bg_main_home);
        darkModeSwitch.setChecked(isDark);

        // Toggle background on switch
        darkModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("dark_mode", isChecked);
            editor.apply();

            if (isChecked) {
                mainLayout.setBackgroundResource(R.drawable.bg_main_home_night); // dark bg
                //NOTE: create a night version of bg_main_home image
            } else {
                mainLayout.setBackgroundResource(R.drawable.bg_main_home); // light bg
            }
        });


    }
    /**
    Initialize the game
     */
    protected void mainGame(){
        Intent intent = new Intent(MainActivity.this, GameLogic.class);
        startActivity(intent);
    }

    protected void skinScreen(){
        Intent intent = new Intent(MainActivity.this, Skins.class);
        startActivity(intent);
    }

    // Opens the How to Play screen
    protected void openHowToPlay() {
        Intent intent = new Intent(MainActivity.this, HowToPlay.class);
        startActivity(intent);
    }

    //Stop the music properly when leaving the activity
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

}
