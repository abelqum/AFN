package com.mycompany.userafncreator;

public class UserAFNCreator {

public static void main(String[] args) {
    int i = 1; // Contador para las imágenes
    System.out.println("=== GENERANDO SECUENCIA PASO A PASO ===");

    // PASO 1: Primer AFN Básico ('a')
    AFN afn1 = new AFN();
    afn1.crearAFNBasico('a');
    afn1.generarGrafico(i++ + "_Basico_A"); 
    // Genera: 1_Basico_A.png

    // PASO 2: Segundo AFN Básico ('b')
    AFN afn2 = new AFN();
    afn2.crearAFNBasico('b');
    afn2.generarGrafico(i++ + "_Basico_B"); 
    // Genera: 2_Basico_B.png

    // PASO 3: Concatenación (ab)
    // El final de 'a' se une al inicio de 'b'
    afn1.concatenacion(afn2);
    afn1.generarGrafico(i++ + "_Concatenado_AB"); 
    // Genera: 3_Concatenado_AB.png

    // PASO 4: Cerradura Positiva (ab)+
    // Agrega cables de regreso para repetir 1 o más veces
    afn1.cerraduraPositiva();
    afn1.generarGrafico(i++ + "_Positiva"); 
    // Genera: 4_Positiva.png

    // PASO 5: Cerradura de Kleene ((ab)+)*
    // Agrega el salto para aceptar "vacío" (0 o más veces)
    afn1.cerraduraKleene();
    afn1.generarGrafico(i++ + "_Kleene"); 
    // Genera: 5_Kleene.png

    // PASO 6: Opcional (((ab)+)*)?
    // Envuelve todo para que sea opcional
    afn1.opcional();
    afn1.generarGrafico(i++ + "_Opcional_Final"); 
    // Genera: 6_Opcional_Final.png

    System.out.println("\n✅ ¡Listo! Revisa la carpeta 'imagenes'.");
    System.out.println("Deberías tener desde la 1_... hasta la 6_...");
}
}