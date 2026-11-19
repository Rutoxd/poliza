package Polizas.Marcas;

import Polizas.Entidades.Marca;
import Polizas.General.CrudTM;

// Modelo de la tabla de marcas
public class MarcasTM extends CrudTM<Marca> {

    public MarcasTM() {
        super("Id", "Marca");
    }

    @Override
    protected Object valor(Marca m, int columna) {
        return columna == 0 ? m.getId() : m.getNombre();
    }
}
