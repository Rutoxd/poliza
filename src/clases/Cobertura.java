package Polizas.clases;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// Cobertura que puede incluir una póliza (daños a terceros, robo total...).
// Tabla: cobertura.
public class Cobertura {

    private int id;
    private String nombre;
    private String descripcion;

    public Cobertura() {
    }

    public Cobertura(int id, String nombre, String descripcion) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public void registrar() throws SQLException {
        Conexion.ejecutar("INSERT INTO cobertura (nombre, descripcion) VALUES (?, ?)", nombre, descripcion);
    }

    public void actualizar() throws SQLException {
        Conexion.ejecutar("UPDATE cobertura SET nombre = ?, descripcion = ? WHERE idCobertura = ?", nombre, descripcion, id);
    }

    public void eliminar() throws SQLException {
        Conexion.ejecutar("DELETE FROM cobertura WHERE idCobertura = ?", id);
    }

    public static Cobertura consultar(int id) throws SQLException {
        for (Cobertura c : listar()) {
            if (c.getId() == id) {
                return c;
            }
        }
        return null;
    }

    public static List<Cobertura> listar() throws SQLException {
        List<Cobertura> lista = new ArrayList<>();
        try (Connection cn = Conexion.conectar();
             PreparedStatement ps = cn.prepareStatement("SELECT * FROM cobertura ORDER BY nombre");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Cobertura(rs.getInt("idCobertura"), rs.getString("nombre"), rs.getString("descripcion")));
            }
        }
        return lista;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    @Override
    public String toString() {
        return nombre;
    }
}
