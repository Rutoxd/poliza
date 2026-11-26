package Polizas.vista;

import Polizas.dao.TipoPolizaDAO;
import Polizas.modelo.TipoPoliza;
import java.sql.SQLException;
import java.util.List;
import javax.swing.JCheckBox;
import javax.swing.JTextField;

// Capa de vista - catálogo de tipos de póliza. Los tipos no se borran: el botón "Desactivar"
// llama a desactivar() de TipoPolizaDAO.
public class PantallaTiposPoliza extends Pantalla {

    private final TipoPolizaDAO dao = new TipoPolizaDAO();   // acceso a datos (capa DAO)

    private JTextField txtNombre = new JTextField();
    private JTextField txtDescripcion = new JTextField();
    private JCheckBox chkActivo = new JCheckBox("Activo", true);
    private List<TipoPoliza> tipos;

    // Arma la pantalla: agrega los campos al formulario y llena la tabla.
    public PantallaTiposPoliza() {
        super("Tipos de póliza", "Id", "Nombre", "Descripción", "Activo");
        btnEliminar.setText("Desactivar");
        campo("Nombre:", txtNombre);
        campo("Descripción:", txtDescripcion);
        campo("Estado:", chkActivo);
        recargar();
    }

    // Arma el objeto con lo escrito en el formulario y revisa que los datos estén completos.
    private TipoPoliza leer() {
        TipoPoliza t = new TipoPoliza();
        t.setNombre(obligatorio(txtNombre, "Nombre"));
        t.setDescripcion(opcional(txtDescripcion));
        t.setActivo(chkActivo.isSelected());
        return t;
    }

    // Trae los registros con listar() del DAO y llena la tabla.
    @Override
    protected void cargarTabla() throws SQLException {
        tipos = dao.listar();
        datos.setRowCount(0);
        for (TipoPoliza t : tipos) {
            datos.addRow(new Object[]{t.getId(), t.getNombre(), t.getDescripcion(), t.isActivo() ? "Sí" : "No"});
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
        TipoPoliza t = leer();
        t.setId(tipos.get(filaElegida()).getId());
        dao.actualizar(t);
    }

    // Botón Eliminar: pide al DAO que borre (o desactive) el registro elegido.
    @Override
    protected void eliminar() throws SQLException {
        dao.desactivar(tipos.get(filaElegida()).getId());
    }

    // Pasa los datos de la fila elegida al formulario.
    @Override
    protected void mostrar(int fila) {
        TipoPoliza t = tipos.get(fila);
        txtNombre.setText(t.getNombre());
        txtDescripcion.setText(t.getDescripcion());
        chkActivo.setSelected(t.isActivo());
    }

    // Vacía los campos del formulario.
    @Override
    protected void limpiar() {
        txtNombre.setText("");
        txtDescripcion.setText("");
        chkActivo.setSelected(true);
    }
}
