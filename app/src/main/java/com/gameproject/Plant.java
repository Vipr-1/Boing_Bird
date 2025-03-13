package com.gameproject;

import android.widget.ImageView;

public class Plant {
    //vars
    ImageView plantGif;
    String orientation;
    int plantHeight;
    int plantWidth;
    int plantX;
    int plantY;

    //methods

    /**
     * Checks if the bird has collided with a plant
     * @param birdX -The X coordinate of the bird
     * @param birdY -The Y coordinate of the bird
     * @param birdWidth -The bird's width value, used for extending X for hitbox purposes
     * @param birdHeight -The bird's width value, used for extending Y for hitbox purposes
     * @return -If a collision has occurred, true will be returned, else it will return false
     */
    boolean collidePlant(int birdX, int birdY, int birdWidth, int birdHeight){

    }

    /**
     * updates plants attributes
     */
    void update(){

    }

    /**
     * resets plant to default state
     */
    void reset(){

    }
}
