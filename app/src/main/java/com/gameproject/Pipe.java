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

    public ViewGroup.LayoutParams params; //Used by the set height method to change xml values

    public Pipe(ImageView texture, int speed) {
        this.texture = texture;
        this.speed = speed;
        this.pipeX = (int)texture.getX();
        this.pipeY = (int)texture.getY();
        this.pipeWidth = texture.getWidth();
        this.pipeHeight = texture.getHeight();
        this.params = texture.getLayoutParams();
    }

    public void move(int screenWidth) {
        pipeX -= speed;
        if (pipeX < -pipeWidth) {
            pipeX = screenWidth;
        }
        texture.setX(pipeX);
    }


    /**
     * changes The xml of the pipe to change its height
     * @param height the new xml height value
     */
    public void setPipeY(int height){
        params.height = height;
        texture.setLayoutParams(params);
    }
}
