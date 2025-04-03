package com.gameproject;

import android.widget.ImageView;

public class Pipe {

    public ImageView texture;
    public int speed;
    public int pipeHeight;
    public int pipeWidth;
    public int pipeX;
    public int pipeY;
    public boolean hasPlant;

    public Pipe(ImageView texture, int speed) {
        this.texture = texture;
        this.speed = speed;
        this.pipeX = (int)texture.getX();
        this.pipeY = (int)texture.getY();
        this.pipeWidth = texture.getWidth();
        this.pipeHeight = texture.getHeight();
    }

    public void move(int screenWidth) {
        pipeX -= speed;
        if (pipeX < -pipeWidth) {
            pipeX = screenWidth;
        }
        texture.setX(pipeX);
    }
}
