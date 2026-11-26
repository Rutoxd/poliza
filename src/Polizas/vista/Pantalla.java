package Polizas.vista;

import Polizas.dao.Conexion;
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
import javax.swing.RowFilter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.util.regex.Pattern;

// Capa de vista: molde de todas las pantallas de captura (título, formulario, botones, búsqueda y tabla).
// Cada pantalla (Clientes, Vehículos, Pólizas...) hereda de esta y solo dice qué campos tiene
// y qué método de su DAO llama en cada botón. Las pantallas no escriben SQL (separación por capas).
public abstract class Pantalla extends JPanel {

    protected JPanel formulario = new JPanel(new GridLayout(0, 4, 10, 6)); // etiqueta, campo, etiqueta, campo
    protected JButton btnAgregar = new JButton("Agregar");
    protected JButton btnActualizar = new JButton("Actualizar");
    protected JButton btnEliminar = new JButton("Eliminar");
    protected JButton btnLimpiar = new JButton("Limpiar");
    protected JPanel botones;                                              // fila de botones
    protected JTextField txtBuscar = new JTextField(20);
    protected JPanel debajo = new JPanel(new BorderLayout());              // espacio extra bajo el formulario
    protected DefaultTableModel datos;
    protected JTable tabla;
    private TableRowSorter<DefaultTableModel> filtro;

    // titulo: nombre de la pantalla. columnas: encabezados de la tabla.
    protected Pantalla(String titulo, String... columnas) {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitulo.setForeground(new Color(15, 127, 108));

        botones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        botones.add(btnAgregar);
        botones.add(btnActualizar);
        botones.add(btnEliminar);
        botones.add(btnLimpiar);
        botones.add(new JLabel("    Buscar:"));
        botones.add(txtBuscar);

        JPanel arriba = new JPanel(new BorderLayout(5, 10));
        arriba.add(lblTitulo, BorderLayout.NORTH);
        JPanel centro = new JPanel(new BorderLayout(5, 5));
        centro.add(formulario, BorderLayout.NORTH);
        centro.add(debajo, BorderLayout.CENTER);
        arriba.add(centro, BorderLayout.CENTER);
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
        filtro = new TableRowSorter<>(datos);
        tabla.setRowSorter(filtro);                          // clic en un encabezado para ordenar

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
        // Consulta: al escribir en Buscar, la tabla solo muestra las filas que contienen ese texto
        txtBuscar.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filtrar(); }
            public void removeUpdate(DocumentEvent e) { filtrar(); }
            public void changedUpdate(DocumentEvent e) { filtrar(); }
        });
        // Al hacer clic en una fila, sus datos pasan al formulario
        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tabla.getSelectedRow() >= 0) {
                mostrar(tabla.convertRowIndexToModel(tabla.getSelectedRow()));
            }
        });
    }

    // Lo que cada pantalla debe hacer (métodos abstractos)
    protected abstract void cargarTabla() throws SQLException;  // trae los registros y llena la tabla
    protected abstract void agregar() throws SQLException;      // lee el formulario y lo guarda con el DAO
    protected abstract void actualizar() throws SQLException;   // lee el formulario y guarda los cambios con el DAO
    protected abstract void eliminar() throws SQLException;     // pide al DAO borrar el registro elegido
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

    // Número de la fila elegida en la tabla (en la lista original, aunque la tabla esté filtrada
    // u ordenada). Si no hay, avisa con un error.
    protected int filaElegida() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            throw new IllegalArgumentException("Primero elige un registro de la tabla.");
        }
        return tabla.convertRowIndexToModel(fila);
    }

    // Muestra solo las filas que contienen el texto de Buscar (sin importar mayúsculas).
    private void filtrar() {
        String texto = txtBuscar.getText().trim();
        filtro.setRowFilter(texto.isEmpty() ? null : RowFilter.regexFilter("(?i)" + Pattern.quote(texto)));
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
