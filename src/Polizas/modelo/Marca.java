package Polizas.modelo;

// Capa de modelo: marca de vehículo (Nissan, Toyota...). Clase agregada en la Fase 3 al normalizar.
// Se guarda en la tabla marca por medio de MarcaDAO.
public class Marca {

    private int id;
    private String nombre;

    // Constructor vacío: crea el objeto para después llenarlo con los métodos set.
    public Marca() {
    }

    // Constructor con todos los datos (lo usan los DAO al leer de la base).
    public Marca(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    // Getters y setters: leen y cambian los atributos privados (encapsulamiento).
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    @Override
    public String toString() {
        return nombre;
    }
}
