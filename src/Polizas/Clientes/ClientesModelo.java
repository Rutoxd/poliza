package Polizas.Clientes;

import Polizas.Entidades.Cliente;
import Polizas.General.Conexion;
import Polizas.General.DAO;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// Modelo: CRUD de la tabla cliente (RF-01 a RF-04)
public class ClientesModelo extends Conexion implements DAO<Cliente> {

    @Override
    public List<Cliente> read() {
        List<Cliente> lista = new ArrayList<>();
        error = "";
        try {
            conectar();
            if (cnx == null) {
                return lista;
            }
            ps = cnx.prepareStatement(
                    "SELECT idCliente, nombre, rfc, telefono, correo, domicilio FROM cliente ORDER BY nombre");
            rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(new Cliente(rs.getInt("idCliente"), rs.getString("nombre"), rs.getString("rfc"),
                        rs.getString("telefono"), rs.getString("correo"), rs.getString("domicilio")));
            }
        } catch (SQLException ex) {
            registrarError(ex);
        } finally {
            close();
        }
        return lista;
    }

    @Override
    public int create(Cliente c) {
        return ejecutar("INSERT INTO cliente (nombre, rfc, telefono, correo, domicilio) VALUES (?, ?, ?, ?, ?)",
                c.getNombre(), c.getRfc().toUpperCase(), nulo(c.getTelefono()), nulo(c.getCorreo()), nulo(c.getDomicilio()));
    }

    @Override
    public int update(Cliente c) {
        return ejecutar("UPDATE cliente SET nombre = ?, rfc = ?, telefono = ?, correo = ?, domicilio = ? WHERE idCliente = ?",
                c.getNombre(), c.getRfc().toUpperCase(), nulo(c.getTelefono()), nulo(c.getCorreo()),
                nulo(c.getDomicilio()), c.getId());
    }

    // RN-19: la llave foránea de vehiculo impide borrar clientes con vehículos registrados
    @Override
    public int delete(Cliente c) {
        return ejecutar("DELETE FROM cliente WHERE idCliente = ?", c.getId());
    }
}
