package Polizas.pantallas;

import Polizas.clases.Conexion;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.sql.SQLException;
import java.util.function.Predicate;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

// Molde de todas las pantallas de captura: título, formulario, botones y tabla.
// Cada pantalla (Clientes, Vehículos...) hereda de esta y solo dice qué campos tiene
// y qué método de su clase llama en cada botón.
public abstract class Pantalla extends JPanel {

    protected JPanel formulario = new JPanel(new GridLayout(0, 4, 10, 6)); // etiqueta, campo, etiqueta, campo
    protected JButton btnAgregar = new JButton("Agregar");
    protected JButton btnActualizar = new JButton("Actualizar");
    protected JButton btnEliminar = new JButton("Eliminar");
    protected JButton btnLimpiar = new JButton("Limpiar");
    protected DefaultTableModel datos;
    protected JTable tabla;

    // titulo: nombre de la pantalla. columnas: encabezados de la tabla.
    protected Pantalla(String titulo, String... columnas) {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitulo.setForeground(new Color(15, 127, 108));

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        botones.add(btnAgregar);
        botones.add(btnActualizar);
        botones.add(btnEliminar);
        botones.add(btnLimpiar);

        JPanel arriba = new JPanel(new BorderLayout(5, 10));
        arriba.add(lblTitulo, BorderLayout.NORTH);
        arriba.add(formulario, BorderLayout.CENTER);
        arriba.add(botones, BorderLayout.SOUTH);

        // La tabla no se puede editar a mano: se edita con el formulario.
        datos = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
        tabla = new JTable(datos);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setRowHeight(24);
        tabla.getColumnModel().getColumn(0).setMaxWidth(60); // la columna Id, angosta

        add(arriba, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        // Qué hace cada botón
        btnAgregar.addActionListener(e -> hacer(this::agregar, "Registro agregado."));
        btnActualizar.addActionListener(e -> hacer(this::actualizar, "Registro actualizado."));
        btnEliminar.addActionListener(e -> {
            if (tabla.getSelectedRow() >= 0 && JOptionPane.showConfirmDialog(this, "¿Seguro que quieres "
                    + btnEliminar.getText().toLowerCase() + " el registro?", "Confirmar", JOptionPane.YES_NO_OPTION) != 0) {
                return;
            }
            hacer(this::eliminar, "Listo.");
        });
        btnLimpiar.addActionListener(e -> limpiarTodo());
        // Al hacer clic en una fila, sus datos pasan al formulario
        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tabla.getSelectedRow() >= 0) {
                mostrar(tabla.getSelectedRow());
            }
        });
    }

    // Lo que cada pantalla debe hacer (métodos abstractos)
    protected abstract void cargarTabla() throws SQLException;  // trae los registros y llena la tabla
    protected abstract void agregar() throws SQLException;      // lee el formulario y llama a registrar()
    protected abstract void actualizar() throws SQLException;   // lee el formulario y llama a actualizar()
    protected abstract void eliminar() throws SQLException;     // llama a eliminar() del registro elegido
    protected abstract void mostrar(int fila);                  // pasa la fila elegida al formulario
    protected abstract void limpiar();                          // vacía los campos

    // Agrega una etiqueta y su campo al formulario.
    protected void campo(String etiqueta, JComponent componente) {
        formulario.add(new JLabel(etiqueta));
        formulario.add(componente);
    }

    // Recarga la tabla; si falla la base, avisa.
    protected void recargar() {
        try {
            cargarTabla();
        } catch (SQLException e) {
            aviso(Conexion.mensaje(e));
        }
    }

    // Número de la fila elegida en la tabla. Si no hay, avisa con un error.
    protected int filaElegida() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            throw new IllegalArgumentException("Primero elige un registro de la tabla.");
        }
        return fila;
    }

    // Texto de un campo obligatorio; si está vacío, avisa con un error.
    protected String obligatorio(JTextField campo, String nombre) {
        String texto = campo.getText().trim();
        if (texto.isEmpty()) {
            throw new IllegalArgumentException("El campo " + nombre + " es obligatorio.");
        }
        return texto;
    }

    // Texto de un campo opcional: vacío se guarda como null.
    protected String opcional(JTextField campo) {
        String texto = campo.getText().trim();
        return texto.isEmpty() ? null : texto;
    }

    // En una lista desplegable, elige el primer elemento que cumpla la condición.
    protected <T> void elegir(JComboBox<T> lista, Predicate<T> esEste) {
        for (int i = 0; i < lista.getItemCount(); i++) {
            if (esEste.test(lista.getItemAt(i))) {
                lista.setSelectedIndex(i);
                return;
            }
        }
    }

    // Muestra un mensaje en una ventana emergente.
    protected void aviso(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje);
    }

    // Quita la selección de la tabla y vacía el formulario.
    private void limpiarTodo() {
        tabla.clearSelection();
        limpiar();
    }

    // Ejecuta la acción de un botón. Si sale bien, recarga la tabla; si no, muestra el error.
    private void hacer(Accion accion, String exito) {
        try {
            accion.ejecutar();
            cargarTabla();
            limpiarTodo();
            aviso(exito);
        } catch (SQLException e) {
            aviso(Conexion.mensaje(e));
        } catch (IllegalArgumentException | IllegalStateException e) {
            aviso(e.getMessage());
        }
    }

    // Una acción que puede fallar con un error de la base (agregar, actualizar o eliminar).
    private interface Accion {
        void ejecutar() throws SQLException;
    }
}
