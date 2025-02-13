package com.example.final_proyect;

import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

public class SplashActivity extends AppCompatActivity {

    private ImageView splashImageView;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        splashImageView = findViewById(R.id.splashImageView);

        // Cargar el primer GIF
        CustomTarget<Drawable> imageViewTarget = new CustomTarget<Drawable>() {
            @Override
            public void onResourceReady(Drawable resource, @Nullable Transition<? super Drawable> transition) {
                splashImageView.setImageDrawable(resource);
                if (resource instanceof Animatable) {
                    ((Animatable) resource).start();
                }

                // Duración del primer GIF (ajústalo según la duración real)
                int duration = 2500; // 3 segundos aprox
                handler.postDelayed(() -> showSecondGif(), duration);
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {
                // Handle cleanup if needed
            }
        };

        Glide.with(this)
                .load(R.raw.graffiti)
                .into(imageViewTarget);
    }

    private void fadeOutAndShowSecondGif() {
        // Animación de difuminación
        AlphaAnimation fadeOut = new AlphaAnimation(1.0f, 0.0f);
        fadeOut.setDuration(500); // 1 segundo
        fadeOut.setFillAfter(true);

        fadeOut.setAnimationListener(new android.view.animation.Animation.AnimationListener() {
            @Override
            public void onAnimationStart(android.view.animation.Animation animation) {}

            @Override
            public void onAnimationEnd(android.view.animation.Animation animation) {
                showSecondGif();
            }

            @Override
            public void onAnimationRepeat(android.view.animation.Animation animation) {}
        });

        splashImageView.startAnimation(fadeOut);
    }

    private void showSecondGif() {
        CustomTarget<Drawable> imageViewTarget = new CustomTarget<Drawable>() {
            @Override
            public void onResourceReady(Drawable resource, @Nullable Transition<? super Drawable> transition) {
                splashImageView.setImageDrawable(resource);
                if (resource instanceof Animatable) {
                    ((Animatable) resource).start();
                }

                // Duración del segundo GIF (ajústalo según la duración real)
                int duration = 7300; // 3 segundos aprox
                handler.postDelayed(() -> {
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    finish();
                }, duration);
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {
                // Handle cleanup if needed
            }
        };

        Glide.with(this)
                .load(R.raw.loading)
                .into(imageViewTarget);
    }
}