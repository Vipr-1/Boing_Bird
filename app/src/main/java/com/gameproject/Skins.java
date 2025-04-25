package com.gameproject;

import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;

public class Skins extends AppCompatActivity {
    public static int CURRENT_BIRD_INDEX = 0;
    private final Map<String, Pair<Integer, Boolean>> birdSkinImages = new HashMap<>();
    private String[] birdNames;
    private int birdChosen;
    private boolean isMuted;
    private ImageView birdImage;
    private ImageButton buttonLeft, buttonRight;
    private ImageButton buttonEquipped, buttonEquip;
    private ImageView bgImage;
    private MediaPlayer buttonSFX;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.skins);

        initBirdSkins();
        linkViews();
        initPreferencesAndBackground();

        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        boolean musicMuted = prefs.getBoolean("isMusicMuted", false);
        MusicManager.setMuted(musicMuted, this);
        MusicManager.play(this);

        initAudio();
        initArrowButtons();
        equipSkin();
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

    private void initBirdSkins() {
        birdSkinImages.put("birdDefaultYellow", new Pair<>(R.drawable.bird_default, false));
        birdSkinImages.put("birdBlue", new Pair<>(R.drawable.bird_blue, false));
        birdSkinImages.put("birdPurple", new Pair<>(R.drawable.bird_purple, false));
        birdSkinImages.put("birdRedCrown", new Pair<>(R.drawable.bird_red_crown, true));
        birdSkinImages.put("birdChristmas", new Pair<>(R.drawable.bird_chritmass, true));
        birdSkinImages.put("birdPinkCowboy", new Pair<>(R.drawable.bird_pink_cowboy, true));
        birdSkinImages.put("birdYellowSummer", new Pair<>(R.drawable.bird_yellow_summerhat, true));
        birdSkinImages.put("birdMario", new Pair<>(R.drawable.bird_mario, true));

        birdNames = birdSkinImages.keySet().toArray(new String[0]);
    }

    private void linkViews() {
        birdImage      = findViewById(R.id.birdImageView);
        buttonLeft     = findViewById(R.id.arrowLeft);
        buttonRight    = findViewById(R.id.arrowRight);
        buttonEquipped = findViewById(R.id.buttonEquipped);
        buttonEquip    = findViewById(R.id.buttonEquip);
        bgImage        = findViewById(R.id.bgDay);

        ImageButton backButton = findViewById(R.id.buttonBack);
        backButton.setOnClickListener(v -> finish());
    }

    private void initPreferencesAndBackground() {
        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        boolean isDark = prefs.getBoolean("dark_mode", false);
        isMuted = prefs.getBoolean("isMusicMuted", false);

        if (isDark) {
            bgImage.setImageResource(R.drawable.bg_skins_dark);
        } else {
            bgImage.setImageResource(R.drawable.bg_empty);
        }
    }

    private void initAudio() {
        buttonSFX = MediaPlayer.create(getApplicationContext(), R.raw.click_sfx);
    }

    private void initArrowButtons() {
        buttonRight.setOnClickListener(v -> {
            CURRENT_BIRD_INDEX = (CURRENT_BIRD_INDEX + 1) % birdNames.length;
            playSFX(buttonSFX);
            updateBirdSkin();
        });

        buttonLeft.setOnClickListener(v -> {
            CURRENT_BIRD_INDEX = (CURRENT_BIRD_INDEX - 1 + birdNames.length) % birdNames.length;
            playSFX(buttonSFX);
            updateBirdSkin();
        });
    }

    private void equipSkin() {
        buttonEquipped.setVisibility(View.INVISIBLE);
        buttonEquipped.setEnabled(false);

        buttonEquip.setOnClickListener(v -> {
            String currentBird = birdNames[CURRENT_BIRD_INDEX];
            birdChosen = getSkinImage(currentBird);

            SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
            prefs.edit().putInt("chosen_bird", birdChosen).apply();

            buttonEquip.setVisibility(View.INVISIBLE);
            buttonEquip.setEnabled(false);
            playSFX(buttonSFX);

            buttonEquipped.setVisibility(View.VISIBLE);
            buttonEquipped.setEnabled(true);
        });
    }

    private void updateBirdSkin() {
        String currentBird = birdNames[CURRENT_BIRD_INDEX];
        int currentImgRes  = birdSkinImages.get(currentBird).first;
        birdImage.setImageResource(currentImgRes);

        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        int equippedRes = prefs.getInt("chosen_bird", R.drawable.bird_default);

        boolean isEquipped = (currentImgRes == equippedRes);

        if (isEquipped) {
            buttonEquip.setVisibility(View.INVISIBLE);
            buttonEquip.setEnabled(false);
            buttonEquipped.setVisibility(View.VISIBLE);
            buttonEquipped.setEnabled(true);
        } else {
            buttonEquip.setVisibility(View.VISIBLE);
            buttonEquip.setEnabled(true);
            buttonEquipped.setVisibility(View.INVISIBLE);
            buttonEquipped.setEnabled(false);
        }

    }
    private void playSFX(MediaPlayer sfx) {
        if (sfx.isPlaying()) {
            sfx.seekTo(0);
            sfx.start();
        } else if (!isMuted) {
            sfx.start();
        }
    }

    public int getSkinImage(String birdName) {
        return birdSkinImages.getOrDefault(birdName,
                new Pair<>(R.drawable.bird_default, false)).first;
    }
}
