package Polizas.Modelos;

import Polizas.Entidades.Marca;
import Polizas.Entidades.Modelo;
import Polizas.General.CrudVista;
import Polizas.General.DatoInvalidoException;
import java.util.List;
import javax.swing.JComboBox;
import javax.swing.JTextField;

// Pantalla de modelos de vehículo
public class ModelosVista extends CrudVista<Modelo> {

    protected JComboBox<Marca> cmbMarca = new JComboBox<>();
    protected JTextField txtNombre = new JTextField(25);

    public ModelosVista() {
        super("Modelos de vehículos", new ModelosTM());
        campo("Marca:", cmbMarca);
        campo("Modelo:", txtNombre);
    }

    public void llenarMarcas(List<Marca> marcas) {
        cmbMarca.removeAllItems();
        for (Marca m : marcas) {
            cmbMarca.addItem(m);
        }
    }

    @Override
    public Modelo leerFormulario() throws DatoInvalidoException {
        Marca marca = (Marca) cmbMarca.getSelectedItem();
        if (marca == null) {
            throw new DatoInvalidoException("Primero registra una marca.");
        }
        return new Modelo(0, marca, obligatorio(txtNombre, "Modelo"));
    }

    @Override
    public void mostrar(Modelo m) {
        seleccionar(cmbMarca, x -> x.getId() == m.getMarca().getId());
        txtNombre.setText(m.getNombre());
    }

    @Override
    public void limpiar() {
        txtNombre.setText("");
    }
}
