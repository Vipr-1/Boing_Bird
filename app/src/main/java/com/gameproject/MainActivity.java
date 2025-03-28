package com.gameproject;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.widget.Button;
import android.view.View;
import android.content.Intent;
import android.widget.ImageButton;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageButton playClassicButton = findViewById(R.id.imagePlay);
        playClassicButton.setOnClickListener(v -> mainGame());

        ImageButton howToPlayButton = findViewById(R.id.imageButton8);
        howToPlayButton.setOnClickListener(v -> openHowToPlay()); // Corrected


    }
    /**
    Initialize the game
     */
    protected void mainGame(){
        Intent intent = new Intent(MainActivity.this, GameLogic.class);
        startActivity(intent);
    }

    // Opens How to Play screen
    protected void openHowToPlay() {
        Intent intent = new Intent(MainActivity.this, HowToPlay.class);
        startActivity(intent);
    }
}
/**
 * Test
 */
