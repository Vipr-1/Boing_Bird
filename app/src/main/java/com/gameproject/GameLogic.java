package com.gameproject;

import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
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

    // New fields for pipes and screen width
    private Pipe pipeNorthObj;
    private Pipe pipeSouthObj;
    private int screenWidth;
    private final int pipe_speed = 10;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        pipeNorthImage = findViewById(R.id.pipeNorth);
        pipeSouthImage = findViewById(R.id.pipeSouth);
        birdImage = findViewById(R.id.birdImage);
        bird = new Bird(birdImage);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth = displayMetrics.widthPixels;

        pipeNorthObj = new Pipe(pipeNorthImage, pipe_speed);
        pipeSouthObj = new Pipe(pipeSouthImage, pipe_speed);

        findViewById(android.R.id.content).setOnClickListener(new View.OnClickListener() {
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
        bird.birdX = 539;
        bird.birdY = 1169;
        bird.velocityY = 0;
        bird.isDead = false;
    }

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
        bird.velocityY += gravity;
        bird.birdY += bird.velocityY;
        birdImage.setY(bird.birdY);
        if (hitFloor() || checkForCollisions()) {
            bird.isDead = true;
            bird.velocityY = 0;
        }
        if(bird.isDead){
            restart();
        }
        //test changing pipe height
        pipeNorthObj.setPipeY(200);

        pipeSouthObj.setPipeY(600);

        pipeNorthObj.move(screenWidth);
        pipeSouthObj.move(screenWidth);
    }

    public void restart(){
        startGame();
    }

    public void displayGameOver() {
    }

    public boolean hitFloor() {
        return bird.birdY >= 2338.875;
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