package com.gameproject;

import android.widget.ImageView;

public class Pipe {
    //vars
    String orientation;
    ImageView texture;
    int speed;
    int pipeHeight;
    int pipeWidth;
    int pipeX;
    int pipeY;
    boolean hasPlant;

    //methods
    void update(){

    }
    boolean collidePipe(int birdX, int birdY, int birdWidth, int birdHeight){
        int birdHitboxWidth = birdX + birdWidth;
        int birdHitboxHeight = birdY + birdHeight;
        int pipeHitboxWidth = pipeX + pipeWidth;
        int pipHitboxHeight = pipeY + pipeHeight;
        if (birdHitboxWidth == pipeHitboxWidth && birdHitboxHeight >= pipHitboxHeight && orientation == "top"){
            return true;
        } else if (birdHitboxWidth == pipeHitboxWidth && birdHitboxHeight <= pipHitboxHeight && orientation == "bottom") {
            return true;
        }
        else {
            return false;
        }

    }
}
