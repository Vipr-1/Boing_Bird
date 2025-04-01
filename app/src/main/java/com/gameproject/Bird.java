package com.gameproject;

import android.widget.ImageView;

public class Bird {
    public int birdHeight;
    public int birdWidth;
    public int birdX;
    public int birdY;
    public int velocityY;
    public boolean isDead;
    public final int minY = -500; //top of the screen
    private ImageView birdImage;

    public Bird(ImageView birdImage) {
        this.birdX = 100;
        this.birdY = 300;
        this.velocityY = 0;
        this.isDead = false;
        this.birdImage = birdImage;
    }

    // Update bird position
    public void update() {
        velocityY += 2;  // Gravity effect
        birdY += velocityY;
        if (birdY < minY){
            birdY = minY;
        }
        birdImage.setY(birdY);
    }

    public void setSkin(ImageView birdImage){
        this.birdImage = birdImage;
    }

    // bird jump
    public void jump() {
        velocityY = -25;  // Apply jump force
    }
}
