package com.mycompany.userafncreator;

import java.util.ArrayList;

public class DescRecGram_Gram {
    
    public String cadenaGramatica;
    public AnalizadorLexico L; 
    public Gramatica gramaticaResultante;

    public DescRecGram_Gram(String sigma, String archivoAFD) {
        this.cadenaGramatica = sigma;
        this.L = new AnalizadorLexico(archivoAFD);
        this.L.setSigma(sigma);
        
        this.gramaticaResultante = new Gramatica(100);
        this.gramaticaResultante.numReglas = 0;
    }

    // =========================================================
    // LA CURA CONTRA LOS ESPACIOS (Filtro Léxico)
    // =========================================================
    private int pedirToken() {
        int token;
        do {
            token = L.yylex();
        } while (token == 20001); // Ignorar espacios, saltos de línea y tabuladores
        return token;
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
                L.undoToken(); // Viene otra regla
                if (ListaReglas()) {
                    return true;
                }
                return false;
            }
            L.undoToken(); // Ya no hay reglas
            return true;
        }
        return false;
    }

    // Regla -> SIMBOLO FLECHA LadosDerechos PC
    private boolean Regla() {
        int token = pedirToken();
        if (token == TokensGramatica.SIMBOLO) {
            String lexemaJefe = L.getLexema();
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
        L.undoToken(); // Transición Epsilon
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
            String lexema = L.getLexema();
            
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
        
        // Manejo de Epsilon
        if (L.getLexema().equalsIgnoreCase("epsilon") || L.getLexema().equalsIgnoreCase("ε")) {
             Simbolo S = new Simbolo(Simbolo.EPSILON, true);
             l.add(0, S);
             return true;
        }

        return false;
    }

    // ListaSimbolosP -> SIMBOLO ListaSimbolosP | epsilon
    private boolean ListaSimbolosP(ArrayList<Simbolo> l) {
        int Token = pedirToken();
        if (Token == TokensGramatica.SIMBOLO) {
            L.undoToken(); 
            return ListaSimbolos(l);
        }
        L.undoToken(); // Epsilon
        return true;
    }
}