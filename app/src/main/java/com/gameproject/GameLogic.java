package com.gameproject;

import android.graphics.Rect;
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
    private int score;
    private int coins;
    private ImageView birdImage;
    private ImageView pipeNorthImage;
    private ImageView pipeSouthImage;
    private boolean gameStarted = false;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        pipeNorthImage = findViewById(R.id.imageView);
        pipeSouthImage = findViewById(R.id.imageView7);
        birdImage = findViewById(R.id.birdImage);
        bird = new Bird(birdImage);

        findViewById(android.R.id.content).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!gameStarted) {
                    gameStarted = true;
                    startGame();
                    handler.postDelayed(gameLoop, FRAME_RATE);
                } else if (!bird.isDead) {
                    bird.jump();
                }
            }
        });
    }

    public void startGame(){
        bird.birdX = 600;
        bird.birdY = 950;
        bird.velocityY = 0;
        bird.isDead = false;
    }

    //Game loop- updates bird and redraws screen
    private Runnable gameLoop = new Runnable() {
        @Override
        public void run() {
            if (!bird.isDead) {
                update();
                handler.postDelayed(this, FRAME_RATE);
            }
        }
    };

    public void update() {
        bird.velocityY += gravity;  //Gravity pulls down the gravity
        bird.birdY += bird.velocityY;  //Update bird position based on velocity

        birdImage.setY(bird.birdY);
        if (hitFloor() || checkForCollisions()) {
            bird.isDead = true;
            bird.velocityY = 0;
        }

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

    public boolean checkForCollisions() {

        Rect birdRect = new Rect();
        birdImage.getHitRect(birdRect);

        Rect pipeNorthRect = new Rect();
        pipeNorthImage.getHitRect(pipeNorthRect);

        Rect pipeSouthRect = new Rect();
        pipeSouthImage.getHitRect(pipeSouthRect);

        return Rect.intersects(birdRect, pipeNorthRect) || Rect.intersects(birdRect, pipeSouthRect);
    }
    public void checkScore() {
    }

    public void checkCoins() {
    }

    public int bestScore() {
        return score;
    }
}
