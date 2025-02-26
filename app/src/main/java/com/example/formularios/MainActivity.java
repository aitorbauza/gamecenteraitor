package com.example.formularios;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Referencias a las vistas
        ImageView logoImage = findViewById(R.id.logoImage);
        TextView loadingText = findViewById(R.id.loadingText);

        // Cargar animaciones
        Animation rotateAnim = AnimationUtils.loadAnimation(this, R.anim.rotate);
        Animation blinkAnim = AnimationUtils.loadAnimation(this, R.anim.blink);

        // Aplicar animaciones
        logoImage.startAnimation(rotateAnim);
        loadingText.startAnimation(blinkAnim);

        // Navegar a la siguiente pantalla después de 5 segundos
        new Handler().postDelayed(() -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }, 5000);
    }
}