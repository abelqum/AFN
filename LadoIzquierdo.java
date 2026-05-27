package com.mycompany.userafncreator;

import java.util.ArrayList;

public class LadoIzquierdo {
    
    public Simbolo simboloIzquierdo;
    public ArrayList<Simbolo> listaSimb; // Todo lo que está a la derecha de la flecha

    public LadoIzquierdo(Simbolo simboloIzquierdo) {
        this.simboloIzquierdo = simboloIzquierdo;
        this.listaSimb = new ArrayList<>();
    }

    public LadoIzquierdo(Simbolo simboloIzquierdo, ArrayList<Simbolo> listaSimb) {
        this.simboloIzquierdo = simboloIzquierdo;
        this.listaSimb = listaSimb;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(simboloIzquierdo.nombre).append(" -> ");
        for (Simbolo s : listaSimb) {
            sb.append(s.nombre).append(" ");
        }
        return sb.toString().trim();
    }
}