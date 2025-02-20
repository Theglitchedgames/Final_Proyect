// Player.java
package com.example.final_proyect.juego2;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Picture;
import android.graphics.RectF;
import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;
import com.example.final_proyect.R;

public class Player {
    private float x, y;
    private Picture playerPicture;
    private int size = 100; // Tamaño igual a un círculo con radio 50 (diámetro 100)
    private static final float SENSITIVITY = 5.0f;

    public Player(Context context) {
        x = 500;
        y = 1500;
        try {
            // Cargar el SVG desde res/raw/player_ship.svg
            SVG svg = SVG.getFromResource(context, R.raw.player_ship);
            if (svg != null) {
                playerPicture = svg.renderToPicture();
            } else {
                System.err.println("Error: No se pudo cargar el SVG del jugador.");
            }
        } catch (SVGParseException e) {
            e.printStackTrace();
        }
    }

    public void update(float sensorX) {
        x -= sensorX * SENSITIVITY;
        if (x < 0) x = 0;
        if (x > 1000) x = 1000;
    }

    public void draw(Canvas canvas) {
        if (playerPicture != null) {
            canvas.save();
            // Ajustar el tamaño del SVG a 100x100 píxeles para coincidir con el círculo
            RectF dst = new RectF(x - 50, y - 50, x + 50, y + 50);
            canvas.drawPicture(playerPicture, dst);
            canvas.restore();
        }
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
}
