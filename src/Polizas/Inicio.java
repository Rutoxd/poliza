package Polizas;

import Polizas.General.Bienvenida;
import Polizas.General.Controlador;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

// Clase principal: arranca el Sistema de Gestión de Pólizas
public class Inicio {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // si no se puede, Swing usa su apariencia por defecto
        }
        SwingUtilities.invokeLater(() -> new Controlador(new Bienvenida())); // ventana en el hilo de Swing
    }
}
