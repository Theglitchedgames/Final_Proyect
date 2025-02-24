package com.example.final_proyect;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ScoresActivity extends AppCompatActivity {
    private TextView score2048TextView;
    private TextView scoreGalactaTextView;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scores);

        score2048TextView = findViewById(R.id.score_2048);
        scoreGalactaTextView = findViewById(R.id.score_galacta);
        databaseHelper = new DatabaseHelper(this);

        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String username = preferences.getString("username", "");

        long score2048 = databaseHelper.getMaxScoreGame1(username);
        long scoreGalacta = databaseHelper.getMaxScoreGame2(username);

        score2048TextView.setText("2048 High Score: " + score2048);
        scoreGalactaTextView.setText("Galacta High Score: " + scoreGalacta);
    }
}
