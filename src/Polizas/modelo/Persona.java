package Polizas.modelo;

// Capa de modelo: clase abstracta con los datos que comparten Cliente y Usuario (herencia).
// No se crean objetos Persona directamente, solo Cliente o Usuario.
public abstract class Persona {

    private int id;
    private String nombre;
    private String telefono;
    private String correo;

    // Constructor vacío: crea el objeto para después llenarlo con los métodos set.
    public Persona() {
    }

    // Constructor con todos los datos (lo usan los DAO al leer de la base).
    public Persona(int id, String nombre, String telefono, String correo) {
        this.id = id;
        this.nombre = nombre;
        this.telefono = telefono;
        this.correo = correo;
    }

    // Revisa que el correo tenga la forma nombre@dominio.com (vacío se permite).
    public boolean validarCorreo() {
        return correo == null || correo.isBlank() || correo.matches("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$");
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
