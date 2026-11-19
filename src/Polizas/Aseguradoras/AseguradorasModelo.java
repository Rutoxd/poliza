package Polizas.Aseguradoras;

import Polizas.Entidades.Aseguradora;
import Polizas.General.Conexion;
import Polizas.General.DAO;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// Modelo: CRUD de la tabla aseguradora (RF-24)
public class AseguradorasModelo extends Conexion implements DAO<Aseguradora> {

    @Override
    public List<Aseguradora> read() {
        List<Aseguradora> lista = new ArrayList<>();
        error = "";
        try {
            conectar();
            if (cnx == null) {
                return lista;
            }
            ps = cnx.prepareStatement("SELECT idAseguradora, nombre, telefono, correo FROM aseguradora ORDER BY nombre");
            rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(new Aseguradora(rs.getInt("idAseguradora"), rs.getString("nombre"),
                        rs.getString("telefono"), rs.getString("correo")));
            }
        } catch (SQLException ex) {
            registrarError(ex);
        } finally {
            close();
        }
        return lista;
    }

    @Override
    public int create(Aseguradora a) {
        return ejecutar("INSERT INTO aseguradora (nombre, telefono, correo) VALUES (?, ?, ?)",
                a.getNombre(), nulo(a.getTelefono()), nulo(a.getCorreo()));
    }

    @Override
    public int update(Aseguradora a) {
        return ejecutar("UPDATE aseguradora SET nombre = ?, telefono = ?, correo = ? WHERE idAseguradora = ?",
                a.getNombre(), nulo(a.getTelefono()), nulo(a.getCorreo()), a.getId());
    }

    @Override
    public int delete(Aseguradora a) {
        return ejecutar("DELETE FROM aseguradora WHERE idAseguradora = ?", a.getId());
    }
}
