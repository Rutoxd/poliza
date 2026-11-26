package Polizas.modelo;

// Capa de modelo: modelo de vehículo (Versa, Jetta...). Cada modelo es de una marca (asociación).
// Clase agregada en la Fase 3: el vehículo guarda su modelo y la marca sale de aquí.
// Se guarda en la tabla modelo por medio de ModeloDAO.
public class Modelo {

    private int id;
    private String nombre;
    private Marca marca;

    // Constructor vacío: crea el objeto para después llenarlo con los métodos set.
    public Modelo() {
    }

    // Constructor con todos los datos (lo usan los DAO al leer de la base).
    public Modelo(int id, String nombre, Marca marca) {
        this.id = id;
        this.nombre = nombre;
        this.marca = marca;
    }

    // Getters y setters: leen y cambian los atributos privados (encapsulamiento).
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public Marca getMarca() { return marca; }
    public void setMarca(Marca marca) { this.marca = marca; }

    @Override
    public String toString() {
        return marca + " " + nombre;
    }
}
