package Polizas.TiposPoliza;

import Polizas.Entidades.TipoPoliza;
import Polizas.General.CrudTM;

// Modelo de la tabla de tipos de póliza
public class TiposPolizaTM extends CrudTM<TipoPoliza> {

    public TiposPolizaTM() {
        super("Id", "Nombre", "Descripción", "Activo");
    }

    @Override
    protected Object valor(TipoPoliza t, int columna) {
        switch (columna) {
            case 0: return t.getId();
            case 1: return t.getNombre();
            case 2: return t.getDescripcion();
            default: return t.isActivo() ? "Sí" : "No";
        }
    }
}
