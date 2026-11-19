package Polizas.Marcas;

import Polizas.Entidades.Marca;
import Polizas.General.CrudControl;

// Controlador de marcas: une la vista con el modelo
public class MarcasControl extends CrudControl<Marca> {

    public MarcasControl(MarcasVista vista) {
        super(vista, new MarcasModelo(), "marca");
        iniciar();
    }

    @Override
    protected void copiarId(Marca origen, Marca destino) {
        destino.setId(origen.getId());
    }

    @Override
    protected String descripcion(Marca m) {
        return m.getNombre();
    }
}
