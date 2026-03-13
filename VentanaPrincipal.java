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
        JMenuItem itemPolinomios = new JMenuItem("Polinomios"); // <-- NUEVO
        //itemPolinomios.setForeground(new Color(204, 102, 0)); // Naranja para diferenciar
        
        subMenuDescenso.add(itemCalculadora);
        subMenuDescenso.add(itemPolinomios); // <-- Añadido al submenú
        menuSintactico.add(subMenuDescenso);

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
        
        // Panel de la Calculadora (Analizador Sintáctico)
        contenedorTarjetas.add(crearPanelCalculadora(), "Calculadora");
        contenedorTarjetas.add(crearPanelPolinomios(), "Polinomios");
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
        
        // Nuevo Evento para la Calculadora
       itemCalculadora.addActionListener(e -> cardLayout.show(contenedorTarjetas, "Calculadora"));
        itemPolinomios.addActionListener(e -> cardLayout.show(contenedorTarjetas, "Polinomios")); // <-- NUEVO EVENTO
        
        itemBorrar.addActionListener(e -> mostrarDialogoBorrar());
    }

    // Un panel simple que se muestra al abrir la aplicación
    private JPanel crearPanelBienvenida() {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel lbl = new JLabel("Seleccione una opción del menú superior.", SwingConstants.CENTER);
        lbl.setFont(new Font("Arial", Font.ITALIC, 16));
        panel.add(lbl, BorderLayout.CENTER);
        return panel;
    }

    
 // =======================================================
    // PANEL DE CALCULADORA (ANALIZADOR SINTÁCTICO) 
    // =======================================================
    private JPanel crearPanelCalculadora() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Norte: Cargar archivo
        JPanel panelNorte = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnCargar = new JButton("Cargar Tabla AFD (.txt)");
        JLabel lblArchivo = new JLabel("Ningún archivo seleccionado");
        lblArchivo.setFont(new Font("Arial", Font.ITALIC, 12));
        lblArchivo.setForeground(Color.GRAY);
        panelNorte.add(btnCargar);
        panelNorte.add(lblArchivo);

        // Centro: Input expresión matemática con JTextArea para alinear arriba
        JPanel panelCentro = new JPanel(new BorderLayout(10, 10));
        JLabel lblInstruccion = new JLabel("Ingrese la expresión matemática (ej: 2 + 3 * 4):");
        lblInstruccion.setFont(new Font("Arial", Font.BOLD, 14));
        panelCentro.add(lblInstruccion, BorderLayout.NORTH);
        
        // --- AQUÍ ESTÁ LA MAGIA: Cambiamos JTextField por JTextArea ---
        JTextArea txtExpresion = new JTextArea();
        txtExpresion.setFont(new Font("Monospaced", Font.BOLD, 22));
        txtExpresion.setMargin(new Insets(10, 10, 10, 10)); 
        txtExpresion.setLineWrap(true); // Hace que el texto baje de renglón si es muy largo
        txtExpresion.setWrapStyleWord(true); // Evita cortar números por la mitad al bajar de renglón
        
        JScrollPane scrollExpresion = new JScrollPane(txtExpresion); // Le agregamos barra de desplazamiento por si acaso
        panelCentro.add(scrollExpresion, BorderLayout.CENTER);

        JButton btnEvaluar = new JButton("Evaluar (Descenso Recursivo)");
        btnEvaluar.setFont(new Font("Arial", Font.BOLD, 14));
        btnEvaluar.setBackground(new Color(153, 0, 153)); 
        btnEvaluar.setForeground(Color.WHITE);
        panelCentro.add(btnEvaluar, BorderLayout.SOUTH);

        // Sur: Resultados visuales en el panel
        JPanel panelSur = new JPanel(new GridLayout(2, 1, 10, 10));
        panelSur.setBorder(BorderFactory.createTitledBorder("Vista Previa de Resultados"));
        
        JLabel lblResultado = new JLabel(" Resultado: ---");
        lblResultado.setFont(new Font("Arial", Font.BOLD, 18));
        
        JLabel lblPostfijo = new JLabel(" Notación Postfija: ---");
        lblPostfijo.setFont(new Font("Arial", Font.ITALIC, 16));
        
        panelSur.add(lblResultado);
        panelSur.add(lblPostfijo);

        panel.add(panelNorte, BorderLayout.NORTH);
        panel.add(panelCentro, BorderLayout.CENTER);
        panel.add(panelSur, BorderLayout.SOUTH);

        final String[] rutaArchivo = {""};

        btnCargar.addActionListener(e -> {
            File rutaDefecto = new File("tablas_afd");
            if (!rutaDefecto.exists()) rutaDefecto = new File(System.getProperty("user.dir")); 
            JFileChooser chooser = new JFileChooser(rutaDefecto.getAbsolutePath());
            chooser.setDialogTitle("Seleccionar Tabla AFD de tu Calculadora");
            
            if (chooser.showOpenDialog(VentanaPrincipal.this) == JFileChooser.APPROVE_OPTION) {
                File archivo = chooser.getSelectedFile();
                rutaArchivo[0] = archivo.getAbsolutePath();
                lblArchivo.setText("Cargado: " + archivo.getName());
                lblArchivo.setForeground(new Color(0, 153, 0)); 
            }
        });

        btnEvaluar.addActionListener(e -> {
            if (rutaArchivo[0].isEmpty()) {
                JOptionPane.showMessageDialog(VentanaPrincipal.this, "Carga el archivo .txt del AFD primero.", "Falta AFD", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (txtExpresion.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(VentanaPrincipal.this, "Ingresa una expresión matemática.", "Campo Vacío", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                EvaluadorExpr evaluador = new EvaluadorExpr(txtExpresion.getText().trim(), rutaArchivo[0]);
                
                if (evaluador.iniEval()) { 
                    lblResultado.setText(" Resultado: " + evaluador.result);
                    lblResultado.setForeground(new Color(0, 102, 0)); 
                    lblPostfijo.setText(" Notación Postfija: " + evaluador.exprPost);
                    lblPostfijo.setForeground(Color.BLUE);

                    String mensajeExito = "¡Análisis Sintáctico Exitoso!\n\n" +
                                          "Resultado: " + evaluador.result + "\n" +
                                          "Postfija: " + evaluador.exprPost;
                    JOptionPane.showMessageDialog(VentanaPrincipal.this, mensajeExito, "Cálculo Finalizado", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    lblResultado.setText(" Resultado: ERROR DE SINTAXIS");
                    lblResultado.setForeground(Color.RED); 
                    lblPostfijo.setText(" Notación Postfija: No se pudo generar");
                    lblPostfijo.setForeground(Color.RED);
                    
                    JOptionPane.showMessageDialog(VentanaPrincipal.this, "Error: La expresión no cumple con la gramática de la calculadora.", "Error Sintáctico", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(VentanaPrincipal.this, "Error crítico: Revisa tus tokens léxicos.\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        return panel;
    }

    
    // =======================================================
    // PANEL DE POLINOMIOS (ANALIZADOR SINTÁCTICO)
    // =======================================================
    private JPanel crearPanelPolinomios() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel panelNorte = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnCargar = new JButton("Cargar Tabla AFD (.txt)");
        JLabel lblArchivo = new JLabel("Ningún archivo seleccionado");
        lblArchivo.setFont(new Font("Arial", Font.ITALIC, 12));
        lblArchivo.setForeground(Color.GRAY);
        panelNorte.add(btnCargar);
        panelNorte.add(lblArchivo);

        JPanel panelCentro = new JPanel(new BorderLayout(10, 10));
        JLabel lblInstruccion = new JLabel("Ingrese expresión de polinomios (ej: R = 3x^2 + 5x - 8):");
        lblInstruccion.setFont(new Font("Arial", Font.BOLD, 14));
        panelCentro.add(lblInstruccion, BorderLayout.NORTH);
        
        JTextArea txtExpresion = new JTextArea();
        txtExpresion.setFont(new Font("Monospaced", Font.BOLD, 22));
        txtExpresion.setMargin(new Insets(10, 10, 10, 10)); 
        txtExpresion.setLineWrap(true);
        txtExpresion.setWrapStyleWord(true);
        
        JScrollPane scrollExpresion = new JScrollPane(txtExpresion);
        panelCentro.add(scrollExpresion, BorderLayout.CENTER);

        JButton btnEvaluar = new JButton("Validar Sintaxis (Descenso Recursivo)");
        btnEvaluar.setFont(new Font("Arial", Font.BOLD, 14));
        btnEvaluar.setBackground(new Color(204, 102, 0)); // Naranja
        btnEvaluar.setForeground(Color.WHITE);
        panelCentro.add(btnEvaluar, BorderLayout.SOUTH);

        JPanel panelSur = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelSur.setBorder(BorderFactory.createTitledBorder("Estado del Análisis"));
        
        JLabel lblResultado = new JLabel("Esperando expresión...");
        lblResultado.setFont(new Font("Arial", Font.BOLD, 18));
        panelSur.add(lblResultado);

        panel.add(panelNorte, BorderLayout.NORTH);
        panel.add(panelCentro, BorderLayout.CENTER);
        panel.add(panelSur, BorderLayout.SOUTH);

        final String[] rutaArchivo = {""};

        btnCargar.addActionListener(e -> {
            File rutaDefecto = new File("tablas_afd");
            if (!rutaDefecto.exists()) rutaDefecto = new File(System.getProperty("user.dir")); 
            JFileChooser chooser = new JFileChooser(rutaDefecto.getAbsolutePath());
            chooser.setDialogTitle("Seleccionar Tabla AFD de Polinomios");
            
            if (chooser.showOpenDialog(VentanaPrincipal.this) == JFileChooser.APPROVE_OPTION) {
                File archivo = chooser.getSelectedFile();
                rutaArchivo[0] = archivo.getAbsolutePath();
                lblArchivo.setText("Cargado: " + archivo.getName());
                lblArchivo.setForeground(new Color(0, 153, 0)); 
            }
        });

        btnEvaluar.addActionListener(e -> {
            if (rutaArchivo[0].isEmpty()) {
                JOptionPane.showMessageDialog(VentanaPrincipal.this, "Carga el archivo .txt del AFD primero.", "Falta AFD", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (txtExpresion.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(VentanaPrincipal.this, "Ingresa una expresión.", "Campo Vacío", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                EvaluadorPolinomios evaluador = new EvaluadorPolinomios(txtExpresion.getText().trim(), rutaArchivo[0]);
                
                if (evaluador.iniEval()) { 
                    lblResultado.setText("✅ SINTAXIS VÁLIDA");
                    lblResultado.setForeground(new Color(0, 102, 0)); 
                    JOptionPane.showMessageDialog(VentanaPrincipal.this, "¡La expresión cumple perfectamente con la gramática de polinomios!", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    lblResultado.setText("❌ ERROR DE SINTAXIS");
                    lblResultado.setForeground(Color.RED); 
                    JOptionPane.showMessageDialog(VentanaPrincipal.this, "Error: La expresión no respeta la estructura de polinomios.", "Error Sintáctico", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(VentanaPrincipal.this, "Error crítico: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        return panel;
    }
    // =======================================================
    // PANEL DE EXPRESIÓN REGULAR A AFN
    // =======================================================
    private JPanel crearPanelERaAFN() {
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(350);

        JPanel panelIzquierdo = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblTitulo = new JLabel("Expresión Regular a AFN");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        
        JTextField txtER = new JTextField(15); 
        txtER.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JTextField txtIdAfn = new JTextField(5);
        JButton btnCrear = new JButton("Crear AFN desde ER");
        btnCrear.setBackground(new Color(0, 153, 76)); 
        btnCrear.setForeground(Color.WHITE);

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; panelIzquierdo.add(lblTitulo, gbc);
        gbc.gridwidth = 1; 
        gbc.gridy = 1; panelIzquierdo.add(new JLabel("Expresión Regular:"), gbc); 
        gbc.gridx = 1; panelIzquierdo.add(txtER, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2; panelIzquierdo.add(new JLabel("ID del AFN:"), gbc); 
        gbc.gridx = 1; panelIzquierdo.add(txtIdAfn, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2; panelIzquierdo.add(btnCrear, gbc);

        JLabel lblAyuda = new JLabel("<html><small>Ejemplos:<br>- [a-z]+<br>- (a|b)*c<br>- \\+ | \\*</small></html>");
        lblAyuda.setForeground(Color.GRAY);
        gbc.gridy = 4; panelIzquierdo.add(lblAyuda, gbc);

        JScrollPane scrollImagen = new JScrollPane(lblImagenERaAFN);
        splitPane.setLeftComponent(panelIzquierdo); 
        splitPane.setRightComponent(scrollImagen);

        btnCrear.addActionListener(e -> {
            try {
                if(txtER.getText().trim().isEmpty() || txtIdAfn.getText().trim().isEmpty()){
                    JOptionPane.showMessageDialog(this, "Por favor llene todos los campos.", "Campos vacíos", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                int nuevoId = Integer.parseInt(txtIdAfn.getText().trim());
                if (obtenerAFNPorId(nuevoId) != null) {
                    JOptionPane.showMessageDialog(this, "Ya existe un autómata con el ID " + nuevoId + ". Por favor use otro.", "ID Duplicado", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                ER_a_AFN conversor = new ER_a_AFN(txtER.getText().trim());
                AFN nuevoAfn = conversor.convertir();
                
                if (nuevoAfn != null) {
                    nuevoAfn.idAFN = nuevoId;
                    if(!AFN.coleccionAFN.contains(nuevoAfn)){
                        AFN.coleccionAFN.add(nuevoAfn);
                    }
                    String nombreImg = "AFN_" + nuevoAfn.idAFN;
                    nuevoAfn.generarGrafico(nombreImg);
                    cargarImagenEnLabel(lblImagenERaAFN, "imagenes/" + nombreImg + ".png");
                    actualizarListas();
                    JOptionPane.showMessageDialog(this, "El AFN fue generado exitosamente a partir de la Expresión Regular.", "Creación Exitosa", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Hubo un error de sintaxis en la Expresión Regular.\nRevisa que los paréntesis y corchetes estén bien cerrados.", "Error Sintáctico", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) { 
                JOptionPane.showMessageDialog(this, "El ID debe ser un número entero válido.", "Error", JOptionPane.ERROR_MESSAGE); 
            }
        });
        
        JPanel panelFinal = new JPanel(new BorderLayout());
        panelFinal.add(splitPane, BorderLayout.CENTER);
        return panelFinal;
    }

    // =======================================================
    // PANEL DE PRUEBA DEL ANALIZADOR LÉXICO
    // =======================================================
    private JPanel crearPanelProbarLexico() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel panelNorte = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnCargar = new JButton("Cargar Tabla AFD (.txt)");
        JLabel lblArchivo = new JLabel("Ningún archivo seleccionado");
        lblArchivo.setFont(new Font("Arial", Font.ITALIC, 12));
        lblArchivo.setForeground(Color.GRAY);
        panelNorte.add(btnCargar);
        panelNorte.add(lblArchivo);

        JPanel panelCentro = new JPanel(new BorderLayout(5, 5));
        JLabel lblInstruccion = new JLabel("Ingrese la cadena o código fuente a analizar:");
        lblInstruccion.setFont(new Font("Arial", Font.BOLD, 14));
        panelCentro.add(lblInstruccion, BorderLayout.NORTH);
        
        JTextArea txtCodigo = new JTextArea(8, 50);
        txtCodigo.setFont(new Font("Monospaced", Font.PLAIN, 16));
        JScrollPane scrollCodigo = new JScrollPane(txtCodigo);
        panelCentro.add(scrollCodigo, BorderLayout.CENTER);

        JButton btnAnalizar = new JButton("Ejecutar Análisis Léxico");
        btnAnalizar.setFont(new Font("Arial", Font.BOLD, 14));
        btnAnalizar.setBackground(new Color(0, 120, 215));
        btnAnalizar.setForeground(Color.WHITE);
        panelCentro.add(btnAnalizar, BorderLayout.SOUTH);

        String[] columnas = {"Token Encontrado", "Lexema"};
        DefaultTableModel modeloResultados = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; } 
        };
        JTable tablaResultados = new JTable(modeloResultados);
        tablaResultados.setRowHeight(25);
        tablaResultados.setFont(new Font("Arial", Font.PLAIN, 14));
        JScrollPane scrollResultados = new JScrollPane(tablaResultados);
        scrollResultados.setBorder(BorderFactory.createTitledBorder("Resultados del Análisis"));

        JSplitPane splitVertical = new JSplitPane(JSplitPane.VERTICAL_SPLIT, panelCentro, scrollResultados);
        splitVertical.setDividerLocation(200);

        panel.add(panelNorte, BorderLayout.NORTH);
        panel.add(splitVertical, BorderLayout.CENTER);

        final String[] rutaArchivo = {""};

        btnCargar.addActionListener(e -> {
            File rutaDefecto = new File("tablas_afd");
            if (!rutaDefecto.exists()) rutaDefecto = new File(System.getProperty("user.dir")); 
            JFileChooser chooser = new JFileChooser(rutaDefecto.getAbsolutePath());
            chooser.setDialogTitle("Seleccionar Tabla de AFD generada (.txt)");
            
            if (chooser.showOpenDialog(VentanaPrincipal.this) == JFileChooser.APPROVE_OPTION) {
                File archivo = chooser.getSelectedFile();
                rutaArchivo[0] = archivo.getAbsolutePath();
                lblArchivo.setText("Cargado: " + archivo.getName());
                lblArchivo.setForeground(new Color(0, 153, 0)); 
            }
        });

        btnAnalizar.addActionListener(e -> {
            if (rutaArchivo[0].isEmpty()) {
                JOptionPane.showMessageDialog(VentanaPrincipal.this, "Debe cargar un archivo Tabla_AFD_X.txt primero.", "Archivo faltante", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (txtCodigo.getText().isEmpty()) {
                JOptionPane.showMessageDialog(VentanaPrincipal.this, "La cadena a analizar está vacía.", "Sin texto", JOptionPane.WARNING_MESSAGE);
                return;
            }

            modeloResultados.setRowCount(0); 
            
            try {
                AnalizadorLexico lexico = new AnalizadorLexico(rutaArchivo[0]);
                lexico.setSigma(txtCodigo.getText());
                
                int tokenHallado;
                while (true) {
                    tokenHallado = lexico.yylex();
                    if (tokenHallado == AnalizadorLexico.TOKEN_FIN) {
                        modeloResultados.addRow(new Object[]{"0", " "}); 
                        break; 
                    } else if (tokenHallado == AnalizadorLexico.TOKEN_ERROR) {
                        modeloResultados.addRow(new Object[]{"ERROR LÉXICO", lexico.getLexema()});
                    } else {
                        modeloResultados.addRow(new Object[]{String.valueOf(tokenHallado), lexico.getLexema()});
                    }
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(VentanaPrincipal.this, "Hubo un problema al leer el archivo.\n" + ex.getMessage(), "Error Interno", JOptionPane.ERROR_MESSAGE);
            }
        });

        return panel;
    }

    // =======================================================
    // PANEL DE CONVERSIÓN A AFD 
    // =======================================================
    private JPanel crearPanelConvertir() {
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(350);

        JPanel panelControles = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 10, 15, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblTitulo = new JLabel("Convertir a AFD");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        JButton btnConvertir = new JButton("Convertir AFN a AFD");

        gbc.gridx = 0; gbc.gridy = 0; panelControles.add(lblTitulo, gbc);
        gbc.gridy = 1; panelControles.add(new JLabel("Seleccione el AFN base:"), gbc);
        gbc.gridy = 2; panelControles.add(comboAfnAfd, gbc);
        gbc.gridy = 3; panelControles.add(btnConvertir, gbc);

        JScrollPane scrollImagen = new JScrollPane(lblImagenAfnAfd);
        scrollImagen.setBorder(BorderFactory.createTitledBorder("Vista Previa del AFN Original"));

        splitPane.setLeftComponent(panelControles);
        splitPane.setRightComponent(scrollImagen);

        comboAfnAfd.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                AFN a = obtenerAFNPorTexto((String) e.getItem());
                if (a != null) cargarImagenEnLabel(lblImagenAfnAfd, "imagenes/AFN_" + a.idAFN + ".png");
            }
        });

        btnConvertir.addActionListener(e -> {
            AFN afnBase = obtenerAFNPorTexto((String) comboAfnAfd.getSelectedItem());
            if (afnBase != null) {
                try {
                    AFD afdResultante = afnBase.convertirAAFD();
                    String nombreImgAfd = "AFD_" + afnBase.idAFN;
                    afdResultante.generarGrafico(nombreImgAfd);
                    
                    String rutaTxt = "tablas_afd/Tabla_AFD_" + afnBase.idAFN + ".txt";
                    afdResultante.guardarAFDEnArchivo(rutaTxt);
                    
                    JOptionPane.showMessageDialog(this, "El autómata se convirtió exitosamente a AFD.\nEstados resultantes: " + afdResultante.numEstados + "\n\nLa tabla se guardó en:\n" + rutaTxt, "Conversión Exitosa", JOptionPane.INFORMATION_MESSAGE);
                    mostrarVentanaFlotante("imagenes/" + nombreImgAfd + ".png", "Resultado: AFD del Autómata " + afnBase.idAFN);
                    
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Hubo un error al intentar convertir el autómata.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Por favor seleccione un AFN de la lista.", "Atención", JOptionPane.WARNING_MESSAGE);
            }
        });

        JPanel panelFinal = new JPanel(new BorderLayout());
        panelFinal.add(splitPane, BorderLayout.CENTER);
        return panelFinal;
    }

    // =======================================================
    // PANEL BÁSICO (UN SOLO CARÁCTER)
    // =======================================================
    private JPanel crearPanelBasicoUnCaracter() {
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(300);

        JPanel panelIzquierdo = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JCheckBox checkAscii = new JCheckBox("Usar código ASCII");
        JTextField txtCaracter = new JTextField(5); 
        JTextField txtIdAfn = new JTextField(5);
        JButton btnCrear = new JButton("Crear AFN");

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; panelIzquierdo.add(checkAscii, gbc);
        gbc.gridwidth = 1; gbc.gridy = 1; panelIzquierdo.add(new JLabel("Carácter:"), gbc); gbc.gridx = 1; panelIzquierdo.add(txtCaracter, gbc);
        gbc.gridx = 0; gbc.gridy = 2; panelIzquierdo.add(new JLabel("ID AFN:"), gbc); gbc.gridx = 1; panelIzquierdo.add(txtIdAfn, gbc);
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2; panelIzquierdo.add(btnCrear, gbc);

        JScrollPane scrollImagen = new JScrollPane(lblImagenBasicoUn);
        splitPane.setLeftComponent(panelIzquierdo); splitPane.setRightComponent(scrollImagen);

        btnCrear.addActionListener(e -> {
            try {
                if(txtIdAfn.getText().trim().isEmpty() || txtCaracter.getText().isEmpty()){
                    JOptionPane.showMessageDialog(this, "Por favor llene todos los campos.", "Campos vacíos", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                int nuevoId = Integer.parseInt(txtIdAfn.getText().trim());
                if (obtenerAFNPorId(nuevoId) != null) {
                    JOptionPane.showMessageDialog(this, "Ya existe un autómata con el ID " + nuevoId + ".", "ID Duplicado", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                AFN nuevoAfn = new AFN();
                char c;
                if (checkAscii.isSelected()) { 
                    c = (char) Integer.parseInt(txtCaracter.getText().trim()); 
                } else { 
                    String input = txtCaracter.getText();
                    if (input.trim().isEmpty()) { c = ' '; } else { c = input.trim().charAt(0); }
                }

                nuevoAfn.crearAFNBasico(c);
                nuevoAfn.idAFN = nuevoId;
                
                String nombreImg = "AFN_" + nuevoAfn.idAFN;
                nuevoAfn.generarGrafico(nombreImg);
                cargarImagenEnLabel(lblImagenBasicoUn, "imagenes/" + nombreImg + ".png");
                actualizarListas();
                
                JOptionPane.showMessageDialog(this, "Autómata Básico con ID " + nuevoId + " creado con éxito.", "Creación Exitosa", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) { 
                JOptionPane.showMessageDialog(this, "Error de entrada. Verifique que el ID sea numérico.", "Error", JOptionPane.ERROR_MESSAGE); 
            }
        });
        
        JPanel panelFinal = new JPanel(new BorderLayout());
        panelFinal.add(splitPane, BorderLayout.CENTER);
        return panelFinal;
    }

    // =======================================================
    // PANEL BÁSICO (RANGO DE CARACTERES)
    // =======================================================
    private JPanel crearPanelBasicoRango() {
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(300);

        JPanel panelIzquierdo = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JCheckBox checkAscii = new JCheckBox("Usar código ASCII");
        JTextField txtInferior = new JTextField(5); JTextField txtSuperior = new JTextField(5); JTextField txtIdAfn = new JTextField(5);
        JButton btnCrear = new JButton("Crear AFN");

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; panelIzquierdo.add(checkAscii, gbc);
        gbc.gridwidth = 1; gbc.gridy = 1; panelIzquierdo.add(new JLabel("Caracter inferior:"), gbc); gbc.gridx = 1; panelIzquierdo.add(txtInferior, gbc);
        gbc.gridx = 0; gbc.gridy = 2; panelIzquierdo.add(new JLabel("Caracter superior:"), gbc); gbc.gridx = 1; panelIzquierdo.add(txtSuperior, gbc);
        gbc.gridx = 0; gbc.gridy = 3; panelIzquierdo.add(new JLabel("ID AFN:"), gbc); gbc.gridx = 1; panelIzquierdo.add(txtIdAfn, gbc);
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2; panelIzquierdo.add(btnCrear, gbc);

        JScrollPane scrollImagen = new JScrollPane(lblImagenBasicoRango);
        splitPane.setLeftComponent(panelIzquierdo); splitPane.setRightComponent(scrollImagen);

        btnCrear.addActionListener(e -> {
            try {
                if(txtIdAfn.getText().trim().isEmpty() || txtInferior.getText().isEmpty() || txtSuperior.getText().isEmpty()){
                    JOptionPane.showMessageDialog(this, "Por favor llene todos los campos.", "Campos vacíos", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                int nuevoId = Integer.parseInt(txtIdAfn.getText().trim());
                if (obtenerAFNPorId(nuevoId) != null) {
                    JOptionPane.showMessageDialog(this, "Ya existe un autómata con el ID " + nuevoId + ".", "ID Duplicado", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                AFN nuevoAfn = new AFN();
                char cInf, cSup;
                if (checkAscii.isSelected()) { 
                    cInf = (char) Integer.parseInt(txtInferior.getText().trim()); 
                    cSup = (char) Integer.parseInt(txtSuperior.getText().trim()); 
                } else { 
                    String inputInf = txtInferior.getText();
                    String inputSup = txtSuperior.getText();
                    cInf = inputInf.trim().isEmpty() ? ' ' : inputInf.trim().charAt(0);
                    cSup = inputSup.trim().isEmpty() ? ' ' : inputSup.trim().charAt(0);
                }

                if (cInf == cSup) nuevoAfn.crearAFNBasico(cInf); else nuevoAfn.crearAFNBasico(cInf, cSup);

                nuevoAfn.idAFN = nuevoId;
                String nombreImg = "AFN_" + nuevoAfn.idAFN;
                nuevoAfn.generarGrafico(nombreImg);
                cargarImagenEnLabel(lblImagenBasicoRango, "imagenes/" + nombreImg + ".png");
                actualizarListas();
                
                JOptionPane.showMessageDialog(this, "Autómata de Rango con ID " + nuevoId + " creado con éxito.", "Creación Exitosa", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) { 
                JOptionPane.showMessageDialog(this, "Error de entrada. Verifique que el ID sea numérico.", "Error", JOptionPane.ERROR_MESSAGE); 
            }
        });
        
        JPanel panelFinal = new JPanel(new BorderLayout());
        panelFinal.add(splitPane, BorderLayout.CENTER);
        return panelFinal;
    }

    // --- PANEL DE UNIÓN ESPECIAL PARA LÉXICO (CON TABLA) ---
    private JPanel crearPanelUnionLexico() {
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(500);

        JPanel panelControles = new JPanel(new BorderLayout());
        
        JPanel panelNorte = new JPanel(new GridLayout(2, 1));
        JLabel lblTitulo = new JLabel("Seleccione los AFNs a Unir y asigne los tokens");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 14));
        JLabel lblAlerta = new JLabel("Para omitir los lexemas de una clase léxica, use el TOKEN 20001");
        lblAlerta.setForeground(Color.RED);
        panelNorte.add(lblTitulo);
        panelNorte.add(lblAlerta);
        panelNorte.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] columnas = {"AFNs", "Seleccionar AFN", "Token"};
        modeloTablaUnionLexica = new DefaultTableModel(columnas, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 1) return Boolean.class; 
                return String.class; 
            }
            @Override
            public boolean isCellEditable(int row, int column) { return column == 1 || column == 2; }
        };
        tablaUnionLexica = new JTable(modeloTablaUnionLexica);
        JScrollPane scrollTabla = new JScrollPane(tablaUnionLexica);

        JPanel panelSur = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelSur.add(new JLabel("Id del AFN resultante:"));
        JTextField txtNuevoId = new JTextField(5);
        panelSur.add(txtNuevoId);
        JButton btnUnir = new JButton("Unir AFNs");
        panelSur.add(btnUnir);

        panelControles.add(panelNorte, BorderLayout.NORTH);
        panelControles.add(scrollTabla, BorderLayout.CENTER);
        panelControles.add(panelSur, BorderLayout.SOUTH);

        JScrollPane scrollImagen = new JScrollPane(lblImagenUnionLexica);
        splitPane.setLeftComponent(panelControles);
        splitPane.setRightComponent(scrollImagen);

        btnUnir.addActionListener(e -> {
            ArrayList<AFN> afnsAUnir = new ArrayList<>();
            ArrayList<Integer> tokensAsignados = new ArrayList<>();

            for (int i = 0; i < modeloTablaUnionLexica.getRowCount(); i++) {
                boolean seleccionado = (boolean) modeloTablaUnionLexica.getValueAt(i, 1);
                if (seleccionado) {
                    int idAfn = Integer.parseInt(modeloTablaUnionLexica.getValueAt(i, 0).toString());
                    try {
                        String tokenStr = modeloTablaUnionLexica.getValueAt(i, 2).toString().trim();
                        if(tokenStr.isEmpty()) throw new Exception();
                        int token = Integer.parseInt(tokenStr);
                        AFN afnSeleccionado = obtenerAFNPorId(idAfn);
                        if(afnSeleccionado != null) {
                            afnsAUnir.add(afnSeleccionado);
                            tokensAsignados.add(token);
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, "Debe ingresar un Token numérico válido para el AFN " + idAfn, "Error de entrada", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
            }

            if (afnsAUnir.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Debe seleccionar al menos un AFN de la lista.", "Atención", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (txtNuevoId.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Debe ingresar un ID para el AFN resultante.", "Atención", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                int nuevoId = Integer.parseInt(txtNuevoId.getText().trim());
                if (obtenerAFNPorId(nuevoId) != null) {
                    boolean esIdDeUnAfnSeleccionado = false;
                    for(AFN a : afnsAUnir) {
                        if (a.idAFN == nuevoId) { esIdDeUnAfnSeleccionado = true; break; }
                    }
                    if (!esIdDeUnAfnSeleccionado) {
                        JOptionPane.showMessageDialog(this, "El ID " + nuevoId + " ya existe en otro autómata. Por favor elija un ID diferente.", "ID Duplicado", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                }

                for(AFN a : afnsAUnir) {
                    new File("imagenes/AFN_" + a.idAFN + ".png").delete();
                    new File("dots/AFN_" + a.idAFN + ".dot").delete();
                }

                AFN superAfn = new AFN();
                superAfn.unionEspecialParaLexico(afnsAUnir, tokensAsignados, nuevoId);
                String nombreImg = "AFN_" + superAfn.idAFN;
                superAfn.generarGrafico(nombreImg);
                cargarImagenEnLabel(lblImagenUnionLexica, "imagenes/" + nombreImg + ".png");

                actualizarListas();
                txtNuevoId.setText("");
                JOptionPane.showMessageDialog(this, "Súper AFN " + nuevoId + " creado con éxito.", "Operación Exitosa", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "El ID resultante debe ser un número entero válido.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel panelFinal = new JPanel(new BorderLayout());
        panelFinal.add(splitPane, BorderLayout.CENTER);
        return panelFinal;
    }

    // --- PANEL DE OPERACIONES BINARIAS SIMPLES (Unión Normal y Concatenación) ---
    private JPanel crearPanelOperacionBinariaSimple(String titulo, String accionTexto, JComboBox<String> combo1, JComboBox<String> combo2, JLabel lblVisor, boolean esUnionNormal) {
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT); splitPane.setDividerLocation(350);
        JPanel panelControles = new JPanel(new GridBagLayout()); GridBagConstraints gbc = new GridBagConstraints(); gbc.insets = new Insets(10, 10, 10, 10); gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblTitulo = new JLabel("Operación " + titulo); lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        JButton btnAplicar = new JButton(titulo);

        gbc.gridx = 0; gbc.gridy = 0; panelControles.add(lblTitulo, gbc);
        gbc.gridy = 1; panelControles.add(new JLabel(accionTexto), gbc); gbc.gridy = 2; panelControles.add(combo1, gbc);
        gbc.gridy = 3; panelControles.add(new JLabel("Con:"), gbc); gbc.gridy = 4; panelControles.add(combo2, gbc);
        gbc.gridy = 5; panelControles.add(btnAplicar, gbc);

        JScrollPane scrollImagen = new JScrollPane(lblVisor);
        splitPane.setLeftComponent(panelControles); splitPane.setRightComponent(scrollImagen);

        btnAplicar.addActionListener(e -> {
            AFN afn1 = obtenerAFNPorTexto((String) combo1.getSelectedItem());
            AFN afn2 = obtenerAFNPorTexto((String) combo2.getSelectedItem());

            if (afn1 != null && afn2 != null) {
                if (afn1.idAFN == afn2.idAFN) { JOptionPane.showMessageDialog(this, "Seleccione dos autómatas distintos.", "Atención", JOptionPane.WARNING_MESSAGE); return; }
                int idPrincipal = afn1.idAFN;
                int idBorrar = afn2.idAFN;
                
                if(esUnionNormal) { afn1.unionAFN(afn2); } else { afn1.concatenacion(afn2); }

                new File("imagenes/AFN_" + idBorrar + ".png").delete(); new File("dots/AFN_" + idBorrar + ".dot").delete();
                String nombreImg = "AFN_" + afn1.idAFN; afn1.generarGrafico(nombreImg);
                cargarImagenEnLabel(lblVisor, "imagenes/" + nombreImg + ".png");
                actualizarListas();
                JOptionPane.showMessageDialog(this, "Operación exitosa. El autómata " + idBorrar + " fue absorbido por el autómata " + idPrincipal + ".", "Proceso completado", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        JPanel panelFinal = new JPanel(new BorderLayout());
        panelFinal.add(splitPane, BorderLayout.CENTER);
        return panelFinal;
    }

    // --- PANEL UNARIAS (+, *, ?) ---
    private JPanel crearPanelOperacionUnaria(String titulo, String simbolo, JComboBox<String> combo, JLabel lblVisor) {
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT); splitPane.setDividerLocation(300);
        JPanel panelControles = new JPanel(new GridBagLayout()); GridBagConstraints gbc = new GridBagConstraints(); gbc.insets = new Insets(15, 10, 15, 10); gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblTitulo = new JLabel("Operación " + titulo); lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        JButton btnAplicar = new JButton("Aplicar Operación");

        gbc.gridx = 0; gbc.gridy = 0; panelControles.add(lblTitulo, gbc);
        gbc.gridy = 1; panelControles.add(new JLabel("Aplicar a:"), gbc); gbc.gridy = 2; panelControles.add(combo, gbc);
        gbc.gridy = 3; panelControles.add(btnAplicar, gbc);

        JScrollPane scrollImagen = new JScrollPane(lblVisor);
        splitPane.setLeftComponent(panelControles); splitPane.setRightComponent(scrollImagen);

        combo.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                AFN a = obtenerAFNPorTexto((String) e.getItem());
                if (a != null) cargarImagenEnLabel(lblVisor, "imagenes/AFN_" + a.idAFN + ".png");
            }
        });

        btnAplicar.addActionListener(e -> {
            AFN a = obtenerAFNPorTexto((String) combo.getSelectedItem());
            if (a != null) {
                if (simbolo.equals("+")) a.cerraduraPositiva(); else if (simbolo.equals("*")) a.cerraduraKleene(); else a.opcional();
                String nombreImg = "AFN_" + a.idAFN; a.generarGrafico(nombreImg);
                cargarImagenEnLabel(lblVisor, "imagenes/" + nombreImg + ".png");
                JOptionPane.showMessageDialog(this, "Operación aplicada con éxito al autómata " + a.idAFN + ".", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Seleccione un AFN primero.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            }
        });
        
        JPanel panelFinal = new JPanel(new BorderLayout());
        panelFinal.add(splitPane, BorderLayout.CENTER);
        return panelFinal;
    }

    // --- MÉTODOS AUXILIARES Y DE ACTUALIZACIÓN ---
    private void actualizarListas() {
        JComboBox[] combos = {comboAFNsPositiva, comboAFNsKleene, comboAFNsOpcional, comboConcat1, comboConcat2, comboUnionNormal1, comboUnionNormal2, comboAfnAfd};
        for (JComboBox c : combos) c.removeAllItems();
        
        modeloTablaUnionLexica.setRowCount(0);

        for (AFN afn : AFN.coleccionAFN) {
            String item = "AFN ID: " + afn.idAFN;
            for (JComboBox c : combos) c.addItem(item);
            modeloTablaUnionLexica.addRow(new Object[]{String.valueOf(afn.idAFN), false, ""});
        }
    }

    private AFN obtenerAFNPorTexto(String texto) {
        if (texto == null || !texto.contains(":")) return null;
        return obtenerAFNPorId(Integer.parseInt(texto.split(":")[1].trim()));
    }

    private AFN obtenerAFNPorId(int id) {
        for (AFN a : AFN.coleccionAFN) if (a.idAFN == id) return a;
        return null;
    }

    private void mostrarDialogoBorrar() {
        if (AFN.coleccionAFN.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay AFNs para borrar.");
            return;
        }
        
        ArrayList<String> opciones = new ArrayList<>();
        for (AFN afn : AFN.coleccionAFN) opciones.add("AFN ID: " + afn.idAFN);

        String sel = (String) JOptionPane.showInputDialog(this, "Selecciona AFN a eliminar:", "Borrar", JOptionPane.WARNING_MESSAGE, null, opciones.toArray(), opciones.get(0));
        if (sel != null) {
            AFN a = obtenerAFNPorTexto(sel);
            if (a != null) {
                AFN.coleccionAFN.remove(a);
                new File("imagenes/AFN_" + a.idAFN + ".png").delete(); 
                new File("dots/AFN_" + a.idAFN + ".dot").delete();
                actualizarListas();
                
                // Limpiar visores
                lblImagenBasicoUn.setIcon(null); lblImagenBasicoUn.setText("AFN Eliminado");
                lblImagenBasicoRango.setIcon(null); lblImagenBasicoRango.setText("AFN Eliminado");
                lblImagenPositiva.setIcon(null); lblImagenPositiva.setText("Selecciona un AFN");
                lblImagenKleene.setIcon(null); lblImagenKleene.setText("Selecciona un AFN");
                lblImagenOpcional.setIcon(null); lblImagenOpcional.setText("Selecciona un AFN");
                lblImagenUnionLexica.setIcon(null); lblImagenUnionLexica.setText("El Súper AFN aparecerá aquí");
                lblImagenConcat.setIcon(null); lblImagenConcat.setText("El AFN concatenado aparecerá aquí");
                lblImagenUnionNormal.setIcon(null); lblImagenUnionNormal.setText("El AFN unido aparecerá aquí");
                lblImagenAfnAfd.setIcon(null); lblImagenAfnAfd.setText("Selecciona un AFN para ver su origen");
                lblImagenERaAFN.setIcon(null); lblImagenERaAFN.setText("El AFN de tu Expresión Regular aparecerá aquí");
                
                JOptionPane.showMessageDialog(this, "AFN y sus imágenes eliminados por completo.");
            }
        }
    }

    private void cargarImagenEnLabel(JLabel label, String ruta) {
        File f = new File(ruta);
        if (f.exists()) {
            ImageIcon i = new ImageIcon(ruta); i.getImage().flush(); 
            label.setIcon(i); label.setText(""); 
        } else label.setText("Error al cargar imagen.");
    }

    private void mostrarVentanaFlotante(String rutaAbsoluta, String tituloVentana) {
        File f = new File(rutaAbsoluta);
        if(f.exists()){
            JFrame visor = new JFrame(tituloVentana);
            visor.setSize(800, 500);
            visor.setLocationRelativeTo(this);
            ImageIcon icono = new ImageIcon(rutaAbsoluta);
            icono.getImage().flush(); 
            JLabel lblImagen = new JLabel(icono);
            lblImagen.setHorizontalAlignment(SwingConstants.CENTER);
            JScrollPane scrollPane = new JScrollPane(lblImagen);
            visor.add(scrollPane);
            visor.setAlwaysOnTop(true);
            visor.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "No se pudo generar la imagen del Autómata Resultante.", "Error visual", JOptionPane.ERROR_MESSAGE);
        }
    }
}