package com.mycompany.userafncreator;

import java.util.ArrayList;

public class DescRecGram_Gram {
    
    public String cadenaGramatica;
    public Gramatica gramaticaResultante;

    // --- MINI-LEXER NATIVO (Adiós Tabla 999) ---
    private String[] tokensNativos;
    private int indiceActual;
    private String lexemaActual;

    // Ya no pedimos archivoAFD, solo la cadena
    public DescRecGram_Gram(String sigma) {
        this.cadenaGramatica = sigma;
        
        // Cortamos por espacios en blanco (¡La magia del Lexer Nativo!)
        // Si la cadena está vacía, evitamos arreglos basura
        if (sigma.trim().isEmpty()) {
            this.tokensNativos = new String[0];
        } else {
            this.tokensNativos = sigma.trim().split("\\s+");
        }
        
        this.indiceActual = 0;
        this.lexemaActual = "";
        
        this.gramaticaResultante = new Gramatica(100);
        this.gramaticaResultante.numReglas = 0;
    }

    // =========================================================
    // MÉTODOS DEL MINI-LEXER
    // =========================================================
    private void undoToken() {
        if (indiceActual > 0) indiceActual--;
    }

    private String getLexema() {
        return lexemaActual;
    }

    private int pedirToken() {
        // 🔥 CORRECCIÓN CLAVE AQUÍ PARA EL FIN DE ARCHIVO 🔥
        if (indiceActual >= tokensNativos.length) {
            lexemaActual = "";
            indiceActual++; // Truco vital: avanzar en el abismo para que el undoToken regrese aquí y no al último ';'
            return AnalizadorLexico.TOKEN_FIN; // 0
        }
        
        lexemaActual = tokensNativos[indiceActual++];
        
        if (lexemaActual.equals("->")) return TokensGramatica.FLECHA; // 10
        if (lexemaActual.equals(";")) return TokensGramatica.PC;     // 20
        if (lexemaActual.equals("|")) return TokensGramatica.OR;     // 30
        
        // Estandarizar epsilon para que no haya confusiones visuales en la tabla
        if (lexemaActual.equalsIgnoreCase("epsilon") || lexemaActual.equals("ε")) {
            lexemaActual = Simbolo.EPSILON;
        }
        
        // Si no es ninguno de los anteriores, ¡ES UN SÍMBOLO UNIVERSAL!
        return TokensGramatica.SIMBOLO; // 40
    }

    // =========================================================
    // INICIO DEL DESCENSO RECURSIVO 
    // =========================================================
    public boolean G() {
        if (ListaReglas()) {
            int token = pedirToken();
            if (token == AnalizadorLexico.TOKEN_FIN || token == 0) {
                return true;
            }
        }
        return false;
    }

    // ListaReglas -> Regla ListaReglas | Regla
    private boolean ListaReglas() {
        if (Regla()) {
            int token = pedirToken();
            if (token == TokensGramatica.SIMBOLO) {
                undoToken(); // Viene otra regla
                if (ListaReglas()) {
                    return true;
                }
                return false;
            }
            undoToken(); // Ya no hay reglas
            return true;
        }
        return false;
    }

    // Regla -> SIMBOLO FLECHA LadosDerechos PC
    private boolean Regla() {
        int token = pedirToken();
        if (token == TokensGramatica.SIMBOLO) {
            String lexemaJefe = getLexema();
            Simbolo s = new Simbolo(lexemaJefe, false); 
            
            // Evitamos duplicados en la lista de No Terminales
            if(!gramaticaResultante.Vn.contains(s)) {
                gramaticaResultante.Vn.add(s); 
            }

            if (pedirToken() == TokensGramatica.FLECHA) { 
                if (LadosDerechos(s)) {
                    if (pedirToken() == TokensGramatica.PC) { 
                        return true;
                    }
                }
            }
        }
        return false;
    }

    // LadosDerechos -> LadoDerecho LadosDerechosP
    private boolean LadosDerechos(Simbolo s) {
        if (LadoDerecho(s)) {
            if (LadosDerechosP(s)) {
                return true;
            }
            return false;
        }
        return false;
    }

    // LadosDerechosP -> OR LadoDerecho LadosDerechosP | epsilon
    private boolean LadosDerechosP(Simbolo s) {
        int Token = pedirToken();
        if (Token == TokensGramatica.OR) { 
            if (LadoDerecho(s)) {
                if (LadosDerechosP(s)) {
                    return true;
                }
                return false;
            }
            return false;
        }
        undoToken(); // Transición Epsilon
        return true;
    }

    // LadoDerecho -> ListaSimbolos
    private boolean LadoDerecho(Simbolo s) {
        ArrayList<Simbolo> l = new ArrayList<>(); 
        l.clear(); 
        
        if (ListaSimbolos(l)) {
            LadoIzquierdo nuevaProduccion = new LadoIzquierdo(s, l);
            gramaticaResultante.reglas[gramaticaResultante.numReglas] = nuevaProduccion;
            gramaticaResultante.numReglas++;
            return true;
        }
        return false;
    }

    // ListaSimbolos -> SIMBOLO ListaSimbolosP
    private boolean ListaSimbolos(ArrayList<Simbolo> l) {
        int Token = pedirToken();
        if (Token == TokensGramatica.SIMBOLO) {
            String lexema = getLexema();
            
            // Si empieza con minúscula o no es letra, es Terminal
            boolean esTerminal = Character.isLowerCase(lexema.charAt(0)) || !Character.isLetter(lexema.charAt(0));
            Simbolo S = new Simbolo(lexema, esTerminal); 
            
            // Llenamos los vocabularios sin repetir
            if (esTerminal) {
                if(!gramaticaResultante.Vt.contains(S)) gramaticaResultante.Vt.add(S);
            } else {
                if(!gramaticaResultante.Vn.contains(S)) gramaticaResultante.Vn.add(S);
            }

            if (ListaSimbolosP(l)) {
                l.add(0, S); 
                return true;
            }
        }
        
        return false;
    }

    // ListaSimbolosP -> SIMBOLO ListaSimbolosP | epsilon
    private boolean ListaSimbolosP(ArrayList<Simbolo> l) {
        int Token = pedirToken();
        if (Token == TokensGramatica.SIMBOLO) {
            undoToken(); 
            return ListaSimbolos(l);
        }
        undoToken(); // Epsilon
        return true;
    }
}