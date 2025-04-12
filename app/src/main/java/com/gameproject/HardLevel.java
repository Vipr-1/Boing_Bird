package com.gameproject;

import android.content.SharedPreferences;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.Random;

public class HardLevel extends AppCompatActivity {

    private Bird bird;
    private Handler handler = new Handler();
    private final int FRAME_RATE = 30;
    private int gravity = 1;
    private int score;

    private int base_pipe_speed = 10;
    private int currentPipeSpeed;
    private int nextScoreThreshold = 5;

    private ImageView pipeNorthImage;
    private ImageView pipeSouthImage;
    private ImageView pipeNorthTwo;
    private ImageView pipeSouthTwo;

    private Random random = new Random();

    private Pipe pipeNorthObj;
    private Pipe pipeSouthObj;
    private Pipe pipeNorthObj2;
    private Pipe pipeSouthObj2;

    private TextView scoreTextView;
    private ConstraintLayout gameLayout;
    private ImageView birdImage;

    private MediaPlayer jumpSFX;
    private MediaPlayer deathSFX;

    private int screenWidth;

    private boolean gameStarted = false;
    private boolean secondPipeShown = false;
    private boolean firstPipeScored = false;
    private boolean secondPipeScored = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hard_level);

        gameLayout = findViewById(R.id.gameLayout);
        SharedPreferences preferences = getSharedPreferences("settings", MODE_PRIVATE);
        boolean isDark = preferences.getBoolean("dark_mode", false);
        gameLayout.setBackgroundResource(isDark ? R.drawable.bg_night : R.drawable.bg_day);

        scoreTextView = findViewById(R.id.textView);

        pipeNorthImage = findViewById(R.id.RedNorth);
        pipeSouthImage = findViewById(R.id.RedSouth);
        pipeNorthTwo = findViewById(R.id.RedNorth2);
        pipeSouthTwo = findViewById(R.id.RedSouth2);

        pipeNorthImage.setVisibility(View.INVISIBLE);
        pipeSouthImage.setVisibility(View.INVISIBLE);
        pipeNorthTwo.setVisibility(View.INVISIBLE);
        pipeSouthTwo.setVisibility(View.INVISIBLE);

        birdImage = findViewById(R.id.birdImage);
        bird = new Bird(birdImage);
        SharedPreferences birdPreferences = getSharedPreferences("settings", MODE_PRIVATE);
        int birdRes = birdPreferences.getInt("chosen_bird", R.drawable.bird_default);
        bird.setSkin(birdRes);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth = displayMetrics.widthPixels;

        pipeNorthObj = new Pipe(pipeNorthImage, base_pipe_speed);
        pipeSouthObj = new Pipe(pipeSouthImage, base_pipe_speed);
        pipeNorthObj2 = new Pipe(pipeNorthTwo, base_pipe_speed);
        pipeSouthObj2 = new Pipe(pipeSouthTwo, base_pipe_speed);

        currentPipeSpeed = base_pipe_speed;
        score = 0;
        nextScoreThreshold = 5;

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
                    jumpSFX.start();
                }
            }
        });
    }

    public void startGame() {
        // Set initial bird position
        bird.birdX = screenWidth / 2;
        bird.birdY = 1169;
        bird.velocityY = 0;
        bird.isDead = false;

        score = 0;
        currentPipeSpeed = base_pipe_speed;
        nextScoreThreshold = 5;
        secondPipeShown = false;
        firstPipeScored = false;
        secondPipeScored = false;

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
        int offset = random.nextInt(251);
        topPipe.setPipeY(-250 + offset);
        bottomPipe.setPipeY(1250 + offset);
    }

    public void update() {
        bird.velocityY += gravity;
        bird.birdY += bird.velocityY;
        birdImage.setY(bird.birdY);

        // Check collisions or if the bird has hit the floor
        if (hitFloor() || checkForCollisions()) {
            bird.isDead = true;
            bird.velocityY = 0;
            deathSFX.start();
            GameOver();
            return;
        }

        updatePipes();
        checkScoreAndUpdate();
    }

    private void updatePipes() {
        pipeSouthImage.setVisibility(View.VISIBLE);
        pipeNorthImage.setVisibility(View.VISIBLE);

        pipeNorthObj.move(screenWidth);
        pipeSouthObj.move(screenWidth);

        if (!secondPipeShown && pipeSouthObj.getPipeX() <= (screenWidth / 2)) {
            pipeSouthTwo.setVisibility(View.VISIBLE);
            pipeNorthTwo.setVisibility(View.VISIBLE);
            randomizePipePair(pipeNorthObj2, pipeSouthObj2);
            secondPipeShown = true;
        }

        if (pipeSouthTwo.getVisibility() == View.VISIBLE) {
            pipeNorthObj2.move(screenWidth);
            pipeSouthObj2.move(screenWidth);
        }

        if (pipeSouthObj.getPipeX() >= screenWidth) {
            randomizePipePair(pipeNorthObj, pipeSouthObj);
            secondPipeShown = false;
            firstPipeScored = false;
        }

        if (pipeSouthTwo.getVisibility() == View.VISIBLE && pipeSouthObj2.getPipeX() >= screenWidth) {
            randomizePipePair(pipeNorthObj2, pipeSouthObj2);
            secondPipeScored = false;
        }
    }

    private void checkScoreAndUpdate() {
        if (!firstPipeScored && (pipeNorthObj.getPipeX() + pipeNorthImage.getWidth() < bird.birdX)) {
            score++;
            firstPipeScored = true;
            updatePipeSpeed();
            updateScoreDisplay();
        }

        if (pipeSouthTwo.getVisibility() == View.VISIBLE && !secondPipeScored &&
                (pipeNorthObj2.getPipeX() + pipeNorthTwo.getWidth() < bird.birdX)) {
            score++;
            secondPipeScored = true;
            updatePipeSpeed();
            updateScoreDisplay();
        }
    }

    private void updateScoreDisplay() {
        scoreTextView.setText(String.valueOf(score));
    }

    private void updatePipeSpeed() {
        if (score >= nextScoreThreshold) {
            currentPipeSpeed += 3;
            nextScoreThreshold += 5;
            pipeNorthObj.setSpeed(currentPipeSpeed);
            pipeSouthObj.setSpeed(currentPipeSpeed);
            pipeNorthObj2.setSpeed(currentPipeSpeed);
            pipeSouthObj2.setSpeed(currentPipeSpeed);
        }
    }

    public void GameOver() {
        pipeNorthImage.setVisibility(View.INVISIBLE);
        pipeSouthImage.setVisibility(View.INVISIBLE);
        pipeNorthTwo.setVisibility(View.INVISIBLE);
        pipeSouthTwo.setVisibility(View.INVISIBLE);
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
        pipeNorthRect.inset(140, 30);
        Rect pipeSouthRect = new Rect();
        pipeSouthImage.getHitRect(pipeSouthRect);
        pipeSouthRect.inset(140, 30);
        Rect pipeNorthRect2 = new Rect();
        pipeNorthTwo.getHitRect(pipeNorthRect2);
        pipeNorthRect2.inset(140, 30);
        Rect pipeSouthRect2 = new Rect();
        pipeSouthTwo.getHitRect(pipeSouthRect2);
        pipeSouthRect2.inset(140, 30);


        return Rect.intersects(birdRect, pipeNorthRect) ||
                Rect.intersects(birdRect, pipeSouthRect) ||
                Rect.intersects(birdRect, pipeNorthRect2) ||
                Rect.intersects(birdRect, pipeSouthRect2);
    }
}
