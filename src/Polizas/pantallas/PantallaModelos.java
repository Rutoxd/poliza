package Polizas.pantallas;

import Polizas.clases.Conexion;
import Polizas.clases.Marca;
import Polizas.clases.Modelo;
import java.sql.SQLException;
import java.util.List;
import javax.swing.JComboBox;
import javax.swing.JTextField;

// Pantalla de modelos: cada modelo se liga a una marca de la lista.
public class PantallaModelos extends Pantalla {

    private JComboBox<Marca> cmbMarca = new JComboBox<>();
    private JTextField txtNombre = new JTextField();
    private List<Modelo> modelos;

    // Constructor vacío: crea el objeto para después llenarlo con los métodos set.
    public PantallaModelos() {
        super("Modelos", "Id", "Marca", "Modelo");
        campo("Marca:", cmbMarca);
        campo("Modelo:", txtNombre);
        try {
            for (Marca m : Marca.listar()) {
                cmbMarca.addItem(m);
            }
        } catch (SQLException e) {
            aviso(Conexion.mensaje(e));
        }
        recargar();
    }

    // Arma el objeto con lo escrito en el formulario y revisa que los datos estén completos.
    private Modelo leer() {
        if (cmbMarca.getSelectedItem() == null) {
            throw new IllegalArgumentException("Primero registra una marca.");
        }
        Modelo m = new Modelo();
        m.setMarca((Marca) cmbMarca.getSelectedItem());
        m.setNombre(obligatorio(txtNombre, "Modelo"));
        return m;
    }

    // Trae los registros con listar() y llena la tabla.
    @Override
    protected void cargarTabla() throws SQLException {
        modelos = Modelo.listar();
        datos.setRowCount(0);
        for (Modelo m : modelos) {
            datos.addRow(new Object[]{m.getId(), m.getMarca().getNombre(), m.getNombre()});
        }
    }

    // Botón Agregar: arma el objeto con el formulario y llama a registrar().
    @Override
    protected void agregar() throws SQLException {
        leer().registrar();
    }

    // Botón Actualizar: arma el objeto, le pone el id de la fila elegida y llama a actualizar().
    @Override
    protected void actualizar() throws SQLException {
        Modelo m = leer();
        m.setId(modelos.get(filaElegida()).getId());
        m.actualizar();
    }

    // Botón Eliminar: llama al método que borra o desactiva el registro elegido.
    @Override
    protected void eliminar() throws SQLException {
        modelos.get(filaElegida()).eliminar();
    }

    // Pasa los datos de la fila elegida al formulario.
    @Override
    protected void mostrar(int fila) {
        Modelo m = modelos.get(fila);
        elegir(cmbMarca, x -> x.getId() == m.getMarca().getId());
        txtNombre.setText(m.getNombre());
    }

    // Vacía los campos del formulario.
    @Override
    protected void limpiar() {
        txtNombre.setText("");
    }
}
