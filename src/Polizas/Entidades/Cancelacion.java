package Polizas.Entidades;

import java.time.LocalDate;

// Cancelación de una póliza (composición con Poliza, RF-21 a RF-23)
public class Cancelacion {

    private int id;
    private Poliza poliza;
    private LocalDate fechaSolicitud;
    private String motivo;
    private EstadoCancelacion estado;
    private double montoDevolucion;

    public Cancelacion() {
    }

    // Método: el cliente pide la cancelación
    public void solicitar(String motivo) {
        if (motivo == null || motivo.isBlank()) {
            throw new IllegalArgumentException("El motivo es obligatorio.");
        }
        this.motivo = motivo;
        this.fechaSolicitud = LocalDate.now();
        this.estado = EstadoCancelacion.SOLICITADA;
    }

    // Método: RN-16, el vendedor la valida antes de pasar al despacho
    public void validar() {
        cambiar(EstadoCancelacion.SOLICITADA, EstadoCancelacion.VALIDADA);
    }

    // Método: RN-17, el Encargado de gestión interna la autoriza
    public void autorizar() {
        cambiar(EstadoCancelacion.VALIDADA, EstadoCancelacion.AUTORIZADA);
    }

    public void rechazar() {
        if (estado == EstadoCancelacion.BAJA_REGISTRADA) {
            throw new IllegalStateException("La baja ya está registrada.");
        }
        estado = EstadoCancelacion.RECHAZADA;
    }

    // Método: RF-23, el Administrador registra la baja ante la aseguradora
    public void registrarBaja() {
        cambiar(EstadoCancelacion.AUTORIZADA, EstadoCancelacion.BAJA_REGISTRADA);
    }

    // Método: la devolución la define la aseguradora; el sistema solo la registra
    public double calcularDevolucion() {
        return montoDevolucion;
    }

    private void cambiar(EstadoCancelacion requerido, EstadoCancelacion nuevo) {
        if (estado != requerido) {
            throw new IllegalStateException("La cancelación debe estar " + requerido + " para pasar a " + nuevo + ".");
        }
        estado = nuevo;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public Poliza getPoliza() { return poliza; }
    public void setPoliza(Poliza poliza) { this.poliza = poliza; }
    public LocalDate getFechaSolicitud() { return fechaSolicitud; }
    public void setFechaSolicitud(LocalDate fechaSolicitud) { this.fechaSolicitud = fechaSolicitud; }
    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
    public EstadoCancelacion getEstado() { return estado; }
    public void setEstado(EstadoCancelacion estado) { this.estado = estado; }
    public double getMontoDevolucion() { return montoDevolucion; }
    public void setMontoDevolucion(double montoDevolucion) { this.montoDevolucion = montoDevolucion; }
}
