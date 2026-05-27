package com.mycompany.userafncreator;

import java.util.HashSet;

public class Gramatica {
    
    public int numReglas;
    public LadoIzquierdo[] reglas;
    
    // Conjuntos de vocabularios
    public HashSet<Simbolo> Vt; // Vocabulario Terminal (Minúsculas / Tokens)
    public HashSet<Simbolo> Vn; // Vocabulario No Terminal (Mayúsculas)
    
    public Simbolo simbInicial;

    // Constructor
    public Gramatica(int numReglas) {
        this.numReglas = numReglas;
        this.reglas = new LadoIzquierdo[numReglas]; // Inicializamos el arreglo de reglas
        this.Vt = new HashSet<>();
        this.Vn = new HashSet<>();
    }
}