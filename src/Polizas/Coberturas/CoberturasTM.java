package Polizas.Coberturas;

import Polizas.Entidades.Cobertura;
import Polizas.General.CrudTM;

// Modelo de la tabla de coberturas
public class CoberturasTM extends CrudTM<Cobertura> {

    public CoberturasTM() {
        super("Id", "Nombre", "Descripción");
    }

    @Override
    protected Object valor(Cobertura c, int columna) {
        switch (columna) {
            case 0: return c.getId();
            case 1: return c.getNombre();
            default: return c.getDescripcion();
        }
    }
}
