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
            pila.push(indice); // Guarda estado para UndoToken
            if (indice >= expr.length()) return 0; // FIN DE CADENA
            
            char c = expr.charAt(indice++);
            yytext = String.valueOf(c);

            // =======================================================
            // EL IF QUE EL PROFE NO PUSO: VALIDACIÓN DEL ESCAPE '\'
            // =======================================================
            if (c == '\\') {
                if (indice < expr.length()) {
                    c = expr.charAt(indice++); // Consume la diagonal y agarra el siguiente
                    yytext = String.valueOf(c);
                    return 100; // Lo devuelve a la fuerza como un Símbolo normal (SIMB)
                }
            }

            switch(c) {
                case '|': return 10;  // OR
                case '+': return 20;  // CERR_POS
                case '*': return 30;  // CERR_KLEEN
                case '?': return 40;  // OPCIONAL
                case '(': return 50;  // PAR_I
                case ')': return 60;  // PAR_D
                case '[': return 70;  // CORCH_I
                case ']': return 80;  // CORCH_D
                case '-': return 90;  // GUION
                default: return 100;  // SIMB (Cualquier otra letra/número)
            }
        }

        public void UndoToken() {
            if (!pila.isEmpty()) indice = pila.pop();
        }

        // Métodos para la concatenación implícita
        public int GetStatus() { return indice; }
        public void SetStatus(int i) { this.indice = i; }
    }

    // --- VARIABLES GLOBALES DEL PARSER ---
    private LexicoER Lexic;

    public ER_a_AFN(String expresionRegular) {
        Lexic = new LexicoER(expresionRegular);
    }

    // --- MÉTODO PRINCIPAL QUE INICIA LA MAGIA ---
    public AFN convertir() {
        AFN fResultante = new AFN();
        if (E(fResultante)) {
            if (Lexic.yylex() == 0) { // Si terminó de leer toda la expresión sin errores
                return fResultante;
            }
        }
        return null; // Error de sintaxis en la expresión regular
    }

    // -----------------------------------------------------------------
    // REGLAS DEL DESCENSO RECURSIVO (CLONADAS DEL PIZARRÓN DEL PROFE)
    // -----------------------------------------------------------------

    // E -> T E'
    private boolean E(AFN f) {
        if (T(f)) {
            if (Ep(f)) return true;
        }
        return false;
    }

    // E' -> OR T E' | epsilon
    private boolean Ep(AFN f) {
        int token = Lexic.yylex();
        if (token == 10) { // OR '|'
            AFN f2 = new AFN();
            if (T(f2)) {
                f.unionAFN(f2);
                if (Ep(f)) return true;
            }
            return false;
        }
        // Epsilon (vacío)
        Lexic.UndoToken();
        return true;
    }

    // T -> C T'
    private boolean T(AFN f) {
        if (C(f)) {
            if (Tp(f)) return true;
        }
        return false;
    }

    // T' -> C T' | epsilon  (OJO: Aquí va la concatenación implícita)
    private boolean Tp(AFN f) {
        int estadoLexico = Lexic.GetStatus(); // Guardar foto del momento
        AFN f2 = new AFN();
        
        if (C(f2)) { // Intenta leer otra pieza, si existe...
            f.concatenacion(f2); // ¡Es una concatenación!
            if (Tp(f)) return true;
            return false;
        }
        // Epsilon: Si falla, restaura el tiempo porque no había concatenación
        Lexic.SetStatus(estadoLexico);
        return true;
    }

    // C -> F C'
    private boolean C(AFN f) {
        if (F(f)) {
            if (Cp(f)) return true;
        }
        return false;
    }

    // C' -> + C' | * C' | ? C' | epsilon
    private boolean Cp(AFN f) {
        int token = Lexic.yylex();
        switch (token) {
            case 20: // CERR_POS '+'
                f.cerraduraPositiva();
                return Cp(f);
            case 30: // CERR_KLEEN '*'
                f.cerraduraKleene();
                return Cp(f);
            case 40: // OPCIONAL '?'
                f.opcional();
                return Cp(f);
        }
        Lexic.UndoToken();
        return true;
    }

    // F -> ( E ) | [ SIMB - SIMB ] | SIMB
    private boolean F(AFN f) {
        int token = Lexic.yylex();
        switch (token) {
            case 50: // PAR_I '('
                if (E(f)) {
                    int t2 = Lexic.yylex();
                    if (t2 == 60) return true; // PAR_D ')'
                }
                return false;
                
            case 100: // SIMB (Una letra normal)
                f.crearAFNBasico(Lexic.yytext.charAt(0));
                return true;
                
            case 70: // CORCH_I '[' (Rango)
                int t1 = Lexic.yylex();
                if (t1 == 100) { // SIMB
                    char charInferior = Lexic.yytext.charAt(0);
                    int t2 = Lexic.yylex();
                    if (t2 == 90) { // GUION '-'
                        int t3 = Lexic.yylex();
                        if (t3 == 100) { // SIMB
                            char charSuperior = Lexic.yytext.charAt(0);
                            int t4 = Lexic.yylex();
                            if (t4 == 80) { // CORCH_D ']'
                                f.crearAFNBasico(charInferior, charSuperior);
                                return true;
                            }
                        }
                    }
                }
                return false; // Error en sintaxis del corchete
        }
        return false;
    }
}