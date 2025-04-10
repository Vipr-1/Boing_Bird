package com.gameproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.Random;

public class GameLogic extends AppCompatActivity {

    private Bird bird;
    private Handler handler = new Handler();
    private final int FRAME_RATE = 30; // Refresh rate for game loop
    private int gravity = 1;  // Gravity effect
    private int score;
    private int coins;
    private ImageView birdImage;

    // Green pipe image views
    private ImageView pipeNorthImage;
    private ImageView pipeSouthImage;
    private ImageView pipeNorthTwo;
    private ImageView pipeSouthTwo;

    // Red pipe image views
    private ImageView RedPipeNorth;
    private ImageView RedPipeNorth2;
    private ImageView RedPipeSouth;
    private ImageView RedPipeSouth2;

    private boolean gameStarted = false;
    private int screenWidth;

    private final int base_pipe_speed = 10;
    private int currentPipeSpeed;
    private int nextScoreThreshold = 10;

    // Sound effect objects
    private MediaPlayer jumpSFX;
    private MediaPlayer deathSFX;

    private boolean scoredFirstPipePair = false;
    private boolean scoredSecondPipePair = false;

    // Flag for switching pipes
    private boolean pipesSwitched = false;

    private final int pipe_speed = base_pipe_speed;
    private Random random = new Random();

    private Pipe pipeNorthObj;
    private Pipe pipeSouthObj;
    private Pipe pipeNorthObj2;
    private Pipe pipeSouthObj2;
    private TextView scoreTextView;
    ConstraintLayout gameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        gameLayout = findViewById(R.id.gameLayout);

        SharedPreferences preferences = getSharedPreferences("settings", MODE_PRIVATE);
        boolean isDark = preferences.getBoolean("dark_mode", false);

        if (isDark) {
            gameLayout.setBackgroundResource(R.drawable.bg_night);
        } else {
            gameLayout.setBackgroundResource(R.drawable.bg_day);
        }

        scoreTextView = findViewById(R.id.textView);

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
        pipeNorthObj2 = new Pipe(pipeNorthTwo, pipe_speed);
        pipeSouthObj2 = new Pipe(pipeSouthTwo, pipe_speed);

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

        jumpSFX = MediaPlayer.create(getApplicationContext(), R.raw.jumpfx);
        deathSFX = MediaPlayer.create(getApplicationContext(), R.raw.death);

        findViewById(android.R.id.content).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!gameStarted) {
                    gameStarted = true;
                    startGame();
                    handler.postDelayed(gameLoop, FRAME_RATE);
                } else if (!bird.isDead) {
                    bird.jump();
                    jumpSFX.start(); //play boing when jumping
                }
            }
        });
    }

    public void startGame() {
        bird.birdX = 539;
        bird.birdY = 1169;
        bird.velocityY = 0;
        bird.isDead = false;

        score = 0;
        currentPipeSpeed = base_pipe_speed;
        nextScoreThreshold = 10;
        scoredFirstPipePair = false;
        scoredSecondPipePair = false;
        pipesSwitched = false;

        pipeSouthImage.setVisibility(View.INVISIBLE);
        pipeNorthImage.setVisibility(View.INVISIBLE);
        pipeSouthTwo.setVisibility(View.INVISIBLE);
        pipeNorthTwo.setVisibility(View.INVISIBLE);
        RedPipeNorth.setVisibility(View.INVISIBLE);
        RedPipeNorth2.setVisibility(View.INVISIBLE);
        RedPipeSouth.setVisibility(View.INVISIBLE);
        RedPipeSouth2.setVisibility(View.INVISIBLE);

        randomizePipePair(pipeNorthObj, pipeSouthObj);
        randomizePipePair(pipeNorthObj2, pipeSouthObj2);
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
        bird.velocityY += gravity;
        bird.birdY += bird.velocityY;
        birdImage.setY(bird.birdY);

        if (hitFloor() && checkForCollisions()) {
            bird.isDead = true;
            bird.velocityY = 0;
            deathSFX.start(); //play death sound when dead
            GameOver();
            return;
        }

        updatePipes();
        checkScoreAndUpdate();
    }

    private void switchToRedPipes() {
        // Fetch red pipe ImageViews from the layout
        ImageView newPipeNorth = findViewById(R.id.RedNorth);
        ImageView newPipeSouth = findViewById(R.id.RedSouth);
        ImageView newPipeNorthTwo = findViewById(R.id.RedNorth2);
        ImageView newPipeSouthTwo = findViewById(R.id.RedSouth2);

        pipeSouthImage.setVisibility(View.INVISIBLE);
        pipeNorthImage.setVisibility(View.INVISIBLE);
        pipeSouthTwo.setVisibility(View.INVISIBLE);
        pipeNorthTwo.setVisibility(View.INVISIBLE);

        pipeNorthImage = newPipeNorth;
        pipeSouthImage = newPipeSouth;
        pipeNorthTwo = newPipeNorthTwo;
        pipeSouthTwo = newPipeSouthTwo;

        pipeNorthObj.setImageView(newPipeNorth);
        pipeSouthObj.setImageView(newPipeSouth);
        pipeNorthObj2.setImageView(newPipeNorthTwo);
        pipeSouthObj2.setImageView(newPipeSouthTwo);

        pipeSouthTwo.setVisibility(View.VISIBLE);
        pipeNorthTwo.setVisibility(View.VISIBLE);

    }

    private void updatePipes() {
        pipeSouthImage.setVisibility(View.VISIBLE);
        pipeNorthImage.setVisibility(View.VISIBLE);

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

        if (pipeSouthObj.getPipeX() >= screenWidth) {
            randomizePipePair(pipeNorthObj, pipeSouthObj);
            scoredFirstPipePair = false;
        }
        if (pipeSouthTwo.getVisibility() == View.VISIBLE && pipeSouthObj2.getPipeX() >= screenWidth) {
            randomizePipePair(pipeNorthObj2, pipeSouthObj2);
            scoredSecondPipePair = false;
        }
    }
    private void updateScoreDisplay() {
        scoreTextView.setText(String.valueOf(score));
    }
    private void checkScoreAndUpdate() {
        if (pipeNorthTwo.getVisibility() == View.VISIBLE &&
                pipeNorthObj.getPipeX() + pipeNorthImage.getWidth() < bird.birdX && !scoredFirstPipePair) {
            score++;
            scoredFirstPipePair = true;
            updatePipeSpeed();
            updateScoreDisplay();
        }
        if (pipeNorthTwo.getVisibility() == View.VISIBLE &&
                pipeNorthObj2.getPipeX() + pipeNorthTwo.getWidth() < bird.birdX && !scoredSecondPipePair) {
            score++;
            scoredSecondPipePair = true;
            updatePipeSpeed();
            updateScoreDisplay();
        }
        if (score >= 10 && !pipesSwitched) {
            switchToRedPipes();
            pipesSwitched = true;
        }
    }


    // Increase pipe speed at set thresholds
    private void updatePipeSpeed() {
        if (score >= nextScoreThreshold) {
            currentPipeSpeed++;
            nextScoreThreshold += 10;
            pipeNorthObj.setSpeed(currentPipeSpeed);
            pipeSouthObj.setSpeed(currentPipeSpeed);
            pipeNorthObj2.setSpeed(currentPipeSpeed);
            pipeSouthObj2.setSpeed(currentPipeSpeed);
        }
    }

    public void restart() {
        startGame();
    }

    public void GameOver() {
        pipeSouthImage.setVisibility(View.INVISIBLE);
        pipeNorthImage.setVisibility(View.INVISIBLE);
        pipeSouthTwo.setVisibility(View.INVISIBLE);
        pipeNorthTwo.setVisibility(View.INVISIBLE);
        RedPipeNorth.setVisibility(View.INVISIBLE);
        RedPipeNorth2.setVisibility(View.INVISIBLE);
        RedPipeSouth.setVisibility(View.INVISIBLE);
        RedPipeSouth2.setVisibility(View.INVISIBLE);
        birdImage.setVisibility(View.INVISIBLE);
        Intent gameOverIntent = new Intent(GameLogic.this, GameOver.class);
        startActivity(gameOverIntent);
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
        Rect pipeNorthRect2 = new Rect();
        pipeNorthTwo.getHitRect(pipeNorthRect2);
        Rect pipeSouthRect2 = new Rect();
        pipeSouthTwo.getHitRect(pipeSouthRect2);
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