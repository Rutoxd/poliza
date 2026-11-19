package Polizas.Entidades;

// Modelo de vehículo: pertenece a una marca (corrección de la Fase 3, 3FN)
public class Modelo {

    private int id;
    private Marca marca;
    private String nombre;

    public Modelo() {
    }

    public Modelo(int id, Marca marca, String nombre) {
        this.id = id;
        this.marca = marca;
        this.nombre = nombre;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public Marca getMarca() { return marca; }
    public void setMarca(Marca marca) { this.marca = marca; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    @Override
    public String toString() {
        return (marca == null ? "" : marca.getNombre() + " ") + nombre;
    }
}
