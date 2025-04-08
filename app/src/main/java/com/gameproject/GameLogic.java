package com.gameproject;

import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Random;

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

    private ImageView pipeNorthTwo;
    private ImageView pipeSouthTwo;
    private boolean gameStarted = false;
    private Pipe pipeNorthObj;
    private Pipe pipeSouthObj;
    private Pipe pipeSouthObj2;
    private Pipe pipeNorthObj2;
    private ImageView RedPipeNorth;
    private ImageView RedPipeNorth2;
    private ImageView RedPipeSouth;
    private ImageView RedPipeSouth2;
    private int screenWidth;

    private final int base_pipe_speed = 10;
    private int currentPipeSpeed;
    private int nextScoreThreshold = 10;

    private boolean scoredFirstPipePair = false;
    private boolean scoredSecondPipePair = false;

    private final int pipe_speed = base_pipe_speed;
    private Random random = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        pipeNorthImage = findViewById(R.id.pipeNorth);
        pipeSouthImage = findViewById(R.id.pipeSouth);
        pipeNorthTwo = findViewById(R.id.pipeNorth2);
        pipeSouthTwo = findViewById(R.id.pipeSouth2);

        RedPipeNorth = findViewById(R.id.RedNorth);
        RedPipeNorth2 = findViewById(R.id.RedNorth2);
        RedPipeSouth = findViewById(R.id.RedSouth);
        RedPipeSouth2 = findViewById(R.id.RedSouth2);

        birdImage = findViewById(R.id.birdImage);
        bird = new Bird(birdImage);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth = displayMetrics.widthPixels;

        pipeNorthObj = new Pipe(pipeNorthImage, pipe_speed);
        pipeSouthObj = new Pipe(pipeSouthImage, pipe_speed);
        pipeSouthObj2 = new Pipe(pipeSouthTwo, pipe_speed);
        pipeNorthObj2 = new Pipe(pipeNorthTwo, pipe_speed);

        pipeSouthImage.setVisibility(View.INVISIBLE);
        pipeNorthImage.setVisibility(View.INVISIBLE);
        pipeSouthTwo.setVisibility(View.INVISIBLE);
        pipeNorthTwo.setVisibility(View.INVISIBLE);
        RedPipeNorth.setVisibility(View.INVISIBLE);
        RedPipeNorth2.setVisibility(View.INVISIBLE);
        RedPipeSouth.setVisibility(View.INVISIBLE);
        RedPipeSouth2.setVisibility(View.INVISIBLE);

        currentPipeSpeed = base_pipe_speed;
        nextScoreThreshold = 10;
        scoredFirstPipePair = false;
        scoredSecondPipePair = false;
        score = 0;

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

        // Reset
        score = 0;
        currentPipeSpeed = base_pipe_speed;
        nextScoreThreshold = 10;
        scoredFirstPipePair = false;
        scoredSecondPipePair = false;
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

    private void randomizePipePair(Pipe topPipe, Pipe bottomPipe) {
        int offset = random.nextInt(351);
        topPipe.setPipeY(-350 + offset);
        bottomPipe.setPipeY(1250 + offset);
    }

    public void update() {
        // Update bird movement
        bird.velocityY += gravity;
        bird.birdY += bird.velocityY;
        birdImage.setY(bird.birdY);

        if (hitFloor() && checkForCollisions()) {
            bird.isDead = true;
            bird.velocityY = 0;
        }

        if (bird.isDead) {
            GameOver();
        }

        if (!bird.isDead) {
            pipeSouthImage.setVisibility(View.VISIBLE);
            pipeNorthImage.setVisibility(View.VISIBLE);
        }
        pipeNorthObj.move(screenWidth);
        pipeSouthObj.move(screenWidth);

        if (pipeSouthObj.getPipeX() == (screenWidth / 2)) {
            pipeSouthTwo.setVisibility(View.VISIBLE);
            pipeNorthTwo.setVisibility(View.VISIBLE);
            randomizePipePair(pipeNorthObj2, pipeSouthObj2);
        }

        if (pipeSouthTwo.getVisibility() == View.VISIBLE) {
            pipeNorthObj2.move(screenWidth);
            pipeSouthObj2.move(screenWidth);
        }

        if (pipeSouthObj.getPipeX() == screenWidth) {
            randomizePipePair(pipeNorthObj, pipeSouthObj);
            scoredFirstPipePair = false;
        }

        if (pipeSouthTwo.getVisibility() == View.VISIBLE && pipeSouthObj2.getPipeX() == screenWidth) {
            randomizePipePair(pipeNorthObj2, pipeSouthObj2);
            scoredSecondPipePair = false;
        }

        checkScore();
    }

    public void checkScore() {
        if (score == 10){
            pipeNorthObj = new Pipe(RedPipeNorth, pipe_speed);
            pipeSouthObj = new Pipe(RedPipeSouth, pipe_speed);
            pipeSouthObj2 = new Pipe(RedPipeSouth2, pipe_speed);
            pipeNorthObj2 = new Pipe(RedPipeNorth2, pipe_speed);
            pipeSouthImage.setVisibility(View.INVISIBLE);
            pipeNorthImage.setVisibility(View.INVISIBLE);
            pipeSouthTwo.setVisibility(View.INVISIBLE);
            pipeNorthTwo.setVisibility(View.INVISIBLE);
            RedPipeNorth.setVisibility(View.VISIBLE);
            RedPipeNorth2.setVisibility(View.VISIBLE);
            RedPipeSouth.setVisibility(View.VISIBLE);
            RedPipeSouth2.setVisibility(View.VISIBLE);
        }

        if (pipeNorthTwo.getVisibility() == View.VISIBLE &&
                pipeNorthObj.getPipeX() + pipeNorthImage.getWidth() < bird.birdX && !scoredFirstPipePair) {
            score++;
            scoredFirstPipePair = true;
            Log.d("Score", "Score increased: " + score);
            updatePipeSpeed();
        }
        if (pipeNorthTwo.getVisibility() == View.VISIBLE &&
                pipeNorthObj2.getPipeX() + pipeNorthTwo.getWidth() < bird.birdX && !scoredSecondPipePair) {
            score++;
            scoredSecondPipePair = true;
            Log.d("Score", "Score increased: " + score);
            updatePipeSpeed();
        }
    }

    private void updatePipeSpeed() {
        if (score >= nextScoreThreshold) {
            currentPipeSpeed++;
            nextScoreThreshold += 10;
            pipeNorthObj.setSpeed(currentPipeSpeed);
            pipeSouthObj.setSpeed(currentPipeSpeed);
            pipeNorthObj2.setSpeed(currentPipeSpeed);
            pipeSouthObj2.setSpeed(currentPipeSpeed);
            Log.d("Speed", "Pipe speed increased to " + currentPipeSpeed);
        }
    }

    public void restart(){
        startGame();
    }

    public void GameOver() {
        pipeSouthImage.setVisibility(View.INVISIBLE);
        pipeNorthImage.setVisibility(View.INVISIBLE);
        pipeSouthTwo.setVisibility(View.INVISIBLE);
        pipeNorthTwo.setVisibility(View.INVISIBLE);
        birdImage.setVisibility(View.INVISIBLE);
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

        Rect pipeSouthRect2 = new Rect();
        pipeSouthTwo.getHitRect(pipeSouthRect2);

        Rect pipeNorthRect2 = new Rect();
        pipeNorthTwo.getHitRect(pipeNorthRect2);

        return Rect.intersects(birdRect, pipeNorthRect) ||
                Rect.intersects(birdRect, pipeSouthRect) ||
                Rect.intersects(birdRect, pipeNorthRect2) ||
                Rect.intersects(birdRect, pipeSouthRect2);
    }

    public void checkCoins() {
    }

    public int bestScore() {
        return score;
    }
}
