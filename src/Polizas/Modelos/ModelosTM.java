package Polizas.Modelos;

import Polizas.Entidades.Modelo;
import Polizas.General.CrudTM;

// Modelo de la tabla de modelos de vehículo
public class ModelosTM extends CrudTM<Modelo> {

    public ModelosTM() {
        super("Id", "Marca", "Modelo");
    }

    @Override
    protected Object valor(Modelo m, int columna) {
        switch (columna) {
            case 0: return m.getId();
            case 1: return m.getMarca().getNombre();
            default: return m.getNombre();
        }
    }
}
