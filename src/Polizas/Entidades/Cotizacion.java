package Polizas.Entidades;

import java.time.LocalDate;

// Cotización de una póliza para un vehículo (RF-09, RN-06)
public class Cotizacion {

    private int id;
    private Vehiculo vehiculo;
    private Aseguradora aseguradora;
    private TipoPoliza tipoPoliza;
    private LocalDate fecha;
    private double costo;
    private FormaPago formaPago;
    private EstadoCotizacion estado = EstadoCotizacion.PENDIENTE;

    public Cotizacion() {
    }

    public Cotizacion(int id, Vehiculo vehiculo, Aseguradora aseguradora, TipoPoliza tipoPoliza,
                      LocalDate fecha, double costo, FormaPago formaPago, EstadoCotizacion estado) {
        this.id = id;
        this.vehiculo = vehiculo;
        this.aseguradora = aseguradora;
        this.tipoPoliza = tipoPoliza;
        this.fecha = fecha;
        this.costo = costo;
        this.formaPago = formaPago;
        this.estado = estado;
    }

    public void aceptar() {
        this.estado = EstadoCotizacion.ACEPTADA;
    }

    // Método: RF-10, la póliza nace de una cotización aceptada con los mismos datos
    public Poliza generarPoliza(String numero, LocalDate inicio, LocalDate fin, Usuario registro) {
        if (estado != EstadoCotizacion.ACEPTADA) {
            throw new IllegalStateException("Solo una cotización aceptada genera una póliza.");
        }
        Poliza p = new Poliza();
        p.setNumero(numero);
        p.setVehiculo(vehiculo);
        p.setAseguradora(aseguradora);
        p.setTipoPoliza(tipoPoliza);
        p.setCotizacion(this);
        p.setUsuario(registro);
        p.setFechaInicio(inicio);
        p.setFechaFin(fin);
        p.setPrimaTotal(costo);
        return p;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public Vehiculo getVehiculo() { return vehiculo; }
    public void setVehiculo(Vehiculo vehiculo) { this.vehiculo = vehiculo; }
    public Aseguradora getAseguradora() { return aseguradora; }
    public void setAseguradora(Aseguradora aseguradora) { this.aseguradora = aseguradora; }
    public TipoPoliza getTipoPoliza() { return tipoPoliza; }
    public void setTipoPoliza(TipoPoliza tipoPoliza) { this.tipoPoliza = tipoPoliza; }
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public double getCosto() { return costo; }
    public void setCosto(double costo) { this.costo = costo; }
    public FormaPago getFormaPago() { return formaPago; }
    public void setFormaPago(FormaPago formaPago) { this.formaPago = formaPago; }
    public EstadoCotizacion getEstado() { return estado; }
    public void setEstado(EstadoCotizacion estado) { this.estado = estado; }
}
