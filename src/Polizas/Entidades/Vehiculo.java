package Polizas.Entidades;

import java.time.LocalDate;
import java.time.Year;
import java.util.List;

// Vehículo de un cliente. Marca y modelo se obtienen de la clase Modelo (Fase 3).
public class Vehiculo {

    private int id;
    private Cliente cliente;
    private Modelo modelo;
    private int anio;
    private String placas;
    private String numeroSerie;

    public Vehiculo() {
    }

    public Vehiculo(int id, Cliente cliente, Modelo modelo, int anio, String placas, String numeroSerie) {
        this.id = id;
        this.cliente = cliente;
        this.modelo = modelo;
        this.anio = anio;
        this.placas = placas;
        this.numeroSerie = numeroSerie;
    }

    // Método: RN-04, un vehículo no puede tener dos pólizas vigentes
    public boolean tienePolizaVigente(List<Poliza> polizas, LocalDate hoy) {
        for (Poliza p : polizas) {
            if (p.getVehiculo() != null && p.getVehiculo().getId() == id) {
                EstadoPoliza estado = p.calcularEstado(hoy);
                if (estado == EstadoPoliza.VIGENTE || estado == EstadoPoliza.POR_VENCER) {
                    return true;
                }
            }
        }
        return false;
    }

    // Método: año entre 1950 y el próximo año
    public boolean validarAnio() {
        return anio >= 1950 && anio <= Year.now().getValue() + 1;
    }

    // Método: el número de serie (VIN) tiene 17 caracteres, sin I, O ni Q
    public boolean validarNumeroSerie() {
        return numeroSerie != null && numeroSerie.toUpperCase().matches("^[A-HJ-NPR-Z0-9]{17}$");
    }

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
