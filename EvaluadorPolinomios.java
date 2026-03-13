package com.mycompany.userafncreator;

public class EvaluadorPolinomios {
    
    private String expresion;
    public AnalizadorLexico L;

    public EvaluadorPolinomios(String sigma, String fileAFD) {
        this.expresion = sigma;
        this.L = new AnalizadorLexico(fileAFD);
        this.L.setSigma(sigma);
    }

    // --- PUNTO DE ENTRADA ---
    public boolean iniEval() {
        // Arranca llamando a la regla inicial S -> ID = E
        if (S()) {
            int token = L.yylex();
            if (token == AnalizadorLexico.TOKEN_FIN) { 
                return true; // Análisis Sintáctico Perfecto
            }
        }
        return false;
    }

    // ----------------------------------------------------
    // REGLAS GRAMATICALES (DESCENSO RECURSIVO)
    // ----------------------------------------------------

    // S -> ID = E
    private boolean S() {
        int token = L.yylex();
        if (token == 80) { // ID
            if (L.yylex() == 40) { // =
                if (E()) {
                    return true;
                }
            }
        }
        return false;
    }

    // E -> T E'
    private boolean E() {
        if (T()) {
            if (Ep()) return true;
        }
        return false;
    }

    // E' -> + T E' | - T E' | epsilon
    private boolean Ep() {
        int token = L.yylex();
        if (token == 10 || token == 20) { // + o -
            if (T()) {
                if (Ep()) return true;
            }
            return false;
        }
        L.undoToken(); // Transición Epsilon
        return true;
    }

    // T -> F T'
    private boolean T() {
        if (F()) {
            if (Tp()) return true;
        }
        return false;
    }

    // T' -> * F T' | epsilon
    private boolean Tp() {
        int token = L.yylex();
        if (token == 30) { // *
            if (F()) {
                if (Tp()) return true;
            }
            return false;
        }
        L.undoToken(); // Transición Epsilon
        return true;
    }

    // F -> ( E ) | ID | POLINOMIO
    private boolean F() {
        int token = L.yylex();
        
        if (token == 50) { // (
            if (E()) {
                if (L.yylex() == 60) return true; // )
            }
            return false;
            
        } else if (token == 80) { // ID (Ej. P1)
            return true;
            
        } else {
            L.undoToken(); // No fue '(' ni 'ID', intentamos POLINOMIO
            if (Polinomio()) {
                return true;
            }
        }
        return false;
    }

    // POLINOMIO -> MON P'
    private boolean Polinomio() {
        if (Mon()) {
            if (PolinomioPrima()) return true;
        }
        return false;
    }

    // P' -> OP POLINOMIO | epsilon
    private boolean PolinomioPrima() {
        int token = L.yylex();
        if (token == 10 || token == 20) { // OP (+ o -)
            if (Polinomio()) return true;
            return false;
        }
        L.undoToken(); // Epsilon
        return true;
    }

    // MON -> NUM VAR ^ INT | NUM VAR | VAR | NUM
    private boolean Mon() {
        int token = L.yylex();
        
        if (token == 100) { // NUM
            int t2 = L.yylex();
            if (t2 == 90) { // VAR (Ej. 'x')
                int t3 = L.yylex();
                if (t3 == 70) { // ^
                    int t4 = L.yylex();
                    if (t4 == 100) { // INT
                        return true; // Coincide: NUM VAR ^ INT
                    }
                    return false; // Error: falta el exponente tras el ^
                }
                L.undoToken(); // Retrocede el ^
                return true; // Coincide: NUM VAR
            }
            L.undoToken(); // Retrocede la VAR
            return true; // Coincide: Solo NUM
            
        } else if (token == 90) { // VAR (Ej. 'x' sola)
            return true;
        }
        
        L.undoToken(); // No coincide con nada
        return false; 
    }
}