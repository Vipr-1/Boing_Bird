package com.gameproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;


public class GameOver extends AppCompatActivity {

    public int score = 1;
    public int highScore = 2;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_over);
        ConstraintLayout gameOverLayout = findViewById(R.id.gameOverLayOut);
        Button quitButton = findViewById(R.id.quit);
        Button againButton = findViewById(R.id.again);
        quitButton.setOnClickListener(v -> quit());
        againButton.setOnClickListener(v -> again());
        TextView scoreText = findViewById(R.id.gameOverScore);
        TextView highScoreText = findViewById(R.id.gameOverBest);
        scoreText.setText(score);
        highScoreText.setText(highScore);

    }

    /**
     * sends you back to the main menu
     */
    public void quit(){
        Intent mainMenuIntent = new Intent(GameOver.this, MainActivity.class);
        startActivity(mainMenuIntent);
        finish(); //kills the game over process
    }

    /**
     * starts the game again
     */
    public void again(){
        Intent gameIntent = new Intent(GameOver.this, GameLogic.class);
        startActivity(gameIntent);
        finish();
    }
}
