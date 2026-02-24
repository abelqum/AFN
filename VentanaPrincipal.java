package com.mycompany.userafncreator;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.io.File;
import java.util.ArrayList;

public class VentanaPrincipal extends JFrame {

    private JTabbedPane pestañas;
    private JPanel panelAFN;
    private JPanel panelVacio;
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

    public VentanaPrincipal() {
        setTitle("Generador de Analizadores Léxicos");
        setSize(1000, 650); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        pestañas = new JTabbedPane();
        panelAFN = new JPanel(new BorderLayout());
        panelVacio = new JPanel(new BorderLayout());
        panelVacio.add(new JLabel("Próximamente...", SwingConstants.CENTER), BorderLayout.CENTER);

        pestañas.addTab("AFN's", panelAFN);
        pestañas.addTab("Lexico", panelVacio);

        JMenuBar menuBar = new JMenuBar();
        JMenu menuOperaciones = new JMenu("Operaciones AFN");

        // --- NUEVOS MENÚS BÁSICOS ---
        JMenuItem itemBasicoUn = new JMenuItem("Básico (Un carácter)");
        JMenuItem itemBasicoRango = new JMenuItem("Básico (Rango)");
        
        JMenuItem itemUnirNormal = new JMenuItem("Unir (Normal)");
        JMenuItem itemUnirLexico = new JMenuItem("Unir (Para Léxico)");
        JMenuItem itemConcatenar = new JMenuItem("Concatenar");
        JMenuItem itemPositiva = new JMenuItem("Cerradura +");
        JMenuItem itemKleene = new JMenuItem("Cerradura *");
        JMenuItem itemOpcional = new JMenuItem("Opcional");
        JMenuItem itemConvertir = new JMenuItem("Convertir AFN a AFD");
        JMenuItem itemBorrar = new JMenuItem("Borrar AFN...");
        itemBorrar.setForeground(Color.RED);

        menuOperaciones.add(itemBasicoUn);
        menuOperaciones.add(itemBasicoRango);
        menuOperaciones.addSeparator();
        menuOperaciones.add(itemUnirNormal);
        menuOperaciones.add(itemUnirLexico);
        menuOperaciones.add(itemConcatenar);
        menuOperaciones.add(itemPositiva);
        menuOperaciones.add(itemKleene);
        menuOperaciones.add(itemOpcional);
        menuOperaciones.addSeparator();
        menuOperaciones.add(itemConvertir);
        menuOperaciones.addSeparator();
        menuOperaciones.add(itemBorrar);

        menuBar.add(menuOperaciones);
        panelAFN.add(menuBar, BorderLayout.NORTH);

        cardLayout = new CardLayout();
        contenedorTarjetas = new JPanel(cardLayout);

        // Inicializar Combos
        comboAFNsPositiva = new JComboBox<>(); comboAFNsKleene = new JComboBox<>(); comboAFNsOpcional = new JComboBox<>();
        comboConcat1 = new JComboBox<>(); comboConcat2 = new JComboBox<>();
        comboUnionNormal1 = new JComboBox<>(); comboUnionNormal2 = new JComboBox<>();
        comboAfnAfd = new JComboBox<>(); 

        // Inicializar Visores
        lblImagenBasicoUn = new JLabel("Aquí aparecerá el AFN de un carácter", SwingConstants.CENTER);
        lblImagenBasicoRango = new JLabel("Aquí aparecerá el AFN de rango", SwingConstants.CENTER);
        lblImagenPositiva = new JLabel("Selecciona un AFN", SwingConstants.CENTER);
        lblImagenKleene = new JLabel("Selecciona un AFN", SwingConstants.CENTER);
        lblImagenOpcional = new JLabel("Selecciona un AFN", SwingConstants.CENTER);
        lblImagenUnionLexica = new JLabel("El Súper AFN aparecerá aquí", SwingConstants.CENTER);
        lblImagenConcat = new JLabel("El AFN concatenado aparecerá aquí", SwingConstants.CENTER);
        lblImagenUnionNormal = new JLabel("El AFN unido aparecerá aquí", SwingConstants.CENTER);
        lblImagenAfnAfd = new JLabel("Selecciona un AFN para ver su origen", SwingConstants.CENTER);

        // Agregando Vistas
        contenedorTarjetas.add(crearPanelBasicoUnCaracter(), "BasicoUn");
        contenedorTarjetas.add(crearPanelBasicoRango(), "BasicoRango");
        contenedorTarjetas.add(crearPanelOperacionUnaria("Cerradura +", "+", comboAFNsPositiva, lblImagenPositiva), "Positiva");
        contenedorTarjetas.add(crearPanelOperacionUnaria("Cerradura *", "*", comboAFNsKleene, lblImagenKleene), "Kleene");
        contenedorTarjetas.add(crearPanelOperacionUnaria("Opcional", "?", comboAFNsOpcional, lblImagenOpcional), "Opcional");
        
        contenedorTarjetas.add(crearPanelUnionLexico(), "UnionLexico");
        contenedorTarjetas.add(crearPanelOperacionBinariaSimple("Unión Normal", "Unir:", comboUnionNormal1, comboUnionNormal2, lblImagenUnionNormal, true), "UnionNormal");
        contenedorTarjetas.add(crearPanelOperacionBinariaSimple("Concatenación", "Concatenar:", comboConcat1, comboConcat2, lblImagenConcat, false), "Concatenar");
        
        contenedorTarjetas.add(crearPanelConvertir(), "Convertir");
        contenedorTarjetas.add(new JPanel(), "Vacio"); 
        
        panelAFN.add(contenedorTarjetas, BorderLayout.CENTER);
        add(pestañas);

        // Eventos del Menú
        itemBasicoUn.addActionListener(e -> cardLayout.show(contenedorTarjetas, "BasicoUn"));
        itemBasicoRango.addActionListener(e -> cardLayout.show(contenedorTarjetas, "BasicoRango"));
        itemPositiva.addActionListener(e -> { actualizarListas(); cardLayout.show(contenedorTarjetas, "Positiva"); });
        itemKleene.addActionListener(e -> { actualizarListas(); cardLayout.show(contenedorTarjetas, "Kleene"); });
        itemOpcional.addActionListener(e -> { actualizarListas(); cardLayout.show(contenedorTarjetas, "Opcional"); });
        itemUnirLexico.addActionListener(e -> { actualizarListas(); cardLayout.show(contenedorTarjetas, "UnionLexico"); });
        itemUnirNormal.addActionListener(e -> { actualizarListas(); cardLayout.show(contenedorTarjetas, "UnionNormal"); });
        itemConcatenar.addActionListener(e -> { actualizarListas(); cardLayout.show(contenedorTarjetas, "Concatenar"); });
        itemConvertir.addActionListener(e -> { actualizarListas(); cardLayout.show(contenedorTarjetas, "Convertir"); });
        itemBorrar.addActionListener(e -> mostrarDialogoBorrar());
    }

    // =======================================================
    // NUEVO: PANEL BÁSICO (UN SOLO CARÁCTER)
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
                if (checkAscii.isSelected()) { c = (char) Integer.parseInt(txtCaracter.getText()); } 
                else { c = txtCaracter.getText().charAt(0); }

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
                if (checkAscii.isSelected()) { cInf = (char) Integer.parseInt(txtInferior.getText()); cSup = (char) Integer.parseInt(txtSuperior.getText()); } 
                else { cInf = txtInferior.getText().charAt(0); cSup = txtSuperior.getText().charAt(0); }

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
                    
                    JOptionPane.showMessageDialog(this, "El autómata se convirtió exitosamente a AFD.\nEstados resultantes: " + afdResultante.numEstados, "Conversión Exitosa", JOptionPane.INFORMATION_MESSAGE);
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
            public boolean isCellEditable(int row, int column) {
                return column == 1 || column == 2; 
            }
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