package com.mycompany.userafncreator;

import java.util.Stack;

public class ER_a_AFN {

    // --- MINI-LEXER ESPECIAL PARA LEER EXPRESIONES REGULARES ---
    private class LexicoER {
        String expr;
        int indice;
        String yytext;
        Stack<Integer> pila;

        public LexicoER(String expr) {
            this.expr = expr;
            this.indice = 0;
            this.pila = new Stack<>();
        }

        public int yylex() {
            pila.push(indice); 
            if (indice >= expr.length()) return 0; 
            
            char c = expr.charAt(indice++);
            yytext = String.valueOf(c);

            // =======================================================
            // MEJORA: TRADUCTOR DE ESCAPES (\n, \s, \r, \t)
            // =======================================================
            if (c == '\\') {
                if (indice < expr.length()) {
                    c = expr.charAt(indice++); 
                    
                    if (c == 'n') c = '\n';       // Salto de línea
                    else if (c == 'r') c = '\r';  // Retorno de carro
                    else if (c == 't') c = '\t';  // Tabulador
                    else if (c == 's') c = ' ';   // Espacio en blanco

                    yytext = String.valueOf(c);
                    return 100; // SIMB
                }
            }

            switch(c) {
                case '|': return 10;
                case '+': return 20;
                case '*': return 30;
                case '?': return 40;
                case '(': return 50;
                case ')': return 60;
                case '[': return 70;
                case ']': return 80;
                case '-': return 90;
                default: return 100; 
            }
        }

        public void UndoToken() {
            if (!pila.isEmpty()) indice = pila.pop();
        }

        public int GetStatus() { return indice; }
        public void SetStatus(int i) { this.indice = i; }
    }

    private LexicoER Lexic;

    public ER_a_AFN(String expresionRegular) {
        Lexic = new LexicoER(expresionRegular);
    }

    public AFN convertir() {
        AFN fResultante = new AFN();
        if (E(fResultante)) {
            if (Lexic.yylex() == 0) { 
                return fResultante;
            }
        }
        return null; 
    }

    private boolean E(AFN f) {
        if (T(f)) {
            if (Ep(f)) return true;
        }
        return false;
    }

    private boolean Ep(AFN f) {
        int token = Lexic.yylex();
        if (token == 10) { 
            AFN f2 = new AFN();
            if (T(f2)) {
                f.unionAFN(f2);
                if (Ep(f)) return true;
            }
            return false;
        }
        Lexic.UndoToken();
        return true;
    }

    private boolean T(AFN f) {
        if (C(f)) {
            if (Tp(f)) return true;
        }
        return false;
    }

    private boolean Tp(AFN f) {
        int estadoLexico = Lexic.GetStatus(); 
        AFN f2 = new AFN();
        
        if (C(f2)) { 
            f.concatenacion(f2); 
            if (Tp(f)) return true;
            return false;
        }
        Lexic.SetStatus(estadoLexico);
        return true;
    }

    private boolean C(AFN f) {
        if (F(f)) {
            if (Cp(f)) return true;
        }
        return false;
    }

    private boolean Cp(AFN f) {
        int token = Lexic.yylex();
        switch (token) {
            case 20: f.cerraduraPositiva(); return Cp(f);
            case 30: f.cerraduraKleene(); return Cp(f);
            case 40: f.opcional(); return Cp(f);
        }
        Lexic.UndoToken();
        return true;
    }

    private boolean F(AFN f) {
        int token = Lexic.yylex();
        switch (token) {
            case 50: 
                if (E(f)) {
                    int t2 = Lexic.yylex();
                    if (t2 == 60) return true; 
                }
                return false;
                
            case 100: 
                f.crearAFNBasico(Lexic.yytext.charAt(0));
                return true;
                
            case 70: 
                int t1 = Lexic.yylex();
                if (t1 == 100) { 
                    char charInferior = Lexic.yytext.charAt(0);
                    int t2 = Lexic.yylex();
                    if (t2 == 90) { 
                        int t3 = Lexic.yylex();
                        if (t3 == 100) { 
                            char charSuperior = Lexic.yytext.charAt(0);
                            int t4 = Lexic.yylex();
                            if (t4 == 80) { 
                                f.crearAFNBasico(charInferior, charSuperior);
                                return true;
                            }
                        }
                    }
                }
                return false; 
        }
        return false;
    }
}