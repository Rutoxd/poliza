package Polizas.Modelos;

import Polizas.Entidades.Modelo;
import Polizas.General.CrudControl;
import Polizas.Marcas.MarcasModelo;

// Controlador de modelos: además llena el combo de marcas
public class ModelosControl extends CrudControl<Modelo> {

    private final ModelosVista mv;

    public ModelosControl(ModelosVista vista) {
        super(vista, new ModelosModelo(), "modelo");
        this.mv = vista;
        iniciar();
    }

    @Override
    protected void cargarCatalogos() {
        mv.llenarMarcas(new MarcasModelo().read());
    }

    @Override
    protected void copiarId(Modelo origen, Modelo destino) {
        destino.setId(origen.getId());
    }

    @Override
    protected String descripcion(Modelo m) {
        return m.toString();
    }
}
