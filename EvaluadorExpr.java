package com.mycompany.userafncreator;

public class EvaluadorExpr {
    
    private String expresion;
    public float result;
    public String exprPost;
    public AnalizadorLexico L; // Instancia de nuestro lexer

    // Constructor que carga directo el archivo de la tabla
    public EvaluadorExpr(String sigma, String fileAFD) {
        this.expresion = sigma;
        this.L = new AnalizadorLexico(fileAFD);
        this.L.setSigma(sigma);
    }
    
    // Constructor auxiliar
    public EvaluadorExpr(String fileAFD) {
        this.L = new AnalizadorLexico(fileAFD);
    }

    public void setExpresion(String sigma) {
        this.expresion = sigma;
        this.L.setSigma(sigma);
    }

    // --- PUNTO DE ENTRADA DEL SINTÁCTICO ---
    public boolean iniEval() {
        int token;
        
        // Simulación del "ref" usando arreglos de 1 posición
        float[] v = new float[1]; 
        String[] postfijo = new String[1];
        
        postfijo[0] = "";
        v[0] = 0f;

        // Arranca el Descenso Recursivo llamando a E (Expresión)
        if (E(v, postfijo)) {
            token = L.yylex();
            if (token == AnalizadorLexico.TOKEN_FIN) { // Si ya se acabó la cadena (Token 0)
                this.result = v[0];
                this.exprPost = postfijo[0];
                return true;
            }
        }
        return false;
    }

    // ----------------------------------------------------
    // REGLAS GRAMATICALES (DESCENSO RECURSIVO)
    // ----------------------------------------------------

    // Regla: E -> T E'
    private boolean E(float[] v, String[] post) {
        if (T(v, post)) {
            if (Ep(v, post)) {
                return true;
            }
        }
        return false;
    }

    // Regla: E' -> + T E' | - T E' | epsilon
    private boolean Ep(float[] v, String[] post) {
        int token = L.yylex();
        
        if (token == 10 || token == 20) { // Token 10 (+) o 20 (-)
            float[] v2 = new float[1];
            String[] post2 = new String[1];
            post2[0] = "";
            
            if (T(v2, post2)) {
                // Cálculo matemático de la suma/resta
                v[0] = v[0] + (token == 10 ? v2[0] : -v2[0]);
                // Concatenación para la notación Postfija
                post[0] = post[0] + " " + post2[0] + " " + (token == 10 ? "+" : "-");
                
                if (Ep(v, post)) {
                    return true;
                }
            }
            return false;
        }
        
        // Transición Epsilon (vacío): si no hay + ni -, regresamos el token a la pila
        L.undoToken();
        return true;
    }

    // Regla: T -> F T'
    private boolean T(float[] v, String[] post) {
        if (F(v, post)) {
            if (Tp(v, post)) {
                return true;
            }
        }
        return false;
    }

    // Regla: T' -> * F T' | / F T' | epsilon
    private boolean Tp(float[] v, String[] post) {
        int token = L.yylex();
        
        if (token == 30 || token == 40) { // Token 30 (*) o 40 (/)
            float[] v2 = new float[1];
            String[] post2 = new String[1];
            post2[0] = "";
            
            if (F(v2, post2)) {
                // Cálculo matemático de mult/división
                v[0] = v[0] * (token == 30 ? v2[0] : (1.0f / v2[0])); 
                // Notación Postfija
                post[0] = post[0] + " " + post2[0] + " " + (token == 30 ? "*" : "/");
                
                if (Tp(v, post)) {
                    return true;
                }
            }
            return false;
        }
        
        // Transición Epsilon
        L.undoToken();
        return true;
    }

    // Regla: F -> ( E ) | NUM
    private boolean F(float[] v, String[] post) {
        int token = L.yylex();
        
        switch (token) {
            case 50: // Token 50 '(' -> Paréntesis izquierdo
                if (E(v, post)) {
                    token = L.yylex();
                    if (token == 60) { // Token 60 ')' -> Paréntesis derecho
                        return true;
                    }
                }
                return false;
                
            case 70: // Token 70 'NUM' -> Número
                v[0] = Float.parseFloat(L.getLexema());
                post[0] = L.getLexema();
                return true;
                
            default: // Si llega algo que no es ni número ni paréntesis, falla.
                return false;
        }
    }
}