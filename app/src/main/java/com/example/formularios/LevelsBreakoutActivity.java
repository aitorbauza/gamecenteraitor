package com.example.formularios;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LevelsBreakoutActivity extends AppCompatActivity {

    private String username;
    private DBHelper dbHelper;
    private ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_breakoutlevels);

        dbHelper = new DBHelper(this);

        // Obtener el nombre de usuario de la actividad anterior
        username = getIntent().getStringExtra("current_username");
        if (username == null) {
            username = "Guest"; // Valor por defecto
        }

        LinearLayout buttonNormal = findViewById(R.id.buttonNormal);
        LinearLayout buttonHard = findViewById(R.id.buttonHard);
        LinearLayout buttonLegendary = findViewById(R.id.buttonLegendary);

        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        buttonNormal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LevelsBreakoutActivity.this, SOBreakoutNormalAct.class);
                intent.putExtra("current_username", username);
                startActivity(intent);
                // Aquí iría la lógica para iniciar el juego 1
                Toast.makeText(LevelsBreakoutActivity.this, "NORMAL LEVEL",
                        Toast.LENGTH_SHORT).show();
            }
        });

        buttonHard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LevelsBreakoutActivity.this, SOBreakoutHardAct.class);
                intent.putExtra("current_username", username);
                startActivity(intent);
                // Aquí iría la lógica para iniciar el juego 1
                Toast.makeText(LevelsBreakoutActivity.this, "HARD LEVEL",
                        Toast.LENGTH_SHORT).show();
            }
        });

        // El botón legendario está deshabilitado por defecto
        buttonLegendary.setEnabled(false);
    }
}