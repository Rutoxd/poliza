package Polizas.modelo;

import java.time.LocalDate;

// Capa de modelo: cancelación de una póliza (composición: no existe sin su póliza). RF-21 a RF-23.
// Pasos: SOLICITADA -> VALIDADA (vendedor) -> AUTORIZADA (encargado) -> BAJA_REGISTRADA (administrador).
// Se guarda en la tabla cancelacion por medio de CancelacionDAO.
public class Cancelacion {

    private int id;
    private Poliza poliza;
    private LocalDate fechaSolicitud;
    private String motivo;
    private EstadoCancelacion estado;
    private double montoDevolucion;

    // Constructor vacío: crea el objeto para después llenarlo con los métodos set.
    public Cancelacion() {
    }

    // El cliente pide la cancelación: queda SOLICITADA con la fecha de hoy. Se llama desde Poliza.cancelar().
    public void solicitar(String motivo) {
        if (motivo == null || motivo.isBlank()) {
            throw new IllegalArgumentException("El motivo es obligatorio.");
        }
        this.motivo = motivo;
        fechaSolicitud = LocalDate.now();
        estado = EstadoCancelacion.SOLICITADA;
    }

    // RN-16: el vendedor la valida.
    public void validar() {
        cambiarEstado(EstadoCancelacion.SOLICITADA, EstadoCancelacion.VALIDADA);
    }

    // RN-17: el Encargado de gestión interna la autoriza.
    public void autorizar() {
        cambiarEstado(EstadoCancelacion.VALIDADA, EstadoCancelacion.AUTORIZADA);
    }

    // RF-23: el Administrador registra la baja ante la aseguradora. Desde aquí la póliza queda CANCELADA.
    public void registrarBaja() {
        cambiarEstado(EstadoCancelacion.AUTORIZADA, EstadoCancelacion.BAJA_REGISTRADA);
    }

    // La devolución la decide la aseguradora; el sistema solo la guarda.
    public double calcularDevolucion() {
        return montoDevolucion;
    }

    // Avanza un paso solo si la cancelación está en el paso anterior.
    private void cambiarEstado(EstadoCancelacion requerido, EstadoCancelacion nuevo) {
        if (estado != requerido) {
            throw new IllegalStateException("La cancelación debe estar " + requerido + " para pasar a " + nuevo + ".");
        }
        estado = nuevo;
    }

    // Getters y setters: leen y cambian los atributos privados (encapsulamiento).
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
