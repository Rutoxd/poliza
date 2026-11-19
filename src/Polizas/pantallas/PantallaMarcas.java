package Polizas.pantallas;

import Polizas.clases.Marca;
import java.sql.SQLException;
import java.util.List;
import javax.swing.JTextField;

// Pantalla de marcas: usa los métodos de la clase Marca.
public class PantallaMarcas extends Pantalla {

    private JTextField txtNombre = new JTextField();
    private List<Marca> marcas;   // lo que muestra la tabla, en el mismo orden

    // Constructor vacío: crea el objeto para después llenarlo con los métodos set.
    public PantallaMarcas() {
        super("Marcas", "Id", "Marca");
        campo("Nombre:", txtNombre);
        recargar();
    }

    // Arma una Marca con lo escrito en el formulario.
    private Marca leer() {
        Marca m = new Marca();
        m.setNombre(obligatorio(txtNombre, "Nombre"));
        return m;
    }

    // Trae los registros con listar() y llena la tabla.
    @Override
    protected void cargarTabla() throws SQLException {
        marcas = Marca.listar();
        datos.setRowCount(0);
        for (Marca m : marcas) {
            datos.addRow(new Object[]{m.getId(), m.getNombre()});
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
        Marca m = leer();
        m.setId(marcas.get(filaElegida()).getId());
        m.actualizar();
    }

    // Botón Eliminar: llama al método que borra o desactiva el registro elegido.
    @Override
    protected void eliminar() throws SQLException {
        marcas.get(filaElegida()).eliminar();
    }

    // Pasa los datos de la fila elegida al formulario.
    @Override
    protected void mostrar(int fila) {
        txtNombre.setText(marcas.get(fila).getNombre());
    }

    // Vacía los campos del formulario.
    @Override
    protected void limpiar() {
        txtNombre.setText("");
    }
}
