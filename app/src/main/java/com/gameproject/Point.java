package com.gameproject;

public class Point {
    public int x;
    public int y;
    public String image;

    public Point(int x, int y, String image){
        this.x = x;
        this.y = y;
        this.image = image;

    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }
}