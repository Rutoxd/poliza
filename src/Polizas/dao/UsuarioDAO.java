package Polizas.dao;

import Polizas.modelo.Rol;
import Polizas.modelo.Usuario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// DAO de Usuario: alta, consulta, modificación y eliminación en la tabla usuario.
// La contraseña se cifra en la base con SHA-256 (RNF-03) y nunca se lee de vuelta.
public class UsuarioDAO implements Dao<Usuario> {

    @Override
    public void registrar(Usuario u) throws SQLException {
        u.setId(Conexion.insertar("INSERT INTO usuario (nombre, telefono, correo, usuario, contrasena, rol, activo) "
                + "VALUES (?, ?, ?, ?, SHA2(?, 256), ?, ?)", u.getNombre(), u.getTelefono(), u.getCorreo(),
                u.getUsuario(), u.getContrasena(), u.getRol().name(), u.isActivo()));
    }

    // Guarda los cambios de datos y rol (la contraseña no se toca aquí).
    @Override
    public void actualizar(Usuario u) throws SQLException {
        Conexion.ejecutar("UPDATE usuario SET nombre = ?, telefono = ?, correo = ?, usuario = ?, rol = ?, activo = ? "
                + "WHERE idUsuario = ?", u.getNombre(), u.getTelefono(), u.getCorreo(), u.getUsuario(),
                u.getRol().name(), u.isActivo(), u.getId());
    }

    // La base no deja borrar un usuario que registró pólizas; para esos, desactivar().
    @Override
    public void eliminar(int id) throws SQLException {
        Conexion.ejecutar("DELETE FROM usuario WHERE idUsuario = ?", id);
    }

    // Deja al usuario sin acceso y conserva quién registró cada póliza.
    public void desactivar(int id) throws SQLException {
        Conexion.ejecutar("UPDATE usuario SET activo = FALSE WHERE idUsuario = ?", id);
    }

    @Override
    public Usuario consultar(int id) throws SQLException {
        List<Usuario> lista = buscar("SELECT * FROM usuario WHERE idUsuario = ?", id);
        return lista.isEmpty() ? null : lista.get(0);
    }

    @Override
    public List<Usuario> listar() throws SQLException {
        return buscar("SELECT * FROM usuario ORDER BY nombre");
    }

    // Solo los usuarios activos.
    public List<Usuario> listarActivos() throws SQLException {
        return buscar("SELECT * FROM usuario WHERE activo = TRUE ORDER BY nombre");
    }

    // RF-35: revisa usuario y contraseña (la base cifra la escrita y la compara).
    // Regresa el usuario con sus datos, o null si no coinciden o está inactivo.
    public Usuario iniciarSesion(String usuario, String contrasena) throws SQLException {
        List<Usuario> lista = buscar("SELECT * FROM usuario WHERE usuario = ? AND contrasena = SHA2(?, 256) "
                + "AND activo = TRUE", usuario, contrasena);
        return lista.isEmpty() ? null : lista.get(0);
    }

    // Ejecuta un SELECT de usuario y convierte cada fila en un objeto Usuario (sin contraseña).
    private List<Usuario> buscar(String sql, Object... datos) throws SQLException {
        List<Usuario> lista = new ArrayList<>();
        try (Connection cn = Conexion.conectar();
             PreparedStatement ps = Conexion.preparar(cn, sql, datos);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Usuario(rs.getInt("idUsuario"), rs.getString("nombre"), rs.getString("telefono"),
                        rs.getString("correo"), rs.getString("usuario"), Rol.valueOf(rs.getString("rol")),
                        rs.getBoolean("activo")));
            }
        }
        return lista;
    }
}
