package com.example.formularios;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.List;

public class UsersActivity extends AppCompatActivity {

    private ListView listViewUsers;
    private DBHelper dbHelper;
    private String currentUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        // Obtener el nombre de usuario actual
        currentUsername = getIntent().getStringExtra("current_username");

        // Configurar la toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("USERS ONLINE");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());

        // Inicializar la base de datos
        dbHelper = new DBHelper(this);

        // Encontrar la ListView
        listViewUsers = findViewById(R.id.listViewUsers);

        // Cargar los usuarios
        loadUsers();
    }

    private void loadUsers() {
        // Obtener la lista de usuarios que han iniciado sesión
        List<String> userList = dbHelper.getLoggedUsers();

        // Crear un adaptador personalizado para resaltar el usuario actual
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                userList) {

            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);

                String username = getItem(position);
                TextView textView = view.findViewById(android.R.id.text1);

                // Si este elemento es el usuario actual, resaltarlo en dorado
                if (username != null && username.equals(currentUsername)) {
                    textView.setTextColor(getResources().getColor(R.color.gold));
                    textView.setText(username);
                    textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.online, 0, 0, 0);
                    textView.setCompoundDrawablePadding(10);
                } else {
                    textView.setTextColor(getResources().getColor(android.R.color.white));
                }

                return view;
            }
        };

        listViewUsers.setAdapter(adapter);
    }
}