package Polizas.General;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

// Ventana principal: barra de menú, encabezado y panel central con CardLayout
public class Bienvenida extends JFrame {

    // Atributos: menú
    protected JMenuItem mInicio, mSalir;
    protected JMenuItem mClientes, mVehiculos;
    protected JMenuItem mAseguradoras, mCoberturas, mTiposPoliza, mMarcas, mModelos;
    protected JMenuItem mPrueba;

    // Atributos: panel central (muestra una pantalla a la vez, por nombre)
    protected CardLayout cl;
    protected JPanel pc;
    protected JLabel lblEstado;

    public Bienvenida() {
        super("Gestor de Pólizas");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1000, 650));
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        menu();

        // Encabezado
        JPanel ps = new JPanel(new BorderLayout());
        ps.setBackground(new Color(15, 127, 108));
        ps.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        JLabel titulo = new JLabel("Sistema de Gestión de Pólizas");
        titulo.setForeground(Color.white);
        titulo.setFont(new Font("Arial", Font.BOLD, 20));
        ps.add(titulo, BorderLayout.WEST);

        // Panel central
        cl = new CardLayout();
        pc = new JPanel(cl);
        JPanel home = new JPanel(new BorderLayout());
        JLabel lblHome = new JLabel("<html><center><h1>Bienvenido</h1>"
                + "Usa el menú para administrar clientes, vehículos y catálogos.</center></html>",
                SwingConstants.CENTER);
        home.add(lblHome, BorderLayout.CENTER);
        pc.add(home, "home");

        // Barra de estado: resultado de la conexión
        lblEstado = new JLabel(" ");
        lblEstado.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));

        setLayout(new BorderLayout());
        add(ps, BorderLayout.NORTH);
        add(pc, BorderLayout.CENTER);
        add(lblEstado, BorderLayout.SOUTH);
        setVisible(true);
    }

    // Método: crea la barra de menú (RNF-06: cada pantalla a dos clics)
    private void menu() {
        JMenuBar mb = new JMenuBar();
        setJMenuBar(mb);

        JMenu mArchivo = new JMenu("Archivo");
        mInicio = new JMenuItem("Inicio");
        mPrueba = new JMenuItem("Probar conexión");
        mSalir = new JMenuItem("Salir");
        mArchivo.add(mInicio);
        mArchivo.add(mPrueba);
        mArchivo.addSeparator();
        mArchivo.add(mSalir);

        JMenu mClientesMenu = new JMenu("Clientes");
        mClientes = new JMenuItem("Clientes");
        mVehiculos = new JMenuItem("Vehículos");
        mClientesMenu.add(mClientes);
        mClientesMenu.add(mVehiculos);

        JMenu mCatalogos = new JMenu("Catálogos");
        mAseguradoras = new JMenuItem("Aseguradoras");
        mTiposPoliza = new JMenuItem("Tipos de póliza");
        mCoberturas = new JMenuItem("Coberturas");
        mMarcas = new JMenuItem("Marcas");
        mModelos = new JMenuItem("Modelos");
        mCatalogos.add(mAseguradoras);
        mCatalogos.add(mTiposPoliza);
        mCatalogos.add(mCoberturas);
        mCatalogos.addSeparator();
        mCatalogos.add(mMarcas);
        mCatalogos.add(mModelos);

        mb.add(mArchivo);
        mb.add(mClientesMenu);
        mb.add(mCatalogos);
    }

    // Método: muestra una pantalla en el panel central
    public void mostrar(JPanel panel, String nombre) {
        pc.add(panel, nombre);
        cl.show(pc, nombre);
    }
}
