package Polizas.Modelos;

import Polizas.Entidades.Marca;
import Polizas.Entidades.Modelo;
import Polizas.General.Conexion;
import Polizas.General.DAO;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// Modelo: CRUD de la tabla modelo, con su marca
public class ModelosModelo extends Conexion implements DAO<Modelo> {

    @Override
    public List<Modelo> read() {
        List<Modelo> lista = new ArrayList<>();
        error = "";
        try {
            conectar();
            if (cnx == null) {
                return lista;
            }
            ps = cnx.prepareStatement(
                    "SELECT mo.idModelo, mo.nombreModelo, ma.idMarca, ma.nombreMarca "
                    + "FROM modelo mo JOIN marca ma ON ma.idMarca = mo.idMarca "
                    + "ORDER BY ma.nombreMarca, mo.nombreModelo");
            rs = ps.executeQuery();
            while (rs.next()) {
                Marca marca = new Marca(rs.getInt("idMarca"), rs.getString("nombreMarca"));
                lista.add(new Modelo(rs.getInt("idModelo"), marca, rs.getString("nombreModelo")));
            }
        } catch (SQLException ex) {
            registrarError(ex);
        } finally {
            close();
        }
        return lista;
    }

    @Override
    public int create(Modelo m) {
        return ejecutar("INSERT INTO modelo (idMarca, nombreModelo) VALUES (?, ?)",
                m.getMarca().getId(), m.getNombre());
    }

    @Override
    public int update(Modelo m) {
        return ejecutar("UPDATE modelo SET idMarca = ?, nombreModelo = ? WHERE idModelo = ?",
                m.getMarca().getId(), m.getNombre(), m.getId());
    }

    @Override
    public int delete(Modelo m) {
        return ejecutar("DELETE FROM modelo WHERE idModelo = ?", m.getId());
    }
}
