package Polizas.TiposPoliza;

import Polizas.Entidades.TipoPoliza;
import Polizas.General.CrudControl;

// Controlador del catálogo de tipos de póliza
public class TiposPolizaControl extends CrudControl<TipoPoliza> {

    public TiposPolizaControl(TiposPolizaVista vista) {
        super(vista, new TiposPolizaModelo(), "tipo de póliza");
        iniciar();
    }

    @Override
    protected void copiarId(TipoPoliza origen, TipoPoliza destino) {
        destino.setId(origen.getId());
    }

    @Override
    protected String descripcion(TipoPoliza t) {
        return t.getNombre();
    }
}
