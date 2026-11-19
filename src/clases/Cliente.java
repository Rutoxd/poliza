package Polizas.clases;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// Cliente del despacho: hereda nombre, teléfono y correo de Persona.
// Tabla: cliente.
public class Cliente extends Persona {

    private String rfc;
    private String domicilio;

    public Cliente() {
    }

    public Cliente(int id, String nombre, String telefono, String correo, String rfc, String domicilio) {
        super(id, nombre, telefono, correo);
        this.rfc = rfc;
        this.domicilio = domicilio;
    }

    // Guarda este cliente como registro nuevo.
    public void registrar() throws SQLException {
        Conexion.ejecutar("INSERT INTO cliente (nombre, rfc, telefono, correo, domicilio) VALUES (?, ?, ?, ?, ?)",
                getNombre(), rfc, getTelefono(), getCorreo(), domicilio);
    }

    // Guarda los cambios de este cliente (lo busca por su id).
    public void actualizar() throws SQLException {
        Conexion.ejecutar("UPDATE cliente SET nombre = ?, rfc = ?, telefono = ?, correo = ?, domicilio = ? WHERE idCliente = ?",
                getNombre(), rfc, getTelefono(), getCorreo(), domicilio, getId());
    }

    // Borra este cliente. La base no lo permite si tiene vehículos registrados.
    public void eliminar() throws SQLException {
        Conexion.ejecutar("DELETE FROM cliente WHERE idCliente = ?", getId());
    }

    // Regresa el cliente con ese id, o null si no existe.
    public static Cliente consultar(int id) throws SQLException {
        for (Cliente c : listar()) {
            if (c.getId() == id) {
                return c;
            }
        }
        return null;
    }

    // Regresa todos los clientes ordenados por nombre (para llenar tablas y listas).
    public static List<Cliente> listar() throws SQLException {
        List<Cliente> lista = new ArrayList<>();
        try (Connection cn = Conexion.conectar();
             PreparedStatement ps = cn.prepareStatement("SELECT * FROM cliente ORDER BY nombre");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Cliente(rs.getInt("idCliente"), rs.getString("nombre"), rs.getString("telefono"),
                        rs.getString("correo"), rs.getString("rfc"), rs.getString("domicilio")));
            }
        }
        return lista;
    }

    // Revisa el formato del RFC: 3 o 4 letras, 6 dígitos de fecha y 3 de homoclave.
    public boolean validarRfc() {
        return rfc != null && rfc.toUpperCase().matches("^[A-ZÑ&]{3,4}\\d{6}[A-Z0-9]{3}$");
    }

    public String getRfc() { return rfc; }
    public void setRfc(String rfc) { this.rfc = rfc; }
    public String getDomicilio() { return domicilio; }
    public void setDomicilio(String domicilio) { this.domicilio = domicilio; }
}
