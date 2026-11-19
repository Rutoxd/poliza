package Polizas.Entidades;

import java.time.LocalDate;

// Cuota de pago de una póliza (composición con Poliza, RF-26)
public class Pago {

    private int id;
    private Poliza poliza;
    private int numeroCuota;
    private double monto;
    private LocalDate fechaLimite;
    private LocalDate fechaPago;     // null mientras no se paga
    private MetodoPago metodoPago;
    private String comprobante;

    public Pago() {
    }

    public Pago(int id, Poliza poliza, int numeroCuota, double monto, LocalDate fechaLimite) {
        this.id = id;
        this.poliza = poliza;
        this.numeroCuota = numeroCuota;
        this.monto = monto;
        this.fechaLimite = fechaLimite;
    }

    // Método: RN-08 y RN-09. Monto positivo y sin rebasar el saldo de la póliza.
    public boolean validarMonto() {
        return monto > 0 && (poliza == null || monto <= poliza.calcularSaldo());
    }

    // Método: RN-10. Vencida si pasó la fecha límite sin pagarse.
    public boolean estaVencido(LocalDate hoy) {
        return fechaPago == null && fechaLimite.isBefore(hoy);
    }

    public boolean estaVencido() {
        return estaVencido(LocalDate.now());
    }

    // Método: estado calculado (no se guarda en la base)
    public EstadoPago getEstado() {
        if (fechaPago != null) {
            return EstadoPago.PAGADO;
        }
        return estaVencido() ? EstadoPago.VENCIDO : EstadoPago.PENDIENTE;
    }

    // Método: marca la cuota como pagada
    public void registrarPago(LocalDate fecha, MetodoPago metodo, String comprobante) {
        this.fechaPago = fecha;
        this.metodoPago = metodo;
        this.comprobante = comprobante;
    }

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
    public MetodoPago getMetodoPago() { return metodoPago; }
    public void setMetodoPago(MetodoPago metodoPago) { this.metodoPago = metodoPago; }
    public String getComprobante() { return comprobante; }
    public void setComprobante(String comprobante) { this.comprobante = comprobante; }
}
