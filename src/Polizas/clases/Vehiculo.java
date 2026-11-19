package Polizas.clases;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// Vehículo de un cliente. Marca y modelo se toman de la clase Modelo (corrección de Fase 3).
// Tabla: vehiculo.
public class Vehiculo {

    private int id;
    private Cliente cliente;
    private Modelo modelo;
    private int anio;
    private String placas;
    private String numeroSerie;

    // Constructor vacío: crea el objeto para después llenarlo con los métodos set.
    public Vehiculo() {
    }

    // Constructor con todos los datos (lo usa listar() al leer de la base).
    public Vehiculo(int id, Cliente cliente, Modelo modelo, int anio, String placas, String numeroSerie) {
        this.id = id;
        this.cliente = cliente;
        this.modelo = modelo;
        this.anio = anio;
        this.placas = placas;
        this.numeroSerie = numeroSerie;
    }

    // Guarda este registro nuevo en la tabla vehiculo.
    public void registrar() throws SQLException {
        Conexion.ejecutar("INSERT INTO vehiculo (idCliente, idModelo, anio, placas, numeroSerie) VALUES (?, ?, ?, ?, ?)",
                cliente.getId(), modelo.getId(), anio, placas, numeroSerie);
    }

    // Guarda los cambios de este registro en la tabla vehiculo (lo busca por su id).
    public void actualizar() throws SQLException {
        Conexion.ejecutar("UPDATE vehiculo SET idCliente = ?, idModelo = ?, anio = ?, placas = ?, numeroSerie = ? "
                + "WHERE idVehiculo = ?", cliente.getId(), modelo.getId(), anio, placas, numeroSerie, id);
    }

    // La base no deja borrar un vehículo que tiene pólizas o cotizaciones.
    public void eliminar() throws SQLException {
        Conexion.ejecutar("DELETE FROM vehiculo WHERE idVehiculo = ?", id);
    }

    // Regresa el registro con ese id, o null si no existe.
    public static Vehiculo consultar(int id) throws SQLException {
        for (Vehiculo v : listar()) {
            if (v.getId() == id) {
                return v;
            }
        }
        return null;
    }

    // Trae los vehículos con su cliente, modelo y marca (JOIN de cuatro tablas).
    public static List<Vehiculo> listar() throws SQLException {
        List<Vehiculo> lista = new ArrayList<>();
        String sql = "SELECT v.*, c.nombre, c.rfc, c.telefono, c.correo, c.domicilio, "
                + "mo.nombreModelo, ma.idMarca, ma.nombreMarca FROM vehiculo v "
                + "JOIN cliente c ON c.idCliente = v.idCliente "
                + "JOIN modelo mo ON mo.idModelo = v.idModelo "
                + "JOIN marca ma ON ma.idMarca = mo.idMarca ORDER BY v.placas";
        try (Connection cn = Conexion.conectar();
             PreparedStatement ps = cn.prepareStatement(sql);
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

    // RN-04: true si el vehículo ya tiene una póliza vigente o por vencer (sin cancelar).
    public boolean tienePolizaVigente() throws SQLException {
        String sql = "SELECT COUNT(*) FROM poliza p LEFT JOIN cancelacion ca ON ca.idPoliza = p.idPoliza "
                + "WHERE p.idVehiculo = ? AND p.fechaFin >= CURDATE() "
                + "AND (ca.estado IS NULL OR ca.estado <> 'BAJA_REGISTRADA')";
        try (Connection cn = Conexion.conectar(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
    }

    // Getters y setters: leen y cambian los atributos privados (encapsulamiento).
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }
    public Modelo getModelo() { return modelo; }
    public void setModelo(Modelo modelo) { this.modelo = modelo; }
    public int getAnio() { return anio; }
    public void setAnio(int anio) { this.anio = anio; }
    public String getPlacas() { return placas; }
    public void setPlacas(String placas) { this.placas = placas; }
    public String getNumeroSerie() { return numeroSerie; }
    public void setNumeroSerie(String numeroSerie) { this.numeroSerie = numeroSerie; }

    @Override
    public String toString() {
        return placas + " - " + modelo + " " + anio;
    }
}
