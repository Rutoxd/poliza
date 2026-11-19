package Polizas.pantallas;

import Polizas.clases.TipoPoliza;
import java.sql.SQLException;
import java.util.List;
import javax.swing.JCheckBox;
import javax.swing.JTextField;

// Pantalla de tipos de póliza. Los tipos no se borran: el botón "Desactivar" llama a desactivar().
public class PantallaTiposPoliza extends Pantalla {

    private JTextField txtNombre = new JTextField();
    private JTextField txtDescripcion = new JTextField();
    private JCheckBox chkActivo = new JCheckBox("Activo", true);
    private List<TipoPoliza> tipos;

    public PantallaTiposPoliza() {
        super("Tipos de póliza", "Id", "Nombre", "Descripción", "Activo");
        btnEliminar.setText("Desactivar");
        campo("Nombre:", txtNombre);
        campo("Descripción:", txtDescripcion);
        campo("Estado:", chkActivo);
        recargar();
    }

    private TipoPoliza leer() {
        TipoPoliza t = new TipoPoliza();
        t.setNombre(obligatorio(txtNombre, "Nombre"));
        t.setDescripcion(opcional(txtDescripcion));
        t.setActivo(chkActivo.isSelected());
        return t;
    }

    @Override
    protected void cargarTabla() throws SQLException {
        tipos = TipoPoliza.listar();
        datos.setRowCount(0);
        for (TipoPoliza t : tipos) {
            datos.addRow(new Object[]{t.getId(), t.getNombre(), t.getDescripcion(), t.isActivo() ? "Sí" : "No"});
        }
    }

    @Override
    protected void agregar() throws SQLException {
        leer().registrar();
    }

    @Override
    protected void actualizar() throws SQLException {
        TipoPoliza t = leer();
        t.setId(tipos.get(filaElegida()).getId());
        t.actualizar();
    }

    @Override
    protected void eliminar() throws SQLException {
        tipos.get(filaElegida()).desactivar();
    }

    @Override
    protected void mostrar(int fila) {
        TipoPoliza t = tipos.get(fila);
        txtNombre.setText(t.getNombre());
        txtDescripcion.setText(t.getDescripcion());
        chkActivo.setSelected(t.isActivo());
    }

    @Override
    protected void limpiar() {
        txtNombre.setText("");
        txtDescripcion.setText("");
        chkActivo.setSelected(true);
    }
}
