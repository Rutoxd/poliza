package Polizas.Entidades;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

// Póliza emitida por una aseguradora. El cliente se obtiene por el vehículo (Fase 3).
public class Poliza {

    public static final int DIAS_POR_VENCER = 7;   // RN-11

    private int id;
    private String numero;
    private Aseguradora aseguradora;
    private TipoPoliza tipoPoliza;
    private Vehiculo vehiculo;
    private Cotizacion cotizacion;       // puede ser null en pólizas migradas
    private Poliza polizaAnterior;       // póliza que esta renueva (RF-20)
    private Usuario usuario;             // quién la registró
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private double primaTotal;
    private List<Cobertura> coberturas = new ArrayList<>();
    private List<Pago> pagos = new ArrayList<>();          // composición
    private Cancelacion cancelacion;                       // composición, 0..1

    public Poliza() {
    }

    // Método: RF-14 y RN-11. El estado no se guarda, se calcula.
    public EstadoPoliza calcularEstado(LocalDate hoy) {
        if (cancelacion != null && cancelacion.getEstado() == EstadoCancelacion.BAJA_REGISTRADA) {
            return EstadoPoliza.CANCELADA;
        }
        if (fechaFin.isBefore(hoy)) {
            return EstadoPoliza.VENCIDA;
        }
        if (ChronoUnit.DAYS.between(hoy, fechaFin) <= DIAS_POR_VENCER) {
            return EstadoPoliza.POR_VENCER;
        }
        return EstadoPoliza.VIGENTE;
    }

    public EstadoPoliza calcularEstado() {
        return calcularEstado(LocalDate.now());
    }

    // Método: RF-27, prima menos lo ya pagado
    public double calcularSaldo() {
        double pagado = 0;
        for (Pago p : pagos) {
            if (p.getFechaPago() != null) {
                pagado += p.getMonto();
            }
        }
        return primaTotal - pagado;
    }

    // Método: primera cuota pendiente, o null si ya está liquidada
    public Pago obtenerSiguienteCuota() {
        Pago siguiente = null;
        for (Pago p : pagos) {
            if (p.getFechaPago() == null && (siguiente == null || p.getNumeroCuota() < siguiente.getNumeroCuota())) {
                siguiente = p;
            }
        }
        return siguiente;
    }

    // Método: RN-01, RN-02 y RN-03
    public void validar() {
        if (fechaInicio == null || fechaFin == null || !fechaFin.isAfter(fechaInicio)) {
            throw new IllegalStateException("La fecha de fin debe ser posterior a la de inicio.");
        }
        if (coberturas.isEmpty()) {
            throw new IllegalStateException("La póliza debe incluir al menos una cobertura.");
        }
        if (primaTotal <= 0) {
            throw new IllegalStateException("La prima total debe ser mayor que cero.");
        }
    }

    // Método: RN-14 y RN-15. Misma duración, tipo y coberturas; inicia al terminar la anterior.
    public Poliza renovar(String nuevoNumero, double nuevaPrima, Usuario registro) {
        EstadoPoliza estado = calcularEstado();
        if (estado != EstadoPoliza.POR_VENCER && estado != EstadoPoliza.VENCIDA) {
            throw new IllegalStateException("Solo se renuevan pólizas por vencer o vencidas.");
        }
        long dias = ChronoUnit.DAYS.between(fechaInicio, fechaFin);
        Poliza nueva = new Poliza();
        nueva.setNumero(nuevoNumero);
        nueva.setAseguradora(aseguradora);
        nueva.setTipoPoliza(tipoPoliza);
        nueva.setVehiculo(vehiculo);
        nueva.setPolizaAnterior(this);
        nueva.setUsuario(registro);
        nueva.setFechaInicio(fechaFin);
        nueva.setFechaFin(fechaFin.plusDays(dias));
        nueva.setPrimaTotal(nuevaPrima);
        nueva.setCoberturas(new ArrayList<>(coberturas));
        return nueva;
    }

    // Método: RF-21, inicia la cancelación (RN-18: una cancelada no se vuelve a cancelar)
    public Cancelacion cancelar(String motivo) {
        if (cancelacion != null) {
            throw new IllegalStateException("La póliza ya tiene una cancelación.");
        }
        cancelacion = new Cancelacion();
        cancelacion.setPoliza(this);
        cancelacion.solicitar(motivo);
        return cancelacion;
    }

    // Método: RF-18. Por ahora imprime en consola; el formato carta llega en la Fase 4.
    public void imprimir() {
        System.out.println("Póliza " + numero + " - " + aseguradora);
        System.out.println("Cliente: " + (vehiculo == null ? "" : vehiculo.getCliente()));
        System.out.println("Vehículo: " + vehiculo);
        System.out.println("Vigencia: " + fechaInicio + " al " + fechaFin + " (" + calcularEstado() + ")");
        System.out.println("Coberturas: " + coberturas);
        System.out.println("Prima total: " + primaTotal + "  Saldo: " + calcularSaldo());
    }

    public Cliente getCliente() {
        return vehiculo == null ? null : vehiculo.getCliente();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }
    public Aseguradora getAseguradora() { return aseguradora; }
    public void setAseguradora(Aseguradora aseguradora) { this.aseguradora = aseguradora; }
    public TipoPoliza getTipoPoliza() { return tipoPoliza; }
    public void setTipoPoliza(TipoPoliza tipoPoliza) { this.tipoPoliza = tipoPoliza; }
    public Vehiculo getVehiculo() { return vehiculo; }
    public void setVehiculo(Vehiculo vehiculo) { this.vehiculo = vehiculo; }
    public Cotizacion getCotizacion() { return cotizacion; }
    public void setCotizacion(Cotizacion cotizacion) { this.cotizacion = cotizacion; }
    public Poliza getPolizaAnterior() { return polizaAnterior; }
    public void setPolizaAnterior(Poliza polizaAnterior) { this.polizaAnterior = polizaAnterior; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }
    public LocalDate getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }
    public double getPrimaTotal() { return primaTotal; }
    public void setPrimaTotal(double primaTotal) { this.primaTotal = primaTotal; }
    public List<Cobertura> getCoberturas() { return coberturas; }
    public void setCoberturas(List<Cobertura> coberturas) { this.coberturas = coberturas; }
    public List<Pago> getPagos() { return pagos; }
    public void setPagos(List<Pago> pagos) { this.pagos = pagos; }
    public Cancelacion getCancelacion() { return cancelacion; }
    public void setCancelacion(Cancelacion cancelacion) { this.cancelacion = cancelacion; }

    @Override
    public String toString() {
        return numero;
    }
}
