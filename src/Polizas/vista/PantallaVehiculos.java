package Polizas.vista;

import Polizas.dao.ClienteDAO;
import Polizas.dao.ModeloDAO;
import Polizas.dao.VehiculoDAO;
import Polizas.modelo.Cliente;
import Polizas.dao.Conexion;
import Polizas.modelo.Modelo;
import Polizas.modelo.Vehiculo;
import java.sql.SQLException;
import java.util.List;
import javax.swing.JComboBox;
import javax.swing.JTextField;

// Capa de vista - Módulo 2, Gestión de Vehículos: alta, consulta, modificación y eliminación.
// El cliente y el modelo se eligen de listas; guarda con VehiculoDAO.
public class PantallaVehiculos extends Pantalla {

    private final VehiculoDAO dao = new VehiculoDAO();   // acceso a datos (capa DAO)

    private JComboBox<Cliente> cmbCliente = new JComboBox<>();
    private JComboBox<Modelo> cmbModelo = new JComboBox<>();
    private JTextField txtAnio = new JTextField();
    private JTextField txtPlacas = new JTextField();
    private JTextField txtSerie = new JTextField();
    private List<Vehiculo> vehiculos;

    // Arma la pantalla: agrega los campos al formulario y llena la tabla.
    public PantallaVehiculos() {
        super("Vehículos", "Id", "Cliente", "Marca", "Modelo", "Año", "Placas", "Núm. de serie");
        campo("Cliente:", cmbCliente);
        campo("Marca y modelo:", cmbModelo);
        campo("Año:", txtAnio);
        campo("Placas:", txtPlacas);
        campo("Núm. de serie:", txtSerie);
        try {
            for (Cliente c : new ClienteDAO().listar()) {
                cmbCliente.addItem(c);
            }
            for (Modelo m : new ModeloDAO().listar()) {
                cmbModelo.addItem(m);
            }
        } catch (SQLException e) {
            aviso(Conexion.mensaje(e));
        }
        recargar();
    }

    // Arma el objeto con lo escrito en el formulario y revisa que los datos estén completos.
    private Vehiculo leer() {
        if (cmbCliente.getSelectedItem() == null) {
            throw new IllegalArgumentException("Primero registra al cliente.");
        }
        if (cmbModelo.getSelectedItem() == null) {
            throw new IllegalArgumentException("Primero registra la marca y el modelo.");
        }
        Vehiculo v = new Vehiculo();
        v.setCliente((Cliente) cmbCliente.getSelectedItem());
        v.setModelo((Modelo) cmbModelo.getSelectedItem());
        try {
            v.setAnio(Integer.parseInt(obligatorio(txtAnio, "Año")));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("El año debe ser un número.");
        }
        v.setPlacas(obligatorio(txtPlacas, "Placas").toUpperCase());
        v.setNumeroSerie(obligatorio(txtSerie, "Núm. de serie").toUpperCase());
        v.validar();
        return v;
    }

    // Trae los registros con listar() del DAO y llena la tabla.
    @Override
    protected void cargarTabla() throws SQLException {
        vehiculos = dao.listar();
        datos.setRowCount(0);
        for (Vehiculo v : vehiculos) {
            datos.addRow(new Object[]{v.getId(), v.getCliente().getNombre(), v.getModelo().getMarca().getNombre(),
                v.getModelo().getNombre(), v.getAnio(), v.getPlacas(), v.getNumeroSerie()});
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
        Vehiculo v = leer();
        v.setId(vehiculos.get(filaElegida()).getId());
        dao.actualizar(v);
    }

    // Botón Eliminar: pide al DAO que borre (o desactive) el registro elegido.
    @Override
    protected void eliminar() throws SQLException {
        dao.eliminar(vehiculos.get(filaElegida()).getId());
    }

    // Pasa los datos de la fila elegida al formulario.
    @Override
    protected void mostrar(int fila) {
        Vehiculo v = vehiculos.get(fila);
        elegir(cmbCliente, c -> c.getId() == v.getCliente().getId());
        elegir(cmbModelo, m -> m.getId() == v.getModelo().getId());
        txtAnio.setText(String.valueOf(v.getAnio()));
        txtPlacas.setText(v.getPlacas());
        txtSerie.setText(v.getNumeroSerie());
    }

    // Vacía los campos del formulario.
    @Override
    protected void limpiar() {
        txtAnio.setText("");
        txtPlacas.setText("");
        txtSerie.setText("");
    }
}
