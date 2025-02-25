package com.example.final_proyect.juego2;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.final_proyect.DatabaseHelper;

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
    private static final int DIFICULTY_INCREASE_INTERVAL = 15;
    private static final float SPEED_INCREASE_FACTOR = 1.2f;
    private int maxFallingObjects = 5;
    private float currentSpeedMultiplier = 1.0f;
    private int lastDifficultyIncrease = 0;
    private boolean isStartScreen = true;
    private Paint titlePaint;
    private Paint descriptionPaint;
    private RectF startButton;
    private String gameDescription = "Your game description here";
    private int maxScore;

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
        SharedPreferences preferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String username = preferences.getString("current_username", "");
        if (!username.isEmpty()) {
            DatabaseHelper dbHelper = new DatabaseHelper(context);
            maxScore = dbHelper.getMaxScoreGame2(username);
        }
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

        titlePaint = new Paint();
        titlePaint.setColor(Color.WHITE);
        titlePaint.setTextSize(140);
        titlePaint.setTextAlign(Paint.Align.CENTER);
        titlePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

        descriptionPaint = new Paint();
        descriptionPaint.setColor(Color.WHITE);
        descriptionPaint.setTextSize(40);
        descriptionPaint.setTextAlign(Paint.Align.CENTER);
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
        startButton = new RectF(
                width / 2 -200,
                height / 2 +150,
                width / 2 + 200,
                height / 2 + 250
        );
        spaceBackground = new SpaceBackground(getContext(), width, height);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (canvas != null) {
            // Dibujar el fondo espacial
            spaceBackground.draw(canvas);

            if (isStartScreen) {
                canvas.drawRect(0, 0, getWidth(), getHeight(), overlayPaint);

                canvas.drawText("GALACTA", getWidth() / 2, getHeight() / 2 - 300, titlePaint);

                String[] lines = gameDescription.split("\n");
                float y = getHeight() / 2 - 100;
                for (String line : lines) {
                    canvas.drawText(line, getWidth() / 2, y, descriptionPaint);
                    y += 50;
                }

                canvas.drawText("Max Score: " + maxScore, getWidth() / 2, y + 50, descriptionPaint);

                canvas.drawRoundRect(startButton, 20, 20, buttonPaint);
                canvas.drawText("START GAME", startButton.centerX(), startButton.centerY() + 20, buttonTextPaint);
            } else if (isGameOver) {
                // Dibujar overlay semi-transparente
                canvas.drawRect(0, 0, getWidth(), getHeight(), overlayPaint);

                // Dibujar texto de GAME OVER
                canvas.drawText("GAME OVER", getWidth() / 2, getHeight() / 2 - 100, gameOverPaint);
                canvas.drawText("Score: " + score, getWidth() / 2, getHeight() / 2, gameOverPaint);

                // Dibujar botón de retry
                canvas.drawRoundRect(retryButton, 20, 20, buttonPaint);
                canvas.drawText("RETRY", retryButton.centerX(), retryButton.centerY() + 20, buttonTextPaint);
            } else {
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
            }
        }
    }

    public void update() {
        if (!isGameOver) {
            spaceBackground.update();
            player.update(sensorX);

            if (score > 0 && score % DIFICULTY_INCREASE_INTERVAL == 0 && score != lastDifficultyIncrease) {
                increaseDifficulty();
                lastDifficultyIncrease = score;
            }

            if (fallingObjects.size() < maxFallingObjects) {
                fallingObjects.add(new FallingObject(getContext(), getWidth(), getHeight(), currentSpeedMultiplier));
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

        // En el método update() de GameView.java
        if (lives <= 0) {
            isGameOver = true;
            scoreHandler.removeCallbacks(scoreRunnable);

            // Guardar puntuación en la base de datos
            saveScoreToDatabase(score);
        }
    }

    // Añadir este nuevo método en la clase GameView
    private void saveScoreToDatabase(int score) {
        // Obtener el nombre de usuario de las preferencias o de donde lo tengas guardado
        SharedPreferences preferences = getContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String username = preferences.getString("current_username", "");

        if (!username.isEmpty()) {
            DatabaseHelper dbHelper = new DatabaseHelper(getContext());
            dbHelper.updateMaxScoreGame2(username, score);
        }
    }

    private void increaseDifficulty() {
        maxFallingObjects += 2;
        currentSpeedMultiplier *= SPEED_INCREASE_FACTOR;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            float x = event.getX();
            float y = event.getY();

            if (isStartScreen && startButton.contains(x, y)) {
                isStartScreen = false;
                scoreHandler.post(scoreRunnable);
                return true;
            } else if (isGameOver && retryButton.contains(x, y)) {
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
        isStartScreen = false;
        fallingObjects.clear();
        player = new Player(getContext());
        maxFallingObjects = 5;
        currentSpeedMultiplier = 1.0f;
        lastDifficultyIncrease = 0;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            sensorX = event.values[0];
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    public void setMaxScore(int score) {
        this.maxScore = score;
    }
}