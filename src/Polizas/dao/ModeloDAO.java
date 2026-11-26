package Polizas.dao;

import Polizas.modelo.Marca;
import Polizas.modelo.Modelo;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// DAO de Modelo: alta, consulta, modificación y eliminación en la tabla modelo.
// Al leer, trae también la marca de cada modelo (JOIN).
public class ModeloDAO implements Dao<Modelo> {

    private static final String SELECT = "SELECT mo.idModelo, mo.nombreModelo, ma.idMarca, ma.nombreMarca "
            + "FROM modelo mo JOIN marca ma ON ma.idMarca = mo.idMarca ";

    @Override
    public void registrar(Modelo m) throws SQLException {
        m.setId(Conexion.insertar("INSERT INTO modelo (idMarca, nombreModelo) VALUES (?, ?)",
                m.getMarca().getId(), m.getNombre()));
    }

    @Override
    public void actualizar(Modelo m) throws SQLException {
        Conexion.ejecutar("UPDATE modelo SET idMarca = ?, nombreModelo = ? WHERE idModelo = ?",
                m.getMarca().getId(), m.getNombre(), m.getId());
    }

    // La base no deja borrar un modelo que ya usa algún vehículo.
    @Override
    public void eliminar(int id) throws SQLException {
        Conexion.ejecutar("DELETE FROM modelo WHERE idModelo = ?", id);
    }

    @Override
    public Modelo consultar(int id) throws SQLException {
        List<Modelo> lista = buscar(SELECT + "WHERE mo.idModelo = ?", id);
        return lista.isEmpty() ? null : lista.get(0);
    }

    @Override
    public List<Modelo> listar() throws SQLException {
        return buscar(SELECT + "ORDER BY ma.nombreMarca, mo.nombreModelo");
    }

    // Ejecuta el SELECT y convierte cada fila en un Modelo con su Marca.
    private List<Modelo> buscar(String sql, Object... datos) throws SQLException {
        List<Modelo> lista = new ArrayList<>();
        try (Connection cn = Conexion.conectar();
             PreparedStatement ps = Conexion.preparar(cn, sql, datos);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Marca marca = new Marca(rs.getInt("idMarca"), rs.getString("nombreMarca"));
                lista.add(new Modelo(rs.getInt("idModelo"), rs.getString("nombreModelo"), marca));
            }
        }
        return lista;
    }
}
