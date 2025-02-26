package com.example.formularios;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class Normal2048Activity extends AppCompatActivity {

    private GestureDetector gestureDetector; // Detecta los gestos dentro del tablero

    private GridLayout gridLayout; // Variable unida al xml del tablero (activity_main.xml)

    private TextView[][] celdas; // Celdas del tablero

    private int[][] board; // Muestra los valores del tablero

    private final int SIZE = 4; // Define el tamaño del tablero (4x4).

    private TextView scoreTextView; // TextView para mostrar la puntuación

    private int score = 0; // Puntuación del jugador

    private TextView bestTextView; // TextView para mostrar la mejor puntuación

    private int best = 0; // Mejor puntuación inicializada en 0

    private int[][] ultimoEstado; // Almacena el estado previo del tablero

    private int ultimoScore; // Almacena la puntuación previa
    private DBHelper dbHelper; // Instancia de la base de datos
    private String currentUsername; // Para almacenar el nombre del usuario actual

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_normal2048);

        dbHelper = new DBHelper(this);

        // Obtener el nombre de usuario de la actividad anterior
        currentUsername = getIntent().getStringExtra("current_username");
        if (currentUsername == null) {
            currentUsername = "Guest"; // Valor por defecto si no hay usuario
        }

        // Recuperar la mejor puntuación del usuario de la base de datos
        best = dbHelper.getBestScore2048(currentUsername);

        gridLayout = findViewById(R.id.tablero); // Se enlaza con el tablero del XML
        gestureDetector = new GestureDetector(this, new GestureListener()); // Se crea un detector de gestos

        scoreTextView = findViewById(R.id.score); // Se enlaza con el TextView de la puntuación en el XML
        bestTextView = findViewById(R.id.best); // Se enlaza con el TextView de la mejor puntuación en el XML

        // Inicializamos el tablero 4x4
        celdas = new TextView[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                int id = getResources().getIdentifier("celda" + i + j, "id", getPackageName());
                celdas[i][j] = findViewById(id);
            }
        }

        // Inicializamos las fichas
        board = new int[SIZE][SIZE];

        // Inicializamos el tablero previo
        ultimoEstado = new int[SIZE][SIZE]; // Creamos el tablero para almacenar el estado previo

        // Colocamos dos fichas iniciales en el tablero
        positionRandom();
        positionRandom();

        updateBoard();

        updatebest();

        // Configuramos el touch listener
        gridLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                return true;
            }
        });

        Button newGameButton = findViewById(R.id.newgame_button); // Se enlaza con el botón NEW GAME del XML

        // Botón NEW GAME
        newGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Una vez clicas aparece MessageBox
                showNewGameDialog();
            }
        });

        Button returnPlayButton = findViewById(R.id.returnplay_button); // Se enlaza con el botón RETURN PLAY del XML

        // Botón REUTURN PLAY
        returnPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Una vez clicas aparece MessageBox
                showReturnPlayDialog();
            }
        });

        ImageButton infoButton = findViewById(R.id.info_button); // Se enlaza con el botón INFO del XML

        // Botón Info
        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Una vez clicas aparece MessageBox
                showInfoDialog1();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Guardar la puntuación actual si es mejor que la anterior
        if (score > dbHelper.getBestScore2048(currentUsername)) {
            dbHelper.saveScore2048(currentUsername, score);
        }
    }


    // Método para posicionar una ficha en una celda aleatoria
    private void positionRandom() {
        Random random = new Random();
        int x, y;

        // Busca una celda vacía aleatoria
        do {
            x = random.nextInt(SIZE); // Genera una fila aleatoria
            y = random.nextInt(SIZE); // Genera una columna aleatoria
        } while (board[x][y] != 0); // Si la celda no está vacía, genera otra

        // Habrá un 10% de que pueda aparecer una ficha con el valor 4
        board[x][y] = random.nextInt(100) < 10 ? 4 : 2;
    }

    // Método para verificar si hay celdas vacías en el tablero
    private boolean hayCeldasVacias() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (board[i][j] == 0) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean hayGameOver() {
        if (hayCeldasVacias()) return false; // Si hay celdas vacías, se sigue jugando

        // Se verifican combinaciones posibles en filas y columnas.
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                // Si en la misma fila hay combinaciones posibles devuelve false, es decir, que aun no se ha perdido
                if (j < SIZE - 1 && board[i][j] == board[i][j + 1]) return false;
                // Igual que el anterior if pero se buscan combinaciones en una misma columna
                if (i < SIZE - 1 && board[i][j] == board[i + 1][j]) return false;
            }
        }
        return true; // Devuelve true cuando no hay celdas vacías ni movimientos posibles.
    }

    // Método para verificar si el jugador ha ganado
    private boolean hayVictoria() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (board[i][j] == 2048) { // Si se encuentra la ficha con valor 2048
                    return true;
                }
            }
        }
        return false;
    }

    // Método para mostrar el diálogo de victoria
    private void victoriaDialog() {
        // Crear y mostrar un AlertDialog para la victoria
        new AlertDialog.Builder(Normal2048Activity.this)
                .setTitle("CONGRATULATIONS!") // Título
                .setMessage("You have reached the 2048 tile. Do you want to continue playing?") // Subtítulo
                .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss(); // Si pulsa en "Continue" se cierra el diálogo
                    }
                })
                .setNegativeButton("New Game", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        restartGame(); // Si pulsa "new Game" se reinicia la partida
                    }
                })
                .setCancelable(false) // El diálogo no se cierra tocando fuera
                .show();
    }




    // Configuramos el detector de gestos
    private class GestureListener extends GestureDetector.SimpleOnGestureListener {

        private static final int MIN_SWIPE_DISTANCE = 30; // Distancia mínima para detectar un swipe
        private static final int MIN_VELOCITY_DISTANCE = 30; // Velocidad mínima para detectar un swipe

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            float diffX = e2.getX() - e1.getX();
            float diffY = e2.getY() - e1.getY();

            boolean moved = false; // No se ha hecho ningún movimiento
            guardarEstado(); // Se guarda el estado actual antes de realizar un movimiento

            if (Math.abs(diffX) > Math.abs(diffY)) {
                // Swipe horizontal
                if (Math.abs(diffX) > MIN_SWIPE_DISTANCE && Math.abs(velocityX) > MIN_VELOCITY_DISTANCE) {
                    if (diffX > 0) {
                        moved = moveRight();
                    } else {
                        moved = moveLeft();
                    }
                }
            } else {
                // Swipe vertical
                if (Math.abs(diffY) > MIN_SWIPE_DISTANCE && Math.abs(velocityY) > MIN_VELOCITY_DISTANCE) {
                    if (diffY > 0) {
                        moved = moveDown();
                    } else {
                        moved = moveUp();
                    }
                }
            }

            if (moved && hayCeldasVacias()) { // Si ha habido un movimiento y hay celdas vacías
                positionRandom(); // Se genera una nueva ficha
            }

            updateBoard();
            return true;
        }
    }

    // Métodos de movimientos con combinación de fichas

    private boolean moveLeft() {
        boolean moved = false;
        for (int i = 0; i < SIZE; i++) {
            for (int j = 1; j < SIZE; j++) {
                if (board[i][j] != 0) {
                    int k = j;
                    while (k > 0 && board[i][k - 1] == 0) {
                        // Mover ficha a la izquierda si la celda está vacía
                        board[i][k - 1] = board[i][k];
                        board[i][k] = 0;
                        k--;
                        moved = true;
                    } // Si dos fichas tienen el mismo valor
                    if (k > 0 && board[i][k - 1] == board[i][k]) {
                        // Se combinan
                        board[i][k - 1] *= 2;
                        board[i][k] = 0;
                        score += board[i][k - 1]; // Y se aumenta la puntuación
                        moved = true;
                    }
                }
            }
        }
        return moved;
    }

    private boolean moveRight() {
        boolean moved = false;
        for (int i = 0; i < SIZE; i++) {
            for (int j = SIZE - 2; j >= 0; j--) {
                if (board[i][j] != 0) {
                    int k = j;
                    while (k < SIZE - 1 && board[i][k + 1] == 0) {
                        // Mover ficha a la derecha si la celda está vacía
                        board[i][k + 1] = board[i][k];
                        board[i][k] = 0;
                        k++;
                        moved = true;
                    }
                    if (k < SIZE - 1 && board[i][k + 1] == board[i][k]) {
                        // Combinación de fichas si tienen el mismo valor
                        board[i][k + 1] *= 2;
                        board[i][k] = 0;
                        score += board[i][k + 1]; // Se aumenta la puntuación
                        moved = true;
                    }
                }
            }
        }
        return moved;
    }

    private boolean moveUp() {
        boolean moved = false;
        for (int j = 0; j < SIZE; j++) {
            for (int i = 1; i < SIZE; i++) {
                if (board[i][j] != 0) {
                    int k = i;
                    while (k > 0 && board[k - 1][j] == 0) {
                        // Mover ficha hacia arriba si la celda está vacía
                        board[k - 1][j] = board[k][j];
                        board[k][j] = 0;
                        k--;
                        moved = true;
                    }
                    if (k > 0 && board[k - 1][j] == board[k][j]) {
                        // Combinar fichas si tienen el mismo valor
                        board[k - 1][j] *= 2;
                        board[k][j] = 0;
                        score += board[k - 1][j]; // Se aumenta la puntuación
                        moved = true;
                    }
                }
            }
        }
        return moved;
    }

    private boolean moveDown() {
        boolean moved = false;
        for (int j = 0; j < SIZE; j++) {
            for (int i = SIZE - 2; i >= 0; i--) {
                if (board[i][j] != 0) {
                    int k = i;
                    while (k < SIZE - 1 && board[k + 1][j] == 0) {
                        // Mover ficha hacia abajo si la celda está vacía
                        board[k + 1][j] = board[k][j];
                        board[k][j] = 0;
                        k++;
                        moved = true;
                    }
                    if (k < SIZE - 1 && board[k + 1][j] == board[k][j]) {
                        // Combinar fichas si tienen el mismo valor
                        board[k + 1][j] *= 2;
                        board[k][j] = 0;
                        score += board[k + 1][j]; // Se aumenta la puntuación
                        moved = true;
                    }
                }
            }
        }
        return moved;
    }

    // Método que actualiza el tablero
    private void updateBoard() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                TextView cell = celdas[i][j];
                int value = board[i][j];
                if (value == 0) { // Si no hay ficha en la celda
                    cell.setText(""); // Se deja vacío
                    cell.setBackgroundResource(R.drawable.borde_celda); // Fondo estándar del tablero
                } else {
                    cell.setText(String.valueOf(value)); // Muestra el valor de la ficha
                    switch (value) {
                        case 2:
                            cell.setBackgroundResource(R.drawable.ficha_color2);
                            cell.setTextColor(getResources().getColor(android.R.color.white)); // Blanco
                            break;
                        case 4:
                            cell.setBackgroundResource(R.drawable.ficha_color4);
                            cell.setTextColor(getResources().getColor(android.R.color.white)); // Blanco
                            break;
                        case 8:
                            cell.setBackgroundResource(R.drawable.ficha_color8);
                            cell.setTextColor(getResources().getColor(android.R.color.white)); // Blanco
                            break;
                        case 16:
                            cell.setBackgroundResource(R.drawable.ficha_color16);
                            cell.setTextColor(getResources().getColor(android.R.color.white)); // Blanco
                            break;
                        case 32:
                            cell.setBackgroundResource(R.drawable.ficha_color32);
                            cell.setTextColor(getResources().getColor(android.R.color.white)); // Blanco
                            break;
                        case 64:
                            cell.setBackgroundResource(R.drawable.ficha_color64);
                            cell.setTextColor(getResources().getColor(android.R.color.white)); // Blanco
                            break;
                        case 128:
                            cell.setBackgroundResource(R.drawable.ficha_color128);
                            cell.setTextColor(getResources().getColor(android.R.color.black)); // Blanco
                            break;
                        case 256:
                            cell.setBackgroundResource(R.drawable.ficha_color256);
                            cell.setTextColor(getResources().getColor(android.R.color.white)); // Blanco
                            break;
                        case 512:
                            cell.setBackgroundResource(R.drawable.ficha_color512);
                            cell.setTextColor(getResources().getColor(android.R.color.white)); // Blanco
                            break;
                        case 1024:
                            cell.setBackgroundResource(R.drawable.ficha_color1024);
                            cell.setTextColor(getResources().getColor(android.R.color.white)); // Blanco
                            break;
                        case 2048:
                            cell.setBackgroundResource(R.drawable.ficha_color2048);
                            cell.setTextColor(getResources().getColor(android.R.color.white)); // Blanco
                            break;
                    }
                }
            }

            if (score > best) {
                best = score; // Actualizar la mejor puntuación
                updatebest(); // Reflejar el cambio en el TextView

                // Guardar la nueva puntuación en la base de datos
                dbHelper.saveScore2048(currentUsername, score);
            }


        }

        // Se actualiza el contador de puntuación
        scoreTextView.setText("SCORE \n " + score);

        // Verifica si el juego ha terminado
        if (hayGameOver()) {
            showGameOverDialog(); // Muestra el Alertdialog de "Game Over"
        }

        // Comprobar si el jugador ha ganado
        if (hayVictoria()) {
            victoriaDialog();
        }
    }

    // Método que muestra el diálogo de NEW GAME
    private void showNewGameDialog() {
        //AlertDialog
        new AlertDialog.Builder(Normal2048Activity.this)
                .setTitle("NEW GAME") // Título
                .setMessage("Are you sure you want to start a new game? All progress will be lost.") // Mensaje
                .setPositiveButton("Start New Game", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        restartGame(); // Se reinicia el juego si el usuario confirma
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss(); // Se cierra el alertdialog si el usuario cancela
                    }
                })
                .setCancelable(false) // Evita que el diálogo se cierre haciendo clic fuera
                .show();
    }

    // Método que muestra el primer diálogo del tutorial
    private void showInfoDialog1() {
        //AlertDialog
        new AlertDialog.Builder(Normal2048Activity.this)
                .setTitle("Tutorial") // Título
                .setMessage("Swipe to move the squares.") // Mensaje
                .setPositiveButton("Next", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showInfoDialog2(); // Se pasa al siguiente alertdialog del tutorial si el usuario pulsa "next"
                    }
                })
                .setNegativeButton("Skip", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss(); // Se cierra el alertdialog si el usuario pulsa "skip"
                    }
                })
                .setCancelable(false) // Evita que el diálogo se cierre haciendo clic fuera
                .show();
    }

    // Método que muestra el segundo diálogo del tutorial
    private void showInfoDialog2() {
        //AlertDialog
        new AlertDialog.Builder(Normal2048Activity.this)
                .setTitle("Tutorial") // Título
                .setMessage("When two squares with the same number touch, they merge!") // Mensaje
                .setPositiveButton("Next", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showInfoDialog3(); // Se pasa al siguiente alertdialog del tutorial si el usuario pulsa "next"
                    }
                })
                .setNegativeButton("Skip", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss(); // Se cierra el alertdialog si el usuario pulsa "skip"
                    }
                })
                .setCancelable(false) // Evita que el diálogo se cierre haciendo clic fuera
                .show();
    }

    // Método que muestra el tercer diálogo del tutorial
    private void showInfoDialog3() {
        //AlertDialog
        new AlertDialog.Builder(Normal2048Activity.this)
                .setTitle("The objective is to get square 2048!") // Título
                .setMessage("The game is lost when the board is full...") // Mensaje
                .setPositiveButton("Play", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setCancelable(false) // Evita que el diálogo se cierre haciendo clic fuera
                .show();
    }

    // Método para guardar el estado actual del tablero y de la puntuación
    private void guardarEstado() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                ultimoEstado[i][j] = board[i][j]; // Se copia el estado actual del tablero
            }
        }
        ultimoScore = score; // Y se guarda la puntuación actual
    }

    // Método para retornar al estado previo
    private void retornarAnteriorEstado() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                board[i][j] = ultimoEstado[i][j]; // Se restaura el estado del tablero
            }
        }
        score = ultimoScore; // Y se restaura la puntuación previa
        scoreTextView.setText("SCORE \n" + score); // Se actualiza el texto de la puntuación
        updateBoard(); // Se actualizan las fichas del tablero
    }

    // Método que muestra el Alertdialog al clicar en el botón RETURN PLAY
    private void showReturnPlayDialog() {
        new AlertDialog.Builder(Normal2048Activity.this)
                .setTitle("RETURN PLAY") // Título
                .setMessage("Are you sure you want to return one move?") // Subtítulo
                .setPositiveButton("Return Play", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        retornarAnteriorEstado(); // Si clica en "Return Play" se restaura al estado previo del tablero
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss(); // Si clica en "cancel" se cierra el Alertdialog
                    }
                })
                .setCancelable(false) // Evita que se cierre al tocar fuera de Alertdialog
                .show();
    }

    // Método que muestra el Alertdialog al perder la partida
    private void showGameOverDialog() {
        new AlertDialog.Builder(Normal2048Activity.this)
                .setTitle("GAME OVER") // Título
                .setMessage("You have lost.") // Subtítulo
                .setPositiveButton("Start New Game", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        restartGame(); // Si clica en "Start New Game" se reinicia la partida
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss(); // Si clica en "cancel" no ocurre nada
                    }
                })
                .setCancelable(false) // Evita que el diálogo se cierre al tocar fuera
                .show();
    }

    // Método para reiniciar el juego
    private void restartGame() {

        if (score > best) {
            best = score;
            // Guardar la puntuación en la base de datos
            dbHelper.saveScore2048(currentUsername, score);
        }

        score = 0; // Se reinicia la puntuación
        scoreTextView.setText("SCORE \n " + score);

        // Se reinicia el tablero
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                board[i][j] = 0;
            }
        }

        // Se colocan dos fichas de forma random
        positionRandom();
        positionRandom();

        updateBoard(); // Se actualiza el tablero
    }

    private void updatebest() {
        bestTextView.setText("BEST\n" + best);
    }

    private void openLeaderboard() {
        Intent intent = new Intent(Normal2048Activity.this, StatsActivity.class);
        startActivity(intent);
    }
}