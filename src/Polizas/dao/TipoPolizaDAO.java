package Polizas.dao;

import Polizas.modelo.TipoPoliza;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// DAO de TipoPoliza: alta, consulta, modificación y eliminación en la tabla tipoPoliza.
// Los tipos en uso no se borran: se desactivan con desactivar().
public class TipoPolizaDAO implements Dao<TipoPoliza> {

    @Override
    public void registrar(TipoPoliza t) throws SQLException {
        t.setId(Conexion.insertar("INSERT INTO tipoPoliza (nombre, descripcion, activo) VALUES (?, ?, ?)",
                t.getNombre(), t.getDescripcion(), t.isActivo()));
    }

    @Override
    public void actualizar(TipoPoliza t) throws SQLException {
        Conexion.ejecutar("UPDATE tipoPoliza SET nombre = ?, descripcion = ?, activo = ? WHERE idTipoPoliza = ?",
                t.getNombre(), t.getDescripcion(), t.isActivo(), t.getId());
    }

    // La base no deja borrar un tipo que ya usan cotizaciones o pólizas; para esos, desactivar().
    @Override
    public void eliminar(int id) throws SQLException {
        Conexion.ejecutar("DELETE FROM tipoPoliza WHERE idTipoPoliza = ?", id);
    }

    // Deja el tipo fuera de uso sin borrarlo (las pólizas anteriores lo siguen usando).
    public void desactivar(int id) throws SQLException {
        Conexion.ejecutar("UPDATE tipoPoliza SET activo = FALSE WHERE idTipoPoliza = ?", id);
    }

    @Override
    public TipoPoliza consultar(int id) throws SQLException {
        List<TipoPoliza> lista = buscar("SELECT * FROM tipoPoliza WHERE idTipoPoliza = ?", id);
        return lista.isEmpty() ? null : lista.get(0);
    }

    @Override
    public List<TipoPoliza> listar() throws SQLException {
        return buscar("SELECT * FROM tipoPoliza ORDER BY nombre");
    }

    // Solo los tipos activos (para elegir al registrar una póliza).
    public List<TipoPoliza> listarActivos() throws SQLException {
        return buscar("SELECT * FROM tipoPoliza WHERE activo = TRUE ORDER BY nombre");
    }

    // Ejecuta un SELECT de tipoPoliza y convierte cada fila en un objeto TipoPoliza.
    private List<TipoPoliza> buscar(String sql, Object... datos) throws SQLException {
        List<TipoPoliza> lista = new ArrayList<>();
        try (Connection cn = Conexion.conectar();
             PreparedStatement ps = Conexion.preparar(cn, sql, datos);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new TipoPoliza(rs.getInt("idTipoPoliza"), rs.getString("nombre"),
                        rs.getString("descripcion"), rs.getBoolean("activo")));
            }
        }
        return lista;
    }
}
