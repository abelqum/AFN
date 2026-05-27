package com.mycompany.userafncreator;

import java.util.Objects;

public class ItemLR0 {
    
    public int NumRegla; // Índice de la regla en el arreglo de tu clase Gramatica
    public int PosPunto; // Posición del punto en tu listaSimb

    public ItemLR0() {
        this.NumRegla = -1;
        this.PosPunto = -1;
    }

    public ItemLR0(int numRegla, int posPunto) {
        this.NumRegla = numRegla;
        this.PosPunto = posPunto;
    }

    // =========================================================
    // EQUIVALENTE A 'ItemComparer' de la Foto 5.
    // Esto hace que tu HashSet no meta items duplicados.
    // =========================================================
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ItemLR0 itemLR0 = (ItemLR0) obj;
        return NumRegla == itemLR0.NumRegla && PosPunto == itemLR0.PosPunto;
    }

    @Override
    public int hashCode() {
        return Objects.hash(NumRegla, PosPunto);
    }
}