package com.mycompany.userafncreator;

import java.util.HashSet;
import java.util.HashMap;
import java.util.Stack;
import java.util.Queue;
import java.util.LinkedList;
import java.io.File;
import java.io.FileWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

// Clase de apoyo para el algoritmo de subconjuntos (el "sj" del pizarrón)
class Subconjunto {
    int j; // ID del subconjunto (estado en el AFD)
    HashSet<Estado> conjEstados;
    HashMap<Character, Integer> arregloTrans; // Transiciones de este subconjunto

    public Subconjunto() {
        conjEstados = new HashSet<>();
        arregloTrans = new HashMap<>();
    }
}

public class AFN {
    // Atributos
    public Estado estadoInicial;
    public HashSet<Character> alfabeto;
    public HashSet<Estado> estadosAFN;
    public HashSet<Estado> estadosAcept;
    public int idAFN;

    // Estáticos
    public static HashSet<AFN> coleccionAFN = new HashSet<>();
    public static int contadorAFNS = 0;
    public static final char EPSILON = '\0';

    public AFN() {
        this.idAFN = contadorAFNS++;
        this.alfabeto = new HashSet<>();
        this.estadosAFN = new HashSet<>();
        this.estadosAcept = new HashSet<>();
    }

    // --- MÉTODOS DE THOMPSON (Construcción) ---
    
    public AFN crearAFNBasico(char c) {
        Estado e1 = new Estado();
        Estado e2 = new Estado();
        Transicion t = new Transicion();
        t.simbolo1 = c;
        t.simbolo2 = c;
        t.edoFinal = e2;
        
        e1.transiciones.add(t);
        e2.edoAccept = true;
        
        this.estadoInicial = e1;
        this.alfabeto.add(c);
        this.estadosAFN.add(e1);
        this.estadosAFN.add(e2);
        this.estadosAcept.add(e2);
        
        coleccionAFN.add(this);
        return this;
    }

    public AFN crearAFNBasico(char c1, char c2) {
        Estado e1 = new Estado();
        Estado e2 = new Estado();
        Transicion t = new Transicion();

        t.simbolo1 = c1;
        t.simbolo2 = c2;
        t.edoFinal = e2;

        e1.transiciones.add(t);
        e2.edoAccept = true;

        this.estadoInicial = e1;
        this.estadosAFN.add(e1);
        this.estadosAFN.add(e2);
        this.estadosAcept.add(e2);

        for (char c = c1; c <= c2; c++) {
            this.alfabeto.add(c);
        }

        coleccionAFN.add(this);
        return this;
    }

    public AFN unionAFN(AFN f2) {
        Estado e1 = new Estado(); 
        Estado e2 = new Estado(); 

        e1.transiciones.add(new Transicion(EPSILON, this.estadoInicial));
        e1.transiciones.add(new Transicion(EPSILON, f2.estadoInicial));

        for (Estado e : this.estadosAcept) {
            e.transiciones.add(new Transicion(EPSILON, e2));
            e.edoAccept = false;
        }
        for (Estado e : f2.estadosAcept) {
            e.transiciones.add(new Transicion(EPSILON, e2));
            e.edoAccept = false;
        }

        e2.edoAccept = true;
        this.estadoInicial = e1;
        this.alfabeto.addAll(f2.alfabeto);
        this.estadosAFN.addAll(f2.estadosAFN);
        this.estadosAFN.add(e1);
        this.estadosAFN.add(e2);
        this.estadosAcept.clear();
        this.estadosAcept.add(e2);

        AFN.coleccionAFN.remove(f2); 
        return this;
    }
    
    public AFN concatenacion(AFN f2) {
        for (Estado e : this.estadosAcept) {
            for (Transicion t : f2.estadoInicial.transiciones) {
                e.transiciones.add(t);
            }
            e.edoAccept = false; 
        }

        this.alfabeto.addAll(f2.alfabeto);
        this.estadosAFN.addAll(f2.estadosAFN);
        this.estadosAFN.remove(f2.estadoInicial); 
        this.estadosAcept.clear();
        this.estadosAcept.addAll(f2.estadosAcept);
        
        AFN.coleccionAFN.remove(f2); 
        return this;
    }

    public AFN cerraduraPositiva() {
        Estado e1 = new Estado();
        Estado e2 = new Estado();

        e1.transiciones.add(new Transicion(EPSILON, this.estadoInicial));

        for (Estado e : this.estadosAcept) {
            e.transiciones.add(new Transicion(EPSILON, e2));
            e.transiciones.add(new Transicion(EPSILON, this.estadoInicial));
            e.edoAccept = false;
        }

        e2.edoAccept = true;
        this.estadoInicial = e1;
        this.estadosAFN.add(e1);
        this.estadosAFN.add(e2);
        this.estadosAcept.clear();
        this.estadosAcept.add(e2);

        return this;
    }

    public AFN opcional() {
        Estado e1 = new Estado();
        Estado e2 = new Estado();

        e1.transiciones.add(new Transicion(EPSILON, this.estadoInicial)); 
        e1.transiciones.add(new Transicion(EPSILON, e2)); 

        for (Estado e : this.estadosAcept) {
            e.transiciones.add(new Transicion(EPSILON, e2));
            e.edoAccept = false; 
        }

        e2.edoAccept = true; 
        this.estadoInicial = e1;
        this.estadosAFN.add(e1);
        this.estadosAFN.add(e2);
        this.estadosAcept.clear();
        this.estadosAcept.add(e2);
        return this;
    }

    public AFN cerraduraKleene() {
        this.cerraduraPositiva();
        Estado eIni = this.estadoInicial;
        Estado eFin = null;
        
        for(Estado e : this.estadosAcept) eFin = e;

        if (eIni != null && eFin != null) {
            eIni.transiciones.add(new Transicion(EPSILON, eFin));
        }
        return this;
    }
    
    // --- MÉTODOS DE CONVERSIÓN AFD (Subconjuntos) ---

    public HashSet<Estado> cerraduraEpsilon(Estado e) {
        HashSet<Estado> c = new HashSet<>();
        Stack<Estado> p = new Stack<>();
        
        c.clear();
        p.clear();
        p.push(e);

        while (!p.isEmpty()) {
            Estado actual = p.pop();
            c.add(actual);
            
            for (Transicion t : actual.transiciones) {
                if (t.simbolo1 == EPSILON) {
                    if (!c.contains(t.edoFinal)) {
                        p.push(t.edoFinal);
                    }
                }
            }
        }
        return c;
    }

    public HashSet<Estado> cerraduraEpsilon(HashSet<Estado> r) {
        HashSet<Estado> c = new HashSet<>(r);
        Stack<Estado> p = new Stack<>();

        for (Estado e : r) {
            p.push(e);
        }

        while (!p.isEmpty()) {
            Estado actual = p.pop();
            for (Transicion t : actual.transiciones) {
                if (t.simbolo1 == EPSILON) {
                    if (!c.contains(t.edoFinal)) {
                        c.add(t.edoFinal);
                        p.push(t.edoFinal);
                    }
                }
            }
        }
        return c;
    }

    public HashSet<Estado> mover(Estado e, char c) {
        HashSet<Estado> r = new HashSet<>();
        r.addAll(e.tieneTransicionA(c));
        return r;
    }

    public HashSet<Estado> mover(HashSet<Estado> A, char c) {
        HashSet<Estado> r = new HashSet<>();
        for (Estado e : A) {
            r.addAll(e.tieneTransicionA(c));
        }
        return r;
    }

    public HashSet<Estado> irA(HashSet<Estado> A, char c) {
        return cerraduraEpsilon(mover(A, c));
    }

    public AFD convertirAAFD() {
        HashSet<Subconjunto> R = new HashSet<>();
        Queue<Subconjunto> Q = new LinkedList<>();
        
        Subconjunto C = new Subconjunto();
        int numSJ = 0;
        int idEncontrado;

        // Paso 1: Cerradura Epsilon del estado inicial
        C.conjEstados = cerraduraEpsilon(this.estadoInicial);
        C.j = numSJ++;
        R.add(C);
        Q.add(C);

        // Paso 2: Evaluar transiciones
        while (!Q.isEmpty()) {
            C = Q.poll(); 

            for (char a : this.alfabeto) {
                Subconjunto sjTemp = new Subconjunto();
                sjTemp.conjEstados = irA(C.conjEstados, a);

                if (sjTemp.conjEstados.isEmpty()) continue;

                idEncontrado = -1;
                for (Subconjunto existente : R) {
                    if (existente.conjEstados.equals(sjTemp.conjEstados)) {
                        idEncontrado = existente.j;
                        break;
                    }
                }

                if (idEncontrado != -1) {
                    C.arregloTrans.put(a, idEncontrado);
                } else {
                    sjTemp.j = numSJ++;
                    C.arregloTrans.put(a, sjTemp.j);
                    R.add(sjTemp);
                    Q.add(sjTemp);
                }
            }
        }

        // Paso 3: Construir el objeto AFD
        AFD afdGenerado = new AFD();
        afdGenerado.numEstados = numSJ;

        for (Subconjunto sj : R) {
            afdGenerado.tablaTransiciones.put(sj.j, sj.arregloTrans);
            
            // Verificar si este subconjunto contiene un estado de aceptación original
            for (Estado e : sj.conjEstados) {
                if (e.edoAccept) {
                    afdGenerado.estadosAceptacion.add(sj.j);
                    break;
                }
            }
        }

        return afdGenerado;
    }

    // --- GRÁFICOS Y UTILIDADES ---

    public void generarGrafico(String nombreArchivo) {
        File carpetaDots = new File("dots");
        File carpetaImagenes = new File("imagenes");
        
        if (!carpetaDots.exists()) carpetaDots.mkdir();
        if (!carpetaImagenes.exists()) carpetaImagenes.mkdir();

        String rutaDot = "dots/" + nombreArchivo + ".dot";
        String rutaPng = "imagenes/" + nombreArchivo + ".png";

       // ... (dentro de generarGrafico, donde empieza el StringBuilder) ...
    StringBuilder dot = new StringBuilder();
    dot.append("digraph AFN {\n");
    dot.append("  charset=\"UTF-8\";\n"); // <-- LÍNEA NUEVA: Le avisa a Graphviz
    dot.append("  rankdir=LR;\n");
    dot.append("  node [shape = circle, fontname=\"Arial\"];\n");
    dot.append("  edge [fontname=\"Arial\"];\n");

    dot.append("  node [shape = none, label=\"\"]; start;\n");
    dot.append("  start -> " + estadoInicial.idEdo + ";\n");

    for (Estado e : estadosAFN) {
        if (e.edoAccept) {
            dot.append("  " + e.idEdo + " [shape = doublecircle, label=\"" + e.idEdo + "\"];\n");
        } else {
            dot.append("  " + e.idEdo + " [shape = circle, label=\"" + e.idEdo + "\"];\n");
        }

        for (Transicion t : e.transiciones) {
            String label = (t.simbolo1 == '\0') ? "ε" : 
                           (t.simbolo1 == t.simbolo2) ? "'" + t.simbolo1 + "'" :
                           "[" + t.simbolo1 + "-" + t.simbolo2 + "]";
            
            dot.append("  " + e.idEdo + " -> " + t.edoFinal.idEdo + " [label=\"" + label + "\"];\n");
        }
    }
    dot.append("}\n");

    try {
        // <-- LÍNEAS NUEVAS: Escribir forzando UTF-8 en lugar de usar FileWriter básico
        OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(rutaDot), StandardCharsets.UTF_8);
        osw.write(dot.toString());
        osw.close();

        ProcessBuilder pb = new ProcessBuilder("dot", "-Tpng", rutaDot, "-o", rutaPng);
        pb.inheritIO(); 
        Process p = pb.start();
        p.waitFor();

        System.out.println("✅ Imagen generada: " + rutaPng);

    } catch (Exception ex) {
        System.err.println("❌ Error al generar imagen: " + ex.getMessage());
    }
    }
}