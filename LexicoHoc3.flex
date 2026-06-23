package com.mycompany.userafncreator;

import java_cup.runtime.*;

%%

%class LexicoHoc3
%public
%cup
%cupsym SintacHoc3Sym
%line
%column

/* DOBLE CANDADO PARA EL EOF */
%eofval{
    return symbol(SintacHoc3Sym.EOF);
%eofval}

%{
    public MaquinaHoc4 maq;

    public LexicoHoc3(java.io.Reader in, MaquinaHoc4 maq) {
        this(in);
        this.maq = maq;
    }

    private Symbol symbol(int type, Object value) {
        return new Symbol(type, yyline + 1, yycolumn + 1, value);
    }

    private Symbol symbol(int type) {
        return new Symbol(type, yyline + 1, yycolumn + 1);
    }
%}

LineTerminator = \r|\n|\r\n
WhiteSpace     = {LineTerminator} | [ \t\f]
Numero         = [0-9]+(\.[0-9]+)?
Identificador  = [a-zA-Z][a-zA-Z0-9]*

%%

<YYINITIAL> {
    {WhiteSpace} { /* Ignorar espacios en blanco */ }

    "="      { return symbol(SintacHoc3Sym.ASSIGN); }
    "+"      { return symbol(SintacHoc3Sym.ADD); }
    "-"      { return symbol(SintacHoc3Sym.SUB); }
    "*"      { return symbol(SintacHoc3Sym.MUL); }
    "/"      { return symbol(SintacHoc3Sym.DIV); }
    "("      { return symbol(SintacHoc3Sym.LPAREN); }
    ")"      { return symbol(SintacHoc3Sym.RPAREN); }
    ";"      { return symbol(SintacHoc3Sym.SEMIC); }

    {Numero} { return symbol(SintacHoc3Sym.NUM, Float.parseFloat(yytext())); }

    {Identificador} {
        SymbolHoc s = maq.TabSimb.lookup(yytext());
        if (s == null) {
            s = maq.TabSimb.install(yytext(), EnumTipoSymbol.UNDEF, 0.0f);
        }
        
        if (s.TipoSymbol == EnumTipoSymbol.BLTIN) {
            return symbol(SintacHoc3Sym.BLTIN, s);
        } else if (s.TipoSymbol == EnumTipoSymbol.CONST_PREDEF) {
            return symbol(SintacHoc3Sym.CONST_PREDEF, s);
        } else {
            return symbol(SintacHoc3Sym.VAR, s);
        }
    }
}

[^] { 
    if (maq.AreaResult != null) {
        maq.AreaResult.append("Error Léxico: Carácter ilegal <" + yytext() + ">\n");
    }
}