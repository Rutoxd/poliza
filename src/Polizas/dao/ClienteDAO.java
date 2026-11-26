package Polizas.dao;

import Polizas.modelo.Cliente;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// DAO de Cliente (Módulo 1): alta, consulta, modificación y eliminación en la tabla cliente.
public class ClienteDAO implements Dao<Cliente> {

    @Override
    public void registrar(Cliente c) throws SQLException {
        c.setId(Conexion.insertar("INSERT INTO cliente (nombre, rfc, telefono, correo, domicilio) VALUES (?, ?, ?, ?, ?)",
                c.getNombre(), c.getRfc(), c.getTelefono(), c.getCorreo(), c.getDomicilio()));
    }

    @Override
    public void actualizar(Cliente c) throws SQLException {
        Conexion.ejecutar("UPDATE cliente SET nombre = ?, rfc = ?, telefono = ?, correo = ?, domicilio = ? WHERE idCliente = ?",
                c.getNombre(), c.getRfc(), c.getTelefono(), c.getCorreo(), c.getDomicilio(), c.getId());
    }

    // RN-19: la base no deja borrar un cliente que tiene vehículos registrados.
    @Override
    public void eliminar(int id) throws SQLException {
        Conexion.ejecutar("DELETE FROM cliente WHERE idCliente = ?", id);
    }

    @Override
    public Cliente consultar(int id) throws SQLException {
        List<Cliente> lista = buscar("SELECT * FROM cliente WHERE idCliente = ?", id);
        return lista.isEmpty() ? null : lista.get(0);
    }

    @Override
    public List<Cliente> listar() throws SQLException {
        return buscar("SELECT * FROM cliente ORDER BY nombre");
    }

    // RF-02: busca clientes cuyo nombre o RFC contenga el texto.
    public List<Cliente> buscarPorNombreORfc(String texto) throws SQLException {
        String patron = "%" + texto + "%";
        return buscar("SELECT * FROM cliente WHERE nombre LIKE ? OR rfc LIKE ? ORDER BY nombre", patron, patron);
    }

    // Ejecuta un SELECT de cliente y convierte cada fila en un objeto Cliente.
    private List<Cliente> buscar(String sql, Object... datos) throws SQLException {
        List<Cliente> lista = new ArrayList<>();
        try (Connection cn = Conexion.conectar();
             PreparedStatement ps = Conexion.preparar(cn, sql, datos);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Cliente(rs.getInt("idCliente"), rs.getString("nombre"), rs.getString("telefono"),
                        rs.getString("correo"), rs.getString("rfc"), rs.getString("domicilio")));
            }
        }
        return lista;
    }
}
