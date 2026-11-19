package Polizas.Aseguradoras;

import Polizas.Entidades.Aseguradora;
import Polizas.General.CrudControl;

// Controlador del catálogo de aseguradoras
public class AseguradorasControl extends CrudControl<Aseguradora> {

    public AseguradorasControl(AseguradorasVista vista) {
        super(vista, new AseguradorasModelo(), "aseguradora");
        iniciar();
    }

    @Override
    protected void copiarId(Aseguradora origen, Aseguradora destino) {
        destino.setId(origen.getId());
    }

    @Override
    protected String descripcion(Aseguradora a) {
        return a.getNombre();
    }
}
