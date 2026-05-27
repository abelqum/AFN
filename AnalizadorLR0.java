package com.mycompany.userafncreator;

import java.util.HashSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.LinkedList;

public class AnalizadorLR0 {

    // Nosotros usamos la clase Gramatica que ya tenías.
    public Gramatica gramatica; 

    // =========================================================
    // VARIABLES GLOBALES PARA LA TABLA LR(0)
    // =========================================================
    public HashSet<LR0_Conj_Sj> Coleccion; 
    public HashMap<Integer, HashMap<String, Integer>> TablaIrA; // Guarda las transiciones
    
    public String[][] TablaAccion; // Matriz para Desplazar (D) y Reducir (R)
    public int[][] TablaGoto;      // Matriz para los saltos de las Mayúsculas
    
    public ArrayList<Simbolo> arrVn;
    public ArrayList<Simbolo> arrVt;

    public AnalizadorLR0(Gramatica gramatica) {
        this.gramatica = gramatica;
    }

    // =========================================================
    // C_CONTIENE (Basado en la Foto 14)
    // =========================================================
    public boolean C_Contiene(HashSet<ItemLR0> C, ItemLR0 Aux) {
        return C.contains(Aux);
    }

    // =========================================================
    // MOVER_LR0 (Basado en la Foto 7 y 8)
    // =========================================================
    public HashSet<ItemLR0> Mover_LR0(HashSet<ItemLR0> C, String SimboloStr) {
        HashSet<ItemLR0> R = new HashSet<>();
        ArrayList<Simbolo> Lista; 
        Simbolo N;                
        
        R.clear();
        for (ItemLR0 I : C) {
            Lista = gramatica.reglas[I.NumRegla].listaSimb; 
            
            if (I.PosPunto < Lista.size()) {
                N = Lista.get(I.PosPunto);
                
                if (N.nombre.equals(SimboloStr)) { 
                    R.add(new ItemLR0(I.NumRegla, I.PosPunto + 1));
                }
            }
        }
        return R;
    }

    // =========================================================
    // CERRADURA_LR0 (Basado en las Fotos 9, 10, 11, 12, 13)
    // =========================================================
    public HashSet<ItemLR0> Cerradura_LR0(HashSet<ItemLR0> C) {
        HashSet<ItemLR0> R = new HashSet<>();
        HashSet<ItemLR0> Temporal = new HashSet<>();
        ItemLR0 Aux;
        ArrayList<Simbolo> Lista;
        Simbolo N;

        R.clear();
        if (C.isEmpty()) { 
            return R;
        }

        R.addAll(C); 
        Temporal.clear();

        for (ItemLR0 I : C) {
            Lista = gramatica.reglas[I.NumRegla].listaSimb;
            
            if (I.PosPunto < Lista.size()) {
                N = Lista.get(I.PosPunto);
                
                if (!N.esTerminal) { 
                    for (int k = 0; k < gramatica.numReglas; k++) {
                        if (gramatica.reglas[k].simboloIzquierdo.nombre.equals(N.nombre)) {
                            Aux = new ItemLR0(k, 0); 
                            if (!C_Contiene(C, Aux)) {
                                Temporal.add(Aux); 
                            }
                        }
                    }
                }
            }
        }

        if (!Temporal.isEmpty()) {
            R.addAll(Cerradura_LR0(Temporal)); 
        }
        
        return R;
    }

    // =========================================================
    // IRA_LR0 (Basado en la Foto 14 y 15)
    // =========================================================
    public HashSet<ItemLR0> IrA_LR0(HashSet<ItemLR0> C, String SimboloStr) {
        return Cerradura_LR0(Mover_LR0(C, SimboloStr));
    }

    // =========================================================
    // 1. CONSTRUIR COLECCIÓN CANÓNICA (El motor de los estados)
    // =========================================================
    public void ConstruirColeccionCanonica() {
        Coleccion = new HashSet<>();
        TablaIrA = new HashMap<>();
        Queue<LR0_Conj_Sj> Q = new LinkedList<>();

        int numSj = 0;

        // Estado inicial S0 = Cerradura({ Regla Aumentada [0, 0] })
        LR0_Conj_Sj S0 = new LR0_Conj_Sj();
        S0.j = numSj++;
        HashSet<ItemLR0> inicio = new HashSet<>();
        inicio.add(new ItemLR0(0, 0)); 
        S0.Sj = Cerradura_LR0(inicio);

        Coleccion.add(S0);
        Q.add(S0);
        TablaIrA.put(S0.j, new HashMap<>());

        // Juntamos todos los símbolos de la gramática (Vt + Vn) para evaluar transiciones
        ArrayList<String> todosLosSimbolos = new ArrayList<>();
        for(Simbolo s : gramatica.Vt) todosLosSimbolos.add(s.nombre);
        for(Simbolo s : gramatica.Vn) todosLosSimbolos.add(s.nombre);

        while (!Q.isEmpty()) {
            LR0_Conj_Sj C = Q.poll();

            for (String X : todosLosSimbolos) {
                HashSet<ItemLR0> Aux = IrA_LR0(C.Sj, X);

                if (Aux.size() > 0) {
                    int idEncontrado = -1;
                    for (LR0_Conj_Sj existente : Coleccion) {
                        if (existente.Sj.equals(Aux)) {
                            idEncontrado = existente.j;
                            break;
                        }
                    }

                    if (idEncontrado != -1) {
                        TablaIrA.get(C.j).put(X, idEncontrado);
                    } else {
                        LR0_Conj_Sj nuevoSj = new LR0_Conj_Sj();
                        nuevoSj.j = numSj++;
                        nuevoSj.Sj = Aux;

                        Coleccion.add(nuevoSj);
                        Q.add(nuevoSj);
                        
                        TablaIrA.put(nuevoSj.j, new HashMap<>());
                        TablaIrA.get(C.j).put(X, nuevoSj.j);
                    }
                }
            }
        }
    }

    // =========================================================
    // 2. CONSTRUIR TABLA LR(0) (Acción y Goto)
    // =========================================================
    public void ConstruirTablaLR0() {
        arrVn = new ArrayList<>(gramatica.Vn);
        arrVt = new ArrayList<>(gramatica.Vt);
        arrVt.remove(new Simbolo(Simbolo.EPSILON, true)); 
        arrVt.add(new Simbolo("$", true)); 

        int numEstados = Coleccion.size();
        TablaAccion = new String[numEstados][arrVt.size()];
        TablaGoto = new int[numEstados][arrVn.size()];

        for(int i = 0; i < numEstados; i++) {
            for(int j = 0; j < arrVt.size(); j++) TablaAccion[i][j] = "";
            for(int j = 0; j < arrVn.size(); j++) TablaGoto[i][j] = -1;
        }

        for (LR0_Conj_Sj EstadoI : Coleccion) {
            int i = EstadoI.j;

            // A) DESPLAZAMIENTOS (Shift) Y GOTOS
            if (TablaIrA.containsKey(i)) {
                for (Map.Entry<String, Integer> transicion : TablaIrA.get(i).entrySet()) {
                    String simboloStr = transicion.getKey();
                    int destino = transicion.getValue();

                    int colTerminal = arrVt.indexOf(new Simbolo(simboloStr, true));
                    if (colTerminal != -1) {
                        TablaAccion[i][colTerminal] = "D" + destino; 
                    } else {
                        int colNoTerminal = arrVn.indexOf(new Simbolo(simboloStr, false));
                        if (colNoTerminal != -1) {
                            TablaGoto[i][colNoTerminal] = destino; 
                        }
                    }
                }
            }

            // B) REDUCCIONES (Reduce) Y ACEPTACIÓN
            for (ItemLR0 Item : EstadoI.Sj) {
                LadoIzquierdo regla = gramatica.reglas[Item.NumRegla];
                
                if (Item.PosPunto == regla.listaSimb.size() || 
                   (regla.listaSimb.size() == 1 && regla.listaSimb.get(0).nombre.equals(Simbolo.EPSILON))) {
                    
                    if (Item.NumRegla == 0) {
                        int colPesos = arrVt.indexOf(new Simbolo("$", true));
                        TablaAccion[i][colPesos] = "ACEPT";
                    } else {
                        for (int j = 0; j < arrVt.size(); j++) {
                            if (TablaAccion[i][j].isEmpty()) {
                                TablaAccion[i][j] = "R" + Item.NumRegla; 
                            } else if (!TablaAccion[i][j].contains("R" + Item.NumRegla)) {
                                TablaAccion[i][j] += " / R" + Item.NumRegla; 
                            }
                        }
                    }
                }
            }
        }
    }
}