package Polizas.modelo;

// Capa de modelo: vehículo de un cliente. Marca y modelo se toman de la clase Modelo (corrección de Fase 3).
// Se guarda en la tabla vehiculo por medio de VehiculoDAO.
public class Vehiculo {

    public static final int ANIO_MINIMO = 1950;

    private int id;
    private Cliente cliente;
    private Modelo modelo;
    private int anio;
    private String placas;
    private String numeroSerie;

    // Constructor vacío: crea el objeto para después llenarlo con los métodos set.
    public Vehiculo() {
    }

    // Constructor con todos los datos (lo usa VehiculoDAO al leer de la base).
    public Vehiculo(int id, Cliente cliente, Modelo modelo, int anio, String placas, String numeroSerie) {
        this.id = id;
        this.cliente = cliente;
        this.modelo = modelo;
        this.anio = anio;
        this.placas = placas;
        this.numeroSerie = numeroSerie;
    }

    // Revisa los datos antes de guardar: año razonable y número de serie (VIN) de 17 caracteres.
    public void validar() {
        if (anio < ANIO_MINIMO || anio > java.time.LocalDate.now().getYear() + 1) {
            throw new IllegalArgumentException("El año del vehículo no es válido.");
        }
        if (numeroSerie == null || numeroSerie.length() != 17) {
            throw new IllegalArgumentException("El número de serie debe tener 17 caracteres.");
        }
    }

    // Getters y setters: leen y cambian los atributos privados (encapsulamiento).
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }
    public Modelo getModelo() { return modelo; }
    public void setModelo(Modelo modelo) { this.modelo = modelo; }
    public int getAnio() { return anio; }
    public void setAnio(int anio) { this.anio = anio; }
    public String getPlacas() { return placas; }
    public void setPlacas(String placas) { this.placas = placas; }
    public String getNumeroSerie() { return numeroSerie; }
    public void setNumeroSerie(String numeroSerie) { this.numeroSerie = numeroSerie; }

    @Override
    public String toString() {
        return placas + " - " + modelo + " " + anio;
    }
}
