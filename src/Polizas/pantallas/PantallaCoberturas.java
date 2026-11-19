package Polizas.pantallas;

import Polizas.clases.Cobertura;
import java.sql.SQLException;
import java.util.List;
import javax.swing.JTextField;

// Pantalla de coberturas: usa los métodos de la clase Cobertura.
public class PantallaCoberturas extends Pantalla {

    private JTextField txtNombre = new JTextField();
    private JTextField txtDescripcion = new JTextField();
    private List<Cobertura> coberturas;

    // Constructor vacío: crea el objeto para después llenarlo con los métodos set.
    public PantallaCoberturas() {
        super("Coberturas", "Id", "Nombre", "Descripción");
        campo("Nombre:", txtNombre);
        campo("Descripción:", txtDescripcion);
        recargar();
    }

    // Arma el objeto con lo escrito en el formulario y revisa que los datos estén completos.
    private Cobertura leer() {
        Cobertura c = new Cobertura();
        c.setNombre(obligatorio(txtNombre, "Nombre"));
        c.setDescripcion(opcional(txtDescripcion));
        return c;
    }

    // Trae los registros con listar() y llena la tabla.
    @Override
    protected void cargarTabla() throws SQLException {
        coberturas = Cobertura.listar();
        datos.setRowCount(0);
        for (Cobertura c : coberturas) {
            datos.addRow(new Object[]{c.getId(), c.getNombre(), c.getDescripcion()});
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
        Cobertura c = leer();
        c.setId(coberturas.get(filaElegida()).getId());
        c.actualizar();
    }

    // Botón Eliminar: llama al método que borra o desactiva el registro elegido.
    @Override
    protected void eliminar() throws SQLException {
        coberturas.get(filaElegida()).eliminar();
    }

    // Pasa los datos de la fila elegida al formulario.
    @Override
    protected void mostrar(int fila) {
        Cobertura c = coberturas.get(fila);
        txtNombre.setText(c.getNombre());
        txtDescripcion.setText(c.getDescripcion());
    }

    // Vacía los campos del formulario.
    @Override
    protected void limpiar() {
        txtNombre.setText("");
        txtDescripcion.setText("");
    }
}
