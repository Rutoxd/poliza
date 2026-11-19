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

    @Override
    protected void cargarTabla() throws SQLException {
        vehiculos = Vehiculo.listar();
        datos.setRowCount(0);
        for (Vehiculo v : vehiculos) {
            datos.addRow(new Object[]{v.getId(), v.getCliente().getNombre(), v.getModelo().getMarca().getNombre(),
                v.getModelo().getNombre(), v.getAnio(), v.getPlacas(), v.getNumeroSerie()});
        }
    }

    @Override
    protected void agregar() throws SQLException {
        leer().registrar();
    }

    @Override
    protected void actualizar() throws SQLException {
        Vehiculo v = leer();
        v.setId(vehiculos.get(filaElegida()).getId());
        v.actualizar();
    }

    @Override
    protected void eliminar() throws SQLException {
        vehiculos.get(filaElegida()).eliminar();
    }

    @Override
    protected void mostrar(int fila) {
        Vehiculo v = vehiculos.get(fila);
        elegir(cmbCliente, c -> c.getId() == v.getCliente().getId());
        elegir(cmbModelo, m -> m.getId() == v.getModelo().getId());
        txtAnio.setText(String.valueOf(v.getAnio()));
        txtPlacas.setText(v.getPlacas());
        txtSerie.setText(v.getNumeroSerie());
    }

    @Override
    protected void limpiar() {
        txtAnio.setText("");
        txtPlacas.setText("");
        txtSerie.setText("");
    }
}
