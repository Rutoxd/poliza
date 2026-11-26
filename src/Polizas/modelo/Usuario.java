package Polizas.modelo;

import java.util.Arrays;

// Capa de modelo: usuario que entra al sistema. Hereda nombre, teléfono y correo de Persona.
// Se guarda en la tabla usuario por medio de UsuarioDAO, que cifra la contraseña (RNF-03).
public class Usuario extends Persona {

    private String usuario;
    private String contrasena;      // solo se usa al registrar o iniciar sesión; nunca se lee de la base
    private Rol rol;
    private boolean activo = true;

    // Constructor vacío: crea el objeto para después llenarlo con los métodos set.
    public Usuario() {
    }

    // Constructor con todos los datos (lo usa UsuarioDAO al leer de la base).
    public Usuario(int id, String nombre, String telefono, String correo, String usuario, Rol rol, boolean activo) {
        super(id, nombre, telefono, correo);
        this.usuario = usuario;
        this.rol = rol;
        this.activo = activo;
    }

    // true si el usuario está activo y su rol es uno de los permitidos (RNF-02).
    public boolean tienePermiso(Rol... permitidos) {
        return activo && Arrays.asList(permitidos).contains(rol);
    }

    // Termina la sesión: olvida la contraseña escrita.
    public void cerrarSesion() {
        contrasena = null;
    }

    // Getters y setters: leen y cambian los atributos privados (encapsulamiento).
    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }
    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }
    public Rol getRol() { return rol; }
    public void setRol(Rol rol) { this.rol = rol; }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
}
