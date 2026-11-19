package Polizas.TiposPoliza;

import Polizas.Entidades.TipoPoliza;
import Polizas.General.CrudVista;
import Polizas.General.DatoInvalidoException;
import javax.swing.JCheckBox;
import javax.swing.JTextField;

// Pantalla del catálogo de tipos de póliza
public class TiposPolizaVista extends CrudVista<TipoPoliza> {

    protected JTextField txtNombre = new JTextField(25);
    protected JTextField txtDescripcion = new JTextField(35);
    protected JCheckBox chkActivo = new JCheckBox("Disponible para nuevas cotizaciones", true);

    public TiposPolizaVista() {
        super("Tipos de póliza", new TiposPolizaTM());
        campo("Nombre:", txtNombre);
        campo("Descripción:", txtDescripcion);
        campo("Activo:", chkActivo);
    }

    @Override
    public TipoPoliza leerFormulario() throws DatoInvalidoException {
        return new TipoPoliza(0, obligatorio(txtNombre, "Nombre"), opcional(txtDescripcion), chkActivo.isSelected());
    }

    @Override
    public void mostrar(TipoPoliza t) {
        txtNombre.setText(t.getNombre());
        txtDescripcion.setText(t.getDescripcion() == null ? "" : t.getDescripcion());
        chkActivo.setSelected(t.isActivo());
    }

    @Override
    public void limpiar() {
        txtNombre.setText("");
        txtDescripcion.setText("");
        chkActivo.setSelected(true);
    }
}
