package Polizas.vista;

import Polizas.dao.ClienteDAO;
import Polizas.modelo.Cliente;
import java.sql.SQLException;
import java.util.List;
import javax.swing.JTextField;

// Capa de vista - Módulo 1, Gestión de Clientes: alta, consulta, modificación y eliminación.
// Revisa RFC y correo con el modelo y guarda con ClienteDAO; aquí no hay SQL.
public class PantallaClientes extends Pantalla {

    private final ClienteDAO dao = new ClienteDAO();   // acceso a datos (capa DAO)

    private JTextField txtNombre = new JTextField();
    private JTextField txtRfc = new JTextField();
    private JTextField txtTelefono = new JTextField();
    private JTextField txtCorreo = new JTextField();
    private JTextField txtDomicilio = new JTextField();
    private List<Cliente> clientes;

    // Arma la pantalla: agrega los campos al formulario y llena la tabla.
    public PantallaClientes() {
        super("Clientes", "Id", "Nombre", "RFC", "Teléfono", "Correo", "Domicilio");
        campo("Nombre:", txtNombre);
        campo("RFC:", txtRfc);
        campo("Teléfono:", txtTelefono);
        campo("Correo:", txtCorreo);
        campo("Domicilio:", txtDomicilio);
        recargar();
    }

    // Arma el objeto con lo escrito en el formulario y revisa que los datos estén completos.
    private Cliente leer() {
        Cliente c = new Cliente();
        c.setNombre(obligatorio(txtNombre, "Nombre"));
        c.setRfc(obligatorio(txtRfc, "RFC").toUpperCase());
        c.setTelefono(opcional(txtTelefono));
        c.setCorreo(opcional(txtCorreo));
        c.setDomicilio(opcional(txtDomicilio));
        if (!c.validarRfc()) {
            throw new IllegalArgumentException("El RFC no es válido (ejemplo: GOMA800101AB1).");
        }
        if (!c.validarCorreo()) {
            throw new IllegalArgumentException("El correo no es válido.");
        }
        return c;
    }

    // Trae los registros con listar() del DAO y llena la tabla.
    @Override
    protected void cargarTabla() throws SQLException {
        clientes = dao.listar();
        datos.setRowCount(0);
        for (Cliente c : clientes) {
            datos.addRow(new Object[]{c.getId(), c.getNombre(), c.getRfc(), c.getTelefono(), c.getCorreo(), c.getDomicilio()});
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
        Cliente c = leer();
        c.setId(clientes.get(filaElegida()).getId());
        dao.actualizar(c);
    }

    // Botón Eliminar: pide al DAO que borre (o desactive) el registro elegido.
    @Override
    protected void eliminar() throws SQLException {
        dao.eliminar(clientes.get(filaElegida()).getId());
    }

    // Pasa los datos de la fila elegida al formulario.
    @Override
    protected void mostrar(int fila) {
        Cliente c = clientes.get(fila);
        txtNombre.setText(c.getNombre());
        txtRfc.setText(c.getRfc());
        txtTelefono.setText(c.getTelefono());
        txtCorreo.setText(c.getCorreo());
        txtDomicilio.setText(c.getDomicilio());
    }

    // Vacía los campos del formulario.
    @Override
    protected void limpiar() {
        txtNombre.setText("");
        txtRfc.setText("");
        txtTelefono.setText("");
        txtCorreo.setText("");
        txtDomicilio.setText("");
    }
}
