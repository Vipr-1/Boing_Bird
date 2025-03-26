package com.gameproject;
import android.os.Bundle;

public class GameLogic extends MainActivity {

    int score;
    int coins;
    private static final int gravity = 1;
    private static final int JUMP_FORCE = -10;
    private Bird bird;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        bird = new Bird();
        startGame();
    }

    public void startGame(){
        bird.birdX = 100;
        bird.birdY = 300;
        bird.velocityY = 0;
        bird.isDead = false;
    }

    public void hardLevel(){

    }

    public void update(){
        bird.update();
        bird.velocityY += gravity;
        bird.birdY += bird.velocityY;


    }

    public void movePipe(){

    }

    public void movePlant(){

    }

    public void restart(){
        startGame();
    }

    public void quit(){

    }

    public boolean checkForCollisions(){
        return false;
    }

    public boolean hitFloor(){
        return bird.birdY <= 0;
    }

    public void displayGameOver(){

    }

    public int bestScore(){
        return 0;

    }

    public void checkScore(){

    }

    public void checkCoins(){

    }
}

