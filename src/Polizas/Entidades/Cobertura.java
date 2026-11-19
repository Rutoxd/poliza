package Polizas.Entidades;

// Cobertura que puede incluir una póliza (catálogo, RF-25)
public class Cobertura {

    private int id;
    private String nombre;
    private String descripcion;

    public Cobertura() {
    }

    public Cobertura(int id, String nombre, String descripcion) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

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
