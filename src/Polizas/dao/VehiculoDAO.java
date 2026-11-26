package Polizas.dao;

import Polizas.modelo.Cliente;
import Polizas.modelo.Marca;
import Polizas.modelo.Modelo;
import Polizas.modelo.Vehiculo;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// DAO de Vehiculo (Módulo 2): alta, consulta, modificación y eliminación en la tabla vehiculo.
// Al leer, trae también el cliente, el modelo y la marca (JOIN de cuatro tablas).
public class VehiculoDAO implements Dao<Vehiculo> {

    private static final String SELECT = "SELECT v.*, c.nombre, c.rfc, c.telefono, c.correo, c.domicilio, "
            + "mo.nombreModelo, ma.idMarca, ma.nombreMarca FROM vehiculo v "
            + "JOIN cliente c ON c.idCliente = v.idCliente "
            + "JOIN modelo mo ON mo.idModelo = v.idModelo "
            + "JOIN marca ma ON ma.idMarca = mo.idMarca ";

    @Override
    public void registrar(Vehiculo v) throws SQLException {
        v.setId(Conexion.insertar("INSERT INTO vehiculo (idCliente, idModelo, anio, placas, numeroSerie) VALUES (?, ?, ?, ?, ?)",
                v.getCliente().getId(), v.getModelo().getId(), v.getAnio(), v.getPlacas(), v.getNumeroSerie()));
    }

    @Override
    public void actualizar(Vehiculo v) throws SQLException {
        Conexion.ejecutar("UPDATE vehiculo SET idCliente = ?, idModelo = ?, anio = ?, placas = ?, numeroSerie = ? "
                + "WHERE idVehiculo = ?", v.getCliente().getId(), v.getModelo().getId(), v.getAnio(),
                v.getPlacas(), v.getNumeroSerie(), v.getId());
    }

    // RN-19: la base no deja borrar un vehículo que tiene pólizas o cotizaciones.
    @Override
    public void eliminar(int id) throws SQLException {
        Conexion.ejecutar("DELETE FROM vehiculo WHERE idVehiculo = ?", id);
    }

    @Override
    public Vehiculo consultar(int id) throws SQLException {
        List<Vehiculo> lista = buscar(SELECT + "WHERE v.idVehiculo = ?", id);
        return lista.isEmpty() ? null : lista.get(0);
    }

    @Override
    public List<Vehiculo> listar() throws SQLException {
        return buscar(SELECT + "ORDER BY v.placas");
    }

    // RF-06: vehículos de un cliente.
    public List<Vehiculo> listarPorCliente(int idCliente) throws SQLException {
        return buscar(SELECT + "WHERE v.idCliente = ? ORDER BY v.placas", idCliente);
    }

    // RN-04: true si el vehículo ya tiene una póliza sin cancelar cuya vigencia se cruza con
    // las fechas dadas. idPolizaExcluida sirve al actualizar, para no compararla consigo misma.
    public boolean tienePolizaVigente(Connection cn, int idVehiculo, java.time.LocalDate inicio,
            java.time.LocalDate fin, int idPolizaExcluida) throws SQLException {
        String sql = "SELECT COUNT(*) FROM poliza p LEFT JOIN cancelacion ca ON ca.idPoliza = p.idPoliza "
                + "WHERE p.idVehiculo = ? AND p.idPoliza <> ? AND p.fechaInicio < ? AND p.fechaFin > ? "
                + "AND (ca.estado IS NULL OR ca.estado <> 'BAJA_REGISTRADA')";
        try (PreparedStatement ps = Conexion.preparar(cn, sql, idVehiculo, idPolizaExcluida, fin, inicio);
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getInt(1) > 0;
        }
    }

    // Ejecuta el SELECT y convierte cada fila en un Vehiculo con su Cliente, Modelo y Marca.
    private List<Vehiculo> buscar(String sql, Object... datos) throws SQLException {
        List<Vehiculo> lista = new ArrayList<>();
        try (Connection cn = Conexion.conectar();
             PreparedStatement ps = Conexion.preparar(cn, sql, datos);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Cliente cliente = new Cliente(rs.getInt("idCliente"), rs.getString("nombre"), rs.getString("telefono"),
                        rs.getString("correo"), rs.getString("rfc"), rs.getString("domicilio"));
                Modelo modelo = new Modelo(rs.getInt("idModelo"), rs.getString("nombreModelo"),
                        new Marca(rs.getInt("idMarca"), rs.getString("nombreMarca")));
                lista.add(new Vehiculo(rs.getInt("idVehiculo"), cliente, modelo, rs.getInt("anio"),
                        rs.getString("placas"), rs.getString("numeroSerie")));
            }
        }
        return lista;
    }
}
