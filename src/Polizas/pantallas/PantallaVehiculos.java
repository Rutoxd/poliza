package Polizas.pantallas;

import Polizas.clases.Cliente;
import Polizas.clases.Conexion;
import Polizas.clases.Modelo;
import Polizas.clases.Vehiculo;
import java.sql.SQLException;
import java.util.List;
import javax.swing.JComboBox;
import javax.swing.JTextField;

// Pantalla de vehículos: el cliente y el modelo se eligen de listas.
public class PantallaVehiculos extends Pantalla {

    private JComboBox<Cliente> cmbCliente = new JComboBox<>();
    private JComboBox<Modelo> cmbModelo = new JComboBox<>();
    private JTextField txtAnio = new JTextField();
    private JTextField txtPlacas = new JTextField();
    private JTextField txtSerie = new JTextField();
    private List<Vehiculo> vehiculos;

    // Constructor vacío: crea el objeto para después llenarlo con los métodos set.
    public PantallaVehiculos() {
        super("Vehículos", "Id", "Cliente", "Marca", "Modelo", "Año", "Placas", "Núm. de serie");
        campo("Cliente:", cmbCliente);
        campo("Marca y modelo:", cmbModelo);
        campo("Año:", txtAnio);
        campo("Placas:", txtPlacas);
        campo("Núm. de serie:", txtSerie);
        try {
            for (Cliente c : Cliente.listar()) {
                cmbCliente.addItem(c);
            }
            for (Modelo m : Modelo.listar()) {
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
        return v;
    }

    // Trae los registros con listar() y llena la tabla.
    @Override
    protected void cargarTabla() throws SQLException {
        vehiculos = Vehiculo.listar();
        datos.setRowCount(0);
        for (Vehiculo v : vehiculos) {
            datos.addRow(new Object[]{v.getId(), v.getCliente().getNombre(), v.getModelo().getMarca().getNombre(),
                v.getModelo().getNombre(), v.getAnio(), v.getPlacas(), v.getNumeroSerie()});
        }
    }

    // Botón Agregar: arma el objeto con el formulario y llama a registrar().
    @Override
    protected void agregar() throws SQLException {
        leer().registrar();
    }

    // Botón Actualizar: arma el objeto, le pone el id de la fila elegida y llama a actualizar().
    @Override
    protected void actualizar() throws SQLException {
        Vehiculo v = leer();
        v.setId(vehiculos.get(filaElegida()).getId());
        v.actualizar();
    }

    // Botón Eliminar: llama al método que borra o desactiva el registro elegido.
    @Override
    protected void eliminar() throws SQLException {
        vehiculos.get(filaElegida()).eliminar();
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
