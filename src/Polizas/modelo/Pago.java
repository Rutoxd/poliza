package Polizas.modelo;

import java.time.LocalDate;

// Capa de modelo: cuota de pago de una póliza (composición: no existe sin su póliza). RF-26.
// Se guarda en la tabla pago por medio de PagoDAO.
// Corrección de Fase 3: el estado no se guarda; estaVencido() lo calcula.
public class Pago {

    private int id;
    private Poliza poliza;
    private int numeroCuota;
    private double monto;
    private LocalDate fechaLimite;
    private LocalDate fechaPago;     // vacía mientras no se paga
    private String metodoPago;       // EFECTIVO, TRANSFERENCIA, TARJETA o DEPOSITO
    private String comprobante;

    // Constructor vacío: crea el objeto para después llenarlo con los métodos set.
    public Pago() {
    }

    // RN-08 y RN-09: monto positivo y que no pase del saldo de la póliza.
    public boolean validarMonto() {
        return monto > 0 && (poliza == null || monto <= poliza.calcularSaldo());
    }

    // RN-10: la cuota está vencida si pasó la fecha límite y no se ha pagado.
    public boolean estaVencido() {
        return fechaPago == null && fechaLimite.isBefore(LocalDate.now());
    }

    // Calculado: PAGADO, VENCIDO o PENDIENTE.
    public EstadoPago calcularEstado() {
        if (fechaPago != null) {
            return EstadoPago.PAGADO;
        }
        return estaVencido() ? EstadoPago.VENCIDO : EstadoPago.PENDIENTE;
    }

    // Getters y setters: leen y cambian los atributos privados (encapsulamiento).
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public Poliza getPoliza() { return poliza; }
    public void setPoliza(Poliza poliza) { this.poliza = poliza; }
    public int getNumeroCuota() { return numeroCuota; }
    public void setNumeroCuota(int numeroCuota) { this.numeroCuota = numeroCuota; }
    public double getMonto() { return monto; }
    public void setMonto(double monto) { this.monto = monto; }
    public LocalDate getFechaLimite() { return fechaLimite; }
    public void setFechaLimite(LocalDate fechaLimite) { this.fechaLimite = fechaLimite; }
    public LocalDate getFechaPago() { return fechaPago; }
    public void setFechaPago(LocalDate fechaPago) { this.fechaPago = fechaPago; }
    public String getMetodoPago() { return metodoPago; }
    public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }
    public String getComprobante() { return comprobante; }
    public void setComprobante(String comprobante) { this.comprobante = comprobante; }
}
