package Polizas.vista;

import Polizas.dao.AseguradoraDAO;
import Polizas.modelo.Aseguradora;
import java.sql.SQLException;
import java.util.List;
import javax.swing.JTextField;

// Capa de vista - catálogo de aseguradoras. Guarda con AseguradoraDAO.
public class PantallaAseguradoras extends Pantalla {

    private final AseguradoraDAO dao = new AseguradoraDAO();   // acceso a datos (capa DAO)

    private JTextField txtNombre = new JTextField();
    private JTextField txtTelefono = new JTextField();
    private JTextField txtCorreo = new JTextField();
    private List<Aseguradora> aseguradoras;

    // Arma la pantalla: agrega los campos al formulario y llena la tabla.
    public PantallaAseguradoras() {
        super("Aseguradoras", "Id", "Nombre", "Teléfono", "Correo");
        campo("Nombre:", txtNombre);
        campo("Teléfono:", txtTelefono);
        campo("Correo:", txtCorreo);
        recargar();
    }

    // Arma el objeto con lo escrito en el formulario y revisa que los datos estén completos.
    private Aseguradora leer() {
        Aseguradora a = new Aseguradora();
        a.setNombre(obligatorio(txtNombre, "Nombre"));
        a.setTelefono(opcional(txtTelefono));
        a.setCorreo(opcional(txtCorreo));
        return a;
    }

    // Trae los registros con listar() del DAO y llena la tabla.
    @Override
    protected void cargarTabla() throws SQLException {
        aseguradoras = dao.listar();
        datos.setRowCount(0);
        for (Aseguradora a : aseguradoras) {
            datos.addRow(new Object[]{a.getId(), a.getNombre(), a.getTelefono(), a.getCorreo()});
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
        Aseguradora a = leer();
        a.setId(aseguradoras.get(filaElegida()).getId());
        dao.actualizar(a);
    }

    // Botón Eliminar: pide al DAO que borre (o desactive) el registro elegido.
    @Override
    protected void eliminar() throws SQLException {
        dao.eliminar(aseguradoras.get(filaElegida()).getId());
    }

    // Pasa los datos de la fila elegida al formulario.
    @Override
    protected void mostrar(int fila) {
        Aseguradora a = aseguradoras.get(fila);
        txtNombre.setText(a.getNombre());
        txtTelefono.setText(a.getTelefono());
        txtCorreo.setText(a.getCorreo());
    }

    // Vacía los campos del formulario.
    @Override
    protected void limpiar() {
        txtNombre.setText("");
        txtTelefono.setText("");
        txtCorreo.setText("");
    }
}
