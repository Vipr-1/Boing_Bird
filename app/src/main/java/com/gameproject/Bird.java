package com.gameproject;

import android.widget.ImageView;

public class Bird {

    public final int minY = 0;

    public int birdX;
    public int birdY;
    public int velocityY;
    public boolean isDead;

    private final ImageView birdImage;

    public Bird(ImageView birdImage) {
        this.birdImage = birdImage;
        this.birdX     = 100;
        this.birdY     = 130;
        this.velocityY = 0;
        this.isDead    = false;
    }
    public void update() {
        velocityY += 2;
        birdY += velocityY;
        if (birdY < minY) birdY = minY;
        birdImage.setY(birdY);
    }
    public void setSkin(int skinResID) {
        birdImage.setImageResource(skinResID);
    }
    public void jump() {
        velocityY = -20;
    }
}
