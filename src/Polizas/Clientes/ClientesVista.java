package Polizas.Clientes;

import Polizas.Entidades.Cliente;
import Polizas.General.CrudVista;
import Polizas.General.DatoInvalidoException;
import javax.swing.JTextField;

// Pantalla de clientes
public class ClientesVista extends CrudVista<Cliente> {

    protected JTextField txtNombre = new JTextField(25);
    protected JTextField txtRfc = new JTextField(15);
    protected JTextField txtTelefono = new JTextField(15);
    protected JTextField txtCorreo = new JTextField(25);
    protected JTextField txtDomicilio = new JTextField(35);

    public ClientesVista() {
        super("Clientes", new ClientesTM());
        campo("Nombre:", txtNombre);
        campo("RFC:", txtRfc);
        campo("Teléfono:", txtTelefono);
        campo("Correo:", txtCorreo);
        campo("Domicilio:", txtDomicilio);
    }

    @Override
    public Cliente leerFormulario() throws DatoInvalidoException {
        Cliente c = new Cliente(0, obligatorio(txtNombre, "Nombre"), obligatorio(txtRfc, "RFC").toUpperCase(),
                opcional(txtTelefono), opcional(txtCorreo), opcional(txtDomicilio));
        if (!c.validarRfc()) {
            throw new DatoInvalidoException("El RFC no es válido (12 o 13 caracteres, ej. PUUC800101AB1).");
        }
        if (!c.validarTelefono()) {
            throw new DatoInvalidoException("El teléfono debe tener 10 dígitos.");
        }
        if (!c.validarCorreo()) {
            throw new DatoInvalidoException("El correo no tiene un formato válido.");
        }
        return c;
    }

    @Override
    public void mostrar(Cliente c) {
        txtNombre.setText(c.getNombre());
        txtRfc.setText(c.getRfc());
        txtTelefono.setText(c.getTelefono() == null ? "" : c.getTelefono());
        txtCorreo.setText(c.getCorreo() == null ? "" : c.getCorreo());
        txtDomicilio.setText(c.getDomicilio() == null ? "" : c.getDomicilio());
    }

    @Override
    public void limpiar() {
        txtNombre.setText("");
        txtRfc.setText("");
        txtTelefono.setText("");
        txtCorreo.setText("");
        txtDomicilio.setText("");
    }
}
