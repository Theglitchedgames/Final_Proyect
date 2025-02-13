package com.example.final_proyect.juego2;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Picture;
import android.graphics.Shader;
import android.graphics.drawable.PictureDrawable;
import com.caverock.androidsvg.SVG;
import com.example.final_proyect.R;

import java.util.ArrayList;
import java.util.Random;

public class SpaceBackground {
    private Paint backgroundPaint;
    private PictureDrawable starsDrawable;
    private PictureDrawable nebulaDrawable;
    private float[] starLayerY;
    private float nebulaY;
    private final float PARALLAX_SPEED_1 = 1f;
    private final float PARALLAX_SPEED_2 = 2f;
    private final float NEBULA_SPEED = 0.5f;
    private int screenHeight;
    private int screenWidth;

    public SpaceBackground(Context context, int width, int height) {
        screenWidth = width;
        screenHeight = height;

        // Crear gradiente para el fondo
        backgroundPaint = new Paint();
        LinearGradient gradient = new LinearGradient(
                0, 0, 0, height,
                new int[]{Color.parseColor("#000033"), Color.parseColor("#000066")},
                null, Shader.TileMode.CLAMP
        );
        backgroundPaint.setShader(gradient);

        try {
            // Cargar patrón de estrellas
            SVG starsSvg = SVG.getFromResource(context, R.raw.background_stars);
            starsDrawable = new PictureDrawable(starsSvg.renderToPicture());

            // Cargar nebulosa
            SVG nebulaSvg = SVG.getFromResource(context, R.raw.background_nebula);
            nebulaDrawable = new PictureDrawable(nebulaSvg.renderToPicture());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Inicializar capas de estrellas
        starLayerY = new float[2];
        resetPositions();
    }

    private void resetPositions() {
        starLayerY[0] = -screenHeight;
        starLayerY[1] = 0;
        nebulaY = 0;
    }

    public void update() {
        // Actualizar posición de las capas de estrellas
        for (int i = 0; i < starLayerY.length; i++) {
            starLayerY[i] += (i + 1) * PARALLAX_SPEED_1;
            if (starLayerY[i] >= screenHeight) {
                starLayerY[i] = -screenHeight;
            }
        }

        // Actualizar posición de la nebulosa
        nebulaY += NEBULA_SPEED;
        if (nebulaY >= screenHeight) {
            nebulaY = -screenHeight;
        }
    }

    public void draw(Canvas canvas) {
        // Dibujar gradiente de fondo
        canvas.drawRect(0, 0, screenWidth, screenHeight, backgroundPaint);

        // Dibujar capas de estrellas con paralaje
        if (starsDrawable != null) {
            for (int i = 0; i < starLayerY.length; i++) {
                canvas.save();
                canvas.translate(0, starLayerY[i]);
                starsDrawable.setBounds(0, 0, screenWidth, screenHeight);
                starsDrawable.draw(canvas);
                canvas.restore();
            }
        }

        // Dibujar nebulosas
        if (nebulaDrawable != null) {
            canvas.save();
            canvas.translate(0, nebulaY);
            nebulaDrawable.setBounds(0, 0, screenWidth, screenHeight);
            nebulaDrawable.setAlpha(100);
            nebulaDrawable.draw(canvas);
            canvas.restore();
        }
    }
}