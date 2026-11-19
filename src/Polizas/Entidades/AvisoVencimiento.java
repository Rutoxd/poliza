package Polizas.Entidades;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

// Aviso de vencimiento de una póliza o de una cuota de pago (RF-31 a RF-33).
// La fecha de vencimiento y el monto se obtienen de la póliza o de la cuota (3FN).
public class AvisoVencimiento {

    private int id;
    private TipoAviso tipo;
    private Poliza poliza;          // solo si tipo = POLIZA
    private Pago pago;              // solo si tipo = PAGO
    private LocalDate fechaAviso;
    private MedioAviso medio;
    private double costoRenovacion; // solo si tipo = POLIZA
    private String resultado;

    public AvisoVencimiento() {
    }

    // Método: RF-31. Aviso del fin de vigencia con el costo de renovación.
    public void generarAvisoPoliza(Poliza poliza, MedioAviso medio, double costoRenovacion) {
        if (poliza.calcularEstado() == EstadoPoliza.CANCELADA) {
            throw new IllegalStateException("Una póliza cancelada no recibe avisos (RN-18).");
        }
        this.tipo = TipoAviso.POLIZA;
        this.poliza = poliza;
        this.pago = null;
        this.medio = medio;
        this.costoRenovacion = costoRenovacion;
        this.fechaAviso = LocalDate.now();
    }

    // Método: RF-32. Aviso de la siguiente cuota (RN-13: solo cuotas pendientes).
    public void generarAvisoPago(Pago pago, MedioAviso medio) {
        if (pago.getFechaPago() != null) {
            throw new IllegalStateException("Solo se avisan cuotas pendientes.");
        }
        this.tipo = TipoAviso.PAGO;
        this.pago = pago;
        this.poliza = null;
        this.medio = medio;
        this.costoRenovacion = 0;
        this.fechaAviso = LocalDate.now();
    }

    public LocalDate getFechaVencimiento() {
        return tipo == TipoAviso.POLIZA ? poliza.getFechaFin() : pago.getFechaLimite();
    }

    public double getMonto() {
        return tipo == TipoAviso.POLIZA ? costoRenovacion : pago.getMonto();
    }

    // Método: días entre el aviso y el vencimiento (RN-12: debe ser 7 o más)
    public int calcularDiasRestantes() {
        return (int) ChronoUnit.DAYS.between(fechaAviso, getFechaVencimiento());
    }

    // Método: RF-33, guarda lo que respondió el cliente
    public void registrarSeguimiento(String resultado) {
        this.resultado = resultado;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public TipoAviso getTipo() { return tipo; }
    public void setTipo(TipoAviso tipo) { this.tipo = tipo; }
    public Poliza getPoliza() { return poliza; }
    public void setPoliza(Poliza poliza) { this.poliza = poliza; }
    public Pago getPago() { return pago; }
    public void setPago(Pago pago) { this.pago = pago; }
    public LocalDate getFechaAviso() { return fechaAviso; }
    public void setFechaAviso(LocalDate fechaAviso) { this.fechaAviso = fechaAviso; }
    public MedioAviso getMedio() { return medio; }
    public void setMedio(MedioAviso medio) { this.medio = medio; }
    public double getCostoRenovacion() { return costoRenovacion; }
    public void setCostoRenovacion(double costoRenovacion) { this.costoRenovacion = costoRenovacion; }
    public String getResultado() { return resultado; }
    public void setResultado(String resultado) { this.resultado = resultado; }
}
