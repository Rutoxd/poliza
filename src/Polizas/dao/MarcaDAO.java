package Polizas.dao;

import Polizas.modelo.Marca;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// DAO de Marca: alta, consulta, modificación y eliminación en la tabla marca.
public class MarcaDAO implements Dao<Marca> {

    @Override
    public void registrar(Marca m) throws SQLException {
        m.setId(Conexion.insertar("INSERT INTO marca (nombreMarca) VALUES (?)", m.getNombre()));
    }

    @Override
    public void actualizar(Marca m) throws SQLException {
        Conexion.ejecutar("UPDATE marca SET nombreMarca = ? WHERE idMarca = ?", m.getNombre(), m.getId());
    }

    // La base no deja borrar una marca que ya tiene modelos.
    @Override
    public void eliminar(int id) throws SQLException {
        Conexion.ejecutar("DELETE FROM marca WHERE idMarca = ?", id);
    }

    @Override
    public Marca consultar(int id) throws SQLException {
        List<Marca> lista = buscar("SELECT * FROM marca WHERE idMarca = ?", id);
        return lista.isEmpty() ? null : lista.get(0);
    }

    @Override
    public List<Marca> listar() throws SQLException {
        return buscar("SELECT * FROM marca ORDER BY nombreMarca");
    }

    // Ejecuta un SELECT de marca y convierte cada fila en un objeto Marca.
    private List<Marca> buscar(String sql, Object... datos) throws SQLException {
        List<Marca> lista = new ArrayList<>();
        try (Connection cn = Conexion.conectar();
             PreparedStatement ps = Conexion.preparar(cn, sql, datos);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Marca(rs.getInt("idMarca"), rs.getString("nombreMarca")));
            }
        }
        return lista;
    }
}
