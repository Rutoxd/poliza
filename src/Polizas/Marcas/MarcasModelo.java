package Polizas.Marcas;

import Polizas.Entidades.Marca;
import Polizas.General.Conexion;
import Polizas.General.DAO;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// Modelo: CRUD de la tabla marca (hereda Conexion, implementa DAO)
public class MarcasModelo extends Conexion implements DAO<Marca> {

    @Override
    public List<Marca> read() {
        List<Marca> lista = new ArrayList<>();
        error = "";
        try {
            conectar();
            if (cnx == null) {
                return lista;
            }
            ps = cnx.prepareStatement("SELECT idMarca, nombreMarca FROM marca ORDER BY nombreMarca");
            rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(new Marca(rs.getInt("idMarca"), rs.getString("nombreMarca")));
            }
        } catch (SQLException ex) {
            registrarError(ex);
        } finally {
            close();
        }
        return lista;
    }

    @Override
    public int create(Marca m) {
        return ejecutar("INSERT INTO marca (nombreMarca) VALUES (?)", m.getNombre());
    }

    @Override
    public int update(Marca m) {
        return ejecutar("UPDATE marca SET nombreMarca = ? WHERE idMarca = ?", m.getNombre(), m.getId());
    }

    @Override
    public int delete(Marca m) {
        return ejecutar("DELETE FROM marca WHERE idMarca = ?", m.getId());
    }
}
