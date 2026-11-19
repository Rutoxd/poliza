package Polizas.Marcas;

import Polizas.Entidades.Marca;
import Polizas.General.CrudVista;
import Polizas.General.DatoInvalidoException;
import javax.swing.JTextField;

// Pantalla de marcas
public class MarcasVista extends CrudVista<Marca> {

    protected JTextField txtNombre = new JTextField(25);

    public MarcasVista() {
        super("Marcas de vehículos", new MarcasTM());
        campo("Nombre:", txtNombre);
    }

    @Override
    public Marca leerFormulario() throws DatoInvalidoException {
        return new Marca(0, obligatorio(txtNombre, "Nombre"));
    }

    @Override
    public void mostrar(Marca m) {
        txtNombre.setText(m.getNombre());
    }

    @Override
    public void limpiar() {
        txtNombre.setText("");
    }
}
