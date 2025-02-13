// FallingObject.java
package com.example.final_proyect.juego2;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Picture;
import android.graphics.RectF;
import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;
import com.example.final_proyect.R;

import java.util.Random;

public class FallingObject {
    private float x, y;
    private Picture objectPicture;
    private int speed;
    private static final int SIZE = 100; // Tamaño ajustado para coincidir con el jugador
    private static final int[] DRAWABLE_RESOURCES = {
            R.raw.asteroid,
            R.raw.satellite,
            R.raw.comet,
            R.raw.space_debris
    };

    public FallingObject(Context context, int screenWidth, int screenHeight) {
        Random random = new Random();
        x = random.nextInt(screenWidth);
        y = 0;
        speed = random.nextInt(10) + 5;

        try {
            // Seleccionar un recurso SVG aleatorio
            int resourceId = DRAWABLE_RESOURCES[random.nextInt(DRAWABLE_RESOURCES.length)];
            SVG svg = SVG.getFromResource(context, resourceId);
            if (svg != null) {
                objectPicture = svg.renderToPicture();
            } else {
                System.err.println("Error: No se pudo cargar el SVG del objeto que cae.");
            }
        } catch (SVGParseException e) {
            e.printStackTrace();
        }
    }

    public void update() {
        y += speed;
    }

    public void draw(Canvas canvas) {
        if (objectPicture != null) {
            canvas.save();
            // Ajustar el tamaño a 100x100 píxeles
            RectF dst = new RectF(x - 50, y - 50, x + 50, y + 50);
            canvas.drawPicture(objectPicture, dst);
            canvas.restore();
        }
    }

    public boolean collidesWith(Player player) {
        float dx = x - player.getX();
        float dy = y - player.getY();
        return Math.sqrt(dx * dx + dy * dy) < (SIZE / 2 + 50); // 50 es el radio del jugador
    }

    public float getY() {
        return y;
    }
}
