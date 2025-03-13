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
    public boolean collidePlant(int plantX, int plantY, int plantWidth, int plantHeight){
        return true;

    }
    public void reset(){

    }
}
