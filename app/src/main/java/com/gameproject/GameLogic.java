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

public class GameLogic extends AppCompatActivity {
    private Bird bird;
    private Handler handler = new Handler();
    private static final int frame_rate = 30;
    private int gravity = 1;
    private int score;
    private ImageView birdImage;
    private ImageView pipeNorthImage, pipeSouthImage;
    private ImageView pipeNorthTwo, pipeSouthTwo;
    private ImageView redPipeNorth, redPipeNorth2, redPipeSouth, redPipeSouth2;

    private ConstraintLayout gameLayout;
    private TextView scoreTextView;
    private ImageView scoreBoard;
    private TextView gameOver, bestScore, currentScore;
    private ImageButton playAgain, backButton;

    private boolean gameStarted = false;
    private int screenWidth;
    private final int basePipeSpeed = 10;
    private int currentPipeSpeed;
    private int nextScoreThreshold = 10;
    private boolean scoredFirstPipePair = false;
    private boolean scoredSecondPipePair = false;
    private boolean pipesSwitched = false;

    private MediaPlayer jumpSFX, deathSFX;
    private boolean isSFXOn;
    private boolean isMusicMuted;

    private Pipe pipeNorthObj, pipeSouthObj, pipeNorthObj2, pipeSouthObj2;
    private Random random = new Random();

    private static final String PREFS_NAME = "settings";
    private static final String KEY_MUSIC_MUTED = "isMusicMuted";
    private static final String KEY_SFX_ON = "game_sound";
    private static final String best_score_file = "best_score.json";
    private static final String best_score_key  = "bestScore";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        isMusicMuted = prefs.getBoolean(KEY_MUSIC_MUTED, false);
        isSFXOn = prefs.getBoolean(KEY_SFX_ON, true);
        boolean isDarkMode = prefs.getBoolean("dark_mode", false);

        MusicManager.setMuted(isMusicMuted, this);

        gameLayout = findViewById(R.id.gameLayout);
        if (isDarkMode) {
            gameLayout.setBackgroundResource(R.drawable.bg_night);
        } else {
            gameLayout.setBackgroundResource(R.drawable.bg_day);
        }

        scoreTextView = findViewById(R.id.score);
        scoreBoard = findViewById(R.id.resultBoard);
        gameOver = findViewById(R.id.gameOverText);
        bestScore = findViewById(R.id.bestScore);
        currentScore = findViewById(R.id.currentScore);
        playAgain = findViewById(R.id.buttonPlayAgain);
        backButton = findViewById(R.id.buttonBackYellow);

        hideGameOverUI();

        backButton.setOnClickListener(v -> {
            MusicManager.pause();
            finish();
        });
        playAgain.setOnClickListener(v -> {
            MusicManager.pause();
            restart();
        });

        pipeNorthImage = findViewById(R.id.pipeNorth);
        pipeSouthImage = findViewById(R.id.pipeSouth);
        pipeNorthTwo = findViewById(R.id.pipeNorth2);
        pipeSouthTwo = findViewById(R.id.pipeSouth2);
        redPipeNorth = findViewById(R.id.RedNorth);
        redPipeNorth2 = findViewById(R.id.RedNorth2);
        redPipeSouth = findViewById(R.id.RedSouth);
        redPipeSouth2 = findViewById(R.id.RedSouth2);

        birdImage = findViewById(R.id.birdImage);
        bird = new Bird(birdImage);
        int birdSkin = prefs.getInt("chosen_bird", R.drawable.bird_default);
        bird.setSkin(birdSkin);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        screenWidth = dm.widthPixels;

        pipeNorthObj = new Pipe(pipeNorthImage, basePipeSpeed);
        pipeSouthObj = new Pipe(pipeSouthImage, basePipeSpeed);
        pipeNorthObj2 = new Pipe(pipeNorthTwo,   basePipeSpeed);
        pipeSouthObj2 = new Pipe(pipeSouthTwo,   basePipeSpeed);

        hideAllPipes();

        currentPipeSpeed = basePipeSpeed;
        nextScoreThreshold = 10;
        scoredFirstPipePair = false;
        scoredSecondPipePair = false;
        score = 0;

        jumpSFX  = MediaPlayer.create(getApplicationContext(), R.raw.jumpfx);
        deathSFX = MediaPlayer.create(getApplicationContext(), R.raw.death);

        findViewById(android.R.id.content).setOnClickListener(v -> {
            if (!gameStarted) {
                gameStarted = true;
                startGame();
                MusicManager.play(this);
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
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        isMusicMuted = prefs.getBoolean(KEY_MUSIC_MUTED, false);
        isSFXOn = prefs.getBoolean(KEY_SFX_ON, true);
        MusicManager.setMuted(isMusicMuted, this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MusicManager.pause();
    }

    private void startGame() {
        bird.birdX = 539;
        bird.birdY = 1169;
        bird.velocityY = 0;
        bird.isDead = false;

        score = 0;
        currentPipeSpeed = basePipeSpeed;
        nextScoreThreshold = 10;
        scoredFirstPipePair = false;
        scoredSecondPipePair = false;
        pipesSwitched = false;
        scoreTextView.setText(String.valueOf(score));

        hideAllPipes();
        randomizePipePair(pipeNorthObj,  pipeSouthObj);
    }

    private final Runnable gameLoop = new Runnable() {
        @Override
        public void run() {
            if (!bird.isDead) {
                update();
                handler.postDelayed(this, frame_rate);
            }
        }
    };

    private void update() {
        bird.velocityY += gravity;
        bird.birdY    += bird.velocityY;
        birdImage.setY(bird.birdY);

        if (hitFloor() || checkForCollisions()) {
            bird.isDead = true;
            bird.velocityY = 0;
            playSFX(deathSFX);
            GameOver();
            return;
        }

        updatePipes();
        checkScoreAndUpdate();
    }

    private void playSFX(MediaPlayer sfx) {
        if (isSFXOn) {
            if (sfx.isPlaying()) {
                sfx.seekTo(0);
            }
            sfx.start();
        }
    }

    private void updatePipes() {
        pipeSouthImage.setVisibility(View.VISIBLE);
        pipeNorthImage.setVisibility(View.VISIBLE);

        pipeNorthObj.move(screenWidth);
        pipeSouthObj.move(screenWidth);

        if (pipeSouthObj.getPipeX() == screenWidth / 2) {
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
        if (pipeSouthTwo.getVisibility() == View.VISIBLE
                && pipeSouthObj2.getPipeX() >= screenWidth) {
            randomizePipePair(pipeNorthObj2, pipeSouthObj2);
            scoredSecondPipePair = false;
        }
    }

    private void checkScoreAndUpdate() {
        if (pipeNorthTwo.getVisibility() == View.VISIBLE
                && pipeNorthObj.getPipeX() + pipeNorthImage.getWidth() < bird.birdX
                && !scoredFirstPipePair) {
            score++;
            scoredFirstPipePair = true;
            updatePipeSpeed();
            scoreTextView.setText(String.valueOf(score));
        }
        if (pipeNorthTwo.getVisibility() == View.VISIBLE
                && pipeNorthObj2.getPipeX() + pipeNorthTwo.getWidth() < bird.birdX
                && !scoredSecondPipePair) {
            score++;
            scoredSecondPipePair = true;
            updatePipeSpeed();
            scoreTextView.setText(String.valueOf(score));
        }
        if (score >= 10 && !pipesSwitched) {
            switchToRedPipes();
            pipesSwitched = true;
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
        }
    }

    private void switchToRedPipes() {
        // grab the red pipe ImageViews
        ImageView northPipeRed  = findViewById(R.id.RedNorth);
        ImageView southPipeRed  = findViewById(R.id.RedSouth);
        ImageView northPipeRed2 = findViewById(R.id.RedNorth2);
        ImageView southPipeRed2 = findViewById(R.id.RedSouth2);

        // hide the green ones
        pipeSouthImage.setVisibility(View.INVISIBLE);
        pipeNorthImage.setVisibility(View.INVISIBLE);
        pipeSouthTwo.setVisibility(View.INVISIBLE);
        pipeNorthTwo.setVisibility(View.INVISIBLE);

        // swap references
        pipeNorthImage = northPipeRed;
        pipeSouthImage = southPipeRed;
        pipeNorthTwo = northPipeRed2;
        pipeSouthTwo = southPipeRed2;

        // reattach Pipe objects
        pipeNorthObj.setImageView(northPipeRed);
        pipeSouthObj.setImageView(southPipeRed);
        pipeNorthObj2.setImageView(northPipeRed2);
        pipeSouthObj2.setImageView(southPipeRed2);

        // show the new pair
        pipeSouthTwo.setVisibility(View.VISIBLE);
        pipeNorthTwo.setVisibility(View.VISIBLE);
    }

    private boolean hitFloor() {
        return bird.birdY >= 2338.875;
    }

    private boolean checkForCollisions() {
        Rect birdRect = new Rect();
        birdImage.getHitRect(birdRect);

        Rect northRect = new Rect();
        pipeNorthImage.getHitRect(northRect);
        northRect.inset(70, 20);

        Rect southRect = new Rect();
        pipeSouthImage.getHitRect(southRect);
        southRect.inset(70, 20);

        Rect northRect2 = new Rect();
        pipeNorthTwo.getHitRect(northRect2);
        northRect2.inset(70, 20);

        Rect southRect2 = new Rect();
        pipeSouthTwo.getHitRect(southRect2);
        southRect2.inset(70, 20);

        return Rect.intersects(birdRect, northRect)
                || Rect.intersects(birdRect, southRect)
                || Rect.intersects(birdRect, northRect)
                || Rect.intersects(birdRect, southRect2);
    }

    private void GameOver() {
        MusicManager.pause();
        hideAllPipes();
        birdImage.setVisibility(View.INVISIBLE);
        gameOver.setVisibility(View.VISIBLE);
        scoreBoard.setVisibility(View.VISIBLE);
        bestScore.setVisibility(View.VISIBLE);
        currentScore.setVisibility(View.VISIBLE);
        playAgain.setVisibility(View.VISIBLE);
        backButton.setVisibility(View.VISIBLE);

        currentScore.setText(String.valueOf(score));

        int savedBest = loadBestScore();
        if (score > savedBest) {
            savedBest = score;
            saveBestScore(savedBest);
        }
        bestScore.setText(String.valueOf(savedBest));
    }

    private void hideAllPipes() {
        pipeNorthImage.setVisibility(View.INVISIBLE);
        pipeSouthImage.setVisibility(View.INVISIBLE);
        pipeNorthTwo.setVisibility(View.INVISIBLE);
        pipeSouthTwo.setVisibility(View.INVISIBLE);
        redPipeNorth.setVisibility(View.INVISIBLE);
        redPipeNorth2.setVisibility(View.INVISIBLE);
        redPipeSouth.setVisibility(View.INVISIBLE);
        redPipeSouth2.setVisibility(View.INVISIBLE);
    }

    private void hideGameOverUI() {
        gameOver.setVisibility(View.INVISIBLE);
        scoreBoard.setVisibility(View.INVISIBLE);
        bestScore.setVisibility(View.INVISIBLE);
        currentScore.setVisibility(View.INVISIBLE);
        playAgain.setVisibility(View.INVISIBLE);
        backButton.setVisibility(View.INVISIBLE);
    }

    private void restart() {
        finish();
        overridePendingTransition(0, 0);
        startActivity(getIntent());
        overridePendingTransition(0, 0);
    }

    private void randomizePipePair(Pipe topPipe, Pipe bottomPipe) {
        int offset = random.nextInt(351);
        topPipe.setPipeY(-350 + offset);
        bottomPipe.setPipeY(1250 + offset);
    }

    private void saveBestScore(int val) {
        try {
            JSONObject json = new JSONObject();
            json.put(best_score_key, val);
            try (FileOutputStream fos = openFileOutput(best_score_file, MODE_PRIVATE)) {
                fos.write(json.toString().getBytes());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int loadBestScore() {
        try (FileInputStream fis = openFileInput(best_score_file)) {
            byte[] data = new byte[fis.available()];
            fis.read(data);
            JSONObject json = new JSONObject(new String(data));
            return json.optInt(best_score_key, 0);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
