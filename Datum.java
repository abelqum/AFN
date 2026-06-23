package com.mycompany.userafncreator;

public class Datum {
    public float val;
    public SymbolHoc symb;

    // 1. Constructor vacío (para cuando hacemos 'new Datum()')
    public Datum() {
        this.val = 0.0f;
        this.symb = null;
    }

    // 2. Constructor numérico (para cuando hacemos 'new Datum(1.0f)' en los saltos lógicos)
    public Datum(float val) {
        this.val = val;
        this.symb = null;
    }

    // 3. Constructor de símbolo (por si metemos la variable directo)
    public Datum(SymbolHoc symb) {
        this.val = 0.0f;
        this.symb = symb;
    }
}