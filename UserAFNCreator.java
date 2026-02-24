package com.mycompany.userafncreator;

public class UserAFNCreator {
    public static void main(String[] args) {
        // Lanzar la ventana gráfica
        java.awt.EventQueue.invokeLater(() -> {
            new VentanaPrincipal().setVisible(true);
        });
    }
}