package Polizas.clases;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// Modelo de vehículo (Versa, Corolla...). Cada modelo es de una marca.
// Clase agregada en la Fase 3: el vehículo guarda su modelo y la marca sale de aquí.
// Tabla: modelo.
public class Modelo {

    private int id;
    private String nombre;
    private Marca marca;

    // Constructor vacío: crea el objeto para después llenarlo con los métodos set.
    public Modelo() {
    }

    // Constructor con todos los datos (lo usa listar() al leer de la base).
    public Modelo(int id, String nombre, Marca marca) {
        this.id = id;
        this.nombre = nombre;
        this.marca = marca;
    }

    // Guarda este registro nuevo en la tabla modelo.
    public void registrar() throws SQLException {
        Conexion.ejecutar("INSERT INTO modelo (idMarca, nombreModelo) VALUES (?, ?)", marca.getId(), nombre);
    }

    // Guarda los cambios de este registro en la tabla modelo (lo busca por su id).
    public void actualizar() throws SQLException {
        Conexion.ejecutar("UPDATE modelo SET idMarca = ?, nombreModelo = ? WHERE idModelo = ?", marca.getId(), nombre, id);
    }

    // La base no deja borrar un modelo que ya usa algún vehículo.
    public void eliminar() throws SQLException {
        Conexion.ejecutar("DELETE FROM modelo WHERE idModelo = ?", id);
    }

    // Regresa el registro con ese id, o null si no existe.
    public static Modelo consultar(int id) throws SQLException {
        for (Modelo m : listar()) {
            if (m.getId() == id) {
                return m;
            }
        }
        return null;
    }

    // Trae los modelos junto con su marca (JOIN).
    public static List<Modelo> listar() throws SQLException {
        List<Modelo> lista = new ArrayList<>();
        String sql = "SELECT mo.idModelo, mo.nombreModelo, ma.idMarca, ma.nombreMarca "
                + "FROM modelo mo JOIN marca ma ON ma.idMarca = mo.idMarca ORDER BY ma.nombreMarca, mo.nombreModelo";
        try (Connection cn = Conexion.conectar();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Marca marca = new Marca(rs.getInt("idMarca"), rs.getString("nombreMarca"));
                lista.add(new Modelo(rs.getInt("idModelo"), rs.getString("nombreModelo"), marca));
            }
        }
        return lista;
    }

    // Getters y setters: leen y cambian los atributos privados (encapsulamiento).
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public Marca getMarca() { return marca; }
    public void setMarca(Marca marca) { this.marca = marca; }

    @Override
    public String toString() {
        return marca + " " + nombre;
    }
}
