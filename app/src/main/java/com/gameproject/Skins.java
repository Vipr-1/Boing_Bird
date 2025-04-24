package com.gameproject;

import android.content.SharedPreferences;
import android.media.Image;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import java.net.BindException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.content.SharedPreferences;


public class Skins extends AppCompatActivity{

    // Map that indicates that we will save a String corresponding to the bird and a pair of int and boolean.
    // The int corresponds to the id of the imageview of the bird in drawables
    // the boolean indicates if the bird is wearing a hat or not so that we can modify the margins of the game depending on that.
    private Map<String, Pair<Integer, Boolean>> birdSkinImages;
    private Bird gameBird;
    public ImageView birdImage;
    public ImageButton buttonLeft;
    public ImageButton buttonRight;
    public ImageButton buttonEquipped;
    public ImageButton buttonEquip;
    public String[] birdNames;
    private MediaPlayer buttonSFX; //click sound
    public int birdChosen;

    private boolean isMuted;

    private ImageView bgImage; //for the background change


    public static int CURRENT_BIRD_INDEX = 0;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.skins); // Starting the layout for the skin class

        // initialize a HashMap with the birds
        birdSkinImages = new HashMap<>();
        // bird without a hat gets false
        birdSkinImages.put("birdDefaultYellow", new Pair<>(R.drawable.bird_default, false));
        birdSkinImages.put("birdBlue", new Pair<>(R.drawable.bird_blue,false));
        birdSkinImages.put("birdPurple", new Pair<>(R.drawable.bird_purple, false));

        //birds with a hat returns true
        birdSkinImages.put("birdRedCrown", new Pair<>(R.drawable.bird_red_crown, true));
        birdSkinImages.put("birdChristmas", new Pair<>(R.drawable.bird_chritmass, true));
        birdSkinImages.put("birdPinkCowboy", new Pair<>(R.drawable.bird_pink_cowboy, true));
        birdSkinImages.put("birdYellowSummer", new Pair<>(R.drawable.bird_yellow_summerhat, true));
        birdSkinImages.put("birdMario", new Pair<>(R.drawable.bird_mario, true));

        // initialzing the array of the birds using the keys of the birdSkinImages
        birdNames = birdSkinImages.keySet().toArray(new String[0]);

        // setting the buttons
        birdImage = findViewById(R.id.birdImageView);
        buttonLeft = findViewById(R.id.arrowLeft);
        buttonRight = findViewById(R.id.arrowRight);

        //init sound effect
        buttonSFX = MediaPlayer.create(getApplicationContext(), R.raw.click_sfx);


        // if you press the right button you increase the counter and divide it by the lenght to restart it o 0
        buttonRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CURRENT_BIRD_INDEX = (CURRENT_BIRD_INDEX + 1) % birdNames.length;
                playSFX(buttonSFX);
                updateBirdSkin();
            }
        });

        buttonLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CURRENT_BIRD_INDEX = (CURRENT_BIRD_INDEX - 1 + birdNames.length) % birdNames.length;
                playSFX(buttonSFX);
                updateBirdSkin();

            }
        });

        // use the back button to go back
        ImageButton backButton = findViewById(R.id.buttonBack);
        backButton.setOnClickListener(v -> finish());

        equipSkin();

        //implementation
        bgImage = findViewById(R.id.bgDay);

        // Load dark mode state
        SharedPreferences preferences = getSharedPreferences("settings", MODE_PRIVATE);
        boolean isDark = preferences.getBoolean("dark_mode", false);
        isMuted = preferences.getBoolean("isMuted", false);

        // Change background image
        if (isDark) {
            bgImage.setImageResource(R.drawable.bg_skins_dark); // your dark background image
        } else {
            bgImage.setImageResource(R.drawable.bg_empty); // your light background image
        }


    }
    private void playSFX(MediaPlayer SFX){
        //check for if the sound file is running and override it
        //to keep playback smooth
        if (SFX.isPlaying()){
            SFX.seekTo(0);
            SFX.start();
        }
        //won't play if muted
        else if (!isMuted) {
            SFX.start();
        }
    }

    private void equipSkin(){
        // the button equipped is invisible and disable in the screen
        buttonEquipped = findViewById(R.id.buttonEquipped);
        buttonEquipped.setVisibility(View.INVISIBLE);
        buttonEquipped.setEnabled(false);

        buttonEquip = findViewById(R.id.buttonEquip);
        buttonEquip.setOnClickListener(v -> {

            String currentBird = birdNames[CURRENT_BIRD_INDEX];
            birdChosen = getSkinImage(currentBird);

            // Save the selected bird's skin to SharedPreferences when user chooses the bird
            SharedPreferences preferences = getSharedPreferences("settings", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt("chosen_bird", birdChosen);
            editor.apply();

            // when button equip is clicked button equip is invisible and equipped is visible
            buttonEquip.setVisibility(View.INVISIBLE);
            buttonEquip.setEnabled(false);
            playSFX(buttonSFX);

            buttonEquipped.setVisibility(View.VISIBLE);
            buttonEquipped.setEnabled(true);
                });
    }

    private void updateBirdSkin(){
        String currentBird = birdNames[CURRENT_BIRD_INDEX];
        int currentBirdImage = birdSkinImages.get(currentBird).first;
        birdImage.setImageResource(currentBirdImage);

        // Check from SharedPreferences if the current bird is equipped
        SharedPreferences preferences = getSharedPreferences("settings", MODE_PRIVATE);
        int equippedBirdImage = preferences.getInt("chosen_bird", R.drawable.bird_default);

        if (currentBirdImage == equippedBirdImage) {
            buttonEquip.setVisibility(View.INVISIBLE);
            buttonEquip.setEnabled(false);

            buttonEquipped.setVisibility(View.VISIBLE);
            buttonEquipped.setEnabled(true);
        } else {
            buttonEquipped.setVisibility(View.INVISIBLE);
            buttonEquipped.setEnabled(false);

            buttonEquip.setVisibility(View.VISIBLE);
            buttonEquip.setEnabled(true);
        }


    }

    public boolean doesBirdHaveHat(String birdName){
        return birdSkinImages.getOrDefault(birdName, new Pair<>(R.drawable.bird_default, false)).second;
    }

    public int getBirdChosen(){
        return birdChosen;
    }

   public int getSkinImage(String birdName){
            return birdSkinImages.getOrDefault(birdName, new Pair<>(R.drawable.bird_default, false)).first;
    }


}
