package Polizas.dao;

import Polizas.modelo.Aseguradora;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// DAO de Aseguradora: alta, consulta, modificación y eliminación en la tabla aseguradora.
public class AseguradoraDAO implements Dao<Aseguradora> {

    @Override
    public void registrar(Aseguradora a) throws SQLException {
        a.setId(Conexion.insertar("INSERT INTO aseguradora (nombre, telefono, correo) VALUES (?, ?, ?)",
                a.getNombre(), a.getTelefono(), a.getCorreo()));
    }

    @Override
    public void actualizar(Aseguradora a) throws SQLException {
        Conexion.ejecutar("UPDATE aseguradora SET nombre = ?, telefono = ?, correo = ? WHERE idAseguradora = ?",
                a.getNombre(), a.getTelefono(), a.getCorreo(), a.getId());
    }

    // La base no deja borrar una aseguradora que tiene cotizaciones o pólizas.
    @Override
    public void eliminar(int id) throws SQLException {
        Conexion.ejecutar("DELETE FROM aseguradora WHERE idAseguradora = ?", id);
    }

    @Override
    public Aseguradora consultar(int id) throws SQLException {
        List<Aseguradora> lista = buscar("SELECT * FROM aseguradora WHERE idAseguradora = ?", id);
        return lista.isEmpty() ? null : lista.get(0);
    }

    @Override
    public List<Aseguradora> listar() throws SQLException {
        return buscar("SELECT * FROM aseguradora ORDER BY nombre");
    }

    // Ejecuta un SELECT de aseguradora y convierte cada fila en un objeto Aseguradora.
    private List<Aseguradora> buscar(String sql, Object... datos) throws SQLException {
        List<Aseguradora> lista = new ArrayList<>();
        try (Connection cn = Conexion.conectar();
             PreparedStatement ps = Conexion.preparar(cn, sql, datos);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Aseguradora(rs.getInt("idAseguradora"), rs.getString("nombre"),
                        rs.getString("telefono"), rs.getString("correo")));
            }
        }
        return lista;
    }
}
