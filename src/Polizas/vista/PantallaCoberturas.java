package Polizas.vista;

import Polizas.dao.CoberturaDAO;
import Polizas.modelo.Cobertura;
import java.sql.SQLException;
import java.util.List;
import javax.swing.JTextField;

// Capa de vista - catálogo de coberturas. Guarda con CoberturaDAO.
public class PantallaCoberturas extends Pantalla {

    private final CoberturaDAO dao = new CoberturaDAO();   // acceso a datos (capa DAO)

    private JTextField txtNombre = new JTextField();
    private JTextField txtDescripcion = new JTextField();
    private List<Cobertura> coberturas;

    // Arma la pantalla: agrega los campos al formulario y llena la tabla.
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

    // Trae los registros con listar() del DAO y llena la tabla.
    @Override
    protected void cargarTabla() throws SQLException {
        coberturas = dao.listar();
        datos.setRowCount(0);
        for (Cobertura c : coberturas) {
            datos.addRow(new Object[]{c.getId(), c.getNombre(), c.getDescripcion()});
        }
    }

    // Botón Agregar: arma el objeto con el formulario y lo guarda con registrar() del DAO.
    @Override
    protected void agregar() throws SQLException {
        dao.registrar(leer());
    }

    // Botón Actualizar: arma el objeto, le pone el id de la fila elegida y lo guarda con actualizar() del DAO.
    @Override
    protected void actualizar() throws SQLException {
        Cobertura c = leer();
        c.setId(coberturas.get(filaElegida()).getId());
        dao.actualizar(c);
    }

    // Botón Eliminar: pide al DAO que borre (o desactive) el registro elegido.
    @Override
    protected void eliminar() throws SQLException {
        dao.eliminar(coberturas.get(filaElegida()).getId());
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
