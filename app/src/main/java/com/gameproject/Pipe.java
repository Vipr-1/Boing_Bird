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
    boolean collidePipe(Bird player){
        int birdHitboxWidth = player.birdX + player.birdWidth;
        int birdHitboxHeight = player.birdY + player.birdHeight;
        int pipeHitboxWidth = pipeX + pipeWidth;
        int pipeHitboxHeight = pipeY + pipeHeight;
        if (birdHitboxWidth == pipeHitboxWidth && birdHitboxHeight <= pipeHitboxHeight && orientation == "top"){
            return true;
        } else if (birdHitboxWidth == pipeHitboxWidth && birdHitboxHeight >= pipeHitboxHeight && orientation == "bottom") {
            return true;
        }
        else {
            return false;
        }

    }
    boolean passedPipe(Bird player){
        int birdHitboxWidth = player.birdX + player.birdWidth;
        int birdHitboxHeight = player.birdY + player.birdHeight;
        int pipeHitboxWidth = pipeX + pipeWidth;
        int pipeHitboxHeight = pipeY + pipeHeight;
        if (player.birdX == pipeX && birdHitboxHeight >= pipeHitboxHeight && orientation == "top"){
            return true;
        } else if (player.birdX == pipeX && birdHitboxHeight <= pipeHitboxHeight && orientation == "bottom") {
            return true;
        }
        else {
            return false;
        }
    }
}
