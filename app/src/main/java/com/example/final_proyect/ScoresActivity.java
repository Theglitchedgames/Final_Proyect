package com.example.final_proyect;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Button;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Map;

public class ScoresActivity extends AppCompatActivity {
    private static final String TAG = "ScoresActivity";
    private TextView score2048TextView;
    private TextView scoreGalactaTextView;
    private TextView usernameTextView;  // Opcional: para mostrar a qué usuario pertenecen los scores
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scores);

        // Inicializar vistas
        score2048TextView = findViewById(R.id.score_2048);
        scoreGalactaTextView = findViewById(R.id.score_galacta);
        // Si añades un TextView para el nombre de usuario:
        usernameTextView = findViewById(R.id.username_text);

        databaseHelper = new DatabaseHelper(this);

        loadAndDisplayScores();

        // Opcional: Añadir un botón para actualizar los puntajes manualmente
        // En onCreate, añade esto temporalmente para pruebas
        Button refreshButton = findViewById(R.id.btn_refresh);
        if (refreshButton != null) {
            refreshButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveUsernameForTesting(); // Prueba guardando un username
                }
            });
        }

        Button backButton = findViewById(R.id.btn_back);
        if (backButton != null) {
            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish(); // Cierra la actividad actual y vuelve a la anterior
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Actualizar los puntajes cada vez que la actividad vuelve a primer plano
        loadAndDisplayScores();
    }

    private void loadAndDisplayScores() {
        // Modificar esta línea para usar las mismas SharedPreferences que en MainActivity
        SharedPreferences preferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String username = preferences.getString("current_username", "");

        Log.d(TAG, "Loading scores for user: " + username);

        if (username.isEmpty()) {
            Log.e(TAG, "Username is empty. No user is logged in.");
            score2048TextView.setText("High Score: N/A");
            scoreGalactaTextView.setText("High Score: N/A");
            usernameTextView.setText("Player: Guest");
            return;
        }

        // Obtener puntuaciones de la base de datos
        long score2048 = databaseHelper.getMaxScoreGame1(username);
        int scoreGalacta = databaseHelper.getMaxScoreGame2(username);

        Log.d(TAG, "Retrieved scores from DB - 2048: " + score2048 + ", Galacta: " + scoreGalacta);

        // Mostrar las puntuaciones con formato
        score2048TextView.setText("High Score: " + formatScore(score2048));
        scoreGalactaTextView.setText("High Score: " + scoreGalacta + " points");

        // Si añadiste un TextView para el nombre de usuario:
        usernameTextView.setText("Player: " + username);
    }

    private String formatScore(long score) {
        if (score >= 1000000) {
            return String.format("%.1fM", score / 1000000.0);
        } else if (score >= 1000) {
            return String.format("%.1fK", score / 1000.0);
        }
        return String.valueOf(score);
    }

    // Método para pruebas - añade esto a ScoresActivity
    private void saveUsernameForTesting() {
        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("username", "testUser"); // Usa un nombre de usuario de prueba
        editor.apply();
        Toast.makeText(this, "Test username set to: testUser", Toast.LENGTH_SHORT).show();
        loadAndDisplayScores(); // Recarga los datos
    }


}