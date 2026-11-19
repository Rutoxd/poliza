package Polizas.Vehiculos;

import Polizas.Entidades.Vehiculo;
import Polizas.General.CrudTM;

// Modelo de la tabla de vehículos
public class VehiculosTM extends CrudTM<Vehiculo> {

    public VehiculosTM() {
        super("Id", "Cliente", "Marca", "Modelo", "Año", "Placas", "Núm. de serie");
    }

    @Override
    protected Object valor(Vehiculo v, int columna) {
        switch (columna) {
            case 0: return v.getId();
            case 1: return v.getCliente().getNombre();
            case 2: return v.getModelo().getMarca().getNombre();
            case 3: return v.getModelo().getNombre();
            case 4: return v.getAnio();
            case 5: return v.getPlacas();
            default: return v.getNumeroSerie();
        }
    }
}
