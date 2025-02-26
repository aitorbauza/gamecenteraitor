package com.example.formularios;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class GamesActivity extends AppCompatActivity {

    private ImageButton buttonGameOne;
    private ImageButton buttonGameTwo;
    private ImageButton buttonStats;
    private ImageButton buttonExitGame;
    private FloatingActionButton fabUsers;
    private DBHelper dbHelper;
    private String username;
    private String currentUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_games);

        // Obtener el nombre de usuario del intent
        username = getIntent().getStringExtra("username");

        // Inicializar la base de datos
        dbHelper = new DBHelper(this);

        // Encontrar los elementos de la interfaz
        buttonGameOne = findViewById(R.id.buttonGameOne);
        buttonGameTwo = findViewById(R.id.buttonGameTwo);
        buttonStats = findViewById(R.id.buttonStats);
        buttonExitGame = findViewById(R.id.buttonExitGame);
        fabUsers = findViewById(R.id.fabUsers);

        currentUsername = getIntent().getStringExtra("current_username");

        // Configurar el título con el nombre del usuario
        TextView textViewWelcome = findViewById(R.id.textViewWelcome);
        textViewWelcome.setText("¡WELCOME, " + username.toUpperCase() + "!");

        // Configurar los botones de juego
        buttonGameOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GamesActivity.this, Levels2048Activity.class);
                intent.putExtra("current_username", username);
                startActivity(intent);
                // Aquí iría la lógica para iniciar el juego 1
                Toast.makeText(GamesActivity.this, "¡2048!",
                        Toast.LENGTH_SHORT).show();
            }
        });

        buttonGameTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GamesActivity.this, LevelsBreakoutActivity.class);
                intent.putExtra("current_username", username);
                startActivity(intent);
                // Aquí iría la lógica para iniciar el juego 2
                Toast.makeText(GamesActivity.this, "¡BREAKOUT!",
                        Toast.LENGTH_SHORT).show();
            }
        });

        buttonStats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GamesActivity.this, StatsActivity.class);
                intent.putExtra("username", currentUsername);
                startActivity(intent);
                // Aquí iría la lógica para iniciar el juego 1
                Toast.makeText(GamesActivity.this, "¡STATS!",
                        Toast.LENGTH_SHORT).show();

            }
        });

        buttonExitGame.setOnClickListener(v -> {
            Toast.makeText(GamesActivity.this, "Saliendo del juego...", Toast.LENGTH_SHORT).show();
            finishAffinity(); // Cierra todas las actividades y sale de la app
        });

        // Configurar el FAB para mostrar usuarios que han iniciado sesión
        fabUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Abrir la nueva actividad de usuarios conectados
                Intent intent = new Intent(GamesActivity.this, UsersActivity.class);
                intent.putExtra("current_username", username);
                startActivity(intent);
            }
        });
    }
}