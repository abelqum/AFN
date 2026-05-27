package com.mycompany.userafncreator;
import java.util.HashSet;
import java.util.ArrayList;

public class AnalizadorLL1 {
    
    public Gramatica DescRecG; // Mismo nombre que usa el profe
    public int[][] TablaLL;    // La matriz
    public ArrayList<Simbolo> arrVn; // Renglones (Mayúsculas)
    public ArrayList<Simbolo> arrVt; // Columnas (Minúsculas)

    public AnalizadorLL1(Gramatica gramatica) {
        this.DescRecG = gramatica;
        this.arrVn = new ArrayList<>(gramatica.Vn);
        this.arrVt = new ArrayList<>(gramatica.Vt);
        
        // Quitar Epsilon de las columnas si se coló, y agregar el Fin de Cadena ($)
        this.arrVt.remove(new Simbolo(Simbolo.EPSILON, true));
        this.arrVt.add(new Simbolo("$", true));

        // Inicializar la tabla con -1 (Dimensiones de las fotos 93 y 94 del profe)
        TablaLL = new int[arrVn.size() + 1][arrVt.size() + 1];
        for (int i = 0; i < arrVn.size() + 1; i++) {
            for (int j = 0; j < arrVt.size() + 1; j++) {
                TablaLL[i][j] = -1;
            }
        }
    }

    // =========================================================
    // CREACIÓN DE LA TABLA LL(1) (CALCA EXACTA DE LA FOTO 35)
    // =========================================================
    public void construirTablaLL1() {
        for (int i = 0; i < DescRecG.numReglas; i++) {
            
            // AuxVt = Firts(ArrReglas[i].LadoDer);
            HashSet<Simbolo> AuxVt = Firts(DescRecG.reglas[i].listaSimb);
            
            // Renglon = ObtenerIndice(ArrReglas[i].LadoIzq.Simb);
            int Renglon = ObtenerIndice(DescRecG.reglas[i].simboloIzquierdo);
            
            Simbolo epsilon = new Simbolo(Simbolo.EPSILON, true);
            
            // Lógica de la flecha azul del pizarrón
            // if (AuxVt.Contiene(Epsilon))
            if (AuxVt.contains(epsilon)) {
                // AuxVt.Remove(Epsilon);
                AuxVt.remove(epsilon);
                // AuxVt.Union(Follw(ArrReglas[i].LadoIzq.Simb));
                AuxVt.addAll(Follw(DescRecG.reglas[i].simboloIzquierdo)); 
            }

            // foreach(string s in AuxVt)
            for (Simbolo s : AuxVt) {
                int Columna = ObtenColumna(s);
                if (Columna != -1 && Renglon != -1) {
                    TablaLL[Renglon][Columna] = i; // Guardamos el índice de la regla en la celda
                }
            }
        }
    }

    private int ObtenerIndice(Simbolo s) {
        return arrVn.indexOf(s);
    }

    private int ObtenColumna(Simbolo s) {
        return arrVt.indexOf(s);
    }

    // =========================================================
    // MÉTODOS FIRST Y FOLLOW (Lógica estándar de compiladores)
    // =========================================================
    
    // Firts(LadoDer)
    public HashSet<Simbolo> Firts(ArrayList<Simbolo> listaLadoDerecho) {
        HashSet<Simbolo> result = new HashSet<>();
        if (listaLadoDerecho.isEmpty()) {
            result.add(new Simbolo(Simbolo.EPSILON, true));
            return result;
        }

        Simbolo primerSimbolo = listaLadoDerecho.get(0);
        
        if (primerSimbolo.esTerminal) {
            result.add(primerSimbolo);
            return result;
        }

        // Si es No Terminal (Mayúscula), buscamos sus producciones
        for (int i = 0; i < DescRecG.numReglas; i++) {
            if (DescRecG.reglas[i].simboloIzquierdo.equals(primerSimbolo)) {
                // Evitar recursividad infinita si la regla es E -> E + T
                if (!DescRecG.reglas[i].listaSimb.isEmpty() && !DescRecG.reglas[i].listaSimb.get(0).equals(primerSimbolo)) {
                    result.addAll(Firts(DescRecG.reglas[i].listaSimb));
                }
            }
        }
        return result;
    }

    // Follw(SimbIzq)
    public HashSet<Simbolo> Follw(Simbolo noTerminal) {
        HashSet<Simbolo> result = new HashSet<>();
        
        // Si es el símbolo inicial de la gramática (Regla 0), se le agrega el '$'
        if (DescRecG.reglas[0].simboloIzquierdo.equals(noTerminal)) {
            result.add(new Simbolo("$", true));
        }

        for (int i = 0; i < DescRecG.numReglas; i++) {
            ArrayList<Simbolo> ladoDer = DescRecG.reglas[i].listaSimb;
            for (int j = 0; j < ladoDer.size(); j++) {
                if (ladoDer.get(j).equals(noTerminal)) {
                    if (j + 1 < ladoDer.size()) {
                        // Lo que sigue después de nuestra variable
                        ArrayList<Simbolo> resto = new ArrayList<>(ladoDer.subList(j + 1, ladoDer.size()));
                        HashSet<Simbolo> firstResto = Firts(resto);
                        Simbolo epsilon = new Simbolo(Simbolo.EPSILON, true);
                        
                        if (firstResto.contains(epsilon)) {
                            firstResto.remove(epsilon);
                            result.addAll(firstResto);
                            // Si lo de adelante se puede desvanecer (epsilon), heredamos el Follow del jefe
                            if (!DescRecG.reglas[i].simboloIzquierdo.equals(noTerminal)) {
                                result.addAll(Follw(DescRecG.reglas[i].simboloIzquierdo));
                            }
                        } else {
                            result.addAll(firstResto);
                        }
                    } else {
                        // Si está al final de la regla, hereda el Follow del jefe
                        if (!DescRecG.reglas[i].simboloIzquierdo.equals(noTerminal)) {
                            result.addAll(Follw(DescRecG.reglas[i].simboloIzquierdo));
                        }
                    }
                }
            }
        }
        return result;
    }
}