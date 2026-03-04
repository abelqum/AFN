package com.mycompany.userafncreator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.io.PrintWriter;

public class AFD {
    public int numEstados;
    public int idAFD;
    public HashSet<Integer> estadosAceptacion;
    public HashMap<Integer, HashMap<Character, Integer>> tablaTransiciones;

    // --- NUEVA ESTRUCTURA DEL PROFESOR ---
    public int[][] tablaAFD; // Matriz [NumEstados][257]

    public AFD() {
        this.numEstados = 0;
        this.idAFD = -1;
        this.estadosAceptacion = new HashSet<>();
        this.tablaTransiciones = new HashMap<>();
    }

    // --- NUEVO MÉTODO: CONSTRUIR LA TABLA MATRICIAL ---
    public void construirTablaBidimensional(HashMap<Integer, Integer> tokensPorEstado) {
        this.tablaAFD = new int[this.numEstados][257];
        
        // 1. Inicializar toda la matriz con -1 (sin transición)
        for (int i = 0; i < this.numEstados; i++) {
            for (int j = 0; j < 257; j++) {
                this.tablaAFD[i][j] = -1;
            }
        }

        // 2. Rellenar las columnas 0 a 255 con los destinos
        for (Map.Entry<Integer, HashMap<Character, Integer>> fila : this.tablaTransiciones.entrySet()) {
            int estadoOrigen = fila.getKey();
            for (Map.Entry<Character, Integer> transicion : fila.getValue().entrySet()) {
                char simbolo = transicion.getKey();
                int estadoDestino = transicion.getValue();
                
                // Asegurar que el carácter esté en el rango ASCII
                if (simbolo >= 0 && simbolo <= 255) {
                    this.tablaAFD[estadoOrigen][simbolo] = estadoDestino;
                }
            }
        }

        // 3. Rellenar la última columna (256) con los TOKENS
        for (int i = 0; i < this.numEstados; i++) {
            if (estadosAceptacion.contains(i) && tokensPorEstado.containsKey(i)) {
                this.tablaAFD[i][256] = tokensPorEstado.get(i);
            }
        }
    }

    // --- NUEVO MÉTODO: GUARDAR EN .TXT ---
    public void guardarAFDEnArchivo(String rutaArchivo) {
        try {
            File archivo = new File(rutaArchivo);
            archivo.getParentFile().mkdirs(); // Crea la carpeta automáticamente
            
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(archivo), StandardCharsets.UTF_8));
            
            // Guardamos el número de estados en el primer renglón
            writer.println(this.numEstados);
            
            // Guardamos cada renglón de la tabla
            for (int i = 0; i < this.numEstados; i++) {
                for (int j = 0; j < 257; j++) {
                    writer.print(this.tablaAFD[i][j]);
                    if (j < 256) {
                        writer.print(";"); // Separador
                    }
                }
                writer.println(); // Salto de línea por cada estado
            }
            
            writer.close();
            System.out.println("✅ Tabla AFD guardada en: " + rutaArchivo);
            
        } catch (Exception e) {
            System.err.println("❌ Error al guardar tabla AFD: " + e.getMessage());
        }
    }
    
    // --- NUEVO MÉTODO: LEER DESDE .TXT (COMO EN LAS FOTOS DEL PROFE) ---
    public boolean leerAFDDeArchivo(String rutaArchivo) {
        try {
            java.io.File archivo = new java.io.File(rutaArchivo);
            if (!archivo.exists()) {
                System.err.println("El archivo no existe: " + rutaArchivo);
                return false;
            }

            java.util.Scanner lector = new java.util.Scanner(archivo);
            
            // Leer el número de estados (primer renglón)
            if (lector.hasNextLine()) {
                this.numEstados = Integer.parseInt(lector.nextLine().trim());
                this.tablaAFD = new int[this.numEstados][257];
                
                int i = 0;
                while (lector.hasNextLine() && i < this.numEstados) {
                    String renglon = lector.nextLine();
                    String[] valores = renglon.split(";");
                    
                    for (int j = 0; j < 257; j++) {
                        this.tablaAFD[i][j] = Integer.parseInt(valores[j]);
                    }
                    i++;
                }
            }
            lector.close();
            System.out.println("✅ AFD Cargado exitosamente desde: " + rutaArchivo);
            return true;

        } catch (Exception e) {
            System.err.println("❌ Error al leer tabla AFD: " + e.getMessage());
            return false;
        }
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

    public void generarGrafico(String nombreArchivo) {
        File carpetaDots = new File("dots"); File carpetaImagenes = new File("imagenes");
        if (!carpetaDots.exists()) carpetaDots.mkdir(); if (!carpetaImagenes.exists()) carpetaImagenes.mkdir();
        String rutaDot = "dots/" + nombreArchivo + ".dot"; String rutaPng = "imagenes/" + nombreArchivo + ".png";

        StringBuilder dot = new StringBuilder();
        dot.append("digraph AFD {\n  charset=\"UTF-8\";\n  rankdir=LR;\n  node [shape = circle, fontname=\"Arial\"];\n  edge [fontname=\"Arial\"];\n");
        dot.append("  node [shape = none, label=\"\"]; start;\n");
        if (numEstados > 0) dot.append("  start -> 0;\n"); 

        for (int i = 0; i < numEstados; i++) {
            // Muestra el Token en el dibujo si lo tiene
            int token = this.tablaAFD != null ? this.tablaAFD[i][256] : -1;
            String label = i + (token != -1 ? "\\nTk:" + token : "");
            
            if (estadosAceptacion.contains(i)) {
                dot.append("  ").append(i).append(" [shape = doublecircle, label=\"").append(label).append("\"];\n");
            } else {
                dot.append("  ").append(i).append(" [shape = circle, label=\"").append(label).append("\"];\n");
            }
        }

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
            osw.write(dot.toString()); osw.close();
            ProcessBuilder pb = new ProcessBuilder("dot", "-Tpng", rutaDot, "-o", rutaPng);
            pb.inheritIO(); Process p = pb.start(); p.waitFor();
        } catch (Exception ex) { System.err.println("❌ Error: " + ex.getMessage()); }
    }
}