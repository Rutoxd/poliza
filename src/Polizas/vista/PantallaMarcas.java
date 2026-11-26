package Polizas.vista;

import Polizas.dao.MarcaDAO;
import Polizas.modelo.Marca;
import java.sql.SQLException;
import java.util.List;
import javax.swing.JTextField;

// Capa de vista - catálogo de marcas. Guarda con MarcaDAO.
public class PantallaMarcas extends Pantalla {

    private final MarcaDAO dao = new MarcaDAO();   // acceso a datos (capa DAO)

    private JTextField txtNombre = new JTextField();
    private List<Marca> marcas;   // lo que muestra la tabla, en el mismo orden

    // Arma la pantalla: agrega los campos al formulario y llena la tabla.
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

    // Trae los registros con listar() del DAO y llena la tabla.
    @Override
    protected void cargarTabla() throws SQLException {
        marcas = dao.listar();
        datos.setRowCount(0);
        for (Marca m : marcas) {
            datos.addRow(new Object[]{m.getId(), m.getNombre()});
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
        Marca m = leer();
        m.setId(marcas.get(filaElegida()).getId());
        dao.actualizar(m);
    }

    // Botón Eliminar: pide al DAO que borre (o desactive) el registro elegido.
    @Override
    protected void eliminar() throws SQLException {
        dao.eliminar(marcas.get(filaElegida()).getId());
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
