package com.gameproject;

import android.media.Image;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Skins extends AppCompatActivity{

    // Map that indicates that we will save a String corresponding to the bird and a pair of int and boolean.
    // The int corresponds to the id of the imageview of the bird in drawables
    // the boolean indicates if the bird is wearing a hat or not so that we can modify the margins of the game depending on that.
    private Map<String, Pair<Integer, Boolean>> birdSkinImages;
    public ImageView birdImage;
    public ImageButton buttonLeft;
    public ImageButton buttonRight;
    public String[] birdNames;

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


        // if you press the right button you increase the counter and divide it by the lenght to restart it o 0
        buttonRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CURRENT_BIRD_INDEX = (CURRENT_BIRD_INDEX + 1) % birdNames.length;
                updateBirdSkin();
            }
        });

        buttonLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CURRENT_BIRD_INDEX = (CURRENT_BIRD_INDEX - 1 + birdNames.length) % birdNames.length;
                updateBirdSkin();

            }
        });



        // use the back button to go back
        ImageButton backButton = findViewById(R.id.buttonBack);
        backButton.setOnClickListener(v -> finish());

    }

    private void updateBirdSkin(){
        String currentBird = birdNames[CURRENT_BIRD_INDEX];
        int currentBirdImage = birdSkinImages.get(currentBird).first;
        birdImage.setImageResource(currentBirdImage);
    }

    public boolean doesBirdHaveHat(String birdName){
        return birdSkinImages.getOrDefault(birdName, new Pair<>(R.drawable.bird_default, false)).second;
    }

   public int getSkinImage(String birdName){
            return birdSkinImages.getOrDefault(birdName, new Pair<>(R.drawable.bird_default, false)).first;
    }

}
