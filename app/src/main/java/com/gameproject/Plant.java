package com.gameproject;

import pl.droidsonroids.gif.GifImageView;
public class Plant {
    GifImageView plantGif;
    int plantX;
    int plantY;
    int speed;

    public Plant(GifImageView gif, int speed) {
        this.plantGif = gif;
        this.speed = speed;
    }

    public void move(int screenWidth) {
        plantX -= speed;
        if (plantX + plantGif.getWidth() < 0) {
            plantX = screenWidth;
        }
        plantGif.setX(plantX);
        plantGif.setY(plantY);
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public void setPlantX(int x) {
        this.plantX = x;
    }

    public void setPlantY(int y) {
        this.plantY = y;
    }
}
