package Polizas.Entidades;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

// Usuario del sistema: hereda de Persona
public class Usuario extends Persona {

    // Atributos
    private String usuario;
    private String contrasena;   // hash SHA-256, nunca en texto plano (RNF-03)
    private Rol rol;
    private boolean activo = true;

    public Usuario() {
    }

    public Usuario(int id, String nombre, String telefono, String correo,
                   String usuario, String contrasena, Rol rol, boolean activo) {
        super(id, nombre, telefono, correo);
        this.usuario = usuario;
        this.contrasena = contrasena;
        this.rol = rol;
        this.activo = activo;
    }

    // Método: compara la contraseña escrita con el hash guardado (RF-35, RNF-01)
    public boolean iniciarSesion(String contrasenaEscrita) {
        return activo && contrasena != null && contrasena.equals(cifrar(contrasenaEscrita));
    }

    public void cerrarSesion() {
        System.out.println("Sesión cerrada: " + usuario);
    }

    public void desactivar() {
        this.activo = false;
    }

    // Método: el rol del usuario está entre los permitidos (RNF-02)
    public boolean tienePermiso(Rol... permitidos) {
        return activo && Arrays.asList(permitidos).contains(rol);
    }

    // Método de clase: hash SHA-256 en hexadecimal, igual que SHA2(..., 256) de MySQL
    public static String cifrar(String texto) {
        try {
            byte[] hash = MessageDigest.getInstance("SHA-256").digest(texto.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }
    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }
    public Rol getRol() { return rol; }
    public void setRol(Rol rol) { this.rol = rol; }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
}
