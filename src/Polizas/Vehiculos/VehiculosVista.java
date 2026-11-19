package Polizas.Vehiculos;

import Polizas.Entidades.Cliente;
import Polizas.Entidades.Modelo;
import Polizas.Entidades.Vehiculo;
import Polizas.General.CrudVista;
import Polizas.General.DatoInvalidoException;
import java.util.List;
import javax.swing.JComboBox;
import javax.swing.JTextField;

// Pantalla de vehículos
public class VehiculosVista extends CrudVista<Vehiculo> {

    protected JComboBox<Cliente> cmbCliente = new JComboBox<>();
    protected JComboBox<Modelo> cmbModelo = new JComboBox<>();
    protected JTextField txtAnio = new JTextField(6);
    protected JTextField txtPlacas = new JTextField(10);
    protected JTextField txtSerie = new JTextField(20);

    public VehiculosVista() {
        super("Vehículos", new VehiculosTM());
        campo("Cliente:", cmbCliente);
        campo("Marca y modelo:", cmbModelo);
        campo("Año:", txtAnio);
        campo("Placas:", txtPlacas);
        campo("Núm. de serie:", txtSerie);
    }

    public void llenarCombos(List<Cliente> clientes, List<Modelo> modelos) {
        cmbCliente.removeAllItems();
        clientes.forEach(cmbCliente::addItem);
        cmbModelo.removeAllItems();
        modelos.forEach(cmbModelo::addItem);
    }

    @Override
    public Vehiculo leerFormulario() throws DatoInvalidoException {
        Cliente cliente = (Cliente) cmbCliente.getSelectedItem();
        Modelo modelo = (Modelo) cmbModelo.getSelectedItem();
        if (cliente == null) {
            throw new DatoInvalidoException("Primero registra al cliente.");
        }
        if (modelo == null) {
            throw new DatoInvalidoException("Primero registra la marca y el modelo.");
        }
        int anio;
        try {
            anio = Integer.parseInt(obligatorio(txtAnio, "Año"));
        } catch (NumberFormatException e) {
            throw new DatoInvalidoException("El año debe ser un número.");
        }
        Vehiculo v = new Vehiculo(0, cliente, modelo, anio,
                obligatorio(txtPlacas, "Placas").toUpperCase(), obligatorio(txtSerie, "Núm. de serie").toUpperCase());
        if (!v.validarAnio()) {
            throw new DatoInvalidoException("El año no es válido.");
        }
        if (!v.validarNumeroSerie()) {
            throw new DatoInvalidoException("El número de serie debe tener 17 caracteres (sin I, O ni Q).");
        }
        return v;
    }

    @Override
    public void mostrar(Vehiculo v) {
        seleccionar(cmbCliente, c -> c.getId() == v.getCliente().getId());
        seleccionar(cmbModelo, m -> m.getId() == v.getModelo().getId());
        txtAnio.setText(String.valueOf(v.getAnio()));
        txtPlacas.setText(v.getPlacas());
        txtSerie.setText(v.getNumeroSerie());
    }

    @Override
    public void limpiar() {
        txtAnio.setText("");
        txtPlacas.setText("");
        txtSerie.setText("");
    }
}
