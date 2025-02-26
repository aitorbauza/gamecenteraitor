package com.example.formularios;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatsActivity extends AppCompatActivity {

    private DBHelper dbHelper;
    private ListView normalLeaderboardListView;
    private ListView hardLeaderboardListView;
    private Spinner gameSpinner;
    private TextView titleTextView;
    private TextView usernameFilterTextView;
    private String currentGame = "2048"; // Por defecto, mostramos las puntuaciones de 2048
    private static final int MAX_SCORES = 10; // Número máximo de puntuaciones a mostrar
    private String filterUsername = null; // Para filtrar puntuaciones por usuario
    private TabHost tabHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        // Inicializar componentes
        dbHelper = new DBHelper(this);
        normalLeaderboardListView = findViewById(R.id.normalLeaderboardListView);
        hardLeaderboardListView = findViewById(R.id.hardLeaderboardListView);
        gameSpinner = findViewById(R.id.gameSpinner);
        titleTextView = findViewById(R.id.titleTextView);
        ImageButton backButton = findViewById(R.id.backButton);
        tabHost = findViewById(R.id.tabHost);

        // Inicializar TabHost
        tabHost.setup();

        // Crear las pestañas
        TabSpec normalTabSpec = tabHost.newTabSpec("normalTab");
        normalTabSpec.setIndicator("Normal Level");
        normalTabSpec.setContent(R.id.normalLeaderboardListView);
        tabHost.addTab(normalTabSpec);

        TabSpec hardTabSpec = tabHost.newTabSpec("hardTab");
        hardTabSpec.setIndicator("Hard Level");
        hardTabSpec.setContent(R.id.hardLeaderboardListView);
        tabHost.addTab(hardTabSpec);

        // Configurar adaptador para el spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.games_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gameSpinner.setAdapter(adapter);

        gameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentGame = parent.getItemAtPosition(position).toString();
                updateLeaderboards();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        backButton.setOnClickListener(v -> finish());

        // Mostrar/ocultar pestañas según el juego seleccionado
        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                updateTabVisibility();
            }
        });

        // Cargar las puntuaciones iniciales
        updateLeaderboards();
    }

    private void updateTabVisibility() {
        // Para 2048, mostrar ambas pestañas (normal y difícil)
        if (currentGame.equals("2048")) {
            tabHost.getTabWidget().getChildAt(1).setVisibility(View.VISIBLE);
        } else {
            if (currentGame.equals("Breakout")) {
                tabHost.getTabWidget().getChildAt(1).setVisibility(View.VISIBLE);
            } else {
                tabHost.getTabWidget().getChildAt(1).setVisibility(View.GONE);
                tabHost.setCurrentTab(0);
            }
        }
    }

    private void updateLeaderboards() {
        updateNormalLeaderboard();
        updateHardLeaderboard();
        updateTabVisibility();
    }

    private void updateNormalLeaderboard() {
        List<Map<String, String>> data = new ArrayList<>();

        if (currentGame.equals("2048")) {
            // Actualizar el título
            titleTextView.setText("Historical Scores 2048");

            List<DBHelper.ScoreEntry2048> scores;

            // Verificar si estamos filtrando por usuario
            if (filterUsername != null) {
                scores = dbHelper.getUserScores2048(filterUsername, MAX_SCORES);
            } else {
                scores = dbHelper.getTopScores2048(MAX_SCORES);
            }

            for (int i = 0; i < scores.size(); i++) {
                DBHelper.ScoreEntry2048 score = scores.get(i);
                Map<String, String> item = new HashMap<>();
                item.put("position", (i + 1) + ".");
                item.put("username", score.getUsername());
                item.put("score", String.valueOf(score.getScore()));
                data.add(item);
            }
        } else if (currentGame.equals("Breakout")) {
            // Actualizar el título
            titleTextView.setText("Historical Scores Breakout");

            List<DBHelper.ScoreEntryBreakout> scores;

            // Verificar si estamos filtrando por usuario
            if (filterUsername != null) {
                scores = dbHelper.getUserScoresBreakout(filterUsername, MAX_SCORES);
            } else {
                scores = dbHelper.getTopScoresBreakout(MAX_SCORES);
            }

            for (int i = 0; i < scores.size(); i++) {
                DBHelper.ScoreEntryBreakout score = scores.get(i);
                Map<String, String> item = new HashMap<>();
                item.put("position", (i + 1) + ".");
                item.put("username", score.getUsername());
                item.put("score", String.valueOf(score.getScore()) + " (" + score.getFormattedTime() + ")");
                data.add(item);
            }
        }

        // Si no hay puntuaciones, agregar un mensaje
        if (data.isEmpty()) {
            Map<String, String> item = new HashMap<>();
            item.put("position", "N/C");
            item.put("username", "N/C");
            item.put("score", "N/C");
            data.add(item);
        }

        // Configurar el adaptador para el ListView
        SimpleAdapter adapter = new SimpleAdapter(
                this,
                data,
                R.layout.stats2048item,
                new String[]{"position", "username", "score"},
                new int[]{R.id.positionTextView, R.id.usernameTextView, R.id.scoreTextView}
        );

        normalLeaderboardListView.setAdapter(adapter);
    }

    private void updateHardLeaderboard() {
        List<Map<String, String>> data = new ArrayList<>();

        if (currentGame.equals("2048")) {
            List<DBHelper.ScoreEntry2048> scores;

            // Verificar si estamos filtrando por usuario
            if (filterUsername != null) {
                scores = dbHelper.getUserScores2048Hard(filterUsername, MAX_SCORES);
            } else {
                scores = dbHelper.getTopScores2048Hard(MAX_SCORES);
            }

            for (int i = 0; i < scores.size(); i++) {
                DBHelper.ScoreEntry2048 score = scores.get(i);
                Map<String, String> item = new HashMap<>();
                item.put("position", (i + 1) + ".");
                item.put("username", score.getUsername());
                item.put("score", String.valueOf(score.getScore()));
                data.add(item);
            }

        } else if (currentGame.equals("Breakout")) {
            List<DBHelper.ScoreEntryBreakout> scores;

            // Verificar si estamos filtrando por usuario
            if (filterUsername != null) {
                scores = dbHelper.getUserScoresBreakoutHard(filterUsername, MAX_SCORES);
            } else {
                scores = dbHelper.getTopScoresBreakoutHard(MAX_SCORES);
            }

            for (int i = 0; i < scores.size(); i++) {
                DBHelper.ScoreEntryBreakout score = scores.get(i);
                Map<String, String> item = new HashMap<>();
                item.put("position", (i + 1) + ".");
                item.put("username", score.getUsername());
                item.put("score", String.valueOf(score.getScore()) + " (" + score.getFormattedTime() + ")");
                data.add(item);
            }
        }

        // Si no hay puntuaciones o estamos en el juego 2048, agregar un mensaje
        if (data.isEmpty()) {
            Map<String, String> item = new HashMap<>();
            item.put("position", "N/C");
            item.put("username", "N/C");
            item.put("score", "N/C");
            data.add(item);
        }

        // Configurar el adaptador para el ListView
        SimpleAdapter adapter = new SimpleAdapter(
                this,
                data,
                R.layout.stats2048item,
                new String[]{"position", "username", "score"},
                new int[]{R.id.positionTextView, R.id.usernameTextView, R.id.scoreTextView}
        );

        hardLeaderboardListView.setAdapter(adapter);
    }
}