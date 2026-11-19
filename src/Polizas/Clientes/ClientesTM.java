package Polizas.Clientes;

import Polizas.Entidades.Cliente;
import Polizas.General.CrudTM;

// Modelo de la tabla de clientes
public class ClientesTM extends CrudTM<Cliente> {

    public ClientesTM() {
        super("Id", "Nombre", "RFC", "Teléfono", "Correo", "Domicilio");
    }

    @Override
    protected Object valor(Cliente c, int columna) {
        switch (columna) {
            case 0: return c.getId();
            case 1: return c.getNombre();
            case 2: return c.getRfc();
            case 3: return c.getTelefono();
            case 4: return c.getCorreo();
            default: return c.getDomicilio();
        }
    }
}
