package com.example.formularios;

public class Ladrillo {
    private final int x, y; // Coordenadas del ladrillo
    private boolean visible; // Estado del ladrillo

    // Constructor de la clase Ladrillo
    public Ladrillo(int x, int y) {
        this.x = x;
        this.y = y;
        this.visible = true; // El ladrillo será visible por defecto
    }

    // Método que devuelve la posición horizontal del ladrillo
    public int getX() {
        return x;
    }

    // Método que devuelve la posición vertical del ladrillo
    public int getY() {
        return y;
    }

    // Método que indica el estado del ladrillo
    public boolean isVisible() {
        return visible;
    }

    // Método que permite cambiar la visibilidad del ladrillo
    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}
