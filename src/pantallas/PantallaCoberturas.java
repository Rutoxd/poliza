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

    public PantallaCoberturas() {
        super("Coberturas", "Id", "Nombre", "Descripción");
        campo("Nombre:", txtNombre);
        campo("Descripción:", txtDescripcion);
        recargar();
    }

    private Cobertura leer() {
        Cobertura c = new Cobertura();
        c.setNombre(obligatorio(txtNombre, "Nombre"));
        c.setDescripcion(opcional(txtDescripcion));
        return c;
    }

    @Override
    protected void cargarTabla() throws SQLException {
        coberturas = Cobertura.listar();
        datos.setRowCount(0);
        for (Cobertura c : coberturas) {
            datos.addRow(new Object[]{c.getId(), c.getNombre(), c.getDescripcion()});
        }
    }

    @Override
    protected void agregar() throws SQLException {
        leer().registrar();
    }

    @Override
    protected void actualizar() throws SQLException {
        Cobertura c = leer();
        c.setId(coberturas.get(filaElegida()).getId());
        c.actualizar();
    }

    @Override
    protected void eliminar() throws SQLException {
        coberturas.get(filaElegida()).eliminar();
    }

    @Override
    protected void mostrar(int fila) {
        Cobertura c = coberturas.get(fila);
        txtNombre.setText(c.getNombre());
        txtDescripcion.setText(c.getDescripcion());
    }

    @Override
    protected void limpiar() {
        txtNombre.setText("");
        txtDescripcion.setText("");
    }
}
