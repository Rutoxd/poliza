package Polizas.vista;

import Polizas.dao.MarcaDAO;
import Polizas.dao.ModeloDAO;
import Polizas.dao.Conexion;
import Polizas.modelo.Marca;
import Polizas.modelo.Modelo;
import java.sql.SQLException;
import java.util.List;
import javax.swing.JComboBox;
import javax.swing.JTextField;

// Capa de vista - catálogo de modelos: cada modelo se liga a una marca de la lista. Guarda con ModeloDAO.
public class PantallaModelos extends Pantalla {

    private final ModeloDAO dao = new ModeloDAO();   // acceso a datos (capa DAO)

    private JComboBox<Marca> cmbMarca = new JComboBox<>();
    private JTextField txtNombre = new JTextField();
    private List<Modelo> modelos;

    // Arma la pantalla: agrega los campos al formulario y llena la tabla.
    public PantallaModelos() {
        super("Modelos", "Id", "Marca", "Modelo");
        campo("Marca:", cmbMarca);
        campo("Modelo:", txtNombre);
        try {
            for (Marca m : new MarcaDAO().listar()) {
                cmbMarca.addItem(m);
            }
        } catch (SQLException e) {
            aviso(Conexion.mensaje(e));
        }
        recargar();
    }

    // Arma el objeto con lo escrito en el formulario y revisa que los datos estén completos.
    private Modelo leer() {
        if (cmbMarca.getSelectedItem() == null) {
            throw new IllegalArgumentException("Primero registra una marca.");
        }
        Modelo m = new Modelo();
        m.setMarca((Marca) cmbMarca.getSelectedItem());
        m.setNombre(obligatorio(txtNombre, "Modelo"));
        return m;
    }

    // Trae los registros con listar() del DAO y llena la tabla.
    @Override
    protected void cargarTabla() throws SQLException {
        modelos = dao.listar();
        datos.setRowCount(0);
        for (Modelo m : modelos) {
            datos.addRow(new Object[]{m.getId(), m.getMarca().getNombre(), m.getNombre()});
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
        Modelo m = leer();
        m.setId(modelos.get(filaElegida()).getId());
        dao.actualizar(m);
    }

    // Botón Eliminar: pide al DAO que borre (o desactive) el registro elegido.
    @Override
    protected void eliminar() throws SQLException {
        dao.eliminar(modelos.get(filaElegida()).getId());
    }

    // Pasa los datos de la fila elegida al formulario.
    @Override
    protected void mostrar(int fila) {
        Modelo m = modelos.get(fila);
        elegir(cmbMarca, x -> x.getId() == m.getMarca().getId());
        txtNombre.setText(m.getNombre());
    }

    // Vacía los campos del formulario.
    @Override
    protected void limpiar() {
        txtNombre.setText("");
    }
}
