package com.mycompany.userafncreator;

import java_cup.runtime.*;

%%

%class LexicoHoc6
%public
%cup
%line
%column

/* ESTA ES LA MAGIA PARA ARREGLAR EL ERROR sym.EOF */
%eofval{
    return symbol(SintacHoc6Sym.EOF);
%eofval}

%{
    public MaquinaHoc4 maq;

    public LexicoHoc6(java.io.Reader in, MaquinaHoc4 maq) {
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

    "proc"     { return symbol(SintacHoc6Sym.PROC); }
    "func"     { return symbol(SintacHoc6Sym.FUNC); }
    "return"   { return symbol(SintacHoc6Sym.RETURN); }
    "$"[1-9][0-9]* { 
        int numArg = Integer.parseInt(yytext().substring(1));
        return symbol(SintacHoc6Sym.ARG, numArg); 
    }

    "if"       { return symbol(SintacHoc6Sym.IF); }
    "else"     { return symbol(SintacHoc6Sym.ELSE); }
    "while"    { return symbol(SintacHoc6Sym.WHILE); }
    "for"      { return symbol(SintacHoc6Sym.FOR); }
    "switch"   { return symbol(SintacHoc6Sym.SWITCH); }
"break"    { return symbol(SintacHoc6Sym.BREAK); } 
    "case"     { return symbol(SintacHoc6Sym.CASE); }
    "default"  { return symbol(SintacHoc6Sym.DEFAULT); }
    "print"    { return symbol(SintacHoc6Sym.PRINT); }

    "=="       { return symbol(SintacHoc6Sym.EQ); }
    "!="       { return symbol(SintacHoc6Sym.NE); }
    ">="       { return symbol(SintacHoc6Sym.GE); }
    "<="       { return symbol(SintacHoc6Sym.LE); }
    ">"        { return symbol(SintacHoc6Sym.GT); }
    "<"        { return symbol(SintacHoc6Sym.LT); }
    "&&"       { return symbol(SintacHoc6Sym.AND); }
    "||"       { return symbol(SintacHoc6Sym.OR); }
    "!"        { return symbol(SintacHoc6Sym.NOT); }

    "="        { return symbol(SintacHoc6Sym.ASSIGN); }
    "+"        { return symbol(SintacHoc6Sym.ADD); }
    "-"        { return symbol(SintacHoc6Sym.SUB); }
    "*"        { return symbol(SintacHoc6Sym.MUL); }
    "/"        { return symbol(SintacHoc6Sym.DIV); }
    "("        { return symbol(SintacHoc6Sym.LPAREN); }
    ")"        { return symbol(SintacHoc6Sym.RPAREN); }
    "{"        { return symbol(SintacHoc6Sym.LBRACE); }
    "}"        { return symbol(SintacHoc6Sym.RBRACE); }
    ";"        { return symbol(SintacHoc6Sym.SEMIC); }
    ":"        { return symbol(SintacHoc6Sym.COLON); }

    {Numero} { return symbol(SintacHoc6Sym.NUM, Float.parseFloat(yytext())); }

    {Identificador} {
        SymbolHoc s = maq.TabSimb.lookup(yytext());
        if (s == null) s = maq.TabSimb.install(yytext(), EnumTipoSymbol.UNDEF, 0.0f);
        
        if (s.TipoSymbol == EnumTipoSymbol.BLTIN) return symbol(SintacHoc6Sym.BLTIN, s);
        else if (s.TipoSymbol == EnumTipoSymbol.CONST_PREDEF) return symbol(SintacHoc6Sym.CONST_PREDEF, s);
        else return symbol(SintacHoc6Sym.VAR, s);
    }
}

[^] { 
    if (maq.AreaResult != null) maq.AreaResult.append("Error Léxico: " + yytext() + "\n");
}