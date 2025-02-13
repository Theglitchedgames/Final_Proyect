package com.example.final_proyect.juego2;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.List;

public class GameView extends SurfaceView implements SurfaceHolder.Callback, SensorEventListener {
    private GameThread gameThread;
    private Player player;
    private List<FallingObject> fallingObjects;
    private int score = 0;
    private int lives = 3;
    private Handler scoreHandler;
    private Runnable scoreRunnable;
    private SensorManager sensorManager;
    private float sensorX;
    private boolean isGameOver = false;
    private Paint gameOverPaint;
    private Paint overlayPaint;
    private RectF retryButton;
    private Paint buttonPaint;
    private Paint buttonTextPaint;
    private SpaceBackground spaceBackground;

    public GameView(Context context) {
        super(context);
        getHolder().addCallback(this);
        initializeGame(context);
        setupPaints();
    }

    private void initializeGame(Context context) {
        player = new Player(context);
        fallingObjects = new ArrayList<>();
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);
        gameThread = new GameThread(getHolder(), this);
        scoreHandler = new Handler();
        scoreRunnable = () -> {
            if (!isGameOver) {
                score++;
                scoreHandler.postDelayed(scoreRunnable, 1000);
            }
        };
        scoreHandler.post(scoreRunnable);
    }

    private void setupPaints() {
        gameOverPaint = new Paint();
        gameOverPaint.setColor(Color.WHITE);
        gameOverPaint.setTextSize(120);
        gameOverPaint.setTextAlign(Paint.Align.CENTER);

        overlayPaint = new Paint();
        overlayPaint.setColor(Color.BLACK);
        overlayPaint.setAlpha(180);  // Semi-transparente

        buttonPaint = new Paint();
        buttonPaint.setColor(Color.BLUE);

        buttonTextPaint = new Paint();
        buttonTextPaint.setColor(Color.WHITE);
        buttonTextPaint.setTextSize(60);
        buttonTextPaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        spaceBackground = new SpaceBackground(getContext(), getWidth(), getHeight());

        if (gameThread == null || !gameThread.isAlive()) { // Evitar múltiples hilos
            gameThread = new GameThread(getHolder(), this);
            gameThread.setRunning(true);
            System.out.println("Iniciando GameThread...");
            gameThread.start();
        }
    }


    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        gameThread.setRunning(false);
        while (retry) {
            try {
                gameThread.join();
                retry = false;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        sensorManager.unregisterListener(this);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // Crear el rectángulo del botón cuando conocemos el tamaño de la superficie
        retryButton = new RectF(
                width / 2 - 200,
                height / 2 + 100,
                width / 2 + 200,
                height / 2 + 200
        );
        spaceBackground = new SpaceBackground(getContext(), width, height);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (canvas != null) {
            // Dibujar el fondo espacial
            spaceBackground.draw(canvas);

            // Resto del código de dibujo existente
            player.draw(canvas);
            for (FallingObject obj : fallingObjects) {
                obj.draw(canvas);
            }

            // Dibujar score y vidas
            Paint paint = new Paint();
            paint.setColor(Color.WHITE);
            paint.setTextSize(50);
            canvas.drawText("Score: " + score, getWidth() - 300, 100, paint);
            canvas.drawText("Lives: " + lives, getWidth() - 300, 160, paint);

            // Si es game over, dibujar la overlay y los elementos de game over
            if (isGameOver) {
                // Dibujar overlay semi-transparente
                canvas.drawRect(0, 0, getWidth(), getHeight(), overlayPaint);

                // Dibujar texto de GAME OVER
                canvas.drawText("GAME OVER", getWidth() / 2, getHeight() / 2 - 100, gameOverPaint);
                canvas.drawText("Score: " + score, getWidth() / 2, getHeight() / 2, gameOverPaint);

                // Dibujar botón de retry
                canvas.drawRoundRect(retryButton, 20, 20, buttonPaint);
                canvas.drawText("RETRY", retryButton.centerX(), retryButton.centerY() + 20, buttonTextPaint);
            }
        }
    }

    public void update() {
        if (!isGameOver) {
            spaceBackground.update();
            player.update(sensorX);
            if (fallingObjects.size() < 5) {
                fallingObjects.add(new FallingObject(getContext(), getWidth(), getHeight()));
            }
            List<FallingObject> toRemove = new ArrayList<>();
            for (FallingObject obj : fallingObjects) {
                obj.update();
                if (obj.collidesWith(player)) {
                    toRemove.add(obj);
                    lives--;
                }
                if (obj.getY() > getHeight()) {
                    toRemove.add(obj);
                }
            }
            fallingObjects.removeAll(toRemove);
            if (lives <= 0) {
                isGameOver = true;
                scoreHandler.removeCallbacks(scoreRunnable);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isGameOver && event.getAction() == MotionEvent.ACTION_DOWN) {
            float x = event.getX();
            float y = event.getY();

            if (retryButton.contains(x, y)) {
                resetGame();
                return true;
            }
        }
        return super.onTouchEvent(event);
    }

    private void resetGame() {
        score = 0;
        lives = 3;
        isGameOver = false;
        fallingObjects.clear();
        player = new Player(getContext());
        scoreHandler.post(scoreRunnable);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            sensorX = event.values[0];
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
}