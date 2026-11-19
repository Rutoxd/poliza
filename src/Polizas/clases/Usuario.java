package Polizas.clases;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

// Usuario que entra al sistema: hereda nombre, teléfono y correo de Persona.
// Tabla: usuario. La contraseña se guarda cifrada con SHA-256 (RNF-03), nunca en texto plano.
public class Usuario extends Persona {

    private String usuario;
    private String contrasena;
    private Rol rol;
    private boolean activo = true;

    // Constructor vacío: crea el objeto para después llenarlo con los métodos set.
    public Usuario() {
    }

    // Constructor con todos los datos (lo usa listar() al leer de la base).
    public Usuario(int id, String nombre, String telefono, String correo, String usuario, Rol rol, boolean activo) {
        super(id, nombre, telefono, correo);
        this.usuario = usuario;
        this.rol = rol;
        this.activo = activo;
    }

    // Revisa usuario y contraseña contra la base (la base cifra la contraseña escrita y la compara).
    // Si son correctos, carga los datos del usuario y regresa true.
    public boolean iniciarSesion() throws SQLException {
        String sql = "SELECT * FROM usuario WHERE usuario = ? AND contrasena = SHA2(?, 256) AND activo = TRUE";
        try (Connection cn = Conexion.conectar(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, usuario);
            ps.setString(2, contrasena);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return false;
                }
                setId(rs.getInt("idUsuario"));
                setNombre(rs.getString("nombre"));
                setTelefono(rs.getString("telefono"));
                setCorreo(rs.getString("correo"));
                rol = Rol.valueOf(rs.getString("rol"));
                contrasena = null; // ya no se necesita en memoria
                return true;
            }
        }
    }

    // Termina la sesión del usuario.
    public void cerrarSesion() {
        System.out.println("Sesión cerrada: " + usuario);
    }

    // Guarda este usuario como registro nuevo (la contraseña se cifra en la base).
    public void registrar() throws SQLException {
        Conexion.ejecutar("INSERT INTO usuario (nombre, telefono, correo, usuario, contrasena, rol, activo) "
                + "VALUES (?, ?, ?, ?, SHA2(?, 256), ?, ?)",
                getNombre(), getTelefono(), getCorreo(), usuario, contrasena, rol.name(), activo);
    }

    // Guarda los cambios de datos y rol (la contraseña no se toca aquí).
    public void actualizar() throws SQLException {
        Conexion.ejecutar("UPDATE usuario SET nombre = ?, telefono = ?, correo = ?, usuario = ?, rol = ? WHERE idUsuario = ?",
                getNombre(), getTelefono(), getCorreo(), usuario, rol.name(), getId());
    }

    // Los usuarios no se borran: se desactivan para conservar quién registró cada póliza.
    public void desactivar() throws SQLException {
        Conexion.ejecutar("UPDATE usuario SET activo = FALSE WHERE idUsuario = ?", getId());
        activo = false;
    }

    // true si el usuario está activo y su rol es uno de los permitidos (RNF-02).
    public boolean tienePermiso(Rol... permitidos) {
        return activo && Arrays.asList(permitidos).contains(rol);
    }

    // Getters y setters: leen y cambian los atributos privados (encapsulamiento).
    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }
    public Rol getRol() { return rol; }
    public void setRol(Rol rol) { this.rol = rol; }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
}
