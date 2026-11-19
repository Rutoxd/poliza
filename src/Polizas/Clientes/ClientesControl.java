package Polizas.Clientes;

import Polizas.Entidades.Cliente;
import Polizas.General.CrudControl;
import Polizas.General.DatoInvalidoException;

// Controlador de clientes
public class ClientesControl extends CrudControl<Cliente> {

    public ClientesControl(ClientesVista vista) {
        super(vista, new ClientesModelo(), "cliente");
        iniciar();
    }

    // RN-05: el RFC no se repite (la base también lo impide con uq_cliente_rfc)
    @Override
    protected void validar(Cliente c, boolean nuevo) throws DatoInvalidoException {
        for (Cliente otro : modelo.read()) {
            if (otro.getRfc().equalsIgnoreCase(c.getRfc()) && otro.getId() != c.getId()) {
                throw new DatoInvalidoException("Ya existe un cliente con el RFC " + c.getRfc() + ".");
            }
        }
    }

    @Override
    protected void copiarId(Cliente origen, Cliente destino) {
        destino.setId(origen.getId());
    }

    @Override
    protected String descripcion(Cliente c) {
        return c.getNombre();
    }
}
