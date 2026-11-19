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

    public Marca() {
    }

    public Marca(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public void registrar() throws SQLException {
        Conexion.ejecutar("INSERT INTO marca (nombreMarca) VALUES (?)", nombre);
    }

    public void actualizar() throws SQLException {
        Conexion.ejecutar("UPDATE marca SET nombreMarca = ? WHERE idMarca = ?", nombre, id);
    }

    // La base no deja borrar una marca que ya tiene modelos.
    public void eliminar() throws SQLException {
        Conexion.ejecutar("DELETE FROM marca WHERE idMarca = ?", id);
    }

    public static Marca consultar(int id) throws SQLException {
        for (Marca m : listar()) {
            if (m.getId() == id) {
                return m;
            }
        }
        return null;
    }

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

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    @Override
    public String toString() {
        return nombre;
    }
}
