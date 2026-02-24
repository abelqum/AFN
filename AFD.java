package com.mycompany.userafncreator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

public class AFD {
    // Número total de estados en el AFD
    public int numEstados;
    
    // Conjunto de IDs de los estados que son de aceptación
    public HashSet<Integer> estadosAceptacion;
    
    // Tabla de transiciones: Estado -> (Símbolo -> Estado Destino)
    public HashMap<Integer, HashMap<Character, Integer>> tablaTransiciones;

    public AFD() {
        this.numEstados = 0;
        this.estadosAceptacion = new HashSet<>();
        this.tablaTransiciones = new HashMap<>();
    }

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

    // --- NUEVO MÉTODO PARA DIBUJAR EL AFD CON GRAPHVIZ ---
    public void generarGrafico(String nombreArchivo) {
        File carpetaDots = new File("dots");
        File carpetaImagenes = new File("imagenes");
        
        if (!carpetaDots.exists()) carpetaDots.mkdir();
        if (!carpetaImagenes.exists()) carpetaImagenes.mkdir();

        String rutaDot = "dots/" + nombreArchivo + ".dot";
        String rutaPng = "imagenes/" + nombreArchivo + ".png";

        StringBuilder dot = new StringBuilder();
        dot.append("digraph AFD {\n");
        dot.append("  charset=\"UTF-8\";\n"); 
        dot.append("  rankdir=LR;\n");
        dot.append("  node [shape = circle, fontname=\"Arial\"];\n");
        dot.append("  edge [fontname=\"Arial\"];\n");

        dot.append("  node [shape = none, label=\"\"]; start;\n");
        if (numEstados > 0) {
            dot.append("  start -> 0;\n"); // El estado inicial del AFD siempre es 0
        }

        // Dibujar los estados (círculos)
        for (int i = 0; i < numEstados; i++) {
            if (estadosAceptacion.contains(i)) {
                dot.append("  ").append(i).append(" [shape = doublecircle, label=\"").append(i).append("\"];\n");
            } else {
                dot.append("  ").append(i).append(" [shape = circle, label=\"").append(i).append("\"];\n");
            }
        }

        // Dibujar las flechas (transiciones)
        for (Map.Entry<Integer, HashMap<Character, Integer>> fila : tablaTransiciones.entrySet()) {
            int estadoOrigen = fila.getKey();
            
            for (Map.Entry<Character, Integer> transicion : fila.getValue().entrySet()) {
                char simbolo = transicion.getKey();
                int estadoDestino = transicion.getValue();
                
                String label = (simbolo == '\0') ? "ε" : "'" + simbolo + "'";
                dot.append("  ").append(estadoOrigen).append(" -> ").append(estadoDestino).append(" [label=\"").append(label).append("\"];\n");
            }
        }
        dot.append("}\n");

        try {
            OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(rutaDot), StandardCharsets.UTF_8);
            osw.write(dot.toString());
            osw.close();

            ProcessBuilder pb = new ProcessBuilder("dot", "-Tpng", rutaDot, "-o", rutaPng);
            pb.inheritIO(); 
            Process p = pb.start();
            p.waitFor();

            System.out.println("✅ Imagen AFD generada: " + rutaPng);

        } catch (Exception ex) {
            System.err.println("❌ Error al generar imagen del AFD: " + ex.getMessage());
        }
    }
}