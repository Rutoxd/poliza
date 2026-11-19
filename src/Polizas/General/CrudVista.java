package Polizas.General;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.table.TableRowSorter;

// Pantalla base de un CRUD: formulario arriba, botones y tabla abajo.
// Cada módulo hereda de ella y solo agrega sus campos.
public abstract class CrudVista<T> extends JPanel {

    // Atributos: componentes comunes
    protected JButton btnAgregar, btnActualizar, btnEliminar, btnLimpiar, btnConsultar;
    protected JTextField txtBuscar;
    protected JTable tabla;
    protected CrudTM<T> modeloTabla;
    protected JPanel formulario;
    private int filaFormulario = 0;
    private TableRowSorter<CrudTM<T>> sorter;

    // Constructor. Parámetros: título de la pantalla y modelo de la tabla
    protected CrudVista(String titulo, CrudTM<T> modeloTabla) {
        this.modeloTabla = modeloTabla;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitulo.setForeground(new Color(15, 127, 108));

        formulario = new JPanel(new GridBagLayout());

        btnAgregar = new JButton("Agregar");
        btnActualizar = new JButton("Actualizar");
        btnEliminar = new JButton("Eliminar");
        btnLimpiar = new JButton("Limpiar");
        btnConsultar = new JButton("Consultar");
        txtBuscar = new JTextField(18);
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        botones.add(btnAgregar);
        botones.add(btnActualizar);
        botones.add(btnEliminar);
        botones.add(btnLimpiar);
        botones.add(btnConsultar);
        botones.add(new JLabel("   Buscar:"));
        botones.add(txtBuscar);

        JPanel norte = new JPanel(new BorderLayout(5, 10));
        norte.add(lblTitulo, BorderLayout.NORTH);
        norte.add(formulario, BorderLayout.CENTER);
        norte.add(botones, BorderLayout.SOUTH);

        tabla = new JTable(modeloTabla);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setRowHeight(24);
        tabla.getColumnModel().getColumn(0).setMaxWidth(60); // columna Id angosta
        sorter = new TableRowSorter<>(modeloTabla);
        tabla.setRowSorter(sorter);

        add(norte, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);
    }

    // Método: agrega una fila "etiqueta: campo" al formulario (dos pares por renglón)
    protected void campo(String etiqueta, JComponent componente) {
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 4, 4, 12);
        c.anchor = GridBagConstraints.WEST;
        c.gridy = filaFormulario / 2;
        c.gridx = (filaFormulario % 2) * 2;
        formulario.add(new JLabel(etiqueta), c);
        c.gridx++;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        formulario.add(componente, c);
        filaFormulario++;
    }

    // Método: fila seleccionada traducida al modelo (la tabla puede estar filtrada u ordenada)
    public int filaSeleccionada() {
        int fila = tabla.getSelectedRow();
        return fila < 0 ? -1 : tabla.convertRowIndexToModel(fila);
    }

    // Método: filtra la tabla con el texto de búsqueda
    public void filtrar() {
        String texto = txtBuscar.getText().trim();
        sorter.setRowFilter(texto.isEmpty() ? null : RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(texto)));
    }

    // Métodos abstractos que implementa cada módulo
    public abstract T leerFormulario() throws DatoInvalidoException; // arma el objeto con los campos
    public abstract void mostrar(T obj);                             // pasa el objeto a los campos
    public abstract void limpiar();                                  // vacía los campos

    // Método de apoyo: selecciona en un combo el primer elemento que cumpla la condición
    protected <E> void seleccionar(javax.swing.JComboBox<E> combo, java.util.function.Predicate<E> condicion) {
        for (int i = 0; i < combo.getItemCount(); i++) {
            if (condicion.test(combo.getItemAt(i))) {
                combo.setSelectedIndex(i);
                return;
            }
        }
    }

    // Método de apoyo: texto opcional (vacío = null)
    protected String opcional(JTextField txt) {
        String valor = txt.getText().trim();
        return valor.isEmpty() ? null : valor;
    }

    // Método de apoyo: texto obligatorio
    protected String obligatorio(JTextField txt, String nombre) throws DatoInvalidoException {
        String valor = txt.getText().trim();
        if (valor.isEmpty()) {
            throw new DatoInvalidoException("El campo " + nombre + " es obligatorio.");
        }
        return valor;
    }
}
