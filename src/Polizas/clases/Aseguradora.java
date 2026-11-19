package Polizas.clases;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// Aseguradora con la que trabaja el despacho. Cotiza y emite las pólizas.
// Tabla: aseguradora.
public class Aseguradora {

    private int id;
    private String nombre;
    private String telefono;
    private String correo;

    // Constructor vacío: crea el objeto para después llenarlo con los métodos set.
    public Aseguradora() {
    }

    // Constructor con todos los datos (lo usa listar() al leer de la base).
    public Aseguradora(int id, String nombre, String telefono, String correo) {
        this.id = id;
        this.nombre = nombre;
        this.telefono = telefono;
        this.correo = correo;
    }

    // Guarda este registro nuevo en la tabla aseguradora.
    public void registrar() throws SQLException {
        Conexion.ejecutar("INSERT INTO aseguradora (nombre, telefono, correo) VALUES (?, ?, ?)", nombre, telefono, correo);
    }

    // Guarda los cambios de este registro en la tabla aseguradora (lo busca por su id).
    public void actualizar() throws SQLException {
        Conexion.ejecutar("UPDATE aseguradora SET nombre = ?, telefono = ?, correo = ? WHERE idAseguradora = ?",
                nombre, telefono, correo, id);
    }

    // Borra este registro de la tabla aseguradora.
    public void eliminar() throws SQLException {
        Conexion.ejecutar("DELETE FROM aseguradora WHERE idAseguradora = ?", id);
    }

    // Regresa el registro con ese id, o null si no existe.
    public static Aseguradora consultar(int id) throws SQLException {
        for (Aseguradora a : listar()) {
            if (a.getId() == id) {
                return a;
            }
        }
        return null;
    }

    // Regresa todos los registros de la tabla, para llenar las tablas y listas de las pantallas.
    public static List<Aseguradora> listar() throws SQLException {
        List<Aseguradora> lista = new ArrayList<>();
        try (Connection cn = Conexion.conectar();
             PreparedStatement ps = cn.prepareStatement("SELECT * FROM aseguradora ORDER BY nombre");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Aseguradora(rs.getInt("idAseguradora"), rs.getString("nombre"),
                        rs.getString("telefono"), rs.getString("correo")));
            }
        }
        return lista;
    }

    // Getters y setters: leen y cambian los atributos privados (encapsulamiento).
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    @Override
    public String toString() {
        return nombre;
    }
}
