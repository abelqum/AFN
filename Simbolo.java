    package com.mycompany.userafncreator;

import java.util.Objects;

public class Simbolo {
    
    public String nombre;
    public boolean esTerminal;

    // Constante para facilitar el uso del Épsilon
    public static final String EPSILON = "ε";

    public Simbolo(String nombre, boolean esTerminal) {
        this.nombre = nombre;
        this.esTerminal = esTerminal;
    }

    // SOBRESCRITURA DE MÉTODOS (Vital para LR0 y conjuntos)
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Simbolo simbolo = (Simbolo) obj;
        return esTerminal == simbolo.esTerminal && Objects.equals(nombre, simbolo.nombre);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nombre, esTerminal);
    }

    @Override
    public String toString() {
        return nombre;
    }
}