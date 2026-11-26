package Polizas.modelo;

// Capa de modelo: cobertura que puede incluir una póliza (daños materiales, robo total...).
// Se guarda en la tabla cobertura por medio de CoberturaDAO.
public class Cobertura {

    private int id;
    private String nombre;
    private String descripcion;

    // Constructor vacío: crea el objeto para después llenarlo con los métodos set.
    public Cobertura() {
    }

    // Constructor con todos los datos (lo usan los DAO al leer de la base).
    public Cobertura(int id, String nombre, String descripcion) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    // Getters y setters: leen y cambian los atributos privados (encapsulamiento).
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    @Override
    public String toString() {
        return nombre;
    }
}
