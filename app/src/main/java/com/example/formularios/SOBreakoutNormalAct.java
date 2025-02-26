package com.example.formularios;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class SOBreakoutNormalAct extends AppCompatActivity {
    private BreakoutNormalActivity breakoutNormalActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_breakoutnormal);


        // Get username from intent
        String username = getIntent().getStringExtra("current_username");
        if (username == null) {
            username = "Guest"; // Default value
        }

        // Save username to SharedPreferences
        SharedPreferences prefs = getSharedPreferences("GamePrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("current_username", username);
        editor.apply();

// También necesitarás una referencia a tu vista del juego:
        BreakoutNormalActivity gameView = findViewById(R.id.gameView);

        // Referencia al botón de pausa
        ImageButton pauseButton = findViewById(R.id.pause_button);

        // Listener para el botón de pausa
        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                breakoutNormalActivity.togglePausa(); // Llama al método que pausa o reanuda el juego
            }
        });

        // Referencia al botón de nuevo juego
        Button newGameButton = findViewById(R.id.newgame_button);
        breakoutNormalActivity = findViewById(R.id.gameView); // Asegúrate de que este ID coincide con el XML

        // Listener para el botón de nuevo juego
        newGameButton.setOnClickListener(view -> {
            // Pausa el juego
            breakoutNormalActivity.togglePausaSinTexto();

            // Construye el AlertDialog
            new AlertDialog.Builder(this)
                    .setTitle("NEW GAME")
                    .setMessage("Are you sure you want to start a new game? All progress will be lost.")
                    .setCancelable(false) // Evita cerrar el diálogo fuera de los botones
                    .setNegativeButton("Cancel", (dialog, which) -> {
                        // Reanuda el juego si el jugador cancela
                        breakoutNormalActivity.togglePausa();
                    })
                    .setPositiveButton("Start New Game", (dialog, which) -> {
                        // Reinicia el juego
                        breakoutNormalActivity.nuevoJuego();
                    })
                    .show();
        });

        // Referencia al botón de información
        ImageButton infoButton = findViewById(R.id.info_button);
        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Una vez clicas aparece MessageBox
                showInfoDialog1();
                breakoutNormalActivity.togglePausaSinTexto();
            }
        });
    }

    // Método que muestra el primer diálogo del tutorial
    private void showInfoDialog1() {
        new AlertDialog.Builder(SOBreakoutNormalAct.this)
                .setTitle("Tutorial")
                .setMessage("There are bricks at the top of the screen and your mission is to break them.")
                .setPositiveButton("Next", (dialog, which) -> showInfoDialog2())
                .setNegativeButton("Skip", (dialog, which) -> breakoutNormalActivity.togglePausa())
                .setCancelable(false)
                .show();
    }

    // Método que muestra el segundo diálogo del tutorial
    private void showInfoDialog2() {
        new AlertDialog.Builder(SOBreakoutNormalAct.this)
                .setTitle("Tutorial")
                .setMessage("Move the paddle at the bottom of the screen. With the paddle, hit the ball against the wall.")
                .setPositiveButton("Next", (dialog, which) -> showInfoDialog3())
                .setNegativeButton("Skip", (dialog, which) -> breakoutNormalActivity.togglePausa())
                .setCancelable(false)
                .show();
    }

    // Método que muestra el tercer diálogo del tutorial
    private void showInfoDialog3() {
        new AlertDialog.Builder(SOBreakoutNormalAct.this)
                .setTitle("If you destroy all the bricks you win!")
                .setMessage("If the ball touches the bottom of the screen you lose!")
                .setPositiveButton("Play", (dialog, which) -> breakoutNormalActivity.togglePausa())
                .setCancelable(false)
                .show();
    }
}