package com.mycompany.userafncreator;

import java.util.Stack;

public class AnalizadorLexico {
    
    // --- VARIABLES GLOBALES DEL ANALIZADOR ---
    private int token;
    private int edoActual;
    private int edoTransicion;
    private String cadenaSigma;
    public String lexema;
    private boolean pasoPorEdoAcept;
    private int iniLexema;
    private int finLexema;
    private int indiceCaracterActual;
    private char caracterActual;
    private Stack<Integer> pila;
    
    private AFD automataFD;

    // Constantes para no usar "números mágicos"
    public static final int TOKEN_ERROR = -1;
    public static final int TOKEN_OMITIR = 20001; // El que indicaba el profe en la UI
    public static final int TOKEN_FIN = 0;

    // --- CONSTRUCTOR ---
    public AnalizadorLexico(String rutaArchivoAFD) {
        cadenaSigma = "";
        pasoPorEdoAcept = false;
        iniLexema = -1;
        finLexema = -1;
        indiceCaracterActual = -1;
        token = -1;
        pila = new Stack<>();
        
        // Cargar el AFD directamente desde el archivo
        automataFD = new AFD();
        automataFD.leerAFDDeArchivo(rutaArchivoAFD);
    }

    // --- CONFIGURAR LA CADENA A ANALIZAR ---
    public void setSigma(String sigma) {
        cadenaSigma = sigma;
        pasoPorEdoAcept = false;
        iniLexema = 0;
        finLexema = -1;
        indiceCaracterActual = 0;
        token = -1;
        pila.clear();
    }

    // --- EL CORAZÓN DEL ANALIZADOR (EL MÉTODO MÁGICO) ---
    public int yylex() {
        while (true) {
            pila.push(indiceCaracterActual); // Guardar estado para posible UndoToken
            
            if (indiceCaracterActual >= cadenaSigma.length()) {
                lexema = "";
                return TOKEN_FIN;
            }
            
            iniLexema = indiceCaracterActual;
            edoActual = 0; // Siempre inicia en el estado 0 del AFD
            pasoPorEdoAcept = false;
            finLexema = -1;
            token = -1;
            
            while (indiceCaracterActual < cadenaSigma.length()) {
                caracterActual = cadenaSigma.charAt(indiceCaracterActual);
                
                // Buscar hacia dónde saltar en la matriz [EstadoActual][CodigoASCII]
                // Se asegura que no sobrepase el ASCII 255
                if(caracterActual >= 0 && caracterActual <= 255) {
                    edoTransicion = automataFD.tablaAFD[edoActual][caracterActual];
                } else {
                    edoTransicion = -1; // Carácter desconocido/fuera de rango
                }
                
                if (edoTransicion != -1) { // Si hay un camino válido
                    // Checar si el nuevo estado es de aceptación (columna 256 tiene el Token)
                    if (automataFD.tablaAFD[edoTransicion][256] != -1) {
                        pasoPorEdoAcept = true;
                        token = automataFD.tablaAFD[edoTransicion][256];
                        finLexema = indiceCaracterActual;
                    }
                    
                    indiceCaracterActual++;
                    edoActual = edoTransicion;
                    continue; // Sigue el bucle buscando el lexema más largo posible
                }
                
                // Si llegó aquí, es porque edoTransicion == -1 (Ya no hay camino)
                break;
            }
            
            // --- EVALUACIÓN DE RESULTADOS ---
            
            // 1. Error Léxico (Se atascó y nunca pasó por un estado de aceptación)
            if (!pasoPorEdoAcept) {
                indiceCaracterActual = iniLexema + 1;
                lexema = cadenaSigma.substring(iniLexema, iniLexema + 1);
                token = TOKEN_ERROR;
                return token; 
            }
            
            // 2. Éxito (Recuperar el lexema válido más largo)
            lexema = cadenaSigma.substring(iniLexema, finLexema + 1);
            indiceCaracterActual = finLexema + 1;
            
            // 3. ¿Es un lexema para ignorar? (Ej: Espacios, saltos de línea)
            if (token == TOKEN_OMITIR) {
                continue; // Reinicia el ciclo gigante (while(true)) buscando el próximo token real
            } else {
                return token; // Retorna el Token válido encontrado
            }
        }
    }

    // --- FUNCIÓN PARA RETROCEDER (BACKTRACKING) ---
    public boolean undoToken() {
        if (pila.isEmpty()) {
            return false;
        }
        indiceCaracterActual = pila.pop();
        return true;
    }
    
    public String getLexema() {
        return lexema;
    }
    public static void main(String[] args) {
        // 1. Ruta del archivo que acabas de generar en la interfaz
        String rutaMiAFD = "tablas_afd/Tabla_AFD_100.txt"; 
        
        AnalizadorLexico lexico = new AnalizadorLexico(rutaMiAFD);
        
        // 2. Nuestra frase de prueba (tiene letras, números, sumas y espacios)
        String codigoFuente = "abc + 123 + hola"; 
        lexico.setSigma(codigoFuente);
        
        System.out.println("\n--- INICIANDO ANÁLISIS LÉXICO ---");
        System.out.println("Cadena a analizar: \"" + codigoFuente + "\"\n");
        
        int tokenEncontrado;
        
        // 3. Ejecutar el analizador hasta que se acabe la cadena
        while ((tokenEncontrado = lexico.yylex()) != AnalizadorLexico.TOKEN_FIN) {
            if (tokenEncontrado == AnalizadorLexico.TOKEN_ERROR) {
                System.out.println("❌ ERROR LÉXICO: Carácter no reconocido -> '" + lexico.getLexema() + "'");
            } else {
                System.out.println("✅ Token: " + tokenEncontrado + " | Lexema: '" + lexico.getLexema() + "'");
            }
        }
        System.out.println("\n--- ANÁLISIS FINALIZADO ---");
    }
}