package com.mycompany.userafncreator;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.io.File;
import java.util.ArrayList;

public class VentanaPrincipal extends JFrame {

    private JPanel contenedorTarjetas;
    private CardLayout cardLayout;
    
    // ComboBoxes
    private JComboBox<String> comboAFNsPositiva, comboAFNsKleene, comboAFNsOpcional;
    private JComboBox<String> comboConcat1, comboConcat2;
    private JComboBox<String> comboUnionNormal1, comboUnionNormal2;
    private JComboBox<String> comboAfnAfd;
    
    // TABLA PARA LA UNIÓN LÉXICA
    private DefaultTableModel modeloTablaUnionLexica;
    private JTable tablaUnionLexica;
    
    // Visores
    private JLabel lblImagenBasicoUn, lblImagenBasicoRango;
    private JLabel lblImagenPositiva, lblImagenKleene, lblImagenOpcional;
    private JLabel lblImagenUnionLexica, lblImagenConcat, lblImagenUnionNormal;
    private JLabel lblImagenAfnAfd;
    private JLabel lblImagenERaAFN; 

    // GLOBALES PARA LL1 Y LR0
    private Gramatica gramaticaActualLL1 = null;
    private AnalizadorLL1 analizadorLL1 = null;
    private Gramatica gramaticaActualLR0 = null;
    private AnalizadorLR0 analizadorLR0 = null;

    public VentanaPrincipal() {
        setTitle("Generador de Analizadores Léxicos y Sintácticos");
        setSize(1000, 680); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Usamos BorderLayout directo en la ventana, eliminando el TabbedPane
        setLayout(new BorderLayout()); 

        // =======================================================
        // BARRA DE MENÚS (NUEVA ORGANIZACIÓN)
        // =======================================================
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar); // Agregamos la barra a la ventana principal

        // --- 1. MENÚ PRINCIPAL: AFN ---
        JMenu menuAFN = new JMenu("AFN");

        JMenuItem itemBasicoUn = new JMenuItem("Básico (Un carácter)");
        JMenuItem itemBasicoRango = new JMenuItem("Básico (Rango)");
        JMenuItem itemERaAFN = new JMenuItem("ER -> AFN (Por Árbol)");
        
        JMenuItem itemUnirNormal = new JMenuItem("Unir (Normal)");
        JMenuItem itemUnirLexico = new JMenuItem("Unir (Para Léxico)");
        JMenuItem itemConcatenar = new JMenuItem("Concatenar");
        JMenuItem itemPositiva = new JMenuItem("Cerradura +");
        JMenuItem itemKleene = new JMenuItem("Cerradura *");
        JMenuItem itemOpcional = new JMenuItem("Opcional");
        JMenuItem itemConvertir = new JMenuItem("Convertir AFN a AFD");
        
        JMenuItem itemProbarLexico = new JMenuItem("Probar Analizador Léxico");
        itemProbarLexico.setForeground(new Color(0, 102, 204)); 
        
        JMenuItem itemBorrar = new JMenuItem("Borrar AFN...");
        itemBorrar.setForeground(Color.RED);

        menuAFN.add(itemBasicoUn);
        menuAFN.add(itemBasicoRango);
        menuAFN.addSeparator();
        menuAFN.add(itemERaAFN); 
        menuAFN.addSeparator();
        menuAFN.add(itemUnirNormal);
        menuAFN.add(itemUnirLexico);
        menuAFN.add(itemConcatenar);
        menuAFN.add(itemPositiva);
        menuAFN.add(itemKleene);
        menuAFN.add(itemOpcional);
        menuAFN.addSeparator();
        menuAFN.add(itemConvertir);
        menuAFN.addSeparator();
        menuAFN.add(itemProbarLexico); 
        menuAFN.addSeparator();
        menuAFN.add(itemBorrar);

       // --- 2. MENÚ PRINCIPAL: ANALIZADOR SINTÁCTICO ---
        JMenu menuSintactico = new JMenu("Analizador Sintáctico");
        JMenu subMenuDescenso = new JMenu("Descenso Recursivo");
        
        JMenuItem itemCalculadora = new JMenuItem("Calculadora");
        JMenuItem itemPolinomios = new JMenuItem("Polinomios"); 
        
        // --- NUEVAS OPCIONES LL1 Y LR0 ---
        JMenuItem itemLL1 = new JMenuItem("Análisis LL(1)");
        itemLL1.setForeground(new Color(0, 102, 102));
        JMenuItem itemLR0 = new JMenuItem("Análisis LR(0)");
        itemLR0.setForeground(new Color(153, 0, 0));
        
        subMenuDescenso.add(itemCalculadora);
        subMenuDescenso.add(itemPolinomios); 
        
        menuSintactico.add(subMenuDescenso);
        menuSintactico.addSeparator();
        menuSintactico.add(itemLL1);
        menuSintactico.add(itemLR0);

        // Agregamos los menús a la barra principal
        menuBar.add(menuAFN);
        menuBar.add(menuSintactico);

        // =======================================================
        // INICIALIZACIÓN DE COMPONENTES VISUALES
        // =======================================================
        cardLayout = new CardLayout();
        contenedorTarjetas = new JPanel(cardLayout);

        comboAFNsPositiva = new JComboBox<>(); comboAFNsKleene = new JComboBox<>(); comboAFNsOpcional = new JComboBox<>();
        comboConcat1 = new JComboBox<>(); comboConcat2 = new JComboBox<>();
        comboUnionNormal1 = new JComboBox<>(); comboUnionNormal2 = new JComboBox<>();
        comboAfnAfd = new JComboBox<>(); 

        lblImagenBasicoUn = new JLabel("Aquí aparecerá el AFN de un carácter", SwingConstants.CENTER);
        lblImagenBasicoRango = new JLabel("Aquí aparecerá el AFN de rango", SwingConstants.CENTER);
        lblImagenPositiva = new JLabel("Selecciona un AFN", SwingConstants.CENTER);
        lblImagenKleene = new JLabel("Selecciona un AFN", SwingConstants.CENTER);
        lblImagenOpcional = new JLabel("Selecciona un AFN", SwingConstants.CENTER);
        lblImagenUnionLexica = new JLabel("El Súper AFN aparecerá aquí", SwingConstants.CENTER);
        lblImagenConcat = new JLabel("El AFN concatenado aparecerá aquí", SwingConstants.CENTER);
        lblImagenUnionNormal = new JLabel("El AFN unido aparecerá aquí", SwingConstants.CENTER);
        lblImagenAfnAfd = new JLabel("Selecciona un AFN para ver su origen", SwingConstants.CENTER);
        lblImagenERaAFN = new JLabel("El AFN de tu Expresión Regular aparecerá aquí", SwingConstants.CENTER);

        // --- AGREGANDO LOS PANELES AL CONTENEDOR ---
        contenedorTarjetas.add(crearPanelBienvenida(), "Bienvenida");
        contenedorTarjetas.add(crearPanelBasicoUnCaracter(), "BasicoUn");
        contenedorTarjetas.add(crearPanelBasicoRango(), "BasicoRango");
        contenedorTarjetas.add(crearPanelERaAFN(), "ERaAFN"); 
        contenedorTarjetas.add(crearPanelOperacionUnaria("Cerradura +", "+", comboAFNsPositiva, lblImagenPositiva), "Positiva");
        contenedorTarjetas.add(crearPanelOperacionUnaria("Cerradura *", "*", comboAFNsKleene, lblImagenKleene), "Kleene");
        contenedorTarjetas.add(crearPanelOperacionUnaria("Opcional", "?", comboAFNsOpcional, lblImagenOpcional), "Opcional");
        contenedorTarjetas.add(crearPanelUnionLexico(), "UnionLexico");
        contenedorTarjetas.add(crearPanelOperacionBinariaSimple("Unión Normal", "Unir:", comboUnionNormal1, comboUnionNormal2, lblImagenUnionNormal, true), "UnionNormal");
        contenedorTarjetas.add(crearPanelOperacionBinariaSimple("Concatenación", "Concatenar:", comboConcat1, comboConcat2, lblImagenConcat, false), "Concatenar");
        contenedorTarjetas.add(crearPanelConvertir(), "Convertir");
        contenedorTarjetas.add(crearPanelProbarLexico(), "ProbarLexico");
        
        // Panel de la Calculadora y Polinomios
        contenedorTarjetas.add(crearPanelCalculadora(), "Calculadora");
        contenedorTarjetas.add(crearPanelPolinomios(), "Polinomios");
        
        // PANELES NUEVOS LL1 Y LR0
        contenedorTarjetas.add(crearPanelLL1(), "LL1");
        contenedorTarjetas.add(crearPanelLR0(), "LR0");

        // Agregamos el contenedor al centro de la ventana
        add(contenedorTarjetas, BorderLayout.CENTER);

        // Pantalla inicial por defecto
        cardLayout.show(contenedorTarjetas, "Bienvenida");

        // =======================================================
        // EVENTOS DEL MENÚ
        // =======================================================
        itemBasicoUn.addActionListener(e -> cardLayout.show(contenedorTarjetas, "BasicoUn"));
        itemBasicoRango.addActionListener(e -> cardLayout.show(contenedorTarjetas, "BasicoRango"));
        itemERaAFN.addActionListener(e -> cardLayout.show(contenedorTarjetas, "ERaAFN")); 
        itemPositiva.addActionListener(e -> { actualizarListas(); cardLayout.show(contenedorTarjetas, "Positiva"); });
        itemKleene.addActionListener(e -> { actualizarListas(); cardLayout.show(contenedorTarjetas, "Kleene"); });
        itemOpcional.addActionListener(e -> { actualizarListas(); cardLayout.show(contenedorTarjetas, "Opcional"); });
        itemUnirLexico.addActionListener(e -> { actualizarListas(); cardLayout.show(contenedorTarjetas, "UnionLexico"); });
        itemUnirNormal.addActionListener(e -> { actualizarListas(); cardLayout.show(contenedorTarjetas, "UnionNormal"); });
        itemConcatenar.addActionListener(e -> { actualizarListas(); cardLayout.show(contenedorTarjetas, "Concatenar"); });
        itemConvertir.addActionListener(e -> { actualizarListas(); cardLayout.show(contenedorTarjetas, "Convertir"); });
        itemProbarLexico.addActionListener(e -> cardLayout.show(contenedorTarjetas, "ProbarLexico"));
        
        itemCalculadora.addActionListener(e -> cardLayout.show(contenedorTarjetas, "Calculadora"));
        itemPolinomios.addActionListener(e -> cardLayout.show(contenedorTarjetas, "Polinomios")); 
        itemLL1.addActionListener(e -> cardLayout.show(contenedorTarjetas, "LL1"));
        itemLR0.addActionListener(e -> cardLayout.show(contenedorTarjetas, "LR0"));
        
        itemBorrar.addActionListener(e -> mostrarDialogoBorrar());
    }

    // =======================================================
    // NUEVO PANEL: ANÁLISIS LL(1)
    // =======================================================
    private JPanel crearPanelLL1() {
        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        // Norte: Gramática
        JPanel panelNorte = new JPanel(new BorderLayout(5, 5));
        panelNorte.setBorder(BorderFactory.createTitledBorder("1. Definir Gramática (BNF)"));
        JTextArea txtGramatica = new JTextArea(4, 50);
        JScrollPane scrollGramatica = new JScrollPane(txtGramatica);
        JButton btnProcesarGramatica = new JButton("Procesar Gramática LL(1)");
        btnProcesarGramatica.setBackground(new Color(0, 102, 102));
        btnProcesarGramatica.setForeground(Color.WHITE);
        panelNorte.add(scrollGramatica, BorderLayout.CENTER);
        panelNorte.add(btnProcesarGramatica, BorderLayout.EAST);

        // Centro: Vocabularios
        JPanel panelCentro = new JPanel(new GridLayout(1, 2, 10, 10));
        panelCentro.setBorder(BorderFactory.createTitledBorder("2. Vocabularios Extraídos (Asignar Tokens)"));
        
        DefaultTableModel modNoTerminales = new DefaultTableModel(new String[]{"No Terminal (Vn)"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tabNoTerminales = new JTable(modNoTerminales);
        panelCentro.add(new JScrollPane(tabNoTerminales));
        
        DefaultTableModel modTerminales = new DefaultTableModel(new String[]{"Terminal (Vt)", "Token Asociado"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return c == 1; }
        };
        JTable tabTerminales = new JTable(modTerminales);
        panelCentro.add(new JScrollPane(tabTerminales));

        // Sur: Análisis de Cadenas
        JPanel panelSur = new JPanel(new BorderLayout(5, 5));
        panelSur.setBorder(BorderFactory.createTitledBorder("3. Analizar Cadena LL(1)"));
        panelSur.setPreferredSize(new Dimension(900, 250));
        
        JPanel panelControlesSur = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        JButton btnCargarAfd = new JButton("Seleccionar AFD Léxico (.txt)");
        JLabel lblAfdCargado = new JLabel("Ningún AFD seleccionado");
        final String[] rutaAfdLexico = {""};
        
        panelControlesSur.add(btnCargarAfd);
        panelControlesSur.add(lblAfdCargado);
        panelControlesSur.add(new JLabel("   Sigma: "));
        JTextField txtSigma = new JTextField(20);
        panelControlesSur.add(txtSigma);
        JButton btnAnalizar = new JButton("Analizar Cadena");
        panelControlesSur.add(btnAnalizar);
        
        panelSur.add(panelControlesSur, BorderLayout.NORTH);
        
        DefaultTableModel modPila = new DefaultTableModel(new String[]{"Pila", "Cadena / Token", "Acción"}, 0);
        JTable tabPila = new JTable(modPila);
        panelSur.add(new JScrollPane(tabPila), BorderLayout.CENTER);

        panelPrincipal.add(panelNorte, BorderLayout.NORTH);
        panelPrincipal.add(panelCentro, BorderLayout.CENTER);
        panelPrincipal.add(panelSur, BorderLayout.SOUTH);

        // Lógica Botón Procesar
        btnProcesarGramatica.addActionListener(e -> {
            if (txtGramatica.getText().trim().isEmpty()) return;
            JFileChooser chooser = new JFileChooser(new File(System.getProperty("user.dir")));
            chooser.setDialogTitle("Seleccionar Lexer de Gramáticas (Ej: Tabla_AFD_999.txt)");
            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                try {
                    DescRecGram_Gram parser = new DescRecGram_Gram(txtGramatica.getText().trim(), chooser.getSelectedFile().getAbsolutePath());
                    if (parser.G()) {
                        gramaticaActualLL1 = parser.gramaticaResultante;
                        modNoTerminales.setRowCount(0); modTerminales.setRowCount(0);
                        for (Simbolo s : gramaticaActualLL1.Vn) modNoTerminales.addRow(new Object[]{s.nombre});
                        for (Simbolo s : gramaticaActualLL1.Vt) if (!s.nombre.equals(Simbolo.EPSILON)) modTerminales.addRow(new Object[]{s.nombre, ""});
                        
                        analizadorLL1 = new AnalizadorLL1(gramaticaActualLL1);
                        analizadorLL1.construirTablaLL1();
                        JOptionPane.showMessageDialog(this, "Gramática procesada correctamente para LL(1).");
                    } else {
                        JOptionPane.showMessageDialog(this, "Error de Descenso Recursivo. Verifica la sintaxis (No olvides los espacios en tu AFD 999).", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage()); }
            }
        });

        // Lógica Botones Inferiores LL(1)
        btnCargarAfd.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser(new File(System.getProperty("user.dir")));
            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                rutaAfdLexico[0] = chooser.getSelectedFile().getAbsolutePath();
                lblAfdCargado.setText(chooser.getSelectedFile().getName());
            }
        });

        btnAnalizar.addActionListener(e -> {
            if (gramaticaActualLL1 == null || rutaAfdLexico[0].isEmpty() || txtSigma.getText().isEmpty()) return;
            java.util.HashMap<Integer, String> mapa = new java.util.HashMap<>();
            for (int i = 0; i < modTerminales.getRowCount(); i++) {
                String tkStr = (String) modTerminales.getValueAt(i, 1);
                if (tkStr != null && !tkStr.isEmpty()) mapa.put(Integer.parseInt(tkStr.trim()), (String) modTerminales.getValueAt(i, 0));
            }
            
            modPila.setRowCount(0);
            try {
                AnalizadorLexico lex = new AnalizadorLexico(rutaAfdLexico[0]);
                lex.setSigma(txtSigma.getText().trim());
                java.util.Stack<Simbolo> pila = new java.util.Stack<>();
                pila.push(new Simbolo("$", true)); pila.push(gramaticaActualLL1.reglas[0].simboloIzquierdo);
                
                int tokenActual = lex.yylex();
                boolean error = false;
                while (!pila.isEmpty()) {
                    StringBuilder sb = new StringBuilder();
                    for (int i = pila.size() - 1; i >= 0; i--) sb.append(pila.get(i).nombre).append(" ");
                    
                    Simbolo X = pila.peek();
                    String terminal = (tokenActual == AnalizadorLexico.TOKEN_FIN) ? "$" : mapa.getOrDefault(tokenActual, "");
                    
                    if (X.esTerminal) {
                        if (X.nombre.equals(terminal)) {
                            modPila.addRow(new Object[]{sb.toString(), terminal, "POP & MATCH"});
                            pila.pop();
                            if (!X.nombre.equals("$")) tokenActual = lex.yylex();
                        } else {
                            modPila.addRow(new Object[]{sb.toString(), terminal, "ERROR SINTÁCTICO"}); error = true; break;
                        }
                    } else {
                        int r = analizadorLL1.arrVn.indexOf(X); int c = analizadorLL1.arrVt.indexOf(new Simbolo(terminal, true));
                        int regla = (r != -1 && c != -1) ? analizadorLL1.TablaLL[r][c] : -1;
                        if (regla != -1) {
                            LadoIzquierdo prod = gramaticaActualLL1.reglas[regla];
                            modPila.addRow(new Object[]{sb.toString(), terminal, prod.toString()});
                            pila.pop();
                            if (!(prod.listaSimb.size() == 1 && prod.listaSimb.get(0).nombre.equals(Simbolo.EPSILON))) {
                                for (int i = prod.listaSimb.size() - 1; i >= 0; i--) pila.push(prod.listaSimb.get(i));
                            }
                        } else {
                            modPila.addRow(new Object[]{sb.toString(), terminal, "ERROR: Celda Vacía"}); error = true; break;
                        }
                    }
                }
                if (!error) JOptionPane.showMessageDialog(this, "Cadena válida en LL(1).");
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Error en análisis."); }
        });

        return panelPrincipal;
    }

    // =======================================================
    // NUEVO PANEL: ANÁLISIS LR(0) (MATRIZ DE ACCIÓN Y GOTO)
    // =======================================================
  private JPanel crearPanelLR0() {
        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        // --- NORTE: Gramática y Botón ---
        JPanel panelNorte = new JPanel(new BorderLayout(5, 5));
        panelNorte.setBorder(BorderFactory.createTitledBorder("1. Definir Gramática para LR(0)"));
        JTextArea txtGramatica = new JTextArea(4, 50);
        JScrollPane scrollGramatica = new JScrollPane(txtGramatica);
        JButton btnProcesarGramatica = new JButton("Construir Tabla LR(0)");
        btnProcesarGramatica.setBackground(new Color(153, 0, 0));
        btnProcesarGramatica.setForeground(Color.WHITE);
        panelNorte.add(scrollGramatica, BorderLayout.CENTER);
        panelNorte.add(btnProcesarGramatica, BorderLayout.EAST);

        // --- CENTRO: Tabla LR(0) y Análisis de Cadena ---
        JPanel panelCentro = new JPanel(new GridLayout(2, 1, 10, 10));

        // Sub-panel Tabla
        DefaultTableModel modTablaLR0 = new DefaultTableModel();
        JTable tablaLR0 = new JTable(modTablaLR0);
        tablaLR0.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        JScrollPane scrollTablaLR0 = new JScrollPane(tablaLR0);
        scrollTablaLR0.setBorder(BorderFactory.createTitledBorder("2. Tabla de Acción y Goto LR(0)"));
        panelCentro.add(scrollTablaLR0);

        // Sub-panel Análisis (Igual que LL1)
        JPanel panelAnalisis = new JPanel(new BorderLayout(5, 5));
        panelAnalisis.setBorder(BorderFactory.createTitledBorder("3. Analizar Cadena LR(0)"));
        
        JPanel panelControlesSur = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        JButton btnCargarAfd = new JButton("Seleccionar AFD Léxico (.txt)");
        JLabel lblAfdCargado = new JLabel("Ningún AFD");
        final String[] rutaAfdLexico = {""};
        JTextField txtSigma = new JTextField(15);
        JButton btnAnalizar = new JButton("Analizar Cadena LR(0)");
        
        panelControlesSur.add(btnCargarAfd);
        panelControlesSur.add(lblAfdCargado);
        panelControlesSur.add(new JLabel(" Sigma: "));
        panelControlesSur.add(txtSigma);
        panelControlesSur.add(btnAnalizar);
        
        DefaultTableModel modPilaLR = new DefaultTableModel(new String[]{"Pila", "Cadena / Token", "Acción"}, 0);
        JTable tabPilaLR = new JTable(modPilaLR);
        
        panelAnalisis.add(panelControlesSur, BorderLayout.NORTH);
        panelAnalisis.add(new JScrollPane(tabPilaLR), BorderLayout.CENTER);
        panelCentro.add(panelAnalisis);

        panelPrincipal.add(panelNorte, BorderLayout.NORTH);
        panelPrincipal.add(panelCentro, BorderLayout.CENTER);

        // --- LÓGICA DE CONSTRUCCIÓN ---
        btnProcesarGramatica.addActionListener(e -> {
            if (txtGramatica.getText().trim().isEmpty()) return;
            JFileChooser chooser = new JFileChooser(new File(System.getProperty("user.dir")));
            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                try {
                    DescRecGram_Gram parser = new DescRecGram_Gram(txtGramatica.getText().trim(), chooser.getSelectedFile().getAbsolutePath());
                    if (parser.G()) {
                        gramaticaActualLR0 = parser.gramaticaResultante;
                        analizadorLR0 = new AnalizadorLR0(gramaticaActualLR0);
                        analizadorLR0.ConstruirColeccionCanonica();
                        analizadorLR0.ConstruirTablaLR0();
                        
                        // Llenado de tabla
                        modTablaLR0.setColumnCount(0);
                        modTablaLR0.addColumn("Estado");
                        for(Simbolo s : analizadorLR0.arrVt) modTablaLR0.addColumn(s.nombre);
                        for(Simbolo s : analizadorLR0.arrVn) modTablaLR0.addColumn(s.nombre);
                        modTablaLR0.setRowCount(0);
                        for (int i = 0; i < analizadorLR0.TablaAccion.length; i++) {
                            Object[] fila = new Object[1 + analizadorLR0.arrVt.size() + analizadorLR0.arrVn.size()];
                            fila[0] = i; int c = 1;
                            for (int j = 0; j < analizadorLR0.arrVt.size(); j++) fila[c++] = analizadorLR0.TablaAccion[i][j];
                            for (int j = 0; j < analizadorLR0.arrVn.size(); j++) fila[c++] = (analizadorLR0.TablaGoto[i][j] != -1) ? analizadorLR0.TablaGoto[i][j] : "";
                            modTablaLR0.addRow(fila);
                        }
                    }
                } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage()); }
            }
        });

        // --- LÓGICA ANÁLISIS CADENA LR(0) ---
        btnCargarAfd.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser(new File(System.getProperty("user.dir")));
            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                rutaAfdLexico[0] = chooser.getSelectedFile().getAbsolutePath();
                lblAfdCargado.setText(chooser.getSelectedFile().getName());
            }
        });

        btnAnalizar.addActionListener(e -> {
            if (analizadorLR0 == null || rutaAfdLexico[0].isEmpty()) return;
            modPilaLR.setRowCount(0);
            try {
                AnalizadorLexico lex = new AnalizadorLexico(rutaAfdLexico[0]);
                lex.setSigma(txtSigma.getText().trim());
                java.util.Stack<Object> pila = new java.util.Stack<>();
                pila.push(0); // Estado inicial 0
                
                int token = lex.yylex();
                while (true) {
                    int estadoCima = (int) pila.peek();
                    String terminal = (token == AnalizadorLexico.TOKEN_FIN) ? "$" : lex.getLexema();
                    int col = analizadorLR0.arrVt.indexOf(new Simbolo(terminal, true));
                    
                    if (col == -1) { modPilaLR.addRow(new Object[]{pila.toString(), terminal, "Error Token"}); break; }
                    
                    String accion = analizadorLR0.TablaAccion[estadoCima][col];
                    modPilaLR.addRow(new Object[]{pila.toString(), terminal, accion});
                    
                    if (accion.startsWith("D")) { // Desplazar
                        pila.push(terminal);
                        pila.push(Integer.parseInt(accion.substring(1)));
                        token = lex.yylex();
                    } else if (accion.startsWith("R")) { // Reducir
                        int numRegla = Integer.parseInt(accion.substring(1));
                        int tamRegla = gramaticaActualLR0.reglas[numRegla].listaSimb.size() * 2;
                        for(int k=0; k<tamRegla; k++) pila.pop();
                        int estadoGoto = analizadorLR0.TablaGoto[(int)pila.peek()][analizadorLR0.arrVn.indexOf(gramaticaActualLR0.reglas[numRegla].simboloIzquierdo)];
                        pila.push(gramaticaActualLR0.reglas[numRegla].simboloIzquierdo.nombre);
                        pila.push(estadoGoto);
                    } else if (accion.equals("ACEPT")) {
                        JOptionPane.showMessageDialog(this, "¡Cadena Aceptada por LR(0)!"); break;
                    } else { break; }
                }
            } catch (Exception ex) { ex.printStackTrace(); }
        });
        return panelPrincipal;
    }

    // =======================================================
    // PANELES ORIGINALES (NO SE MODIFICÓ NINGÚN DISEÑO AQUÍ)
    // =======================================================
    private JPanel crearPanelBienvenida() {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel lbl = new JLabel("Seleccione una opción del menú superior.", SwingConstants.CENTER);
        lbl.setFont(new Font("Arial", Font.ITALIC, 16));
        panel.add(lbl, BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearPanelCalculadora() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel panelNorte = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnCargar = new JButton("Cargar Tabla AFD (.txt)");
        JLabel lblArchivo = new JLabel("Ningún archivo seleccionado");
        lblArchivo.setFont(new Font("Arial", Font.ITALIC, 12));
        lblArchivo.setForeground(Color.GRAY);
        panelNorte.add(btnCargar); panelNorte.add(lblArchivo);

        JPanel panelCentro = new JPanel(new BorderLayout(10, 10));
        JLabel lblInstruccion = new JLabel("Ingrese la expresión matemática (ej: 2 + 3 * 4):");
        lblInstruccion.setFont(new Font("Arial", Font.BOLD, 14));
        panelCentro.add(lblInstruccion, BorderLayout.NORTH);
        
        JTextArea txtExpresion = new JTextArea();
        txtExpresion.setFont(new Font("Monospaced", Font.BOLD, 22));
        txtExpresion.setMargin(new Insets(10, 10, 10, 10)); 
        txtExpresion.setLineWrap(true); txtExpresion.setWrapStyleWord(true); 
        JScrollPane scrollExpresion = new JScrollPane(txtExpresion);
        panelCentro.add(scrollExpresion, BorderLayout.CENTER);

        JButton btnEvaluar = new JButton("Evaluar (Descenso Recursivo)");
        btnEvaluar.setFont(new Font("Arial", Font.BOLD, 14));
        btnEvaluar.setBackground(new Color(153, 0, 153)); 
        btnEvaluar.setForeground(Color.WHITE);
        panelCentro.add(btnEvaluar, BorderLayout.SOUTH);

        JPanel panelSur = new JPanel(new GridLayout(2, 1, 10, 10));
        panelSur.setBorder(BorderFactory.createTitledBorder("Vista Previa de Resultados"));
        JLabel lblResultado = new JLabel(" Resultado: ---"); lblResultado.setFont(new Font("Arial", Font.BOLD, 18));
        JLabel lblPostfijo = new JLabel(" Notación Postfija: ---"); lblPostfijo.setFont(new Font("Arial", Font.ITALIC, 16));
        panelSur.add(lblResultado); panelSur.add(lblPostfijo);

        panel.add(panelNorte, BorderLayout.NORTH); panel.add(panelCentro, BorderLayout.CENTER); panel.add(panelSur, BorderLayout.SOUTH);

        final String[] rutaArchivo = {""};
        btnCargar.addActionListener(e -> {
            File rutaDefecto = new File("tablas_afd");
            if (!rutaDefecto.exists()) rutaDefecto = new File(System.getProperty("user.dir")); 
            JFileChooser chooser = new JFileChooser(rutaDefecto.getAbsolutePath());
            chooser.setDialogTitle("Seleccionar Tabla AFD de tu Calculadora");
            if (chooser.showOpenDialog(VentanaPrincipal.this) == JFileChooser.APPROVE_OPTION) {
                File archivo = chooser.getSelectedFile(); rutaArchivo[0] = archivo.getAbsolutePath();
                lblArchivo.setText("Cargado: " + archivo.getName()); lblArchivo.setForeground(new Color(0, 153, 0)); 
            }
        });

        btnEvaluar.addActionListener(e -> {
            if (rutaArchivo[0].isEmpty()) { JOptionPane.showMessageDialog(VentanaPrincipal.this, "Carga AFD.", "Falta AFD", JOptionPane.WARNING_MESSAGE); return; }
            if (txtExpresion.getText().trim().isEmpty()) { JOptionPane.showMessageDialog(VentanaPrincipal.this, "Ingresa expresión.", "Campo Vacío", JOptionPane.WARNING_MESSAGE); return; }
            try {
                EvaluadorExpr evaluador = new EvaluadorExpr(txtExpresion.getText().trim(), rutaArchivo[0]);
                if (evaluador.iniEval()) { 
                    lblResultado.setText(" Resultado: " + evaluador.result); lblResultado.setForeground(new Color(0, 102, 0)); 
                    lblPostfijo.setText(" Notación Postfija: " + evaluador.exprPost); lblPostfijo.setForeground(Color.BLUE);
                } else {
                    lblResultado.setText(" Resultado: ERROR DE SINTAXIS"); lblResultado.setForeground(Color.RED); 
                    lblPostfijo.setText(" Notación Postfija: No se pudo generar"); lblPostfijo.setForeground(Color.RED);
                }
            } catch (Exception ex) { JOptionPane.showMessageDialog(VentanaPrincipal.this, "Error crítico.", "Error", JOptionPane.ERROR_MESSAGE); }
        });
        return panel;
    }

    private JPanel crearPanelPolinomios() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel panelNorte = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnCargar = new JButton("Cargar Tabla AFD (.txt)");
        JLabel lblArchivo = new JLabel("Ningún archivo seleccionado");
        lblArchivo.setFont(new Font("Arial", Font.ITALIC, 12)); lblArchivo.setForeground(Color.GRAY);
        panelNorte.add(btnCargar); panelNorte.add(lblArchivo);

        JPanel panelCentro = new JPanel(new BorderLayout(10, 10));
        JLabel lblInstruccion = new JLabel("Ingrese expresión de polinomios (ej: R = 3x^2 + 5x - 8):");
        lblInstruccion.setFont(new Font("Arial", Font.BOLD, 14));
        panelCentro.add(lblInstruccion, BorderLayout.NORTH);
        
        JTextArea txtExpresion = new JTextArea(); txtExpresion.setFont(new Font("Monospaced", Font.BOLD, 22)); txtExpresion.setMargin(new Insets(10, 10, 10, 10)); txtExpresion.setLineWrap(true); txtExpresion.setWrapStyleWord(true); 
        JScrollPane scrollExpresion = new JScrollPane(txtExpresion); panelCentro.add(scrollExpresion, BorderLayout.CENTER);

        JButton btnEvaluar = new JButton("Validar Sintaxis (Descenso Recursivo)");
        btnEvaluar.setFont(new Font("Arial", Font.BOLD, 14)); btnEvaluar.setBackground(new Color(204, 102, 0)); btnEvaluar.setForeground(Color.WHITE);
        panelCentro.add(btnEvaluar, BorderLayout.SOUTH);

        JPanel panelSur = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelSur.setBorder(BorderFactory.createTitledBorder("Estado del Análisis"));
        JLabel lblResultado = new JLabel("Esperando expresión..."); lblResultado.setFont(new Font("Arial", Font.BOLD, 18)); panelSur.add(lblResultado);

        panel.add(panelNorte, BorderLayout.NORTH); panel.add(panelCentro, BorderLayout.CENTER); panel.add(panelSur, BorderLayout.SOUTH);

        final String[] rutaArchivo = {""};
        btnCargar.addActionListener(e -> {
            File rutaDefecto = new File("tablas_afd");
            if (!rutaDefecto.exists()) rutaDefecto = new File(System.getProperty("user.dir")); 
            JFileChooser chooser = new JFileChooser(rutaDefecto.getAbsolutePath());
            if (chooser.showOpenDialog(VentanaPrincipal.this) == JFileChooser.APPROVE_OPTION) {
                File archivo = chooser.getSelectedFile(); rutaArchivo[0] = archivo.getAbsolutePath(); lblArchivo.setText("Cargado: " + archivo.getName()); lblArchivo.setForeground(new Color(0, 153, 0)); 
            }
        });

        btnEvaluar.addActionListener(e -> {
            if (rutaArchivo[0].isEmpty() || txtExpresion.getText().trim().isEmpty()) return;
            try {
                EvaluadorPolinomios ev = new EvaluadorPolinomios(txtExpresion.getText().trim(), rutaArchivo[0]);
                if (ev.iniEval()) { lblResultado.setText("✅ SINTAXIS VÁLIDA"); lblResultado.setForeground(new Color(0, 102, 0)); }
                else { lblResultado.setText("❌ ERROR DE SINTAXIS"); lblResultado.setForeground(Color.RED); }
            } catch (Exception ex) { JOptionPane.showMessageDialog(VentanaPrincipal.this, "Error crítico.", "Error", JOptionPane.ERROR_MESSAGE); }
        });
        return panel;
    }

    private JPanel crearPanelERaAFN() {
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT); splitPane.setDividerLocation(350);
        JPanel panelIzquierdo = new JPanel(new GridBagLayout()); GridBagConstraints gbc = new GridBagConstraints(); gbc.insets = new Insets(10, 10, 10, 10); gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblTitulo = new JLabel("Expresión Regular a AFN"); lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        JTextField txtER = new JTextField(15); txtER.setFont(new Font("Monospaced", Font.PLAIN, 14)); JTextField txtIdAfn = new JTextField(5);
        JButton btnCrear = new JButton("Crear AFN desde ER"); btnCrear.setBackground(new Color(0, 153, 76)); btnCrear.setForeground(Color.WHITE);

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; panelIzquierdo.add(lblTitulo, gbc);
        gbc.gridwidth = 1; gbc.gridy = 1; panelIzquierdo.add(new JLabel("Expresión Regular:"), gbc); gbc.gridx = 1; panelIzquierdo.add(txtER, gbc);
        gbc.gridx = 0; gbc.gridy = 2; panelIzquierdo.add(new JLabel("ID del AFN:"), gbc); gbc.gridx = 1; panelIzquierdo.add(txtIdAfn, gbc);
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2; panelIzquierdo.add(btnCrear, gbc);

        JLabel lblAyuda = new JLabel("<html><small>Ejemplos:<br>- [a-z]+<br>- (a|b)*c<br>- \\+ | \\*</small></html>");
        lblAyuda.setForeground(Color.GRAY); gbc.gridy = 4; panelIzquierdo.add(lblAyuda, gbc);

        JScrollPane scrollImagen = new JScrollPane(lblImagenERaAFN);
        splitPane.setLeftComponent(panelIzquierdo); splitPane.setRightComponent(scrollImagen);

        btnCrear.addActionListener(e -> {
            try {
                if(txtER.getText().trim().isEmpty() || txtIdAfn.getText().trim().isEmpty()){ JOptionPane.showMessageDialog(this, "Llene campos.", "Vacío", JOptionPane.WARNING_MESSAGE); return; }
                int nuevoId = Integer.parseInt(txtIdAfn.getText().trim());
                if (obtenerAFNPorId(nuevoId) != null) { JOptionPane.showMessageDialog(this, "ID Duplicado.", "Error", JOptionPane.WARNING_MESSAGE); return; }

                ER_a_AFN conversor = new ER_a_AFN(txtER.getText().trim());
                AFN nuevoAfn = conversor.convertir();
                if (nuevoAfn != null) {
                    nuevoAfn.idAFN = nuevoId;
                    if(!AFN.coleccionAFN.contains(nuevoAfn)) AFN.coleccionAFN.add(nuevoAfn);
                    String nombreImg = "AFN_" + nuevoAfn.idAFN; nuevoAfn.generarGrafico(nombreImg);
                    cargarImagenEnLabel(lblImagenERaAFN, "imagenes/" + nombreImg + ".png");
                    actualizarListas();
                    JOptionPane.showMessageDialog(this, "AFN generado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                } else JOptionPane.showMessageDialog(this, "Error de sintaxis en ER.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "ID inválido.", "Error", JOptionPane.ERROR_MESSAGE); }
        });
        JPanel panelFinal = new JPanel(new BorderLayout()); panelFinal.add(splitPane, BorderLayout.CENTER); return panelFinal;
    }

    private JPanel crearPanelProbarLexico() {
        JPanel panel = new JPanel(new BorderLayout(10, 10)); panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        JPanel panelNorte = new JPanel(new FlowLayout(FlowLayout.LEFT)); JButton btnCargar = new JButton("Cargar Tabla AFD (.txt)"); JLabel lblArchivo = new JLabel("Ningún archivo seleccionado");
        panelNorte.add(btnCargar); panelNorte.add(lblArchivo);

        JPanel panelCentro = new JPanel(new BorderLayout(5, 5)); JTextArea txtCodigo = new JTextArea(8, 50); JScrollPane scrollCodigo = new JScrollPane(txtCodigo);
        JButton btnAnalizar = new JButton("Ejecutar Análisis Léxico"); panelCentro.add(scrollCodigo, BorderLayout.CENTER); panelCentro.add(btnAnalizar, BorderLayout.SOUTH);

        DefaultTableModel modeloResultados = new DefaultTableModel(new String[]{"Token Encontrado", "Lexema"}, 0);
        JTable tablaResultados = new JTable(modeloResultados); JScrollPane scrollResultados = new JScrollPane(tablaResultados);
        JSplitPane splitVertical = new JSplitPane(JSplitPane.VERTICAL_SPLIT, panelCentro, scrollResultados); splitVertical.setDividerLocation(200);

        panel.add(panelNorte, BorderLayout.NORTH); panel.add(splitVertical, BorderLayout.CENTER);
        final String[] rutaArchivo = {""};

        btnCargar.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser(new File(System.getProperty("user.dir")));
            if (chooser.showOpenDialog(VentanaPrincipal.this) == JFileChooser.APPROVE_OPTION) {
                rutaArchivo[0] = chooser.getSelectedFile().getAbsolutePath(); lblArchivo.setText(chooser.getSelectedFile().getName());
            }
        });

        btnAnalizar.addActionListener(e -> {
            if (rutaArchivo[0].isEmpty() || txtCodigo.getText().isEmpty()) return;
            modeloResultados.setRowCount(0); 
            try {
                AnalizadorLexico lexico = new AnalizadorLexico(rutaArchivo[0]); lexico.setSigma(txtCodigo.getText());
                int token;
                while ((token = lexico.yylex()) != AnalizadorLexico.TOKEN_FIN) {
                    if (token == AnalizadorLexico.TOKEN_ERROR) modeloResultados.addRow(new Object[]{"ERROR", lexico.getLexema()});
                    else modeloResultados.addRow(new Object[]{String.valueOf(token), lexico.getLexema()});
                }
                modeloResultados.addRow(new Object[]{"0", " "}); 
            } catch (Exception ex) {}
        });
        return panel;
    }

    private JPanel crearPanelConvertir() {
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT); splitPane.setDividerLocation(350);
        JPanel panelControles = new JPanel(new GridBagLayout()); GridBagConstraints gbc = new GridBagConstraints(); gbc.insets = new Insets(15, 10, 15, 10); gbc.fill = GridBagConstraints.HORIZONTAL;
        JButton btnConvertir = new JButton("Convertir AFN a AFD");

        gbc.gridx = 0; gbc.gridy = 1; panelControles.add(new JLabel("Seleccione el AFN base:"), gbc);
        gbc.gridy = 2; panelControles.add(comboAfnAfd, gbc); gbc.gridy = 3; panelControles.add(btnConvertir, gbc);

        JScrollPane scrollImagen = new JScrollPane(lblImagenAfnAfd);
        splitPane.setLeftComponent(panelControles); splitPane.setRightComponent(scrollImagen);

        comboAfnAfd.addItemListener(e -> { if (e.getStateChange() == ItemEvent.SELECTED) { AFN a = obtenerAFNPorTexto((String) e.getItem()); if (a != null) cargarImagenEnLabel(lblImagenAfnAfd, "imagenes/AFN_" + a.idAFN + ".png"); } });
        btnConvertir.addActionListener(e -> {
            AFN afnBase = obtenerAFNPorTexto((String) comboAfnAfd.getSelectedItem());
            if (afnBase != null) {
                try {
                    AFD afdResultante = afnBase.convertirAAFD();
                    String nombreImgAfd = "AFD_" + afnBase.idAFN; afdResultante.generarGrafico(nombreImgAfd);
                    String rutaTxt = "tablas_afd/Tabla_AFD_" + afnBase.idAFN + ".txt"; afdResultante.guardarAFDEnArchivo(rutaTxt);
                    mostrarVentanaFlotante("imagenes/" + nombreImgAfd + ".png", "Resultado AFD");
                } catch (Exception ex) {}
            }
        });
        JPanel panelFinal = new JPanel(new BorderLayout()); panelFinal.add(splitPane, BorderLayout.CENTER); return panelFinal;
    }

    private JPanel crearPanelBasicoUnCaracter() {
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT); splitPane.setDividerLocation(300);
        JPanel panelIzquierdo = new JPanel(new GridBagLayout()); GridBagConstraints gbc = new GridBagConstraints(); gbc.insets = new Insets(10, 10, 10, 10); gbc.fill = GridBagConstraints.HORIZONTAL;
        JCheckBox checkAscii = new JCheckBox("Usar código ASCII"); JTextField txtCaracter = new JTextField(5); JTextField txtIdAfn = new JTextField(5); JButton btnCrear = new JButton("Crear AFN");
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; panelIzquierdo.add(checkAscii, gbc); gbc.gridwidth = 1; gbc.gridy = 1; panelIzquierdo.add(txtCaracter, gbc); gbc.gridy = 2; panelIzquierdo.add(txtIdAfn, gbc); gbc.gridy = 3; panelIzquierdo.add(btnCrear, gbc);
        JScrollPane scrollImagen = new JScrollPane(lblImagenBasicoUn); splitPane.setLeftComponent(panelIzquierdo); splitPane.setRightComponent(scrollImagen);
        btnCrear.addActionListener(e -> {
            AFN a = new AFN(); a.crearAFNBasico(txtCaracter.getText().charAt(0)); a.idAFN = Integer.parseInt(txtIdAfn.getText());
            AFN.coleccionAFN.add(a); a.generarGrafico("AFN_" + a.idAFN); cargarImagenEnLabel(lblImagenBasicoUn, "imagenes/AFN_" + a.idAFN + ".png"); actualizarListas();
        });
        JPanel panelFinal = new JPanel(new BorderLayout()); panelFinal.add(splitPane, BorderLayout.CENTER); return panelFinal;
    }

    private JPanel crearPanelBasicoRango() {
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT); splitPane.setDividerLocation(300);
        JPanel panelIzquierdo = new JPanel(new GridBagLayout()); GridBagConstraints gbc = new GridBagConstraints(); gbc.insets = new Insets(10, 10, 10, 10); gbc.fill = GridBagConstraints.HORIZONTAL;
        JTextField txtInferior = new JTextField(5); JTextField txtSuperior = new JTextField(5); JTextField txtIdAfn = new JTextField(5); JButton btnCrear = new JButton("Crear AFN");
        gbc.gridx = 0; gbc.gridy = 1; panelIzquierdo.add(txtInferior, gbc); gbc.gridy = 2; panelIzquierdo.add(txtSuperior, gbc); gbc.gridy = 3; panelIzquierdo.add(txtIdAfn, gbc); gbc.gridy = 4; panelIzquierdo.add(btnCrear, gbc);
        JScrollPane scrollImagen = new JScrollPane(lblImagenBasicoRango); splitPane.setLeftComponent(panelIzquierdo); splitPane.setRightComponent(scrollImagen);
        btnCrear.addActionListener(e -> {
            AFN a = new AFN(); a.crearAFNBasico(txtInferior.getText().charAt(0), txtSuperior.getText().charAt(0)); a.idAFN = Integer.parseInt(txtIdAfn.getText());
            AFN.coleccionAFN.add(a); a.generarGrafico("AFN_" + a.idAFN); cargarImagenEnLabel(lblImagenBasicoRango, "imagenes/AFN_" + a.idAFN + ".png"); actualizarListas();
        });
        JPanel panelFinal = new JPanel(new BorderLayout()); panelFinal.add(splitPane, BorderLayout.CENTER); return panelFinal;
    }

    private JPanel crearPanelUnionLexico() {
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT); splitPane.setDividerLocation(500);
        JPanel panelControles = new JPanel(new BorderLayout());
        modeloTablaUnionLexica = new DefaultTableModel(new String[]{"AFNs", "Seleccionar AFN", "Token"}, 0) {
            @Override public Class<?> getColumnClass(int columnIndex) { return (columnIndex == 1) ? Boolean.class : String.class; }
            @Override public boolean isCellEditable(int r, int c) { return c == 1 || c == 2; }
        };
        tablaUnionLexica = new JTable(modeloTablaUnionLexica); JScrollPane scrollTabla = new JScrollPane(tablaUnionLexica);
        JPanel panelSur = new JPanel(new FlowLayout(FlowLayout.RIGHT)); JTextField txtNuevoId = new JTextField(5); JButton btnUnir = new JButton("Unir AFNs");
        panelSur.add(txtNuevoId); panelSur.add(btnUnir); panelControles.add(scrollTabla, BorderLayout.CENTER); panelControles.add(panelSur, BorderLayout.SOUTH);
        JScrollPane scrollImagen = new JScrollPane(lblImagenUnionLexica); splitPane.setLeftComponent(panelControles); splitPane.setRightComponent(scrollImagen);

        btnUnir.addActionListener(e -> {
            ArrayList<AFN> afnsAUnir = new ArrayList<>(); ArrayList<Integer> tokensAsignados = new ArrayList<>();
            for (int i = 0; i < modeloTablaUnionLexica.getRowCount(); i++) {
                if ((boolean) modeloTablaUnionLexica.getValueAt(i, 1)) {
                    afnsAUnir.add(obtenerAFNPorId(Integer.parseInt(modeloTablaUnionLexica.getValueAt(i, 0).toString())));
                    tokensAsignados.add(Integer.parseInt(modeloTablaUnionLexica.getValueAt(i, 2).toString().trim()));
                }
            }
            AFN superAfn = new AFN(); superAfn.unionEspecialParaLexico(afnsAUnir, tokensAsignados, Integer.parseInt(txtNuevoId.getText().trim()));
            superAfn.generarGrafico("AFN_" + superAfn.idAFN); cargarImagenEnLabel(lblImagenUnionLexica, "imagenes/AFN_" + superAfn.idAFN + ".png"); actualizarListas();
        });
        JPanel panelFinal = new JPanel(new BorderLayout()); panelFinal.add(splitPane, BorderLayout.CENTER); return panelFinal;
    }

    private JPanel crearPanelOperacionBinariaSimple(String titulo, String accionTexto, JComboBox<String> combo1, JComboBox<String> combo2, JLabel lblVisor, boolean esUnionNormal) {
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT); splitPane.setDividerLocation(350);
        JPanel panelControles = new JPanel(new GridBagLayout()); GridBagConstraints gbc = new GridBagConstraints(); gbc.insets = new Insets(10, 10, 10, 10); gbc.fill = GridBagConstraints.HORIZONTAL;
        JButton btnAplicar = new JButton(titulo);
        gbc.gridx = 0; gbc.gridy = 2; panelControles.add(combo1, gbc); gbc.gridy = 4; panelControles.add(combo2, gbc); gbc.gridy = 5; panelControles.add(btnAplicar, gbc);
        JScrollPane scrollImagen = new JScrollPane(lblVisor); splitPane.setLeftComponent(panelControles); splitPane.setRightComponent(scrollImagen);
        btnAplicar.addActionListener(e -> {
            AFN afn1 = obtenerAFNPorTexto((String) combo1.getSelectedItem()); AFN afn2 = obtenerAFNPorTexto((String) combo2.getSelectedItem());
            if (afn1 != null && afn2 != null) {
                if(esUnionNormal) afn1.unionAFN(afn2); else afn1.concatenacion(afn2);
                afn1.generarGrafico("AFN_" + afn1.idAFN); cargarImagenEnLabel(lblVisor, "imagenes/AFN_" + afn1.idAFN + ".png"); actualizarListas();
                mostrarVentanaFlotante("imagenes/AFN_" + afn1.idAFN + ".png", "Absorción completada");
            }
        });
        JPanel panelFinal = new JPanel(new BorderLayout()); panelFinal.add(splitPane, BorderLayout.CENTER); return panelFinal;
    }

    private JPanel crearPanelOperacionUnaria(String titulo, String simbolo, JComboBox<String> combo, JLabel lblVisor) {
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT); splitPane.setDividerLocation(300);
        JPanel panelControles = new JPanel(new GridBagLayout()); GridBagConstraints gbc = new GridBagConstraints(); gbc.insets = new Insets(15, 10, 15, 10); gbc.fill = GridBagConstraints.HORIZONTAL;
        JButton btnAplicar = new JButton("Aplicar");
        gbc.gridx = 0; gbc.gridy = 2; panelControles.add(combo, gbc); gbc.gridy = 3; panelControles.add(btnAplicar, gbc);
        JScrollPane scrollImagen = new JScrollPane(lblVisor); splitPane.setLeftComponent(panelControles); splitPane.setRightComponent(scrollImagen);
        combo.addItemListener(e -> { if (e.getStateChange() == ItemEvent.SELECTED) { AFN a = obtenerAFNPorTexto((String) e.getItem()); if (a != null) cargarImagenEnLabel(lblVisor, "imagenes/AFN_" + a.idAFN + ".png"); } });
        btnAplicar.addActionListener(e -> {
            AFN a = obtenerAFNPorTexto((String) combo.getSelectedItem());
            if (a != null) {
                if (simbolo.equals("+")) a.cerraduraPositiva(); else if (simbolo.equals("*")) a.cerraduraKleene(); else a.opcional();
                a.generarGrafico("AFN_" + a.idAFN); cargarImagenEnLabel(lblVisor, "imagenes/AFN_" + a.idAFN + ".png");
            }
        });
        JPanel panelFinal = new JPanel(new BorderLayout()); panelFinal.add(splitPane, BorderLayout.CENTER); return panelFinal;
    }

    private void actualizarListas() {
        JComboBox[] combos = {comboAFNsPositiva, comboAFNsKleene, comboAFNsOpcional, comboConcat1, comboConcat2, comboUnionNormal1, comboUnionNormal2, comboAfnAfd};
        for (JComboBox c : combos) c.removeAllItems();
        modeloTablaUnionLexica.setRowCount(0);
        for (AFN afn : AFN.coleccionAFN) {
            for (JComboBox c : combos) c.addItem("AFN ID: " + afn.idAFN);
            modeloTablaUnionLexica.addRow(new Object[]{String.valueOf(afn.idAFN), false, ""});
        }
    }

    private AFN obtenerAFNPorTexto(String texto) { if (texto == null || !texto.contains(":")) return null; return obtenerAFNPorId(Integer.parseInt(texto.split(":")[1].trim())); }
    private AFN obtenerAFNPorId(int id) { for (AFN a : AFN.coleccionAFN) if (a.idAFN == id) return a; return null; }

    private void mostrarDialogoBorrar() {
        if (AFN.coleccionAFN.isEmpty()) return;
        ArrayList<String> opciones = new ArrayList<>(); for (AFN afn : AFN.coleccionAFN) opciones.add("AFN ID: " + afn.idAFN);
        String sel = (String) JOptionPane.showInputDialog(this, "Selecciona AFN a eliminar:", "Borrar", JOptionPane.WARNING_MESSAGE, null, opciones.toArray(), opciones.get(0));
        if (sel != null) {
            AFN a = obtenerAFNPorTexto(sel);
            if (a != null) { AFN.coleccionAFN.remove(a); actualizarListas(); JOptionPane.showMessageDialog(this, "Borrado."); }
        }
    }

    private void cargarImagenEnLabel(JLabel label, String ruta) {
        File f = new File(ruta); if (f.exists()) { ImageIcon i = new ImageIcon(ruta); i.getImage().flush(); label.setIcon(i); label.setText(""); } else label.setText("Sin imagen");
    }

    private void mostrarVentanaFlotante(String rutaAbsoluta, String tituloVentana) {
        File f = new File(rutaAbsoluta);
        if(f.exists()){
            JFrame visor = new JFrame(tituloVentana); visor.setSize(800, 500); visor.setLocationRelativeTo(this);
            ImageIcon icono = new ImageIcon(rutaAbsoluta); icono.getImage().flush(); JLabel lblImagen = new JLabel(icono);
            visor.add(new JScrollPane(lblImagen)); visor.setAlwaysOnTop(true); visor.setVisible(true);
        }
    }
}