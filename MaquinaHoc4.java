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
    public float ax; // Registro auxiliar para el switch
    public JTextArea AreaResult;
    public JTable jTablePila;
// Clase interna para guardar el contexto de las funciones (HOC 6)
    public class Frame {
        public int retpc;
        public java.util.ArrayList<Datum> args;
        public Frame(int retpc) { this.retpc = retpc; this.args = new java.util.ArrayList<>(); }
    }
    public Stack<Frame> callStack = new Stack<>();
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
                        // ... (debajo de case JUMP_FALSE: jump_false(); break;)
                    case GT: gt(); break;
                    case LT: lt(); break;
                    case EQ: eq(); break;
                    case GE: ge(); break;
                    case LE: le(); break;
                    case NE: ne(); break;
                    case AND: and(); break;
                    case OR: or(); break;
                    case NOT: not(); break;
                    case SET_AX: set_ax(); break;
                    case PUSH_AX: push_ax(); break;
                    case POP: stack.pop(); break;
                    case CALL: call(); break;
                    case RET: ret(); break;
                    case PROCRET: procret(); break;
                    case ARGPUSH: argpush(); break;
                    case ARGASSIGN: argassign(); break;
                    case STOP:
                        return;
                }
            } else {
                pc++;
            }
        }
    }

    /* --- OPERACIONES DE LA ALU Y PILA --- */

   private void constpush() {
        Datum d = new Datum();
        d.val = Prog[pc].symb.val; // Extraemos el valor real de la instrucción
        pc++; // Saltamos a la siguiente instrucción
        push(d);
    }

    private void varpush() {
        Datum d = new Datum();
        d.symb = Prog[pc].symb; // Guardamos la referencia de la variable
        pc++; // Saltamos a la siguiente instrucción
        push(d);
    }

    private void eval() {
        Datum d = pop();
        if (d.symb.TipoSymbol == EnumTipoSymbol.UNDEF) {
            if (AreaResult != null) {
                AreaResult.append("❌ Error: variable indefinida '" + d.symb.name + "'\n");
            }
        }
        Datum dNuevo = new Datum();
        dNuevo.val = d.symb.val; // Pasamos su valor a la pila para hacer matemáticas
        push(dNuevo);
    }

    private void assign() {
        Datum dVar = pop(); // Primero sale la variable (ej. x)
        Datum dVal = pop(); // Luego sale el valor (ej. 7.5)
        
        dVar.symb.val = dVal.val; // Le inyectamos el valor
        
        // ¡ESTA LÍNEA ES LA CLAVE! Le quitamos lo "indefinida"
        dVar.symb.TipoSymbol = EnumTipoSymbol.VAR; 
        
        push(dVal); // Volvemos a meter el valor por si hay asignaciones en cadena (y = z = x)
    }

    private void print() {
        Datum d = pop(); // Sacamos el resultado final de la expresión
        if (AreaResult != null) {
            AreaResult.append(">> RESULTADO: " + d.val + "\n");
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

    /* --- OPERACIONES LÓGICAS Y DE SWITCH (HOC 5) --- */
    private void set_ax() {
        Datum d = pop();
        this.ax = (d.symb != null) ? d.symb.val : d.val;
    }

    private void push_ax() {
        push(new Datum(this.ax));
    }

    private void gt() {
        Datum d2 = pop(); Datum d1 = pop();
        float v1 = (d1.symb != null) ? d1.symb.val : d1.val;
        float v2 = (d2.symb != null) ? d2.symb.val : d2.val;
        push(new Datum(v1 > v2 ? 1.0f : 0.0f));
    }

    private void lt() {
        Datum d2 = pop(); Datum d1 = pop();
        float v1 = (d1.symb != null) ? d1.symb.val : d1.val;
        float v2 = (d2.symb != null) ? d2.symb.val : d2.val;
        push(new Datum(v1 < v2 ? 1.0f : 0.0f));
    }

    private void eq() {
        Datum d2 = pop(); Datum d1 = pop();
        float v1 = (d1.symb != null) ? d1.symb.val : d1.val;
        float v2 = (d2.symb != null) ? d2.symb.val : d2.val;
        push(new Datum(v1 == v2 ? 1.0f : 0.0f));
    }

    private void ge() {
        Datum d2 = pop(); Datum d1 = pop();
        float v1 = (d1.symb != null) ? d1.symb.val : d1.val;
        float v2 = (d2.symb != null) ? d2.symb.val : d2.val;
        push(new Datum(v1 >= v2 ? 1.0f : 0.0f));
    }

    private void le() {
        Datum d2 = pop(); Datum d1 = pop();
        float v1 = (d1.symb != null) ? d1.symb.val : d1.val;
        float v2 = (d2.symb != null) ? d2.symb.val : d2.val;
        push(new Datum(v1 <= v2 ? 1.0f : 0.0f));
    }

    private void ne() {
        Datum d2 = pop(); Datum d1 = pop();
        float v1 = (d1.symb != null) ? d1.symb.val : d1.val;
        float v2 = (d2.symb != null) ? d2.symb.val : d2.val;
        push(new Datum(v1 != v2 ? 1.0f : 0.0f));
    }

    private void and() {
        Datum d2 = pop(); Datum d1 = pop();
        float v1 = (d1.symb != null) ? d1.symb.val : d1.val;
        float v2 = (d2.symb != null) ? d2.symb.val : d2.val;
        push(new Datum((v1 != 0 && v2 != 0) ? 1.0f : 0.0f));
    }

    private void or() {
        Datum d2 = pop(); Datum d1 = pop();
        float v1 = (d1.symb != null) ? d1.symb.val : d1.val;
        float v2 = (d2.symb != null) ? d2.symb.val : d2.val;
        push(new Datum((v1 != 0 || v2 != 0) ? 1.0f : 0.0f));
    }

    private void not() {
        Datum d = pop();
        float v = (d.symb != null) ? d.symb.val : d.val;
        push(new Datum(v == 0 ? 1.0f : 0.0f));
    }
    
    /* --- CONTROL DE FUNCIONES HOC 6 --- */
    private void call() {
        SymbolHoc f = Prog[pc].symb; pc++; // Saca el símbolo de la función
        int numArgs = Prog[pc].jump; pc++; // Saca la cantidad de argumentos
        Frame frame = new Frame(pc); // Guarda la línea a la que debe regresar
        
        // Saca los argumentos de la pila y los guarda en el Frame (al revés por ser pila)
        Datum[] argsTemporales = new Datum[numArgs];
        for(int i = numArgs - 1; i >= 0; i--) argsTemporales[i] = pop();
        for(Datum d : argsTemporales) frame.args.add(d);
        
        callStack.push(frame); // Se mete a la función
        pc = (int) f.val; // Salta a la línea de código donde empieza la función
    }

    private void ret() {
        Frame frame = callStack.pop();
        pc = frame.retpc; // Regresa de la función, el valor de retorno ya está en la pila (Datum)
    }

    private void procret() {
        Frame frame = callStack.pop();
        pc = frame.retpc;
    }

    private void argpush() {
        int argIndex = Prog[pc].jump; pc++; // Es el número del argumento (ej. el 1 del $1)
        Frame frame = callStack.peek();
        push(new Datum(frame.args.get(argIndex - 1).val));
    }

    private void argassign() {
        int argIndex = Prog[pc].jump; pc++; // Es el número del argumento
        Datum dVal = pop(); // Saca el valor a asignar
        Frame frame = callStack.peek();
        frame.args.get(argIndex - 1).val = dVal.val; // Asigna directo (ej. $1 = 1)
        push(dVal);
    }
}