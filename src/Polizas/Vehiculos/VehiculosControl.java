package Polizas.Vehiculos;

import Polizas.Clientes.ClientesModelo;
import Polizas.Entidades.Vehiculo;
import Polizas.General.CrudControl;
import Polizas.General.DatoInvalidoException;
import Polizas.Modelos.ModelosModelo;

// Controlador de vehículos: llena los combos de clientes y modelos
public class VehiculosControl extends CrudControl<Vehiculo> {

    private final VehiculosVista vv;

    public VehiculosControl(VehiculosVista vista) {
        super(vista, new VehiculosModelo(), "vehículo");
        this.vv = vista;
        iniciar();
    }

    @Override
    protected void cargarCatalogos() {
        vv.llenarCombos(new ClientesModelo().read(), new ModelosModelo().read());
    }

    // RN-05: placas y número de serie no se repiten
    @Override
    protected void validar(Vehiculo v, boolean nuevo) throws DatoInvalidoException {
        for (Vehiculo otro : modelo.read()) {
            if (otro.getId() == v.getId()) {
                continue;
            }
            if (otro.getPlacas().equalsIgnoreCase(v.getPlacas())) {
                throw new DatoInvalidoException("Ya existe un vehículo con las placas " + v.getPlacas() + ".");
            }
            if (otro.getNumeroSerie().equalsIgnoreCase(v.getNumeroSerie())) {
                throw new DatoInvalidoException("Ya existe un vehículo con ese número de serie.");
            }
        }
    }

    @Override
    protected void copiarId(Vehiculo origen, Vehiculo destino) {
        destino.setId(origen.getId());
    }

    @Override
    protected String descripcion(Vehiculo v) {
        return v.getPlacas();
    }
}
