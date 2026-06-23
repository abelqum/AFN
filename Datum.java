package com.mycompany.userafncreator;

public class Datum {
    public float val;
    public SymbolHoc symb;

    public Datum(float valor) {
        this.val = valor;
        this.symb = null;
    }

    public Datum(SymbolHoc simbolo) {
        this.symb = simbolo;
        this.val = 0;
    }
}