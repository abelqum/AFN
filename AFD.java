package com.mycompany.userafncreator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class AFD {
    // Número total de estados en el AFD
    public int numEstados;
    
    // Conjunto de IDs de los estados que son de aceptación
    public HashSet<Integer> estadosAceptacion;
    
    // Tabla de transiciones: Estado -> (Símbolo -> Estado Destino)
    // Funciona como la matriz bidimensional del profe, pero optimizada para memoria
    public HashMap<Integer, HashMap<Character, Integer>> tablaTransiciones;

    public AFD() {
        this.numEstados = 0;
        this.estadosAceptacion = new HashSet<>();
        this.tablaTransiciones = new HashMap<>();
    }

    // Método extra para ver la tabla del AFD en consola
    public void imprimirTabla() {
        System.out.println("\n=== TABLA DE TRANSICIONES AFD ===");
        System.out.println("Total de estados: " + numEstados);
        System.out.println("Estados de aceptación: " + estadosAceptacion);
        
        for (Map.Entry<Integer, HashMap<Character, Integer>> fila : tablaTransiciones.entrySet()) {
            int estadoOrigen = fila.getKey();
            String marcaAceptacion = estadosAceptacion.contains(estadoOrigen) ? "*" : " ";
            
            for (Map.Entry<Character, Integer> transicion : fila.getValue().entrySet()) {
                char simbolo = transicion.getKey();
                int estadoDestino = transicion.getValue();
                
                System.out.println("Estado " + estadoOrigen + marcaAceptacion + " -- '" + simbolo + "' --> Estado " + estadoDestino);
            }
        }
        System.out.println("=================================\n");
    }
}