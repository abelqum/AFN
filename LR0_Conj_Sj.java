package com.mycompany.userafncreator;

import java.util.HashSet;

public class LR0_Conj_Sj {
    public int j; // El número de estado (S0, S1, S2...)
    public HashSet<ItemLR0> Sj; // La bolsa de puntitos

    public LR0_Conj_Sj() {
        this.j = -1;
        this.Sj = new HashSet<>();
    }
}