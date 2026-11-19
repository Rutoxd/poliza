package Polizas.Aseguradoras;

import Polizas.Entidades.Aseguradora;
import Polizas.General.CrudTM;

// Modelo de la tabla de aseguradoras
public class AseguradorasTM extends CrudTM<Aseguradora> {

    public AseguradorasTM() {
        super("Id", "Nombre", "Teléfono", "Correo");
    }

    @Override
    protected Object valor(Aseguradora a, int columna) {
        switch (columna) {
            case 0: return a.getId();
            case 1: return a.getNombre();
            case 2: return a.getTelefono();
            default: return a.getCorreo();
        }
    }
}
