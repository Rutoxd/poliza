package Polizas.Coberturas;

import Polizas.Entidades.Cobertura;
import Polizas.General.CrudControl;

// Controlador del catálogo de coberturas
public class CoberturasControl extends CrudControl<Cobertura> {

    public CoberturasControl(CoberturasVista vista) {
        super(vista, new CoberturasModelo(), "cobertura");
        iniciar();
    }

    @Override
    protected void copiarId(Cobertura origen, Cobertura destino) {
        destino.setId(origen.getId());
    }

    @Override
    protected String descripcion(Cobertura c) {
        return c.getNombre();
    }
}
