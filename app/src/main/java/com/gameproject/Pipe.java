package com.gameproject;
import android.widget.ImageView;
public class Pipe {
    public ImageView texture;
    public int speed;
    public int pipeHeight;
    public int pipeWidth;
    public int pipeX;
    public int pipeY;

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
    public void setSpeed(int newSpeed) {
        this.speed = newSpeed;
    }
    public void setImageView(ImageView newImageView) {
        this.texture = newImageView;
        newImageView.setX(pipeX);
        newImageView.setY(pipeY);
    }
}
