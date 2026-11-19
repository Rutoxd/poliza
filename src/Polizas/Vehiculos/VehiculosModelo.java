package Polizas.Vehiculos;

import Polizas.Entidades.Cliente;
import Polizas.Entidades.Marca;
import Polizas.Entidades.Modelo;
import Polizas.Entidades.Vehiculo;
import Polizas.General.Conexion;
import Polizas.General.DAO;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// Modelo: CRUD de la tabla vehiculo, con su cliente y su modelo (RF-05 a RF-08)
public class VehiculosModelo extends Conexion implements DAO<Vehiculo> {

    @Override
    public List<Vehiculo> read() {
        List<Vehiculo> lista = new ArrayList<>();
        error = "";
        try {
            conectar();
            if (cnx == null) {
                return lista;
            }
            ps = cnx.prepareStatement(
                    "SELECT v.idVehiculo, v.anio, v.placas, v.numeroSerie, "
                    + "c.idCliente, c.nombre, c.rfc, mo.idModelo, mo.nombreModelo, ma.idMarca, ma.nombreMarca "
                    + "FROM vehiculo v "
                    + "JOIN cliente c ON c.idCliente = v.idCliente "
                    + "JOIN modelo mo ON mo.idModelo = v.idModelo "
                    + "JOIN marca ma ON ma.idMarca = mo.idMarca "
                    + "ORDER BY c.nombre, v.placas");
            rs = ps.executeQuery();
            while (rs.next()) {
                Cliente cliente = new Cliente(rs.getInt("idCliente"), rs.getString("nombre"), rs.getString("rfc"),
                        null, null, null);
                Modelo modelo = new Modelo(rs.getInt("idModelo"),
                        new Marca(rs.getInt("idMarca"), rs.getString("nombreMarca")), rs.getString("nombreModelo"));
                lista.add(new Vehiculo(rs.getInt("idVehiculo"), cliente, modelo, rs.getInt("anio"),
                        rs.getString("placas"), rs.getString("numeroSerie")));
            }
        } catch (SQLException ex) {
            registrarError(ex);
        } finally {
            close();
        }
        return lista;
    }

    @Override
    public int create(Vehiculo v) {
        return ejecutar("INSERT INTO vehiculo (idCliente, idModelo, anio, placas, numeroSerie) VALUES (?, ?, ?, ?, ?)",
                v.getCliente().getId(), v.getModelo().getId(), v.getAnio(),
                v.getPlacas().toUpperCase(), v.getNumeroSerie().toUpperCase());
    }

    @Override
    public int update(Vehiculo v) {
        return ejecutar("UPDATE vehiculo SET idCliente = ?, idModelo = ?, anio = ?, placas = ?, numeroSerie = ? "
                + "WHERE idVehiculo = ?",
                v.getCliente().getId(), v.getModelo().getId(), v.getAnio(),
                v.getPlacas().toUpperCase(), v.getNumeroSerie().toUpperCase(), v.getId());
    }

    // RN-19: la llave foránea de poliza impide borrar vehículos con pólizas
    @Override
    public int delete(Vehiculo v) {
        return ejecutar("DELETE FROM vehiculo WHERE idVehiculo = ?", v.getId());
    }
}
