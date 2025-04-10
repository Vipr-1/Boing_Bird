package com.gameproject;

import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

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
        this.pipeX = (int) texture.getX();
        this.pipeY = (int) texture.getY();
        this.pipeWidth = texture.getWidth();
        this.pipeHeight = texture.getHeight();
    }

    public void move(int screenWidth) {
        pipeX -= speed;
        if (pipeX <= 0) {
            pipeX = screenWidth;
        }
        texture.setX(pipeX);
    }

    /**
     * Changes the xml of the pipe to change its height.
     * @param y the new xml height value
     */
    public void setPipeY(int y) {
        this.pipeY = y;
        texture.setY(y);
    }

    public int getPipeX(){
        return pipeX;
    }

    public int getPipeY(){
        return pipeY;
    }

    public int getPipeWidth(){
        return pipeWidth;
    }

    public void setPipeX(int x){
        this.pipeX = x;
    }

    public void setSpeed(int newSpeed) {
        this.speed = newSpeed;
    }

    // Updated method: update the texture reference and dimensions.
    public void setImageView(ImageView newImageView) {
        this.texture = newImageView;
        newImageView.setX(pipeX);
        newImageView.setY(pipeY);
    }
}
