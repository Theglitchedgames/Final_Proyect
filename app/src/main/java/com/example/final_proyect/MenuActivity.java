package com.example.final_proyect;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.final_proyect.juego1.MainActivity;
import com.example.final_proyect.juego2.MainActivity2;

public class MenuActivity extends AppCompatActivity {

    private Button button2048;
    private Button buttonGalacta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        button2048 = findViewById(R.id.button_2048);
        buttonGalacta = findViewById(R.id.button_juego2);

        button2048.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        buttonGalacta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuActivity.this, MainActivity2.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }
}