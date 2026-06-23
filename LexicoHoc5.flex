package com.mycompany.userafncreator;

import java_cup.runtime.*;

%%

%class LexicoHoc5
%public
%cup
%line
%column

/* ESTA ES LA MAGIA PARA ARREGLAR EL ERROR sym.EOF */
%eofval{
    return symbol(SintacHoc5Sym.EOF);
%eofval}

%{
    public MaquinaHoc4 maq;

    public LexicoHoc5(java.io.Reader in, MaquinaHoc4 maq) {
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
    {WhiteSpace} { /* Ignorar */ }

    "if"       { return symbol(SintacHoc5Sym.IF); }
    "else"     { return symbol(SintacHoc5Sym.ELSE); }
    "while"    { return symbol(SintacHoc5Sym.WHILE); }
    "for"      { return symbol(SintacHoc5Sym.FOR); }
    "switch"   { return symbol(SintacHoc5Sym.SWITCH); }
"break"    { return symbol(SintacHoc5Sym.BREAK); } 
    "case"     { return symbol(SintacHoc5Sym.CASE); }
    "default"  { return symbol(SintacHoc5Sym.DEFAULT); }
    "print"    { return symbol(SintacHoc5Sym.PRINT); }

    "=="       { return symbol(SintacHoc5Sym.EQ); }
    "!="       { return symbol(SintacHoc5Sym.NE); }
    ">="       { return symbol(SintacHoc5Sym.GE); }
    "<="       { return symbol(SintacHoc5Sym.LE); }
    ">"        { return symbol(SintacHoc5Sym.GT); }
    "<"        { return symbol(SintacHoc5Sym.LT); }
    "&&"       { return symbol(SintacHoc5Sym.AND); }
    "||"       { return symbol(SintacHoc5Sym.OR); }
    "!"        { return symbol(SintacHoc5Sym.NOT); }

    "="        { return symbol(SintacHoc5Sym.ASSIGN); }
    "+"        { return symbol(SintacHoc5Sym.ADD); }
    "-"        { return symbol(SintacHoc5Sym.SUB); }
    "*"        { return symbol(SintacHoc5Sym.MUL); }
    "/"        { return symbol(SintacHoc5Sym.DIV); }
    "("        { return symbol(SintacHoc5Sym.LPAREN); }
    ")"        { return symbol(SintacHoc5Sym.RPAREN); }
    "{"        { return symbol(SintacHoc5Sym.LBRACE); }
    "}"        { return symbol(SintacHoc5Sym.RBRACE); }
    ";"        { return symbol(SintacHoc5Sym.SEMIC); }
    ":"        { return symbol(SintacHoc5Sym.COLON); }

    {Numero} { return symbol(SintacHoc5Sym.NUM, Float.parseFloat(yytext())); }

    {Identificador} {
        SymbolHoc s = maq.TabSimb.lookup(yytext());
        if (s == null) s = maq.TabSimb.install(yytext(), EnumTipoSymbol.UNDEF, 0.0f);
        
        if (s.TipoSymbol == EnumTipoSymbol.BLTIN) return symbol(SintacHoc5Sym.BLTIN, s);
        else if (s.TipoSymbol == EnumTipoSymbol.CONST_PREDEF) return symbol(SintacHoc5Sym.CONST_PREDEF, s);
        else return symbol(SintacHoc5Sym.VAR, s);
    }
}

[^] { 
    if (maq.AreaResult != null) maq.AreaResult.append("Error Léxico: " + yytext() + "\n");
}