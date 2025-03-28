package com.gameproject;

import android.widget.ImageView;

public class Bird {
    int birdHeight;
    int birdWidth;
    int birdX;
    int birdY;
    public int velocityY;
    public boolean isDead;
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
        velocityY += 1;  // Gravity effect
        birdY += velocityY;

        birdImage.setY(birdY);
    }

    public void setSkin(ImageView birdImage){
        this.birdImage = birdImage;
    }

    // bird jump
    public void jump() {
        velocityY = -20;  // Apply jump force
    }
}
