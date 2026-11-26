package Polizas.modelo;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

// Capa de modelo: aviso al cliente de que se acerca el fin de su póliza (tipo POLIZA) o una cuota
// (tipo PAGO). RF-31 a RF-33. Se guarda en la tabla avisoVencimiento por medio de AvisoVencimientoDAO.
// Corrección de Fase 3: la fecha y el monto no se guardan aquí, se toman de la póliza o de la cuota;
// solo se guarda costoRenovacion en avisos de póliza.
public class AvisoVencimiento {

    private int id;
    private TipoAviso tipo;
    private Poliza poliza;           // solo si tipo = POLIZA
    private Pago pago;               // solo si tipo = PAGO
    private LocalDate fechaAviso;
    private MedioAviso medio;
    private double costoRenovacion;  // solo si tipo = POLIZA
    private String resultado;

    // Constructor vacío: crea el objeto para después llenarlo con los métodos set.
    public AvisoVencimiento() {
    }

    // RF-31: arma el aviso de fin de vigencia con el costo de renovación.
    public void generarAvisoPoliza(Poliza poliza, MedioAviso medio, double costoRenovacion) {
        if (poliza.calcularEstado() == EstadoPoliza.CANCELADA) {
            throw new IllegalStateException("Una póliza cancelada no recibe avisos.");
        }
        this.tipo = TipoAviso.POLIZA;
        this.poliza = poliza;
        this.medio = medio;
        this.costoRenovacion = costoRenovacion;
        this.fechaAviso = LocalDate.now();
    }

    // RF-32: arma el aviso de una cuota pendiente (RN-13: solo cuotas sin pagar).
    public void generarAvisoPago(Pago pago, MedioAviso medio) {
        if (pago.getFechaPago() != null) {
            throw new IllegalStateException("Solo se avisan cuotas pendientes.");
        }
        this.tipo = TipoAviso.PAGO;
        this.pago = pago;
        this.medio = medio;
        this.fechaAviso = LocalDate.now();
    }

    // Días entre el aviso y el vencimiento (RN-12: deben ser 7 o más).
    public int calcularDiasRestantes() {
        LocalDate vence = tipo == TipoAviso.POLIZA ? poliza.getFechaFin() : pago.getFechaLimite();
        return (int) ChronoUnit.DAYS.between(fechaAviso, vence);
    }

    // RF-33: anota lo que respondió el cliente (se guarda con AvisoVencimientoDAO.actualizar).
    public void registrarSeguimiento(String resultado) {
        this.resultado = resultado;
    }

    // Getters y setters: leen y cambian los atributos privados (encapsulamiento).
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
