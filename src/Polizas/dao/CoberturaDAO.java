package Polizas.dao;

import Polizas.modelo.Cobertura;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// DAO de Cobertura: alta, consulta, modificación y eliminación en la tabla cobertura.
public class CoberturaDAO implements Dao<Cobertura> {

    @Override
    public void registrar(Cobertura c) throws SQLException {
        c.setId(Conexion.insertar("INSERT INTO cobertura (nombre, descripcion) VALUES (?, ?)",
                c.getNombre(), c.getDescripcion()));
    }

    @Override
    public void actualizar(Cobertura c) throws SQLException {
        Conexion.ejecutar("UPDATE cobertura SET nombre = ?, descripcion = ? WHERE idCobertura = ?",
                c.getNombre(), c.getDescripcion(), c.getId());
    }

    // La base no deja borrar una cobertura que ya incluye alguna póliza.
    @Override
    public void eliminar(int id) throws SQLException {
        Conexion.ejecutar("DELETE FROM cobertura WHERE idCobertura = ?", id);
    }

    @Override
    public Cobertura consultar(int id) throws SQLException {
        List<Cobertura> lista = buscar("SELECT * FROM cobertura WHERE idCobertura = ?", id);
        return lista.isEmpty() ? null : lista.get(0);
    }

    @Override
    public List<Cobertura> listar() throws SQLException {
        return buscar("SELECT * FROM cobertura ORDER BY nombre");
    }

    // Coberturas que incluye una póliza (tabla intermedia polizaCobertura).
    public List<Cobertura> listarPorPoliza(int idPoliza) throws SQLException {
        return buscar("SELECT c.* FROM polizaCobertura pc JOIN cobertura c ON c.idCobertura = pc.idCobertura "
                + "WHERE pc.idPoliza = ? ORDER BY c.nombre", idPoliza);
    }

    // Ejecuta un SELECT de cobertura y convierte cada fila en un objeto Cobertura.
    private List<Cobertura> buscar(String sql, Object... datos) throws SQLException {
        List<Cobertura> lista = new ArrayList<>();
        try (Connection cn = Conexion.conectar();
             PreparedStatement ps = Conexion.preparar(cn, sql, datos);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Cobertura(rs.getInt("idCobertura"), rs.getString("nombre"), rs.getString("descripcion")));
            }
        }
        return lista;
    }
}
