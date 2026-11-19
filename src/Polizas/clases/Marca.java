package Polizas.clases;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// Marca de vehículo (Nissan, Toyota...). Clase agregada en la Fase 3 al normalizar.
// Tabla: marca.
public class Marca {

    private int id;
    private String nombre;

    // Constructor vacío: crea el objeto para después llenarlo con los métodos set.
    public Marca() {
    }

    // Constructor con todos los datos (lo usa listar() al leer de la base).
    public Marca(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    // Guarda este registro nuevo en la tabla marca.
    public void registrar() throws SQLException {
        Conexion.ejecutar("INSERT INTO marca (nombreMarca) VALUES (?)", nombre);
    }

    // Guarda los cambios de este registro en la tabla marca (lo busca por su id).
    public void actualizar() throws SQLException {
        Conexion.ejecutar("UPDATE marca SET nombreMarca = ? WHERE idMarca = ?", nombre, id);
    }

    // La base no deja borrar una marca que ya tiene modelos.
    public void eliminar() throws SQLException {
        Conexion.ejecutar("DELETE FROM marca WHERE idMarca = ?", id);
    }

    // Regresa el registro con ese id, o null si no existe.
    public static Marca consultar(int id) throws SQLException {
        for (Marca m : listar()) {
            if (m.getId() == id) {
                return m;
            }
        }
        return null;
    }

    // Regresa todos los registros de la tabla, para llenar las tablas y listas de las pantallas.
    public static List<Marca> listar() throws SQLException {
        List<Marca> lista = new ArrayList<>();
        try (Connection cn = Conexion.conectar();
             PreparedStatement ps = cn.prepareStatement("SELECT * FROM marca ORDER BY nombreMarca");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Marca(rs.getInt("idMarca"), rs.getString("nombreMarca")));
            }
        }
        return lista;
    }

    // Getters y setters: leen y cambian los atributos privados (encapsulamiento).
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    @Override
    public String toString() {
        return nombre;
    }
}
