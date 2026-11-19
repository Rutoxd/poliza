package Polizas.Coberturas;

import Polizas.Entidades.Cobertura;
import Polizas.General.CrudVista;
import Polizas.General.DatoInvalidoException;
import javax.swing.JTextField;

// Pantalla del catálogo de coberturas
public class CoberturasVista extends CrudVista<Cobertura> {

    protected JTextField txtNombre = new JTextField(25);
    protected JTextField txtDescripcion = new JTextField(35);

    public CoberturasVista() {
        super("Coberturas", new CoberturasTM());
        campo("Nombre:", txtNombre);
        campo("Descripción:", txtDescripcion);
    }

    @Override
    public Cobertura leerFormulario() throws DatoInvalidoException {
        return new Cobertura(0, obligatorio(txtNombre, "Nombre"), opcional(txtDescripcion));
    }

    @Override
    public void mostrar(Cobertura c) {
        txtNombre.setText(c.getNombre());
        txtDescripcion.setText(c.getDescripcion() == null ? "" : c.getDescripcion());
    }

    @Override
    public void limpiar() {
        txtNombre.setText("");
        txtDescripcion.setText("");
    }
}
