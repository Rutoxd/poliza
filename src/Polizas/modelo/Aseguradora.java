package Polizas.modelo;

// Capa de modelo: aseguradora con la que trabaja el despacho. Cotiza y emite las pólizas.
// Se guarda en la tabla aseguradora por medio de AseguradoraDAO.
public class Aseguradora {

    private int id;
    private String nombre;
    private String telefono;
    private String correo;

    // Constructor vacío: crea el objeto para después llenarlo con los métodos set.
    public Aseguradora() {
    }

    // Constructor con todos los datos (lo usan los DAO al leer de la base).
    public Aseguradora(int id, String nombre, String telefono, String correo) {
        this.id = id;
        this.nombre = nombre;
        this.telefono = telefono;
        this.correo = correo;
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
