package Polizas.pantallas;

import Polizas.clases.Marca;
import java.sql.SQLException;
import java.util.List;
import javax.swing.JTextField;

// Pantalla de marcas: usa los métodos de la clase Marca.
public class PantallaMarcas extends Pantalla {

    private JTextField txtNombre = new JTextField();
    private List<Marca> marcas;   // lo que muestra la tabla, en el mismo orden

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

    @Override
    protected void cargarTabla() throws SQLException {
        marcas = Marca.listar();
        datos.setRowCount(0);
        for (Marca m : marcas) {
            datos.addRow(new Object[]{m.getId(), m.getNombre()});
        }
    }

    @Override
    protected void agregar() throws SQLException {
        leer().registrar();
    }

    @Override
    protected void actualizar() throws SQLException {
        Marca m = leer();
        m.setId(marcas.get(filaElegida()).getId());
        m.actualizar();
    }

    @Override
    protected void eliminar() throws SQLException {
        marcas.get(filaElegida()).eliminar();
    }

    @Override
    protected void mostrar(int fila) {
        txtNombre.setText(marcas.get(fila).getNombre());
    }

    @Override
    protected void limpiar() {
        txtNombre.setText("");
    }
}
