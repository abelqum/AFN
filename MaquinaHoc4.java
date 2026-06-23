package com.mycompany.userafncreator;

import java.util.Stack;
import javax.swing.JTextArea;
import javax.swing.JTable;

public class MaquinaHoc4 {
    
    public TablaSimbolos TabSimb;
    public InstrucPrograma Prog[];
    public int progp;
    public int pc;
    public Stack<Datum> stack;
    
    public JTextArea AreaResult;
    public JTable jTablePila;

    public MaquinaHoc4() {
        TabSimb = new TablaSimbolos();
        TabSimb.init();
        Prog = new InstrucPrograma[2048];
        progp = 0;
        pc = 0;
        stack = new Stack<>();
    }
    
    public MaquinaHoc4(JTextArea area, JTable tabla) {
        this();
        this.AreaResult = area;
        this.jTablePila = tabla;
    }

    public void initcode() {
        progp = 0;
        stack.clear();
    }

    public int code(EnumInstrMaq inst) {
        int o = progp;
        Prog[progp] = new InstrucPrograma();
        Prog[progp].TipoInstr = EnumTipoInstr.INSTRUC;
        Prog[progp].Instruc = inst;
        progp++;
        return o;
    }

    public int code(SymbolHoc s) {
        int o = progp;
        Prog[progp] = new InstrucPrograma();
        Prog[progp].TipoInstr = EnumTipoInstr.SYMBOL;
        Prog[progp].symb = s;
        progp++;
        return o;
    }

    public int code(EnumBLTIN b) {
        int o = progp;
        Prog[progp] = new InstrucPrograma();
        Prog[progp].TipoInstr = EnumTipoInstr.BLTIN;
        Prog[progp].bltin = b;
        progp++;
        return o;
    }

    public int code(int j) {
        int o = progp;
        Prog[progp] = new InstrucPrograma();
        Prog[progp].TipoInstr = EnumTipoInstr.DIRECC;
        Prog[progp].jump = j;
        progp++;
        return o;
    }

    public void push(Datum d) {
        stack.push(d);
    }

    public Datum pop() {
        if (stack.isEmpty()) {
            return new Datum(0.0f);
        }
        return stack.pop();
    }

    public void execute(int p) {
        for (pc = p; Prog[pc] != null; ) {
            InstrucPrograma ip = Prog[pc];
            if (ip.TipoInstr == EnumTipoInstr.INSTRUC) {
                pc++; // Avanza antes de ejecutar para que la instrucción pueda alterar el pc si es necesario
                switch (ip.Instruc) {
                    case VARPUSH:
                        varpush();
                        break;
                    case CONSTPUSH:
                        constpush();
                        break;
                    case EVAL:
                        eval();
                        break;
                    case ADD:
                        add();
                        break;
                    case SUB:
                        sub();
                        break;
                    case MUL:
                        mul();
                        break;
                    case DIV:
                        div();
                        break;
                    case ASSIGN:
                        assign();
                        break;
                    case BLTIN:
                        bltin();
                        break;
                    case PRINT:
                        print();
                        break;
                    case JUMP:
                        jump();
                        break;
                    case JUMP_TRUE:
                        jump_true();
                        break;
                    case JUMP_FALSE:
                        jump_false();
                        break;
                    case STOP:
                        return;
                }
            } else {
                pc++;
            }
        }
    }

    /* --- OPERACIONES DE LA ALU Y PILA --- */

    private void varpush() {
        push(new Datum(Prog[pc].symb));
        pc++;
    }

    private void constpush() {
        push(new Datum(Prog[pc].symb.val));
        pc++;
    }

    private void eval() {
        Datum d = pop();
        if (d.symb != null) {
            if (d.symb.TipoSymbol == EnumTipoSymbol.UNDEF) {
                if (AreaResult != null) AreaResult.append("Error: variable indefinida " + d.symb.name + "\n");
                push(new Datum(0.0f));
            } else {
                push(new Datum(d.symb.val));
            }
        } else {
            push(d);
        }
    }

    private void add() {
        Datum d2 = pop();
        Datum d1 = pop();
        float v1 = (d1.symb != null) ? d1.symb.val : d1.val;
        float v2 = (d2.symb != null) ? d2.symb.val : d2.val;
        push(new Datum(v1 + v2));
    }

    private void sub() {
        Datum d2 = pop();
        Datum d1 = pop();
        float v1 = (d1.symb != null) ? d1.symb.val : d1.val;
        float v2 = (d2.symb != null) ? d2.symb.val : d2.val;
        push(new Datum(v1 - v2));
    }

    private void mul() {
        Datum d2 = pop();
        Datum d1 = pop();
        float v1 = (d1.symb != null) ? d1.symb.val : d1.val;
        float v2 = (d2.symb != null) ? d2.symb.val : d2.val;
        push(new Datum(v1 * v2));
    }

    private void div() {
        Datum d2 = pop();
        Datum d1 = pop();
        float v1 = (d1.symb != null) ? d1.symb.val : d1.val;
        float v2 = (d2.symb != null) ? d2.symb.val : d2.val;
        if (v2 == 0) {
            if (AreaResult != null) AreaResult.append("Error: División por cero\n");
            push(new Datum(0.0f));
        } else {
            push(new Datum(v1 / v2));
        }
    }

    private void assign() {
        Datum d2 = pop(); // El valor
        Datum d1 = pop(); // El símbolo (variable)
        if (d1.symb != null) {
            float val = (d2.symb != null) ? d2.symb.val : d2.val;
            d1.symb.val = val;
            if (d1.symb.TipoSymbol == EnumTipoSymbol.UNDEF) {
                d1.symb.TipoSymbol = EnumTipoSymbol.VAR;
            }
            push(new Datum(val)); // Deja el resultado en la pila
        }
    }

    private void print() {
        Datum d = pop();
        float val = (d.symb != null) ? d.symb.val : d.val;
        if (AreaResult != null) {
            AreaResult.append("" + val + "\n");
        } else {
            System.out.println("" + val);
        }
    }

    private void bltin() {
        EnumBLTIN tipo = Prog[pc].bltin;
        pc++;
        Datum d = pop();
        float val = (d.symb != null) ? d.symb.val : d.val;
        float res = 0;
        
        switch (tipo) {
            case SIN: res = (float) Math.sin(val); break;
            case COS: res = (float) Math.cos(val); break;
            case ATAN: res = (float) Math.atan(val); break;
            case LOG: res = (float) Math.log(val); break;
            case LOG10: res = (float) Math.log10(val); break;
            case EXP: res = (float) Math.exp(val); break;
            case SQRT: res = (float) Math.sqrt(val); break;
            case INT: res = (float) Math.floor(val); break;
            case ABS: res = (float) Math.abs(val); break;
        }
        push(new Datum(res));
    }

    /* --- MECANISMOS DE SALTO (CONTROL DE FLUJO) --- */

    private void jump() {
        pc = Prog[pc].jump;
    }

    private void jump_true() {
        Datum d = pop();
        float val = (d.symb != null) ? d.symb.val : d.val;
        if (val != 0) {
            pc = Prog[pc].jump;
        } else {
            pc++;
        }
    }

    private void jump_false() {
        Datum d = pop();
        float val = (d.symb != null) ? d.symb.val : d.val;
        if (val == 0) {
            pc = Prog[pc].jump;
        } else {
            pc++;
        }
    }
}