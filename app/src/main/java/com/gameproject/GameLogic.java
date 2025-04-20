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
    private final int FRAME_RATE = 30; // Refresh rate for game loop
    private int gravity = 1;  // Gravity effect
    private int score;
    private int coins;
    private ImageView birdImage;
    private boolean isMuted;              // ← Mute flag

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

    private ImageView scoreBoard;
    private TextView gameOver;
    private TextView bestScore;
    private TextView currentScore;

    private ImageButton playAgain;
    private ImageButton backButton;

    private boolean gameStarted = false;
    private int screenWidth;

    private final int base_pipe_speed = 10;
    private int currentPipeSpeed;
    private int nextScoreThreshold = 10;

    // Sound effects
    private MediaPlayer jumpSFX;
    private MediaPlayer deathSFX;

    private boolean scoredFirstPipePair = false;
    private boolean scoredSecondPipePair = false;

    private boolean pipesSwitched = false;

    private final int pipe_speed = base_pipe_speed;
    private Random random = new Random();

    private Pipe pipeNorthObj;
    private Pipe pipeSouthObj;
    private Pipe pipeNorthObj2;
    private Pipe pipeSouthObj2;
    private TextView scoreTextView;
    private ConstraintLayout gameLayout;

    private static final String best_score_file = "best_score.json";
    private static final String best_score_key = "bestScore";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // Grab preferences
        SharedPreferences preferences = getSharedPreferences("settings", MODE_PRIVATE);
        isMuted = preferences.getBoolean("isMuted", false);
        boolean isDark = preferences.getBoolean("dark_mode", false);

        // Background
        gameLayout = findViewById(R.id.gameLayout);
        gameLayout.setBackgroundResource(isDark ? R.drawable.bg_night : R.drawable.bg_day);

        // UI setup
        gameOver     = findViewById(R.id.gameOverText);
        scoreBoard  = findViewById(R.id.resultBoard);
        bestScore   = findViewById(R.id.bestScore);
        currentScore= findViewById(R.id.currentScore);
        playAgain   = findViewById(R.id.buttonPlayAgain);
        backButton  = findViewById(R.id.buttonBackYellow);
        scoreTextView = findViewById(R.id.score);

        gameOver.setVisibility(View.INVISIBLE);
        scoreBoard.setVisibility(View.INVISIBLE);
        bestScore.setVisibility(View.INVISIBLE);
        currentScore.setVisibility(View.INVISIBLE);
        playAgain.setVisibility(View.INVISIBLE);
        backButton.setVisibility(View.INVISIBLE);

        backButton.setOnClickListener(v -> finish());
        playAgain.setOnClickListener(v -> restart());

        // Pipes & bird
        pipeNorthImage = findViewById(R.id.pipeNorth);
        pipeSouthImage = findViewById(R.id.pipeSouth);
        pipeNorthTwo   = findViewById(R.id.pipeNorth2);
        pipeSouthTwo   = findViewById(R.id.pipeSouth2);
        RedPipeNorth   = findViewById(R.id.RedNorth);
        RedPipeNorth2  = findViewById(R.id.RedNorth2);
        RedPipeSouth   = findViewById(R.id.RedSouth);
        RedPipeSouth2  = findViewById(R.id.RedSouth2);

        birdImage = findViewById(R.id.birdImage);
        bird = new Bird(birdImage);
        int birdRes = preferences.getInt("chosen_bird", R.drawable.bird_default);
        bird.setSkin(birdRes);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        screenWidth = dm.widthPixels;

        pipeNorthObj  = new Pipe(pipeNorthImage, pipe_speed);
        pipeSouthObj  = new Pipe(pipeSouthImage, pipe_speed);
        pipeNorthObj2 = new Pipe(pipeNorthTwo, pipe_speed);
        pipeSouthObj2 = new Pipe(pipeSouthTwo, pipe_speed);

        // Hide all pipes initially
        pipeSouthImage.setVisibility(View.INVISIBLE);
        pipeNorthImage.setVisibility(View.INVISIBLE);
        pipeSouthTwo.setVisibility(View.INVISIBLE);
        pipeNorthTwo.setVisibility(View.INVISIBLE);
        RedPipeNorth.setVisibility(View.INVISIBLE);
        RedPipeNorth2.setVisibility(View.INVISIBLE);
        RedPipeSouth.setVisibility(View.INVISIBLE);
        RedPipeSouth2.setVisibility(View.INVISIBLE);

        // Scores
        currentPipeSpeed   = base_pipe_speed;
        nextScoreThreshold = 10;
        scoredFirstPipePair  = false;
        scoredSecondPipePair = false;
        score = 0;

        // SFX
        jumpSFX  = MediaPlayer.create(getApplicationContext(), R.raw.jumpfx);
        deathSFX = MediaPlayer.create(getApplicationContext(), R.raw.death);

        // Tap to jump / start
        findViewById(android.R.id.content).setOnClickListener(v -> {
            if (!gameStarted) {
                gameStarted = true;
                startGame();
                handler.postDelayed(gameLoop, FRAME_RATE);
            } else if (!bird.isDead) {
                bird.jump();
                if (!isMuted) jumpSFX.start();   // ← guard jump SFX
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // re‑read mute setting if user changed it in Settings
        isMuted = getSharedPreferences("settings", MODE_PRIVATE)
                .getBoolean("isMuted", false);
    }

    public void startGame() {
        bird.birdX = 539;
        bird.birdY = 1169;
        bird.velocityY = 0;
        bird.isDead = false;

        score = 0;
        currentPipeSpeed   = base_pipe_speed;
        nextScoreThreshold = 10;
        scoredFirstPipePair  = false;
        scoredSecondPipePair = false;
        pipesSwitched        = false;
        scoreTextView.setText(String.valueOf(score));

        // Hide everything, then spawn new pipes
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

    private final Runnable gameLoop = new Runnable() {
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

        if (hitFloor() || checkForCollisions()) {
            bird.isDead = true;
            bird.velocityY = 0;
            if (!isMuted) deathSFX.start();   // ← guard death SFX
            GameOver();
            return;
        }

        updatePipes();
        checkScoreAndUpdate();
    }

    private void switchToRedPipes() {
        ImageView nn = findViewById(R.id.RedNorth);
        ImageView ns = findViewById(R.id.RedSouth);
        ImageView nn2 = findViewById(R.id.RedNorth2);
        ImageView ns2 = findViewById(R.id.RedSouth2);

        pipeSouthImage.setVisibility(View.INVISIBLE);
        pipeNorthImage.setVisibility(View.INVISIBLE);
        pipeSouthTwo.setVisibility(View.INVISIBLE);
        pipeNorthTwo.setVisibility(View.INVISIBLE);

        pipeNorthImage = nn;
        pipeSouthImage = ns;
        pipeNorthTwo  = nn2;
        pipeSouthTwo  = ns2;

        pipeNorthObj.setImageView(nn);
        pipeSouthObj.setImageView(ns);
        pipeNorthObj2.setImageView(nn2);
        pipeSouthObj2.setImageView(ns2);

        pipeSouthTwo.setVisibility(View.VISIBLE);
        pipeNorthTwo.setVisibility(View.VISIBLE);
    }

    private void updatePipes() {
        pipeSouthImage.setVisibility(View.VISIBLE);
        pipeNorthImage.setVisibility(View.VISIBLE);

        pipeNorthObj.move(screenWidth);
        pipeSouthObj.move(screenWidth);

        if (pipeSouthObj.getPipeX() == screenWidth/2) {
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
        if (pipeSouthTwo.getVisibility()==View.VISIBLE
                && pipeSouthObj2.getPipeX()>=screenWidth) {
            randomizePipePair(pipeNorthObj2, pipeSouthObj2);
            scoredSecondPipePair = false;
        }
    }

    private void updateScoreDisplay() {
        scoreTextView.setText(String.valueOf(score));
    }

    private void checkScoreAndUpdate() {
        if (pipeNorthTwo.getVisibility()==View.VISIBLE
                && pipeNorthObj.getPipeX()+pipeNorthImage.getWidth()<bird.birdX
                && !scoredFirstPipePair) {
            score++;
            scoredFirstPipePair = true;
            updatePipeSpeed();
            updateScoreDisplay();
        }
        if (pipeNorthTwo.getVisibility()==View.VISIBLE
                && pipeNorthObj2.getPipeX()+pipeNorthTwo.getWidth()<bird.birdX
                && !scoredSecondPipePair) {
            score++;
            scoredSecondPipePair = true;
            updatePipeSpeed();
            updateScoreDisplay();
        }
        if (score>=10 && !pipesSwitched) {
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

    public void GameOver() {
        gameOver.setVisibility(View.VISIBLE);
        scoreBoard.setVisibility(View.VISIBLE);
        backButton.setVisibility(View.VISIBLE);
        playAgain.setVisibility(View.VISIBLE);
        bestScore.setVisibility(View.VISIBLE);
        currentScore.setVisibility(View.VISIBLE);

        currentScore.setText("" + score);

        int savedBest = loadBestScore();
        if (score > savedBest) {
            savedBest = score;
            saveBestScore(savedBest);
        }
        bestScore.setText("" + savedBest);

        // hide everything
        pipeSouthImage.setVisibility(View.INVISIBLE);
        pipeNorthImage.setVisibility(View.INVISIBLE);
        pipeSouthTwo.setVisibility(View.INVISIBLE);
        pipeNorthTwo.setVisibility(View.INVISIBLE);
        RedPipeNorth.setVisibility(View.INVISIBLE);
        RedPipeNorth2.setVisibility(View.INVISIBLE);
        RedPipeSouth.setVisibility(View.INVISIBLE);
        RedPipeSouth2.setVisibility(View.INVISIBLE);
        birdImage.setVisibility(View.INVISIBLE);
    }

    public boolean hitFloor() {
        return bird.birdY >= 2338.875;
    }

    public boolean checkForCollisions() {
        Rect birdRect = new Rect();
        birdImage.getHitRect(birdRect);

        Rect nRect  = new Rect(); pipeNorthImage.getHitRect(nRect);  nRect.inset(70,20);
        Rect sRect  = new Rect(); pipeSouthImage.getHitRect(sRect);  sRect.inset(70,20);
        Rect n2Rect = new Rect(); pipeNorthTwo.getHitRect(n2Rect); n2Rect.inset(70,20);
        Rect s2Rect = new Rect(); pipeSouthTwo.getHitRect(s2Rect); s2Rect.inset(70,20);

        return Rect.intersects(birdRect,nRect)
                || Rect.intersects(birdRect,sRect)
                || Rect.intersects(birdRect,n2Rect)
                || Rect.intersects(birdRect,s2Rect);
    }
}
