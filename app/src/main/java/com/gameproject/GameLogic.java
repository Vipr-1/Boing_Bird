package com.gameproject;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

public class GameLogic extends AppCompatActivity {

    private Bird bird;
    private Handler handler = new Handler();
    private final int FRAME_RATE = 30; // Refresh rate for game loop
    private int gravity = 1;  // Gravity effect

    //private Pipe bottomPipe;
    //private Pipe topPipe;
    private int score;
    private int coins;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        ImageView birdImage = findViewById(R.id.birdImage);
        bird = new Bird(birdImage);

        startGame();

        //Start game loop (runs every FRAME_RATE ms)
        handler.postDelayed(gameLoop, FRAME_RATE);

        //making the whole screen detect taps
        findViewById(android.R.id.content).setOnClickListener(v -> bird.jump());
    }

    public void startGame(){
        bird.birdX = 600;
        bird.birdY = 300;
        bird.velocityY = 0;
        bird.isDead = false;
    }

    //Game loop- updates bird and redraws screen
    private Runnable gameLoop = new Runnable() {
        @Override
        public void run() {
            update();  //update bird position and apply gravity
            handler.postDelayed(this, FRAME_RATE);  // Continue the loop
        }
    };

    public void update() {
        bird.velocityY += gravity;  //Gravity pulls down the gravity
        bird.birdY += bird.velocityY;  //Update bird position based on velocity

        //Update the birds position on the screen
        ImageView birdImage = findViewById(R.id.birdImage);
        birdImage.setY(bird.birdY);
        if (hitFloor()){
            bird.isDead = true;
        }
        /*
        if (checkForCollisions()){
            bird.isDead = true;
        }
        if (checkForPass()){
            score += 1;
        }
        */
        if(bird.isDead){
            displayGameOver();
        }
    }


    public void restart(){
        startGame();
    }

    public void displayGameOver() {
    }

    public boolean hitFloor() {
        return bird.birdY >= 1500;
    }
/*
    public boolean checkForCollisions(){
        if (topPipe.collidePipe(bird)){
            return true;
        } else if (bottomPipe.collidePipe(bird)){
            return true;
        }
        else {
            return false;
        }
    }

    public boolean checkForPass(){
        if (topPipe.passedPipe(bird) && bottomPipe.passedPipe(bird)) {
            return true;
        }
        else{
            return false;
        }
    }
*/
    public void checkScore() {
    }

    public void checkCoins() {
    }

    public int bestScore() {
        return score;
    }
}
