package Polizas.clases;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// Tipo de póliza del catálogo (amplia, limitada...). Es clase y no enumeración
// porque los tipos pueden cambiar según el contrato con la aseguradora.
// Tabla: tipoPoliza. No se borra: se desactiva.
public class TipoPoliza {

    private int id;
    private String nombre;
    private String descripcion;
    private boolean activo = true;

    // Constructor vacío: crea el objeto para después llenarlo con los métodos set.
    public TipoPoliza() {
    }

    // Constructor con todos los datos (lo usa listar() al leer de la base).
    public TipoPoliza(int id, String nombre, String descripcion, boolean activo) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.activo = activo;
    }

    // Guarda este registro nuevo en la tabla tipoPoliza.
    public void registrar() throws SQLException {
        Conexion.ejecutar("INSERT INTO tipoPoliza (nombre, descripcion, activo) VALUES (?, ?, ?)", nombre, descripcion, activo);
    }

    // Guarda los cambios de este registro en la tabla tipoPoliza (lo busca por su id).
    public void actualizar() throws SQLException {
        Conexion.ejecutar("UPDATE tipoPoliza SET nombre = ?, descripcion = ?, activo = ? WHERE idTipoPoliza = ?",
                nombre, descripcion, activo, id);
    }

    // Deja el tipo fuera de uso sin borrarlo (las pólizas viejas lo siguen usando).
    public void desactivar() throws SQLException {
        Conexion.ejecutar("UPDATE tipoPoliza SET activo = FALSE WHERE idTipoPoliza = ?", id);
        activo = false;
    }

    // Regresa el registro con ese id, o null si no existe.
    public static TipoPoliza consultar(int id) throws SQLException {
        for (TipoPoliza t : listar()) {
            if (t.getId() == id) {
                return t;
            }
        }
        return null;
    }

    // Regresa todos los registros de la tabla, para llenar las tablas y listas de las pantallas.
    public static List<TipoPoliza> listar() throws SQLException {
        List<TipoPoliza> lista = new ArrayList<>();
        try (Connection cn = Conexion.conectar();
             PreparedStatement ps = cn.prepareStatement("SELECT * FROM tipoPoliza ORDER BY nombre");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new TipoPoliza(rs.getInt("idTipoPoliza"), rs.getString("nombre"),
                        rs.getString("descripcion"), rs.getBoolean("activo")));
            }
        }
        return lista;
    }

    // Getters y setters: leen y cambian los atributos privados (encapsulamiento).
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    @Override
    public String toString() {
        return nombre;
    }
}
