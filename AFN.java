/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.userafncreator;

/**
 *
 * @author abelq
 */
import java.util.HashSet;
import java.io.FileWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

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
    public static final char EPSILON = '\0'; // Representación de transición vacía

    // Constructor corregido
    public AFN() {
        this.idAFN = contadorAFNS++;
        this.alfabeto = new HashSet<>();
        this.estadosAFN = new HashSet<>();
        this.estadosAcept = new HashSet<>();
        // En Java, los HashSets se inicializan vacíos, no hace falta .clear()
    }

   public AFN crearAFNBasico(char c) {
    Estado e1, e2;
    e1 = new Estado();
    e2 = new Estado();
    
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
    // Crear AFN Básico por Rango (ej: 'a'-'z')
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

        // Llenar el alfabeto con el rango
        for (char c = c1; c <= c2; c++) {
            this.alfabeto.add(c);
        }

        coleccionAFN.add(this);
        return this;
    }

    // Unión de dos AFNs (this U f2)
   public AFN unionAFN(AFN f2) {
    Estado e1 = new Estado(); // Nuevo inicio
    Estado e2 = new Estado(); // Nuevo final

    // Conectar nuevo inicio a los viejos inicios
    e1.transiciones.add(new Transicion(EPSILON, this.estadoInicial));
    e1.transiciones.add(new Transicion(EPSILON, f2.estadoInicial));

    // CONECTAR Viejos finales de 'this' al nuevo final
    for (Estado e : this.estadosAcept) {
        e.transiciones.add(new Transicion(EPSILON, e2));
        e.edoAccept = false;
    }
    // CONECTAR Viejos finales de 'f2' al nuevo final
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
    
    // 1. CONCATENACIÓN (this . f2)
    public AFN concatenacion(AFN f2) {
        for (Estado e : this.estadosAcept) {
            // Pasamos las transiciones del inicio de f2 a los finales de 'this'
            for (Transicion t : f2.estadoInicial.transiciones) {
                e.transiciones.add(t);
            }
            e.edoAccept = false; // El final de 'this' ya no es de aceptación
        }

        this.alfabeto.addAll(f2.alfabeto);
        this.estadosAFN.addAll(f2.estadosAFN);
        this.estadosAFN.remove(f2.estadoInicial); // El inicio de f2 se absorbe
        this.estadosAcept.clear();
        this.estadosAcept.addAll(f2.estadosAcept);
        
        AFN.coleccionAFN.remove(f2); // Liberar de la colección global
        return this;
    }

    // 2. CERRADURA POSITIVA (this+)
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

    // 3. OPCIONAL (this?)
    public AFN opcional() {
    Estado e1 = new Estado();
    Estado e2 = new Estado();

    e1.transiciones.add(new Transicion(EPSILON, this.estadoInicial)); // Camino normal
    e1.transiciones.add(new Transicion(EPSILON, e2)); // Salto (cadena vacía)

    for (Estado e : this.estadosAcept) {
        e.transiciones.add(new Transicion(EPSILON, e2));
        e.edoAccept = false; // Ya no es final
    }

    e2.edoAccept = true; // Único final
    this.estadoInicial = e1;
    this.estadosAFN.add(e1);
    this.estadosAFN.add(e2);
    this.estadosAcept.clear();
    this.estadosAcept.add(e2);
    return this;
}
    // 4. CERRADURA DE KLEENE (this*)
    // Usamos la lógica de tus apuntes: Llamar a positiva y agregar el salto
    public AFN cerraduraKleene() {
        this.cerraduraPositiva();
        // Para Kleene, el nuevo estado inicial debe poder ir directo al final
        Estado eIni = this.estadoInicial;
        Estado eFin = null;
        
        // Obtenemos el único estado de aceptación actual
        for(Estado e : this.estadosAcept) eFin = e;

        if (eIni != null && eFin != null) {
            eIni.transiciones.add(new Transicion(EPSILON, eFin));
        }
        
        return this;
    }
    
    // Agrega esto en AFN.java
public void imprimirGrafo() {
    System.out.println("\n===== ESTRUCTURA DEL AFN ID: " + idAFN + " =====");
    System.out.println("Estado Inicial: " + estadoInicial.idEdo);
    
    for (Estado e : estadosAFN) {
        System.out.print("Estado " + e.idEdo + (e.edoAccept ? " [ACEPTACIÓN]" : "") + ": ");
        if (e.transiciones.isEmpty()) {
            System.out.println("(Sin transiciones)");
        } else {
            System.out.println("");
            for (Transicion t : e.transiciones) {
                String simbolo = (t.simbolo1 == EPSILON) ? "EPSILON" : 
                                 (t.simbolo1 == t.simbolo2) ? "'" + t.simbolo1 + "'" :
                                 "[" + t.simbolo1 + "-" + t.simbolo2 + "]";
                
                System.out.println("  -- " + simbolo + " --> Estado " + t.edoFinal.idEdo);
            }
        }
    }
    System.out.println("========================================\n");
}



//convertir de afn a afd

//cerradura epsilon para un estado
/*

hashset<estado>CerraduraEpsilon(Estado e){
estado e;
hashset<Estado> c= new hashset<Estado>();
Pila<Estado>p=new Pila<Estado>();
c.clear();
p.clear();
p.push(e);

while (p.count()!=0){
    e=p.pop();
    c.add(e);
    foreach(Transicion t in e.trancisiones){
    if(t.simbolo==epsilon){
        if(!c.contain(t.edofinal))
            p.push(t.edofinal;
    }
  }
}
return c;
}
*/



//cerradura epsilon para un conjunto de estados
/*

hashset<estado>CerraduraEpsilon(hashset<estado> r){
estado e;
hashset<Estado> c= new hashset<Estado>();
Pila<Estado>p=new Pila<Estado>();
c.clear();
p.clear();

foreach(estado e in r)
p.push(e);

while (p.count()!=0){
    e=p.pop();
    r.add(e);
    foreach(Transicion t in e.trancisiones){
    if(t.simbolo==epsilon){
        if(!r.contain(t.edofinal))
            p.push(t.edofinal;
    }
  }
}
return r;
}
*/




/*
hashset<estado>Mover(estado e, char c){
hashset <estado> r= new hashset<estado>();
r.clear();
foreach(transicion t in e.trancisiones)
    r.union(tieneTransicionA(c));

return r;
}
*/

/*
hashset<estado>Mover(hashset<estado> A, char c){
hashset <estado> r= new hashset<estado>();
r.clear();
foreach(estado e in A){
    foreach(transicion t in e.trancisiones)
        r.union(tieneTransicionA(c));
}
return r;
}
*/




/*
hashset<estado> IrA(hashset<estado> A, char c){
return cerraduraEpsilon(Mover(A,c));
}
*/


/*
AFD ConvertiraAFD(){

hashset<sj> r= new hashset<sj>();
queue<sj> Q= new hashset<sj>();
sj C= new sj;
int numSJ=0;
sj stTem= new sj();
int i;
c.conjEstados=cerraduraEpsilon(this.edoInicial);
c.j=numSj++;
r.add(c);
q.add(c);
while("Q.count()!=0"){
c=Q.Dropqueue();
foreach(char a in this.alfabeto){
    sjTemp.conjEstados= IrA(c.conjEstados,a);
    i= r.ContieneA(sjTemp);
    if(i!=-1){
    c.arregloTrans[a]=i;
    }else{
    sjtemp.j=numSj++;
    c.arregloTrans[a]=sjTemp.j;
    r.add(sjTemp);
    q.add(sjTemp);
    }
}

poer la informacion en la instancia del afd (arreglo bidimensional, valor de n=)
guardar en una rchivo el afd
}





}
*/

/*
clase afd
int n
arreglo bidimensional de enteros
*/










public void generarGrafico(String nombreArchivo) {
    // 1. Crear carpetas si no existen
    File carpetaDots = new File("dots");
    File carpetaImagenes = new File("imagenes");
    
    if (!carpetaDots.exists()) carpetaDots.mkdir();
    if (!carpetaImagenes.exists()) carpetaImagenes.mkdir();

    // 2. Definir rutas completas
    String rutaDot = "dots/" + nombreArchivo + ".dot";
    String rutaPng = "imagenes/" + nombreArchivo + ".png";

    StringBuilder dot = new StringBuilder();
    dot.append("digraph AFN {\n");
    dot.append("  rankdir=LR;\n");
    dot.append("  node [shape = circle, fontname=\"Arial\"];\n");
    dot.append("  edge [fontname=\"Arial\"];\n");

    dot.append("  node [shape = none, label=\"\"]; start;\n");
    dot.append("  start -> " + estadoInicial.idEdo + ";\n");

    for (Estado e : estadosAFN) {
        // Forzamos que CUALQUIER estado sea un círculo y tenga su ID como etiqueta
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
        // 3. Guardar el archivo .dot en su carpeta
        FileWriter fw = new FileWriter(rutaDot);
        fw.write(dot.toString());
        fw.close();

        // 4. Ejecutar dot usando las nuevas rutas
        // Si usaste la ruta completa al .exe, agrégala aquí también
        ProcessBuilder pb = new ProcessBuilder("dot", "-Tpng", rutaDot, "-o", rutaPng);
        pb.inheritIO(); 
        Process p = pb.start();
        p.waitFor();

        System.out.println("✅ Generado: " + rutaPng);

    } catch (Exception ex) {
        System.err.println("❌ Error: " + ex.getMessage());
    }
}
}