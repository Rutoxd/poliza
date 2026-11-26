package Polizas.modelo;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

// Capa de modelo: póliza de seguro de un vehículo. Es la clase central del sistema.
// Se guarda en las tablas poliza y polizaCobertura por medio de PolizaDAO.
// Corrección de Fase 3: el estado no se guarda, se calcula; y el cliente se obtiene del vehículo.
public class Poliza {

    public static final int DIAS_POR_VENCER = 7;   // RN-11

    private int id;
    private String numero;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private double primaTotal;
    private Vehiculo vehiculo;
    private Aseguradora aseguradora;
    private TipoPoliza tipoPoliza;
    private Cotizacion cotizacion;        // puede faltar en pólizas capturadas de las hojas de cálculo
    private Poliza polizaAnterior;        // la póliza que esta renueva
    private Usuario usuario;              // quién la registró
    private List<Cobertura> coberturas = new ArrayList<>();
    private List<Pago> pagos = new ArrayList<>();          // composición
    private Cancelacion cancelacion;                       // composición, 0..1

    // Constructor vacío: crea el objeto para después llenarlo con los métodos set.
    public Poliza() {
    }

    // Revisa las reglas de la póliza antes de guardarla. Si algo falla, lanza un error con el motivo.
    public void validar() {
        if (numero == null || numero.isBlank()) {
            throw new IllegalArgumentException("El número de póliza es obligatorio.");
        }
        if (vehiculo == null || aseguradora == null || tipoPoliza == null || usuario == null) {
            throw new IllegalArgumentException("Elige vehículo, aseguradora, tipo de póliza y usuario.");
        }
        if (fechaInicio == null || fechaFin == null || !fechaFin.isAfter(fechaInicio)) {
            throw new IllegalArgumentException("La fecha de fin debe ser posterior a la de inicio (RN-02).");
        }
        if (primaTotal <= 0) {
            throw new IllegalArgumentException("La prima total debe ser mayor que cero.");
        }
        if (coberturas.isEmpty()) {
            throw new IllegalArgumentException("La póliza debe incluir al menos una cobertura (RN-03).");
        }
    }

    // RF-14 y RN-11: cancelada, vencida, por vencer (7 días o menos) o vigente.
    public EstadoPoliza calcularEstado() {
        LocalDate hoy = LocalDate.now();
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

    // RF-27: prima total menos lo que ya se pagó.
    public double calcularSaldo() {
        double pagado = 0;
        for (Pago p : pagos) {
            if (p.getFechaPago() != null) {
                pagado += p.getMonto();
            }
        }
        return primaTotal - pagado;
    }

    // La primera cuota sin pagar, o null si ya está liquidada.
    public Pago obtenerSiguienteCuota() {
        for (Pago p : pagos) {             // los pagos vienen ordenados por número de cuota
            if (p.getFechaPago() == null) {
                return p;
            }
        }
        return null;
    }

    // RN-14 y RN-15: crea la póliza nueva con el mismo vehículo, tipo y coberturas, que empieza
    // cuando termina esta. Falta ponerle número y usuario, y guardarla con PolizaDAO.
    public Poliza renovar() {
        EstadoPoliza estado = calcularEstado();
        if (estado != EstadoPoliza.POR_VENCER && estado != EstadoPoliza.VENCIDA) {
            throw new IllegalStateException("Solo se renuevan pólizas por vencer o vencidas.");
        }
        Poliza nueva = new Poliza();
        nueva.vehiculo = vehiculo;
        nueva.aseguradora = aseguradora;
        nueva.tipoPoliza = tipoPoliza;
        nueva.polizaAnterior = this;
        nueva.fechaInicio = fechaFin;
        nueva.fechaFin = fechaFin.plusDays(ChronoUnit.DAYS.between(fechaInicio, fechaFin));
        nueva.primaTotal = primaTotal;
        nueva.coberturas = new ArrayList<>(coberturas);
        return nueva;
    }

    // RF-21: crea la solicitud de cancelación de esta póliza (RN-18: solo una vez).
    // La guarda CancelacionDAO.
    public Cancelacion cancelar(String motivo) {
        if (cancelacion != null) {
            throw new IllegalStateException("La póliza ya tiene una cancelación.");
        }
        Cancelacion c = new Cancelacion();
        c.setPoliza(this);
        c.solicitar(motivo);
        cancelacion = c;
        return c;
    }

    // RF-18: arma el texto de la póliza para mostrarlo o imprimirlo.
    public String imprimir() {
        return "Póliza " + numero + " - " + aseguradora + "\n"
                + "Cliente: " + vehiculo.getCliente() + "\n"
                + "Vehículo: " + vehiculo + "\n"
                + "Vigencia: " + fechaInicio + " al " + fechaFin + " (" + calcularEstado() + ")\n"
                + "Coberturas: " + coberturas + "\n"
                + "Prima total: " + primaTotal + "  Saldo: " + calcularSaldo();
    }

    // Getters y setters: leen y cambian los atributos privados (encapsulamiento).
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }
    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }
    public LocalDate getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }
    public double getPrimaTotal() { return primaTotal; }
    public void setPrimaTotal(double primaTotal) { this.primaTotal = primaTotal; }
    public Vehiculo getVehiculo() { return vehiculo; }
    public void setVehiculo(Vehiculo vehiculo) { this.vehiculo = vehiculo; }
    public Aseguradora getAseguradora() { return aseguradora; }
    public void setAseguradora(Aseguradora aseguradora) { this.aseguradora = aseguradora; }
    public TipoPoliza getTipoPoliza() { return tipoPoliza; }
    public void setTipoPoliza(TipoPoliza tipoPoliza) { this.tipoPoliza = tipoPoliza; }
    public Cotizacion getCotizacion() { return cotizacion; }
    public void setCotizacion(Cotizacion cotizacion) { this.cotizacion = cotizacion; }
    public Poliza getPolizaAnterior() { return polizaAnterior; }
    public void setPolizaAnterior(Poliza polizaAnterior) { this.polizaAnterior = polizaAnterior; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
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
