package Polizas.TiposPoliza;

import Polizas.Entidades.TipoPoliza;
import Polizas.General.Conexion;
import Polizas.General.DAO;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// Modelo: CRUD de la tabla tipoPoliza (RF-25)
public class TiposPolizaModelo extends Conexion implements DAO<TipoPoliza> {

    @Override
    public List<TipoPoliza> read() {
        List<TipoPoliza> lista = new ArrayList<>();
        error = "";
        try {
            conectar();
            if (cnx == null) {
                return lista;
            }
            ps = cnx.prepareStatement("SELECT idTipoPoliza, nombre, descripcion, activo FROM tipoPoliza ORDER BY nombre");
            rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(new TipoPoliza(rs.getInt("idTipoPoliza"), rs.getString("nombre"),
                        rs.getString("descripcion"), rs.getBoolean("activo")));
            }
        } catch (SQLException ex) {
            registrarError(ex);
        } finally {
            close();
        }
        return lista;
    }

    @Override
    public int create(TipoPoliza t) {
        return ejecutar("INSERT INTO tipoPoliza (nombre, descripcion, activo) VALUES (?, ?, ?)",
                t.getNombre(), nulo(t.getDescripcion()), t.isActivo());
    }

    @Override
    public int update(TipoPoliza t) {
        return ejecutar("UPDATE tipoPoliza SET nombre = ?, descripcion = ?, activo = ? WHERE idTipoPoliza = ?",
                t.getNombre(), nulo(t.getDescripcion()), t.isActivo(), t.getId());
    }

    // Un tipo ya usado en pólizas no se borra: se desactiva desde Actualizar
    @Override
    public int delete(TipoPoliza t) {
        return ejecutar("DELETE FROM tipoPoliza WHERE idTipoPoliza = ?", t.getId());
    }
}
