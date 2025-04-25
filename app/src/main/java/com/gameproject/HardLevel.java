package com.gameproject;

import android.content.SharedPreferences;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Random;

import pl.droidsonroids.gif.GifImageView;


public class HardLevel extends AppCompatActivity {

    private static final int frame_rate = 30;
    private static final int base_pipe_speed = 10;
    private static final float plant_spawn_probability = 0.30f;

    private static final String best_score_file = "best_score_hardlevel.json";
    private static final String best_score_key  = "bestScore";

    private Bird bird;
    private final Handler handler = new Handler();
    private int gravity = 1;
    private int score;
    private int currentPipeSpeed;
    private int nextScoreThreshold;
    private boolean isMuted;
    private int screenWidth;

    private boolean gameStarted = false;
    private boolean secondPipeShown = false;
    private boolean firstPipeScored = false;
    private boolean secondPipeScored = false;
    private boolean plantActive = false;
    private boolean plantCollisionHandled = false;

    private final Random random = new Random();

    private ConstraintLayout gameLayout;
    private ImageView birdImage;
    private ImageView pipeNorthImage, pipeSouthImage, pipeNorthTwo, pipeSouthTwo;
    private GifImageView plantGIF;
    private TextView scoreTextView;
    private TextView gameOver, bestScore, currentScore;
    private ImageView scoreBoard;
    private ImageButton playAgain, backButton;
    private Pipe   pipeNorthObj, pipeSouthObj, pipeNorthObj2, pipeSouthObj2;
    private Plant  plantObj;

    private MediaPlayer jumpSFX, deathSFX;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hard_level);

        gameLayout = findViewById(R.id.gameLayout);
        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        boolean isDark = prefs.getBoolean("dark_mode", false);
        gameLayout.setBackgroundResource(isDark ? R.drawable.bg_night : R.drawable.bg_day);


        boolean bgMuted = prefs.getBoolean("isMusicMuted", false);
        MusicManager.setMuted(bgMuted, this);
        MusicManager.play(this);

        boolean gameSfxOn = prefs.getBoolean("game_sound", true);
        isMuted = !gameSfxOn;

        scoreTextView = findViewById(R.id.textView);
        gameOver = findViewById(R.id.gameOverText);
        scoreBoard = findViewById(R.id.resultBoard);
        bestScore = findViewById(R.id.bestScore);
        currentScore = findViewById(R.id.currentScore);
        playAgain = findViewById(R.id.buttonPlayAgain);
        backButton = findViewById(R.id.buttonBackYellow);
        hideResults();

        playAgain.setOnClickListener(v -> restart());
        backButton.setOnClickListener(v -> finish());

        pipeNorthImage = findViewById(R.id.RedNorth);
        pipeSouthImage = findViewById(R.id.RedSouth);
        pipeNorthTwo = findViewById(R.id.RedNorth2);
        pipeSouthTwo = findViewById(R.id.RedSouth2);
        plantGIF = findViewById(R.id.plantImage);
        hideAllPipes();

        birdImage = findViewById(R.id.birdImage);
        bird = new Bird(birdImage);
        int birdRes = prefs.getInt("chosen_bird", R.drawable.bird_default);
        bird.setSkin(birdRes);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        screenWidth = dm.widthPixels;

        pipeNorthObj  = new Pipe(pipeNorthImage, base_pipe_speed);
        pipeSouthObj  = new Pipe(pipeSouthImage, base_pipe_speed);
        pipeNorthObj2 = new Pipe(pipeNorthTwo, base_pipe_speed);
        pipeSouthObj2 = new Pipe(pipeSouthTwo, base_pipe_speed);
        plantObj = new Plant(plantGIF, base_pipe_speed);

        currentPipeSpeed = base_pipe_speed;
        score = 0;
        nextScoreThreshold = 5;

        jumpSFX = MediaPlayer.create(getApplicationContext(), R.raw.jumpfx);
        deathSFX = MediaPlayer.create(getApplicationContext(), R.raw.death);

        findViewById(android.R.id.content).setOnClickListener(v -> {
            if (!gameStarted) {
                gameStarted = true;
                startGame();
                handler.postDelayed(gameLoop, frame_rate);
            } else if (!bird.isDead) {
                bird.jump();
                playSFX(jumpSFX);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        MusicManager.play(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MusicManager.pause();
    }

    private void startGame() {
        bird.birdX = screenWidth / 2;
        bird.birdY = 1169;
        bird.velocityY = 0;
        bird.isDead = false;

        score                 = 0;
        currentPipeSpeed      = base_pipe_speed;
        nextScoreThreshold    = 5;

        secondPipeShown       = false;
        firstPipeScored       = false;
        secondPipeScored      = false;

        randomizePipePair(pipeNorthObj, pipeSouthObj);
        randomizePipePair(pipeNorthObj2, pipeSouthObj2);
        attemptSpawnPlant();
    }

    private final Runnable gameLoop = new Runnable() {
        @Override public void run() {
            if (!bird.isDead) {
                update();
                handler.postDelayed(this, frame_rate);
            }
        }
    };

    private void update() {
        bird.velocityY += gravity;
        bird.birdY += bird.velocityY;
        birdImage.setY(bird.birdY);

        if (hitFloor() || checkForCollisions()) {
            bird.isDead = true;
            bird.velocityY = 0;
            playSFX(deathSFX);
            GameOver();
            return;
        }

        boolean birdHitsPlant = plantActive && checkForCollisionsPlant();
        if (birdHitsPlant && !plantCollisionHandled) {
            currentPipeSpeed++;
            setAllPipeSpeeds(currentPipeSpeed);
            plantCollisionHandled = true;
        }
        if (!birdHitsPlant) plantCollisionHandled = false;

        updatePipes();
        checkScoreAndUpdate();
    }

    private void randomizePipePair(Pipe topPipe, Pipe bottomPipe) {
        int offset = random.nextInt(251);
        topPipe.setPipeY(-250 + offset);
        bottomPipe.setPipeY(1250 + offset);
    }

    private void attemptSpawnPlant() {
        if (random.nextFloat() < plant_spawn_probability) {
            plantActive = true;
        } else {
            plantActive = false;
            plantGIF.setVisibility(View.INVISIBLE);
        }
    }

    private void updatePipes() {
        pipeNorthImage.setVisibility(View.VISIBLE);
        pipeSouthImage.setVisibility(View.VISIBLE);

        pipeNorthObj.move(screenWidth);
        pipeSouthObj.move(screenWidth);

        if (plantActive) {
            plantObj.setPlantX(pipeSouthObj.getPipeX() + 130);
            plantObj.setPlantY(pipeSouthObj.getPipeY() - plantGIF.getHeight());
            plantObj.move(screenWidth);
            plantGIF.setVisibility(View.VISIBLE);
        }
        if (!secondPipeShown && pipeSouthObj.getPipeX() <= screenWidth/2) {
            pipeNorthTwo.setVisibility(View.VISIBLE);
            pipeSouthTwo.setVisibility(View.VISIBLE);
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
            attemptSpawnPlant();
        }

        if (pipeSouthTwo.getVisibility() == View.VISIBLE &&
                pipeSouthObj2.getPipeX() >= screenWidth) {
            randomizePipePair(pipeNorthObj2, pipeSouthObj2);
            secondPipeScored = false;
        }
    }

    private void checkScoreAndUpdate() {
        if (!firstPipeScored &&
                pipeNorthObj.getPipeX() + pipeNorthImage.getWidth() < bird.birdX) {
            score++;
            firstPipeScored = true;
            applyScoreSpeedBoost();
        }
        if (pipeSouthTwo.getVisibility() == View.VISIBLE &&
                !secondPipeScored &&
                pipeNorthObj2.getPipeX() + pipeNorthTwo.getWidth() < bird.birdX) {
            score++;
            secondPipeScored = true;
            applyScoreSpeedBoost();
        }
    }

    private void applyScoreSpeedBoost() {
        updateScoreDisplay();
        if (score >= nextScoreThreshold) {
            currentPipeSpeed += 3;
            nextScoreThreshold += 5;
            setAllPipeSpeeds(currentPipeSpeed);
        }
    }

    private void updateScoreDisplay() {
        scoreTextView.setText(String.valueOf(score));
    }

    private void setAllPipeSpeeds(int speed) {
        pipeNorthObj.setSpeed(speed);
        pipeSouthObj.setSpeed(speed);
        pipeNorthObj2.setSpeed(speed);
        pipeSouthObj2.setSpeed(speed);
        plantObj.setSpeed(speed);
    }

    public boolean hitFloor() {
        return bird.birdY >= 2338.875;
    }

    public boolean checkForCollisions() {
        Rect birdRect = new Rect(); birdImage.getHitRect(birdRect);
        Rect northRect = new Rect();

        pipeNorthImage.getHitRect(northRect);
        northRect.inset(140,30);

        Rect SouthRect = new Rect();
        pipeSouthImage.getHitRect(SouthRect);
        SouthRect.inset(140,30);

        Rect northRect2 = new Rect();
        pipeNorthTwo.getHitRect(northRect2);
        northRect2.inset(140,30);

        Rect SouthRect2 = new Rect();
        pipeSouthTwo.getHitRect(SouthRect2);
        SouthRect2.inset(140,30);

        return Rect.intersects(birdRect,northRect)
                || Rect.intersects(birdRect,SouthRect)
                || Rect.intersects(birdRect,northRect2)
                || Rect.intersects(birdRect,SouthRect2);
    }

    public boolean checkForCollisionsPlant() {
        Rect birdRect  = new Rect(); birdImage.getHitRect(birdRect);
        Rect plantRect = new Rect(); plantGIF.getHitRect(plantRect);
        return Rect.intersects(birdRect, plantRect);
    }

    private void GameOver() {
        hideAllPipes();
        plantGIF.setVisibility(View.INVISIBLE);
        birdImage.setVisibility(View.INVISIBLE);

        showResults();
        currentScore.setText(String.valueOf(score));

        int savedBest = loadBestScore();
        if (score > savedBest) {
            saveBestScore(score);
            savedBest = score;
        }
        bestScore.setText(String.valueOf(savedBest));
    }

    public void restart() {
        finish();
        overridePendingTransition(0,0);
        startActivity(getIntent());
        overridePendingTransition(0,0);
    }

    private void saveBestScore(int val) {
        try {
            JSONObject json = new JSONObject();
            json.put(best_score_key, val);
            try (FileOutputStream fos = openFileOutput(best_score_file, MODE_PRIVATE)) {
                fos.write(json.toString().getBytes());
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private int loadBestScore() {
        try (FileInputStream fis = openFileInput(best_score_file)) {
            byte[] data = new byte[fis.available()];
            fis.read(data);
            JSONObject json = new JSONObject(new String(data));
            return json.optInt(best_score_key, 0);
        } catch (Exception e) { e.printStackTrace(); return 0; }
    }

    public void playSFX(MediaPlayer sfx) {
        if (sfx.isPlaying()) {
            sfx.seekTo(0);
            sfx.start();
        } else if (!isMuted) {
            sfx.start();
        }
    }

    private void hideAllPipes() {
        pipeNorthImage.setVisibility(View.INVISIBLE);
        pipeSouthImage.setVisibility(View.INVISIBLE);
        pipeNorthTwo.setVisibility(View.INVISIBLE);
        pipeSouthTwo.setVisibility(View.INVISIBLE);
        plantGIF.setVisibility(View.INVISIBLE);
    }

    private void hideResults() {
        gameOver.setVisibility(View.INVISIBLE);
        scoreBoard.setVisibility(View.INVISIBLE);
        bestScore.setVisibility(View.INVISIBLE);
        currentScore.setVisibility(View.INVISIBLE);
        playAgain.setVisibility(View.INVISIBLE);
        backButton.setVisibility(View.INVISIBLE);
    }

    private void showResults() {
        gameOver.setVisibility(View.VISIBLE);
        scoreBoard.setVisibility(View.VISIBLE);
        bestScore.setVisibility(View.VISIBLE);
        currentScore.setVisibility(View.VISIBLE);
        playAgain.setVisibility(View.VISIBLE);
        backButton.setVisibility(View.VISIBLE);
    }
}
