package Polizas.Coberturas;

import Polizas.Entidades.Cobertura;
import Polizas.General.Conexion;
import Polizas.General.DAO;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// Modelo: CRUD de la tabla cobertura (RF-25)
public class CoberturasModelo extends Conexion implements DAO<Cobertura> {

    @Override
    public List<Cobertura> read() {
        List<Cobertura> lista = new ArrayList<>();
        error = "";
        try {
            conectar();
            if (cnx == null) {
                return lista;
            }
            ps = cnx.prepareStatement("SELECT idCobertura, nombre, descripcion FROM cobertura ORDER BY nombre");
            rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(new Cobertura(rs.getInt("idCobertura"), rs.getString("nombre"), rs.getString("descripcion")));
            }
        } catch (SQLException ex) {
            registrarError(ex);
        } finally {
            close();
        }
        return lista;
    }

    @Override
    public int create(Cobertura c) {
        return ejecutar("INSERT INTO cobertura (nombre, descripcion) VALUES (?, ?)", c.getNombre(), nulo(c.getDescripcion()));
    }

    @Override
    public int update(Cobertura c) {
        return ejecutar("UPDATE cobertura SET nombre = ?, descripcion = ? WHERE idCobertura = ?",
                c.getNombre(), nulo(c.getDescripcion()), c.getId());
    }

    @Override
    public int delete(Cobertura c) {
        return ejecutar("DELETE FROM cobertura WHERE idCobertura = ?", c.getId());
    }
}
