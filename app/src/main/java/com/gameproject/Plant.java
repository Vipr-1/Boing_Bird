package com.gameproject;

import android.widget.ImageView;

public class Plant extends Pipe{
    //vars
    ImageView plantGif;
    String orientation;
    int plantHeight;
    int plantWidth;
    int plantX;
    int plantY;

    public Plant(){

    }
    //methods
    public boolean collidePlant(int birdX, int birdY, int birdWidth, int birdHeight){
        int birdHitboxWidth = birdX + birdWidth;
        int birdHitboxHeight = birdY + birdHeight;
        int plantHitboxWidth = plantX + plantWidth;
        int plantHitboxHeight = plantY + plantHeight;
        if (birdHitboxWidth == plantHitboxWidth && birdHitboxHeight >= plantHitboxHeight && orientation == "top"){
            return true;
        } else if (birdHitboxWidth == plantHitboxWidth && birdHitboxHeight <= plantHitboxHeight && orientation == "bottom") {
            return true;
        }
        else {
            return false;
        }

    }
    public void reset(){

    }
}
