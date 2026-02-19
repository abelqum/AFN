/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.userafncreator;
import java.util.HashSet;

/**
 *
 * @author abelq
 */


public class Estado {
    // Atributos
    public int idEdo;
    public HashSet<Transicion> transiciones;
    public boolean edoAccept;
    public int token;
    public static int contadorEdos = 0;

    // Constructor por defecto
    public Estado() {
        this.idEdo = contadorEdos++;
        this.transiciones = new HashSet<>(); // Inicialización necesaria
        this.edoAccept = false;
        this.token = -1;
    }

    // Constructor con una transición inicial
    public Estado(Transicion t) {
        this.idEdo = contadorEdos++;
        this.transiciones = new HashSet<>();
        this.transiciones.add(t);
        this.edoAccept = false;
        this.token = -1;
    }

    // Método para obtener estados alcanzables con un caracter 'c'
    public HashSet<Estado> tieneTransicionA(char c) {
        HashSet<Estado> alcanzables = new HashSet<>();
        
        for (Transicion t : this.transiciones) {
            // Verifica si el caracter está en el rango [simbolo1, simbolo2]
            if (t.simbolo1 <= c && c <= t.simbolo2) {
                alcanzables.add(t.edoFinal);
            }
        }
        return alcanzables;
    }
    
    // Agrega esto en Estado.java
public HashSet<Transicion> getTransiciones() {
    return this.transiciones;
}
}