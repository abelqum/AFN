package com.mycompany.userafncreator;

import javax.swing.*;
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
    
    // ComboBoxes Unarios
    private JComboBox<String> comboAFNsPositiva, comboAFNsKleene, comboAFNsOpcional;
    // ComboBoxes Binarios
    private JComboBox<String> comboUnion1, comboUnion2;
    private JComboBox<String> comboConcat1, comboConcat2;
    
    // Labels para mostrar las imágenes dentro de la ventana
    private JLabel lblImagenBasico;
    private JLabel lblImagenPositiva;
    private JLabel lblImagenKleene;
    private JLabel lblImagenOpcional;
    private JLabel lblImagenUnion;
    private JLabel lblImagenConcat;

    public VentanaPrincipal() {
        setTitle("Generador de Analizadores Léxicos");
        setSize(950, 650); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // 1. Crear las pestañas
        pestañas = new JTabbedPane();
        panelAFN = new JPanel(new BorderLayout());
        panelVacio = new JPanel(new BorderLayout());
        panelVacio.add(new JLabel("Próximamente...", SwingConstants.CENTER), BorderLayout.CENTER);

        pestañas.addTab("AFN's", panelAFN);
        pestañas.addTab("Lexico", panelVacio);

        // 2. Crear el menú desplegable
        JMenuBar menuBar = new JMenuBar();
        JMenu menuOperaciones = new JMenu("Operaciones AFN");

        JMenuItem itemBasico = new JMenuItem("Básico");
        JMenuItem itemUnir = new JMenuItem("Unir");
        JMenuItem itemConcatenar = new JMenuItem("Concatenar");
        JMenuItem itemPositiva = new JMenuItem("Cerradura +");
        JMenuItem itemKleene = new JMenuItem("Cerradura *");
        JMenuItem itemOpcional = new JMenuItem("Opcional");
        JMenuItem itemConvertir = new JMenuItem("Convertir AFN a AFD");
        
        JMenuItem itemBorrar = new JMenuItem("Borrar AFN...");
        itemBorrar.setForeground(Color.RED);

        menuOperaciones.add(itemBasico);
        menuOperaciones.add(itemUnir);
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

        // 3. Contenedor de Vistas
        cardLayout = new CardLayout();
        contenedorTarjetas = new JPanel(cardLayout);

        // Inicializar Combos
        comboAFNsPositiva = new JComboBox<>();
        comboAFNsKleene = new JComboBox<>();
        comboAFNsOpcional = new JComboBox<>();
        comboUnion1 = new JComboBox<>(); comboUnion2 = new JComboBox<>();
        comboConcat1 = new JComboBox<>(); comboConcat2 = new JComboBox<>();

        // Inicializar Visores
        lblImagenBasico = new JLabel("Aquí aparecerá el AFN", SwingConstants.CENTER);
        lblImagenPositiva = new JLabel("Selecciona un AFN para visualizar", SwingConstants.CENTER);
        lblImagenKleene = new JLabel("Selecciona un AFN para visualizar", SwingConstants.CENTER);
        lblImagenOpcional = new JLabel("Selecciona un AFN para visualizar", SwingConstants.CENTER);
        lblImagenUnion = new JLabel("El resultado aparecerá aquí al unir", SwingConstants.CENTER);
        lblImagenConcat = new JLabel("El resultado aparecerá aquí al concatenar", SwingConstants.CENTER);

        // Agregar Vistas al Contenedor
        contenedorTarjetas.add(crearPanelBasico(), "Basico");
        contenedorTarjetas.add(crearPanelOperacionUnaria("Cerradura +", "+", comboAFNsPositiva, lblImagenPositiva), "Positiva");
        contenedorTarjetas.add(crearPanelOperacionUnaria("Cerradura *", "*", comboAFNsKleene, lblImagenKleene), "Kleene");
        contenedorTarjetas.add(crearPanelOperacionUnaria("Opcional", "?", comboAFNsOpcional, lblImagenOpcional), "Opcional");
        
        // --- NUEVAS VISTAS BINARIAS ---
        contenedorTarjetas.add(crearPanelOperacionBinaria("Unión", "Unir", "con", true, comboUnion1, comboUnion2, lblImagenUnion), "Union");
        contenedorTarjetas.add(crearPanelOperacionBinaria("Concatenación", "Concatenar", "con", false, comboConcat1, comboConcat2, lblImagenConcat), "Concatenar");
        
        contenedorTarjetas.add(new JPanel(), "Vacio"); 

        panelAFN.add(contenedorTarjetas, BorderLayout.CENTER);
        add(pestañas);

        // 4. Lógica de Menús
        itemBasico.addActionListener(e -> cardLayout.show(contenedorTarjetas, "Basico"));
        itemPositiva.addActionListener(e -> { actualizarListas(); cardLayout.show(contenedorTarjetas, "Positiva"); });
        itemKleene.addActionListener(e -> { actualizarListas(); cardLayout.show(contenedorTarjetas, "Kleene"); });
        itemOpcional.addActionListener(e -> { actualizarListas(); cardLayout.show(contenedorTarjetas, "Opcional"); });
        
        itemUnir.addActionListener(e -> { actualizarListas(); cardLayout.show(contenedorTarjetas, "Union"); });
        itemConcatenar.addActionListener(e -> { actualizarListas(); cardLayout.show(contenedorTarjetas, "Concatenar"); });
        
        itemConvertir.addActionListener(e -> cardLayout.show(contenedorTarjetas, "Vacio"));
        itemBorrar.addActionListener(e -> mostrarDialogoBorrar());
    }

    // --- PANEL BÁSICO ---
    private JPanel crearPanelBasico() {
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(350);

        JPanel panelIzquierdo = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JCheckBox checkAscii = new JCheckBox("Usar código ASCII");
        JTextField txtInferior = new JTextField(5);
        JTextField txtSuperior = new JTextField(5);
        JTextField txtIdAfn = new JTextField(5);
        JButton btnCrear = new JButton("Crear AFN");

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; panelIzquierdo.add(checkAscii, gbc);
        gbc.gridwidth = 1; gbc.gridy = 1; panelIzquierdo.add(new JLabel("Caracter inferior:"), gbc);
        gbc.gridx = 1; panelIzquierdo.add(txtInferior, gbc);
        gbc.gridx = 0; gbc.gridy = 2; panelIzquierdo.add(new JLabel("Caracter superior:"), gbc);
        gbc.gridx = 1; panelIzquierdo.add(txtSuperior, gbc);
        gbc.gridx = 0; gbc.gridy = 3; panelIzquierdo.add(new JLabel("ID AFN:"), gbc);
        gbc.gridx = 1; panelIzquierdo.add(txtIdAfn, gbc);
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2; panelIzquierdo.add(btnCrear, gbc);

        JScrollPane scrollImagen = new JScrollPane(lblImagenBasico);
        scrollImagen.setBorder(BorderFactory.createTitledBorder("Vista Previa del AFN"));

        splitPane.setLeftComponent(panelIzquierdo);
        splitPane.setRightComponent(scrollImagen);

        btnCrear.addActionListener(e -> {
            try {
                AFN nuevoAfn = new AFN();
                char cInf, cSup;
                if (checkAscii.isSelected()) {
                    cInf = (char) Integer.parseInt(txtInferior.getText());
                    cSup = (char) Integer.parseInt(txtSuperior.getText());
                } else {
                    cInf = txtInferior.getText().charAt(0);
                    cSup = txtSuperior.getText().charAt(0);
                }

                if (cInf == cSup) nuevoAfn.crearAFNBasico(cInf);
                else nuevoAfn.crearAFNBasico(cInf, cSup);

                nuevoAfn.idAFN = Integer.parseInt(txtIdAfn.getText());
                
                String nombreImg = "AFN_" + nuevoAfn.idAFN;
                nuevoAfn.generarGrafico(nombreImg);
                cargarImagenEnLabel(lblImagenBasico, "imagenes/" + nombreImg + ".png");
                
                actualizarListas();
                JOptionPane.showMessageDialog(this, "AFN Básico creado con éxito.");

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al crear: Verifica tus datos.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel panelFinal = new JPanel(new BorderLayout());
        panelFinal.add(splitPane, BorderLayout.CENTER);
        return panelFinal;
    }

    // --- PANELES DE OPERACIONES UNARIAS (+, *, ?) ---
    private JPanel crearPanelOperacionUnaria(String titulo, String simbolo, JComboBox<String> combo, JLabel lblVisor) {
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(350);

        JPanel panelControles = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 10, 15, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblTitulo = new JLabel("Operación " + titulo);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        JLabel lblSub = new JLabel("Aplicar cerradura " + simbolo + " a:");
        JButton btnAplicar = new JButton("Aplicar Operación");

        gbc.gridx = 0; gbc.gridy = 0; panelControles.add(lblTitulo, gbc);
        gbc.gridy = 1; panelControles.add(lblSub, gbc);
        gbc.gridy = 2; panelControles.add(combo, gbc);
        gbc.gridy = 3; panelControles.add(btnAplicar, gbc);

        JScrollPane scrollImagen = new JScrollPane(lblVisor);
        scrollImagen.setBorder(BorderFactory.createTitledBorder("Estado Actual del AFN"));

        splitPane.setLeftComponent(panelControles);
        splitPane.setRightComponent(scrollImagen);

        combo.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                AFN afnObj = obtenerAFNSeleccionado((String) e.getItem());
                if (afnObj != null) cargarImagenEnLabel(lblVisor, "imagenes/AFN_" + afnObj.idAFN + ".png");
            }
        });

        btnAplicar.addActionListener(e -> {
            if (combo.getSelectedIndex() != -1) {
                AFN afnObj = obtenerAFNSeleccionado((String) combo.getSelectedItem());
                if (afnObj != null) {
                    if (simbolo.equals("+")) afnObj.cerraduraPositiva();
                    else if (simbolo.equals("*")) afnObj.cerraduraKleene();
                    else if (simbolo.equals("?")) afnObj.opcional();

                    String nombreImg = "AFN_" + afnObj.idAFN;
                    afnObj.generarGrafico(nombreImg);
                    cargarImagenEnLabel(lblVisor, "imagenes/" + nombreImg + ".png");
                    JOptionPane.showMessageDialog(this, "Operación aplicada correctamente al AFN " + afnObj.idAFN);
                }
            } else {
                JOptionPane.showMessageDialog(this, "No hay ningún AFN seleccionado.");
            }
        });

        JPanel panelFinal = new JPanel(new BorderLayout());
        panelFinal.add(splitPane, BorderLayout.CENTER);
        return panelFinal;
    }

    // --- NUEVO: PANELES DE OPERACIONES BINARIAS (Unir, Concatenar) ---
    private JPanel crearPanelOperacionBinaria(String titulo, String txtAccion1, String txtAccion2, boolean esUnion, JComboBox<String> combo1, JComboBox<String> combo2, JLabel lblVisor) {
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(350);

        JPanel panelControles = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblTitulo = new JLabel("Operación " + titulo);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        
        JButton btnAplicar = new JButton("Realizar Operación");

        gbc.gridx = 0; gbc.gridy = 0; panelControles.add(lblTitulo, gbc);
        gbc.gridy = 1; panelControles.add(new JLabel(txtAccion1 + ":"), gbc);
        gbc.gridy = 2; panelControles.add(combo1, gbc);
        gbc.gridy = 3; panelControles.add(new JLabel(txtAccion2 + ":"), gbc);
        gbc.gridy = 4; panelControles.add(combo2, gbc);
        gbc.gridy = 5; panelControles.add(btnAplicar, gbc);

        JScrollPane scrollImagen = new JScrollPane(lblVisor);
        scrollImagen.setBorder(BorderFactory.createTitledBorder("Resultado del Nuevo AFN"));

        splitPane.setLeftComponent(panelControles);
        splitPane.setRightComponent(scrollImagen);

        // OJO: Aquí no hay previsualización automática al seleccionar, justo como pediste.
        // Solo se actualiza al dar clic en el botón.

        btnAplicar.addActionListener(e -> {
            if (combo1.getSelectedIndex() != -1 && combo2.getSelectedIndex() != -1) {
                AFN afn1 = obtenerAFNSeleccionado((String) combo1.getSelectedItem());
                AFN afn2 = obtenerAFNSeleccionado((String) combo2.getSelectedItem());

                if (afn1 != null && afn2 != null) {
                    if (afn1.idAFN == afn2.idAFN) {
                        JOptionPane.showMessageDialog(this, "No puedes operar un AFN consigo mismo.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    int idBorrar = afn2.idAFN; // Guardamos el ID del que va a desaparecer

                    // Aplicamos lógica matemática
                    if (esUnion) afn1.unionAFN(afn2);
                    else afn1.concatenacion(afn2);

                    // Borramos archivos físicos del AFN 2 (el absorbido)
                    File imgFile = new File("imagenes/AFN_" + idBorrar + ".png");
                    if (imgFile.exists()) imgFile.delete();
                    File dotFile = new File("dots/AFN_" + idBorrar + ".dot");
                    if (dotFile.exists()) dotFile.delete();

                    // Sobreescribimos el AFN 1 con su nueva forma monstruosa
                    String nombreImg = "AFN_" + afn1.idAFN;
                    afn1.generarGrafico(nombreImg);
                    
                    // Mostramos en pantalla
                    cargarImagenEnLabel(lblVisor, "imagenes/" + nombreImg + ".png");
                    
                    JOptionPane.showMessageDialog(this, "Operación exitosa. El AFN " + idBorrar + " fue absorbido por el AFN " + afn1.idAFN + ".");
                    
                    // Actualizamos todas las listas para que ya no aparezca el AFN 2
                    actualizarListas();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Debes seleccionar dos AFNs distintos.");
            }
        });

        JPanel panelFinal = new JPanel(new BorderLayout());
        panelFinal.add(splitPane, BorderLayout.CENTER);
        return panelFinal;
    }

    // --- MÉTODOS DE BORRADO Y AUXILIARES ---

    private void mostrarDialogoBorrar() {
        if (AFN.coleccionAFN.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay AFNs para borrar.");
            return;
        }

        ArrayList<String> opciones = new ArrayList<>();
        for (AFN afn : AFN.coleccionAFN) opciones.add("AFN ID: " + afn.idAFN);

        String seleccion = (String) JOptionPane.showInputDialog(
                this, "Selecciona el AFN que deseas eliminar:", "Borrar AFN",
                JOptionPane.WARNING_MESSAGE, null, opciones.toArray(), opciones.get(0));

        if (seleccion != null) {
            AFN afnABorrar = obtenerAFNSeleccionado(seleccion);
            if (afnABorrar != null) {
                int idEliminado = afnABorrar.idAFN;
                AFN.coleccionAFN.remove(afnABorrar);
                
                File imgFile = new File("imagenes/AFN_" + idEliminado + ".png");
                if (imgFile.exists()) imgFile.delete();
                File dotFile = new File("dots/AFN_" + idEliminado + ".dot");
                if (dotFile.exists()) dotFile.delete();

                JOptionPane.showMessageDialog(this, "AFN y sus imágenes eliminados por completo.");
                actualizarListas();
                
                lblImagenBasico.setIcon(null); lblImagenBasico.setText("AFN Eliminado");
                lblImagenPositiva.setIcon(null); lblImagenPositiva.setText("Selecciona un AFN");
                lblImagenKleene.setIcon(null); lblImagenKleene.setText("Selecciona un AFN");
                lblImagenOpcional.setIcon(null); lblImagenOpcional.setText("Selecciona un AFN");
                lblImagenUnion.setIcon(null); lblImagenUnion.setText("El resultado aparecerá aquí al unir");
                lblImagenConcat.setIcon(null); lblImagenConcat.setText("El resultado aparecerá aquí al concatenar");
            }
        }
    }

    private AFN obtenerAFNSeleccionado(String textoLista) {
        if (textoLista == null || !textoLista.contains(":")) return null;
        int idBuscado = Integer.parseInt(textoLista.split(":")[1].trim());
        for (AFN a : AFN.coleccionAFN) {
            if (a.idAFN == idBuscado) return a;
        }
        return null;
    }

    private void actualizarListas() {
        // Metemos todos los combos en un arreglo para actualizar de golpe y no escribir tanto código
        JComboBox[] todosLosCombos = {comboAFNsPositiva, comboAFNsKleene, comboAFNsOpcional, 
                                      comboUnion1, comboUnion2, comboConcat1, comboConcat2};

        // Guardamos las selecciones actuales
        String[] selecciones = new String[todosLosCombos.length];
        for (int i = 0; i < todosLosCombos.length; i++) {
            selecciones[i] = (String) todosLosCombos[i].getSelectedItem();
            todosLosCombos[i].removeAllItems();
        }

        // Llenamos de nuevo con los AFNs vivos
        for (AFN afn : AFN.coleccionAFN) {
            String item = "AFN ID: " + afn.idAFN;
            for (JComboBox combo : todosLosCombos) {
                combo.addItem(item);
            }
        }

        // Restauramos selecciones (si el AFN sigue existiendo)
        for (int i = 0; i < todosLosCombos.length; i++) {
            if (selecciones[i] != null) {
                todosLosCombos[i].setSelectedItem(selecciones[i]);
            }
        }
    }

    private void cargarImagenEnLabel(JLabel label, String rutaAbsoluta) {
        File f = new File(rutaAbsoluta);
        if (f.exists()) {
            ImageIcon iconoOriginal = new ImageIcon(rutaAbsoluta);
            iconoOriginal.getImage().flush(); 
            label.setIcon(iconoOriginal);
            label.setText(""); 
        } else {
            label.setIcon(null);
            label.setText("Error al cargar imagen.");
        }
    }
}