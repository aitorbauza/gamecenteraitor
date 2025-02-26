package com.example.formularios;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class BreakoutHardActivity extends View {
    private int anchoPantalla, altoPantalla; // Tamaño pantalla
    private int anchoPaleta = 200, altoPaleta = 30; // Tamaño paleta
    private int paletaX; // Posición paleta
    private int pelotaX, pelotaY, radioPelota = 20; // Tamaño y posición de la pelota
    private int velocidPelotaX = 20, velocidPelotaY = -20; // Velocidad pelota
    private boolean isGameOver = false; // Indica si el juego ha terminado
    private boolean pelotaPegada = true; // Indica si la pelota está pegada a la paleta
    private Paint paint; // Sirve para pintar desde el java

    // Configuración del área de juego
    private int anchoAreaJuego; // Ancho área de juego
    private int altoAreaJuego;// Alto área de juego
    private int izqAreaJuego, topAreaJuego;// Posición área de juego
    private final int anchoLadrillo = 115; // Ancho ladrillos
    private final int altoLadrillo = 70; // Alto ladrillos

    private Ladrillo[][] ladrillos;  // Array bidimensional que almacena los ladrillos en el área de juego
    private int[] coloresFilas; // Array para los colores de las filas
    private int maxVelocidPelotaX = 45;  // Velocidad máxima de la pelota (horizontal)
    private int maxVelocidPelotaY = 45;  // Velocidad máxima de la pelota (vertical)

    // Nueva variable para la puntuación
    private int puntuacion = 0; // Almacena la puntuación actual del jugador

    private boolean estaPausado = false; // Indica si el juego está en pausa
    private String textoPausa = "THE GAME IS PAUSED"; // Texto que aparece cuando el juego está en pausa

    private long tiempoInicial; // Almacena el tiempo en el que inicia el contador
    private int segundosTranscurridos = 0; // Tiempo transcurrido en segundos
    private boolean contadorActivo = false; // Si el contador está activo

    private String username; // Para almacenar el nombre del usuario
    private DBHelper dbHelper; // Para acceder a la base de datos

    // Constructor para inflar desde XML
    public BreakoutHardActivity(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint(); // Creación objeto paint

        dbHelper = new DBHelper(context);

        // Obtener el username de las preferencias compartidas
        SharedPreferences prefs = context.getSharedPreferences("GamePrefs", Context.MODE_PRIVATE);
        username = prefs.getString("current_username", "Guest"); // "Guest" como valor predeterminado

        // Se inicializan los colores para cada fila de ladrillos
        coloresFilas = new int[]{Color.rgb(30, 30, 30),    // Gris carbón (más oscuro que el gris anterior)
                Color.rgb(192, 192, 192), // Plateado (Silver)
                Color.rgb(128, 0, 0),     // Burdeos (Burgundy)
                Color.rgb(218, 165, 32),  // Dorado oscuro (Goldenrod)
                Color.rgb(25, 25, 112)    // Azul medianoche (Midnight Blue)
        };

        iniciarLadrillos(); // Se crean los ladrillos
    }

    // Método que inicializa los ladrillos en el área de juego
    private void iniciarLadrillos() {
        int filas = 5;  // Número de filas de ladrillos
        int columnas = 7; // Número de columnas de ladrillos
        ladrillos = new Ladrillo[filas][columnas]; // Array bidimensional que define cuantas filas y columnas de ladrillos habrá

        // Posiciones de los ladrillos dentro del área de juego
        int inicioX = izqAreaJuego + (anchoAreaJuego - (anchoLadrillo * columnas + 10 * (columnas - 1))) / 2;
        int inicioY = topAreaJuego + 50;

        // Colocación de los ladrillos
        for (int fila = 0; fila < filas; fila++) {
            for (int col = 0; col < columnas; col++) {
                int x = inicioX + col * (anchoLadrillo + 10); // Colocación horizontal con espacio entre columnas
                int y = inicioY + fila * (altoLadrillo + 10); // Colocación vertical con espacio entre filas

                ladrillos[fila][col] = new Ladrillo(x, y); // Coloca los ladrillos en la pantalla y los almacena en el array bidimensional "ladrillos"
            }
        }
    }

    // Método que se encarga de ajustar la posición y tamaño de la pelota y paleta para que entren en el área de juego
    @Override
    protected void onSizeChanged(int ancho, int alto, int antigAncho, int antigAlto) {
        super.onSizeChanged(ancho, alto, antigAncho, antigAlto);
        anchoPantalla = ancho;
        altoPantalla = alto;

        // Configurar área de juego
        anchoAreaJuego = 900;
        altoAreaJuego = 1500;
        izqAreaJuego = (anchoPantalla - anchoAreaJuego) / 2;
        topAreaJuego = (altoPantalla - altoAreaJuego + 140) / 2;

        // Inicializar paleta y pelota
        paletaX = izqAreaJuego + anchoAreaJuego / 2 - anchoPaleta / 2;
        pelotaX = paletaX + anchoPaleta / 2;
        pelotaY = topAreaJuego + altoAreaJuego - altoPaleta - radioPelota;

        // Inicializar ladrillos
        iniciarLadrillos();
    }

    // Método que se utiliza para pintar el contenido del juego
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int margenHorizontal = 70;
        int margenSuperior = 150;  // Aumentado para bajarlo más
        int margenInferior = 10;  // Mantiene el mismo tamaño inferior

// Fondo negro más estrecho y más bajo
        Paint fondoPaint = new Paint();
        fondoPaint.setColor(Color.BLACK);
        canvas.drawRect(
                margenHorizontal,
                margenSuperior,
                getWidth() - margenHorizontal,
                getHeight() - margenInferior,
                fondoPaint
        );

// Borde dorado ajustado al nuevo tamaño
        Paint bordePaint = new Paint();
        bordePaint.setColor(Color.rgb(193, 163, 95)); // Color dorado
        bordePaint.setStyle(Paint.Style.STROKE);
        bordePaint.setStrokeWidth(10); // Grosor del borde
        canvas.drawRect(
                margenHorizontal,
                margenSuperior,
                getWidth() - margenHorizontal,
                getHeight() - margenInferior,
                bordePaint
        );

        // Dibujar ladrillos
        for (int fila = 0; fila < ladrillos.length; fila++) {
            for (int col = 0; col < ladrillos[fila].length; col++) {
                if (ladrillos[fila][col] != null && ladrillos[fila][col].isVisible()) {
                    paint.setColor(coloresFilas[fila % coloresFilas.length]);
                    canvas.drawRect(ladrillos[fila][col].getX(), ladrillos[fila][col].getY(),
                            ladrillos[fila][col].getX() + anchoLadrillo,
                            ladrillos[fila][col].getY() + altoLadrillo, paint);
                }
            }
        }

        // Dibujar paleta y pelota
        paint.setColor(Color.rgb(193, 163, 95));
        canvas.drawRect(paletaX, topAreaJuego + altoAreaJuego - altoPaleta, paletaX + anchoPaleta, topAreaJuego + altoAreaJuego, paint);
        paint.setColor(Color.WHITE);
        canvas.drawCircle(pelotaX, pelotaY, radioPelota, paint);

        // Dibujar puntuación
        paint.setColor(Color.WHITE);
        paint.setTextSize(50);
        paint.setTextAlign(Paint.Align.LEFT); // Asegura que el texto SCORE esté alineado a la izquierda
        canvas.drawText("SCORE: " + puntuacion, izqAreaJuego + 60, topAreaJuego - 95, paint);

        // Se dibuja el timer
        paint.setColor(Color.rgb(193, 163, 95));
        int minutos = segundosTranscurridos / 60;
        int segundos = segundosTranscurridos % 60;
        canvas.drawText(String.format("TIME: %02d:%02d", minutos, segundos), izqAreaJuego + 590, topAreaJuego - 95, paint);

        if (estaPausado) {  // Si el juego está pausado, se pinta esto
            paint.setColor(Color.WHITE);
            paint.setTextSize(60);
            paint.setTextAlign(Paint.Align.CENTER); // Alineación centrada para el texto de pausa
            canvas.drawText(textoPausa, anchoPantalla / 2, altoPantalla / 2, paint);
            return; // Y se pausan los movimientos y las colisiones
        }

        if (isGameOver) {
            paint.setTextSize(70);
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setColor(Color.rgb(193, 163, 95));
            canvas.drawText("GAME OVER", anchoPantalla / 2, altoPantalla / 2, paint);
        } else if (hayLadrillosDestruidos()) { // Si se han destruido los ladrilolos
            paint.setColor(Color.CYAN);
            mensajeGanar(); // Se pausa el juego y muestra el mensaje de victoria
        } else {
            // Actualizar el tiempo si el contador está activo
            if (contadorActivo) {
                segundosTranscurridos = (int) ((SystemClock.elapsedRealtime() - tiempoInicial) / 1000);
            }
            moverPelota();
            detectarColisiones();
            invalidate();
        }
    }

    // Método para iniciar el contador
    private void iniciarContador() {
        tiempoInicial = SystemClock.elapsedRealtime(); // Tiempo actual en milisegundos
        contadorActivo = true;
    }

    // Método para pausar el contador
    private void pausarContador() {
        contadorActivo = false; // Detenemos el contador
    }

    // Método para pausar o reanudar la partida
    public void togglePausa() {
        estaPausado = !estaPausado;

        if (estaPausado) {
            pausarContador();
        } else {
            tiempoInicial = SystemClock.elapsedRealtime() - (segundosTranscurridos * 1000);
            contadorActivo = true;
        }

        textoPausa = "THE GAME IS PAUSED";
        invalidate();
    }

    // Método para pausar o reanudar la partida
    public void togglePausaSinTexto() {
        estaPausado = !estaPausado;

        if (estaPausado) {
            pausarContador();
        } else {
            tiempoInicial = SystemClock.elapsedRealtime() - (segundosTranscurridos * 1000);
            contadorActivo = true;
        }

        textoPausa = " ";
        invalidate();
    }

    // Método que se encarga del movimiento de la pelota
    private void moverPelota() {
        if (pelotaPegada) { // Si la pelota está pegada a la paleta
            pelotaX = paletaX + anchoPaleta / 2;
            pelotaY = topAreaJuego + altoAreaJuego - altoPaleta - radioPelota;
            return; // No saldrá disparada
        }

        // Movimiento de la pelota hacia la izquierda o la derecha
        pelotaX += velocidPelotaX;
        pelotaY += velocidPelotaY;

        // Rebote con las paredes
        if (pelotaX <= izqAreaJuego || pelotaX >= izqAreaJuego + anchoAreaJuego) {
            velocidPelotaX = -velocidPelotaX; // Rebote en las paredes laterales
        }
        if (pelotaY <= topAreaJuego) {
            velocidPelotaY = -velocidPelotaY; // Rebote en la parte superior
        }

        // Si la pelota cae fuera de la pantalla
        if (pelotaY >= topAreaJuego + altoAreaJuego) {
            isGameOver = true; // Has perdido
            pausarContador(); // Aseguramos que el contador se detiene

            // Asegurarse de que el username está asignado correctamente
            SharedPreferences prefs = getContext().getSharedPreferences("GamePrefs", Context.MODE_PRIVATE);
            String actualUsername = prefs.getString("current_username", "Guest");

            // Guardar puntuación en la base de datos con el username correcto
            if (dbHelper != null && puntuacion > 0) { // Solo guardar si hay puntuación
                dbHelper.saveScoreBreakoutHard(actualUsername, puntuacion, segundosTranscurridos); // Cambiado para usar el método específico para nivel difícil
            }
        }

        // Rebote en la paleta
        if (pelotaY + radioPelota >= topAreaJuego + altoAreaJuego - altoPaleta &&
                pelotaX >= paletaX &&
                pelotaX <= paletaX + anchoPaleta) {
            velocidPelotaY = -velocidPelotaY; // Rebote con la paleta
        }
    }

    // Detecta colisiones entre la pelota y los ladrillos
    private void detectarColisiones() {
        for (int fila = 0; fila < ladrillos.length; fila++) { //Recorre las filas de los ladrillos
            for (int col = 0; col < ladrillos[fila].length; col++) { //Recorre las columnas de los ladrillos
                Ladrillo ladrillo = ladrillos[fila][col]; //La variable "ladrillo" tendrá asignado un ladrillo
                if (ladrillo != null && ladrillo.isVisible()) { //Si este ladrillo existe y esá visible
                    if (pelotaX + radioPelota > ladrillo.getX() && pelotaX - radioPelota < ladrillo.getX() + anchoLadrillo &&
                            pelotaY + radioPelota > ladrillo.getY() && pelotaY - radioPelota < ladrillo.getY() + altoLadrillo) { // Y si la pelota colisiona con el ladrillo
                        ladrillo.setVisible(false); // El ladrillo desaparece
                        velocidPelotaY = -velocidPelotaY; // Y rebotará la pelota
                        aumentoVelocidadPelota(); // Por cada colisión con un ladrillo se incremenntará la velocidad de la pelota
                        actualizarPuntuacion(fila);
                    }
                }
            }
        }
    }

    //Método que detecta cuando se han destruido todos los ladrillos
    private boolean hayLadrillosDestruidos() {
        for (int fila = 0; fila < ladrillos.length; fila++) { // Se recorren las filas
            for (int col = 0; col < ladrillos[fila].length; col++) { // Se recorren las columnas
                if (ladrillos[fila][col] != null && ladrillos[fila][col].isVisible()) { // Si quedan ladrillos
                    return false; // El jugador no ha ganado
                }
            }
        }
        return true; // Si todos los ladrillos están destruidos si ha ganado
    }

    //Método que se encarga de pausar el juego y mostrar el mensaje de victoria en caso de ganar
    private void mensajeGanar() {
        estaPausado = true; // Pausa el juego
        textoPausa = "¡HAS GANADO!"; // Muestra el mensaje de victoria
        pausarContador(); // Aseguramos que el contador se detiene

        // Asegurarse de que el username está asignado correctamente
        SharedPreferences prefs = getContext().getSharedPreferences("GamePrefs", Context.MODE_PRIVATE);
        String actualUsername = prefs.getString("current_username", "Guest");

        // Guardar puntuación en la base de datos con el username correcto
        if (dbHelper != null) {
            dbHelper.saveScoreBreakoutHard(actualUsername, puntuacion, segundosTranscurridos); // Cambiado para usar el método específico para nivel difícil
        }

        invalidate(); // Redibuja la vista para mostrar el mensaje
    }

    // Método para reiniciar el juego
    public void nuevoJuego() {
        isGameOver = false;        // La partida no está perdida
        pelotaPegada = true;       // La pelota vuelve a estar pegada a la paleta
        puntuacion = 0;            // Se reinicia la puntuación
        segundosTranscurridos = 0; // Se reinicia el timer
        estaPausado = false;       // El juego no está pausado
        velocidPelotaX = 20;        // Se reinicia velocidad de la pelota
        velocidPelotaY = -20;

        // Se recoloca la paleta y la pelota
        paletaX = izqAreaJuego + anchoAreaJuego / 2 - anchoPaleta / 2;
        pelotaX = paletaX + anchoPaleta / 2;
        pelotaY = topAreaJuego + altoAreaJuego - altoPaleta - radioPelota;

        iniciarLadrillos();        // Se reinicializar los ladrillos
        invalidate();              // Se redibuja el juego
    }

    // Método para actualizar la puntuación según la fila del ladrillo
    private void actualizarPuntuacion(int fila) {
        switch (fila) {
            case 0: // Primera fila
                puntuacion += 50;
                break;
            case 1: // Segunda fila
                puntuacion += 40;
                break;
            case 2: // Tercera fila
                puntuacion += 30;
                break;
            case 3: // Cuarta fila
                puntuacion += 20;
                break;
            case 4: // Quinta fila
                puntuacion += 10;
                break;
            default:
                break;
        }
    }

    //Método que se encarga de aumentar la velocidad de la pelota
    private void aumentoVelocidadPelota() {
        // Aumenta la velocidad en X, pero solo si no ha alcanzado el máximo
        if (Math.abs(velocidPelotaX) < maxVelocidPelotaX) {
            if (velocidPelotaX > 0) {
                velocidPelotaX += 1;  // Si se mueve a la derecha, se incrementa la velocidad horizontal
            } else {
                velocidPelotaX -= 1;  // Si se mueve a la izquierda, se decrementa la velocidad horizontal
            }
        }

        // Aumenta la velocidad en Y, pero solo si no ha alcanzado el máximo
        if (Math.abs(velocidPelotaY) < maxVelocidPelotaY) {
            if (velocidPelotaY > 0) {
                velocidPelotaY += 1;  // Si se mueve hacia abajo, se incrementa la velocidad vertical
            } else {
                velocidPelotaY -= 1;  // Si se mueve hacia arriba, se decrementa la velocidad vertical
            }
        }
    }

    // Método que permite mover la paleta y controlar la pelota
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();

        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            paletaX = Math.min(Math.max(x - anchoPaleta / 2, izqAreaJuego), izqAreaJuego + anchoAreaJuego - anchoPaleta);

            if (pelotaPegada) {
                pelotaX = paletaX + anchoPaleta / 2;
                pelotaY = topAreaJuego + altoAreaJuego - altoPaleta - radioPelota;
            }
        }

        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (pelotaPegada) {
                pelotaPegada = false;
                if (!contadorActivo) {
                    iniciarContador(); // Inicia el contador si no está activo
                }
            }
        }
        return true;
    }
}