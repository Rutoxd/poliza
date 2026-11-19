package Polizas.Aseguradoras;

import Polizas.Entidades.Aseguradora;
import Polizas.General.CrudVista;
import Polizas.General.DatoInvalidoException;
import javax.swing.JTextField;

// Pantalla del catálogo de aseguradoras
public class AseguradorasVista extends CrudVista<Aseguradora> {

    protected JTextField txtNombre = new JTextField(25);
    protected JTextField txtTelefono = new JTextField(15);
    protected JTextField txtCorreo = new JTextField(25);

    public AseguradorasVista() {
        super("Aseguradoras", new AseguradorasTM());
        campo("Nombre:", txtNombre);
        campo("Teléfono:", txtTelefono);
        campo("Correo:", txtCorreo);
    }

    @Override
    public Aseguradora leerFormulario() throws DatoInvalidoException {
        String telefono = opcional(txtTelefono);
        String correo = opcional(txtCorreo);
        if (telefono != null && !telefono.matches("\\d{10}")) {
            throw new DatoInvalidoException("El teléfono debe tener 10 dígitos.");
        }
        if (correo != null && !correo.matches("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$")) {
            throw new DatoInvalidoException("El correo no tiene un formato válido.");
        }
        return new Aseguradora(0, obligatorio(txtNombre, "Nombre"), telefono, correo);
    }

    @Override
    public void mostrar(Aseguradora a) {
        txtNombre.setText(a.getNombre());
        txtTelefono.setText(a.getTelefono() == null ? "" : a.getTelefono());
        txtCorreo.setText(a.getCorreo() == null ? "" : a.getCorreo());
    }

    @Override
    public void limpiar() {
        txtNombre.setText("");
        txtTelefono.setText("");
        txtCorreo.setText("");
    }
}
