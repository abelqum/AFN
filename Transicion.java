/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.userafncreator;

/**
 *
 * @author abelq
 */
public class Transicion {
    // Atributos
    public char simbolo1;
    public char simbolo2;
    public Estado edoFinal;

    // Constante para representar Épsilon (carácter nulo)
    public static final char EPSILON = '\0';

    // Constructor para una transición con un solo carácter (o Épsilon)
    public Transicion(char c, Estado sig) {
        this.simbolo1 = c;
        this.simbolo2 = c;
        this.edoFinal = sig;
    }

    // Constructor para un rango de caracteres (ej: 'a' a 'z')
    public Transicion(char c1, char c2, Estado sig) {
        this.simbolo1 = c1;
        this.simbolo2 = c2;
        this.edoFinal = sig;
    }

    // Constructor vacío (como el que usaste en tus apuntes)
    public Transicion() {
        this.simbolo1 = EPSILON;
        this.simbolo2 = EPSILON;
        this.edoFinal = null;
    }
}