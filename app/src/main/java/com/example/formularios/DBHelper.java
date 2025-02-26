package com.example.formularios;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "aitor.db";
    private static final int DATABASE_VERSION = 5; // Incrementado a versión 4 por los nuevos cambios

    // Tabla de usuarios
    public static final String TABLE_USERS = "users";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_PASSWORD = "password";

    // Tabla para registrar inicios de sesión
    public static final String TABLE_LOGIN_HISTORY = "login_history";
    public static final String COLUMN_LOGIN_ID = "login_id";
    public static final String COLUMN_LOGIN_USERNAME = "username";
    public static final String COLUMN_LOGIN_TIMESTAMP = "timestamp";

    // Tabla para almacenar puntuaciones del juego 2048
    public static final String TABLE_GAME_2048 = "game_2048_scores";
    public static final String COLUMN_2048_ID = "score_id";
    public static final String COLUMN_2048_USERNAME = "username";
    public static final String COLUMN_2048_SCORE = "score";
    public static final String COLUMN_2048_TIMESTAMP = "timestamp";

    // Nueva tabla para almacenar puntuaciones del juego 2048 (nivel difícil)
    public static final String TABLE_GAME_2048_HARD = "game_2048_hard_scores";
    public static final String COLUMN_2048_HARD_ID = "score_id";
    public static final String COLUMN_2048_HARD_USERNAME = "username";
    public static final String COLUMN_2048_HARD_SCORE = "score";
    public static final String COLUMN_2048_HARD_TIMESTAMP = "timestamp";

    // Tabla para almacenar puntuaciones del juego Breakout (nivel normal)
    public static final String TABLE_BREAKOUT = "breakout_scores";
    public static final String COLUMN_BREAKOUT_ID = "score_id";
    public static final String COLUMN_BREAKOUT_USERNAME = "username";
    public static final String COLUMN_BREAKOUT_SCORE = "score";
    public static final String COLUMN_BREAKOUT_TIME = "time_seconds";
    public static final String COLUMN_BREAKOUT_TIMESTAMP = "timestamp";

    // Nueva tabla para almacenar puntuaciones del juego Breakout (nivel difícil)
    public static final String TABLE_BREAKOUT_HARD = "breakout_hard_scores";
    public static final String COLUMN_BREAKOUT_HARD_ID = "score_id";
    public static final String COLUMN_BREAKOUT_HARD_USERNAME = "username";
    public static final String COLUMN_BREAKOUT_HARD_SCORE = "score";
    public static final String COLUMN_BREAKOUT_HARD_TIME = "time_seconds";
    public static final String COLUMN_BREAKOUT_HARD_TIMESTAMP = "timestamp";

    // SQL para crear la tabla de usuarios
    private static final String CREATE_TABLE_USERS =
            "CREATE TABLE " + TABLE_USERS + "(" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_USERNAME + " TEXT UNIQUE NOT NULL, " +
                    COLUMN_PASSWORD + " TEXT NOT NULL)";

    // SQL para crear la tabla de historial de inicios de sesión
    private static final String CREATE_TABLE_LOGIN_HISTORY =
            "CREATE TABLE " + TABLE_LOGIN_HISTORY + "(" +
                    COLUMN_LOGIN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_LOGIN_USERNAME + " TEXT NOT NULL, " +
                    COLUMN_LOGIN_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP)";

    // SQL para crear la tabla de puntuaciones del juego 2048
    private static final String CREATE_TABLE_GAME_2048 =
            "CREATE TABLE " + TABLE_GAME_2048 + "(" +
                    COLUMN_2048_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_2048_USERNAME + " TEXT NOT NULL, " +
                    COLUMN_2048_SCORE + " INTEGER NOT NULL, " +
                    COLUMN_2048_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP)";

    // SQL para crear la tabla de puntuaciones del juego 2048 (nivel difícil)
    private static final String CREATE_TABLE_GAME_2048_HARD =
            "CREATE TABLE " + TABLE_GAME_2048_HARD + "(" +
                    COLUMN_2048_HARD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_2048_HARD_USERNAME + " TEXT NOT NULL, " +
                    COLUMN_2048_HARD_SCORE + " INTEGER NOT NULL, " +
                    COLUMN_2048_HARD_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP)";

    // SQL para crear la tabla de puntuaciones del juego Breakout (nivel normal)
    private static final String CREATE_TABLE_BREAKOUT =
            "CREATE TABLE " + TABLE_BREAKOUT + "(" +
                    COLUMN_BREAKOUT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_BREAKOUT_USERNAME + " TEXT NOT NULL, " +
                    COLUMN_BREAKOUT_SCORE + " INTEGER NOT NULL, " +
                    COLUMN_BREAKOUT_TIME + " INTEGER NOT NULL, " +
                    COLUMN_BREAKOUT_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP)";

    // SQL para crear la tabla de puntuaciones del juego Breakout (nivel difícil)
    private static final String CREATE_TABLE_BREAKOUT_HARD =
            "CREATE TABLE " + TABLE_BREAKOUT_HARD + "(" +
                    COLUMN_BREAKOUT_HARD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_BREAKOUT_HARD_USERNAME + " TEXT NOT NULL, " +
                    COLUMN_BREAKOUT_HARD_SCORE + " INTEGER NOT NULL, " +
                    COLUMN_BREAKOUT_HARD_TIME + " INTEGER NOT NULL, " +
                    COLUMN_BREAKOUT_HARD_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP)";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USERS);
        db.execSQL(CREATE_TABLE_LOGIN_HISTORY);
        db.execSQL(CREATE_TABLE_GAME_2048);
        db.execSQL(CREATE_TABLE_GAME_2048_HARD);
        db.execSQL(CREATE_TABLE_BREAKOUT);
        db.execSQL(CREATE_TABLE_BREAKOUT_HARD);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            // Actualización de versión 1 a 2
            db.execSQL(CREATE_TABLE_GAME_2048);
            db.execSQL(CREATE_TABLE_BREAKOUT);
        }

        if (oldVersion < 3) {
            // Asegurarse de que la tabla Breakout existe y tiene la estructura correcta
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_BREAKOUT);
            db.execSQL(CREATE_TABLE_BREAKOUT);
        }

        if (oldVersion < 4) {
            // Añadir la nueva tabla para el nivel difícil de Breakout
            db.execSQL(CREATE_TABLE_BREAKOUT_HARD);
        }
        if (oldVersion < 5) {
            // Añadir la nueva tabla para el nivel difícil de 2048
            db.execSQL(CREATE_TABLE_GAME_2048_HARD);
        }
    }

    // Registra un nuevo usuario en la base de datos
    public boolean registerUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_PASSWORD, password);

        // Intenta insertar el nuevo usuario
        long result = -1;
        try {
            result = db.insertOrThrow(TABLE_USERS, null, values);
        } catch (Exception e) {
            // El usuario ya existe o hubo un error
            return false;
        } finally {
            db.close();
        }

        return result != -1;
    }

    // Verifica las credenciales de un usuario
    public boolean checkUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] columns = {COLUMN_ID};
        String selection = COLUMN_USERNAME + " = ? AND " + COLUMN_PASSWORD + " = ?";
        String[] selectionArgs = {username, password};

        Cursor cursor = db.query(
                TABLE_USERS,
                columns,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        int count = cursor.getCount();
        cursor.close();
        db.close();

        return count > 0;
    }

    // Registra un inicio de sesión exitoso
    public void recordLogin(String username) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_LOGIN_USERNAME, username);

        db.insert(TABLE_LOGIN_HISTORY, null, values);
        db.close();
    }

    // Obtiene la lista de usuarios que han iniciado sesión
    public List<String> getLoggedUsers() {
        List<String> userList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Consulta para obtener usuarios únicos que han iniciado sesión
        String query = "SELECT DISTINCT " + COLUMN_LOGIN_USERNAME +
                " FROM " + TABLE_LOGIN_HISTORY +
                " ORDER BY " + COLUMN_LOGIN_TIMESTAMP + " DESC";

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                userList.add(cursor.getString(
                        cursor.getColumnIndex(COLUMN_LOGIN_USERNAME)));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return userList;
    }

    //* MÉTODOS PARA EL JUEGO 2048 ***/

    // Guarda la puntuación de un usuario en el juego 2048
    public boolean saveScore2048(String username, int score) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_2048_USERNAME, username);
        values.put(COLUMN_2048_SCORE, score);

        long result = db.insert(TABLE_GAME_2048, null, values);
        db.close();

        return result != -1;
    }

    //Obtiene la mejor puntuación de un usuario en el juego 2048
    public int getBestScore2048(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        int bestScore = 0;

        String query = "SELECT MAX(" + COLUMN_2048_SCORE + ") AS best_score" +
                " FROM " + TABLE_GAME_2048 +
                " WHERE " + COLUMN_2048_USERNAME + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{username});

        if (cursor.moveToFirst()) {
            bestScore = cursor.getInt(cursor.getColumnIndex("best_score"));
        }

        cursor.close();
        db.close();

        return bestScore;
    }

    //Obtiene las mejores puntuaciones del juego 2048 para todos los usuarios
    public List<ScoreEntry2048> getTopScores2048(int limit) {
        List<ScoreEntry2048> scoresList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Consulta para obtener el mejor puntaje de cada usuario
        String query = "SELECT " + COLUMN_2048_USERNAME + ", MAX(" + COLUMN_2048_SCORE + ") AS max_score" +
                " FROM " + TABLE_GAME_2048 +
                " GROUP BY " + COLUMN_2048_USERNAME +
                " ORDER BY max_score DESC" +
                " LIMIT " + limit;

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                String username = cursor.getString(cursor.getColumnIndex(COLUMN_2048_USERNAME));
                int score = cursor.getInt(cursor.getColumnIndex("max_score"));
                scoresList.add(new ScoreEntry2048(username, score));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return scoresList;
    }

    // Obtiene las mejores puntuaciones de un usuario específico en el juego 2048
    public List<ScoreEntry2048> getUserScores2048(String username, int limit) {
        List<ScoreEntry2048> scoresList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT " + COLUMN_2048_USERNAME + ", " +
                COLUMN_2048_SCORE +
                " FROM " + TABLE_GAME_2048 +
                " WHERE " + COLUMN_2048_USERNAME + " = ?" +
                " ORDER BY " + COLUMN_2048_SCORE + " DESC" +
                " LIMIT " + limit;

        Cursor cursor = db.rawQuery(query, new String[]{username});

        if (cursor.moveToFirst()) {
            do {
                int score = cursor.getInt(cursor.getColumnIndex(COLUMN_2048_SCORE));
                scoresList.add(new ScoreEntry2048(username, score));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return scoresList;
    }

    // Guarda la puntuación de un usuario en el juego 2048 (nivel difícil)
    public boolean saveScore2048Hard(String username, int score) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_2048_HARD_USERNAME, username);
        values.put(COLUMN_2048_HARD_SCORE, score);

        long result = db.insert(TABLE_GAME_2048_HARD, null, values);
        db.close();

        return result != -1;
    }

    // Obtiene la mejor puntuación de un usuario en el juego 2048 (nivel difícil)
    public int getBestScore2048Hard(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        int bestScore = 0;

        String query = "SELECT MAX(" + COLUMN_2048_HARD_SCORE + ") AS best_score" +
                " FROM " + TABLE_GAME_2048_HARD +
                " WHERE " + COLUMN_2048_HARD_USERNAME + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{username});

        if (cursor.moveToFirst()) {
            bestScore = cursor.getInt(cursor.getColumnIndex("best_score"));
        }

        cursor.close();
        db.close();

        return bestScore;
    }

    // Obtiene las mejores puntuaciones del juego 2048 (nivel difícil) para todos los usuarios
    public List<ScoreEntry2048> getTopScores2048Hard(int limit) {
        List<ScoreEntry2048> scoresList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Consulta para obtener el mejor puntaje de cada usuario
        String query = "SELECT " + COLUMN_2048_HARD_USERNAME + ", MAX(" + COLUMN_2048_HARD_SCORE + ") AS max_score" +
                " FROM " + TABLE_GAME_2048_HARD +
                " GROUP BY " + COLUMN_2048_HARD_USERNAME +
                " ORDER BY max_score DESC" +
                " LIMIT " + limit;

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                String username = cursor.getString(cursor.getColumnIndex(COLUMN_2048_HARD_USERNAME));
                int score = cursor.getInt(cursor.getColumnIndex("max_score"));
                scoresList.add(new ScoreEntry2048(username, score));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return scoresList;
    }

    // Obtiene las mejores puntuaciones de un usuario específico en el juego 2048 (nivel difícil)
    public List<ScoreEntry2048> getUserScores2048Hard(String username, int limit) {
        List<ScoreEntry2048> scoresList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT " + COLUMN_2048_HARD_USERNAME + ", " +
                COLUMN_2048_HARD_SCORE +
                " FROM " + TABLE_GAME_2048_HARD +
                " WHERE " + COLUMN_2048_HARD_USERNAME + " = ?" +
                " ORDER BY " + COLUMN_2048_HARD_SCORE + " DESC" +
                " LIMIT " + limit;

        Cursor cursor = db.rawQuery(query, new String[]{username});

        if (cursor.moveToFirst()) {
            do {
                int score = cursor.getInt(cursor.getColumnIndex(COLUMN_2048_HARD_SCORE));
                scoresList.add(new ScoreEntry2048(username, score));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return scoresList;
    }

    // Cuenta el número total de registros en la tabla 2048 (nivel difícil)
    public int count2048HardScores() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_GAME_2048_HARD, null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        db.close();
        return count;
    }

    //* MÉTODOS PARA EL JUEGO BREAKOUT (NIVEL NORMAL) ***/

    // Guarda la puntuación de un usuario en el juego Breakout (nivel normal)
    public boolean saveScoreBreakout(String username, int score, int timeSeconds) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_BREAKOUT_USERNAME, username);
        values.put(COLUMN_BREAKOUT_SCORE, score);
        values.put(COLUMN_BREAKOUT_TIME, timeSeconds);

        long result = db.insert(TABLE_BREAKOUT, null, values);
        db.close();

        return result != -1;
    }

    // Obtiene las mejores puntuaciones del juego Breakout (nivel normal)
    public List<ScoreEntryBreakout> getTopScoresBreakout(int limit) {
        List<ScoreEntryBreakout> scoresList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Consulta para obtener las mejores puntuaciones (ordenadas por puntaje descendente)
        String query = "SELECT " + COLUMN_BREAKOUT_USERNAME + ", " +
                COLUMN_BREAKOUT_SCORE + ", " +
                COLUMN_BREAKOUT_TIME +
                " FROM " + TABLE_BREAKOUT +
                " ORDER BY " + COLUMN_BREAKOUT_SCORE + " DESC" +
                " LIMIT " + limit;

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                String username = cursor.getString(cursor.getColumnIndex(COLUMN_BREAKOUT_USERNAME));
                int score = cursor.getInt(cursor.getColumnIndex(COLUMN_BREAKOUT_SCORE));
                int time = cursor.getInt(cursor.getColumnIndex(COLUMN_BREAKOUT_TIME));
                scoresList.add(new ScoreEntryBreakout(username, score, time));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return scoresList;
    }

    // Obtiene las mejores puntuaciones de un usuario específico en el juego Breakout (nivel normal)
    public List<ScoreEntryBreakout> getUserScoresBreakout(String username, int limit) {
        List<ScoreEntryBreakout> scoresList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT " + COLUMN_BREAKOUT_USERNAME + ", " +
                COLUMN_BREAKOUT_SCORE + ", " +
                COLUMN_BREAKOUT_TIME +
                " FROM " + TABLE_BREAKOUT +
                " WHERE " + COLUMN_BREAKOUT_USERNAME + " = ?" +
                " ORDER BY " + COLUMN_BREAKOUT_SCORE + " DESC" +
                " LIMIT " + limit;

        Cursor cursor = db.rawQuery(query, new String[]{username});

        if (cursor.moveToFirst()) {
            do {
                int score = cursor.getInt(cursor.getColumnIndex(COLUMN_BREAKOUT_SCORE));
                int time = cursor.getInt(cursor.getColumnIndex(COLUMN_BREAKOUT_TIME));
                scoresList.add(new ScoreEntryBreakout(username, score, time));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return scoresList;
    }

    //* NUEVOS MÉTODOS PARA EL JUEGO BREAKOUT (NIVEL DIFÍCIL) ***/

    // Guarda la puntuación de un usuario en el juego Breakout (nivel difícil)
    public boolean saveScoreBreakoutHard(String username, int score, int timeSeconds) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_BREAKOUT_HARD_USERNAME, username);
        values.put(COLUMN_BREAKOUT_HARD_SCORE, score);
        values.put(COLUMN_BREAKOUT_HARD_TIME, timeSeconds);

        long result = db.insert(TABLE_BREAKOUT_HARD, null, values);
        db.close();

        return result != -1;
    }

    // Obtiene las mejores puntuaciones del juego Breakout (nivel difícil)
    public List<ScoreEntryBreakout> getTopScoresBreakoutHard(int limit) {
        List<ScoreEntryBreakout> scoresList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Consulta para obtener las mejores puntuaciones (ordenadas por puntaje descendente)
        String query = "SELECT " + COLUMN_BREAKOUT_HARD_USERNAME + ", " +
                COLUMN_BREAKOUT_HARD_SCORE + ", " +
                COLUMN_BREAKOUT_HARD_TIME +
                " FROM " + TABLE_BREAKOUT_HARD +
                " ORDER BY " + COLUMN_BREAKOUT_HARD_SCORE + " DESC" +
                " LIMIT " + limit;

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                String username = cursor.getString(cursor.getColumnIndex(COLUMN_BREAKOUT_HARD_USERNAME));
                int score = cursor.getInt(cursor.getColumnIndex(COLUMN_BREAKOUT_HARD_SCORE));
                int time = cursor.getInt(cursor.getColumnIndex(COLUMN_BREAKOUT_HARD_TIME));
                scoresList.add(new ScoreEntryBreakout(username, score, time));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return scoresList;
    }

    // Obtiene las mejores puntuaciones de un usuario específico en el juego Breakout (nivel difícil)
    public List<ScoreEntryBreakout> getUserScoresBreakoutHard(String username, int limit) {
        List<ScoreEntryBreakout> scoresList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT " + COLUMN_BREAKOUT_HARD_USERNAME + ", " +
                COLUMN_BREAKOUT_HARD_SCORE + ", " +
                COLUMN_BREAKOUT_HARD_TIME +
                " FROM " + TABLE_BREAKOUT_HARD +
                " WHERE " + COLUMN_BREAKOUT_HARD_USERNAME + " = ?" +
                " ORDER BY " + COLUMN_BREAKOUT_HARD_SCORE + " DESC" +
                " LIMIT " + limit;

        Cursor cursor = db.rawQuery(query, new String[]{username});

        if (cursor.moveToFirst()) {
            do {
                int score = cursor.getInt(cursor.getColumnIndex(COLUMN_BREAKOUT_HARD_SCORE));
                int time = cursor.getInt(cursor.getColumnIndex(COLUMN_BREAKOUT_HARD_TIME));
                scoresList.add(new ScoreEntryBreakout(username, score, time));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return scoresList;
    }

    // Obtiene el mejor tiempo (más bajo) para un puntaje específico en el juego Breakout (nivel difícil)
    public int getBestTimeForScoreHard(int score) {
        SQLiteDatabase db = this.getReadableDatabase();
        int bestTime = Integer.MAX_VALUE;

        String query = "SELECT MIN(" + COLUMN_BREAKOUT_HARD_TIME + ") AS best_time" +
                " FROM " + TABLE_BREAKOUT_HARD +
                " WHERE " + COLUMN_BREAKOUT_HARD_SCORE + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(score)});

        if (cursor.moveToFirst() && !cursor.isNull(cursor.getColumnIndex("best_time"))) {
            bestTime = cursor.getInt(cursor.getColumnIndex("best_time"));
        }

        cursor.close();
        db.close();

        return bestTime != Integer.MAX_VALUE ? bestTime : 0;
    }

    // Cuenta el número total de registros en la tabla Breakout (nivel difícil)
    public int countBreakoutHardScores() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_BREAKOUT_HARD, null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        db.close();
        return count;
    }

    // Clases para manejar los datos de puntuación
    public static class ScoreEntry2048 {
        private String username;
        private int score;

        public ScoreEntry2048(String username, int score) {
            this.username = username;
            this.score = score;
        }

        public String getUsername() {
            return username;
        }

        public int getScore() {
            return score;
        }
    }

    public static class ScoreEntryBreakout {
        private String username;
        private int score;
        private int timeSeconds;

        public ScoreEntryBreakout(String username, int score, int timeSeconds) {
            this.username = username;
            this.score = score;
            this.timeSeconds = timeSeconds;
        }

        public String getUsername() {
            return username;
        }

        public int getScore() {
            return score;
        }

        public int getTimeSeconds() {
            return timeSeconds;
        }

        public String getFormattedTime() {
            int minutes = timeSeconds / 60;
            int seconds = timeSeconds % 60;
            return String.format("%02d:%02d", minutes, seconds);
        }
    }
}