package Polizas.pantallas;

import Polizas.clases.Conexion;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.util.function.Supplier;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

// Ventana principal del sistema. Aquí empieza el programa (método main).
// El menú abre cada pantalla en el centro de la ventana.
public class Inicio extends JFrame {

    // Constructor vacío: crea el objeto para después llenarlo con los métodos set.
    public Inicio() {
        super("Sistema de Gestión de Pólizas");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 650);
        setLocationRelativeTo(null);

        JMenuBar barra = new JMenuBar();

        JMenu archivo = new JMenu("Archivo");
        opcion(archivo, "Inicio", this::bienvenida);
        JMenuItem probar = new JMenuItem("Probar conexión");
        probar.addActionListener(e -> JOptionPane.showMessageDialog(this, Conexion.probar()));
        archivo.add(probar);
        JMenuItem salir = new JMenuItem("Salir");
        salir.addActionListener(e -> System.exit(0));
        archivo.add(salir);

        JMenu clientes = new JMenu("Clientes");
        opcion(clientes, "Clientes", PantallaClientes::new);
        opcion(clientes, "Vehículos", PantallaVehiculos::new);

        JMenu catalogos = new JMenu("Catálogos");
        opcion(catalogos, "Aseguradoras", PantallaAseguradoras::new);
        opcion(catalogos, "Tipos de póliza", PantallaTiposPoliza::new);
        opcion(catalogos, "Coberturas", PantallaCoberturas::new);
        opcion(catalogos, "Marcas", PantallaMarcas::new);
        opcion(catalogos, "Modelos", PantallaModelos::new);

        barra.add(archivo);
        barra.add(clientes);
        barra.add(catalogos);
        setJMenuBar(barra);

        setContentPane(bienvenida());
    }

    // Agrega una opción al menú. Al elegirla, se crea la pantalla y se pone en la ventana.
    private void opcion(JMenu menu, String texto, Supplier<JPanel> pantalla) {
        JMenuItem item = new JMenuItem(texto);
        item.addActionListener(e -> {
            setContentPane(pantalla.get());
            revalidate();
            repaint();
        });
        menu.add(item);
    }

    // Pantalla de bienvenida.
    private JPanel bienvenida() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        JLabel titulo = new JLabel("Sistema de Gestión de Pólizas", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 28));
        titulo.setForeground(new Color(15, 127, 108));
        JLabel texto = new JLabel("<html><center>Elige una opción del menú para capturar clientes, vehículos o catálogos."
                + "<br>Archivo → Probar conexión revisa la base de datos.</center></html>", SwingConstants.CENTER);
        texto.setFont(new Font("Arial", Font.PLAIN, 15));
        panel.add(titulo, BorderLayout.CENTER);
        panel.add(texto, BorderLayout.SOUTH);
        return panel;
    }

    // Punto de inicio del programa: abre la ventana principal.
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // si no se puede, Swing usa su apariencia normal
        }
        SwingUtilities.invokeLater(() -> new Inicio().setVisible(true));
    }
}
