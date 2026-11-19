package Polizas.pantallas;

import Polizas.clases.Cliente;
import java.sql.SQLException;
import java.util.List;
import javax.swing.JTextField;

// Pantalla de clientes: usa los métodos de la clase Cliente y revisa RFC y correo.
public class PantallaClientes extends Pantalla {

    private JTextField txtNombre = new JTextField();
    private JTextField txtRfc = new JTextField();
    private JTextField txtTelefono = new JTextField();
    private JTextField txtCorreo = new JTextField();
    private JTextField txtDomicilio = new JTextField();
    private List<Cliente> clientes;

    public PantallaClientes() {
        super("Clientes", "Id", "Nombre", "RFC", "Teléfono", "Correo", "Domicilio");
        campo("Nombre:", txtNombre);
        campo("RFC:", txtRfc);
        campo("Teléfono:", txtTelefono);
        campo("Correo:", txtCorreo);
        campo("Domicilio:", txtDomicilio);
        recargar();
    }

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

    @Override
    protected void cargarTabla() throws SQLException {
        clientes = Cliente.listar();
        datos.setRowCount(0);
        for (Cliente c : clientes) {
            datos.addRow(new Object[]{c.getId(), c.getNombre(), c.getRfc(), c.getTelefono(), c.getCorreo(), c.getDomicilio()});
        }
    }

    @Override
    protected void agregar() throws SQLException {
        leer().registrar();
    }

    @Override
    protected void actualizar() throws SQLException {
        Cliente c = leer();
        c.setId(clientes.get(filaElegida()).getId());
        c.actualizar();
    }

    @Override
    protected void eliminar() throws SQLException {
        clientes.get(filaElegida()).eliminar();
    }

    @Override
    protected void mostrar(int fila) {
        Cliente c = clientes.get(fila);
        txtNombre.setText(c.getNombre());
        txtRfc.setText(c.getRfc());
        txtTelefono.setText(c.getTelefono());
        txtCorreo.setText(c.getCorreo());
        txtDomicilio.setText(c.getDomicilio());
    }

    @Override
    protected void limpiar() {
        txtNombre.setText("");
        txtRfc.setText("");
        txtTelefono.setText("");
        txtCorreo.setText("");
        txtDomicilio.setText("");
    }
}
