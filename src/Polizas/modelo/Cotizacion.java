package Polizas.modelo;

import java.time.LocalDate;

// Capa de modelo: cotización de un seguro para un vehículo, con una aseguradora y un tipo de póliza.
// Si el cliente la acepta, de ella se genera la póliza (RF-09, RF-10).
// Se guarda en la tabla cotizacion por medio de CotizacionDAO.
public class Cotizacion {

    private int id;
    private Vehiculo vehiculo;
    private Aseguradora aseguradora;
    private TipoPoliza tipoPoliza;
    private LocalDate fecha;
    private double costo;
    private FormaPago formaPago;
    private String estado = "PENDIENTE";   // PENDIENTE, ACEPTADA o RECHAZADA

    // Constructor vacío: crea el objeto para después llenarlo con los métodos set.
    public Cotizacion() {
    }

    // El cliente acepta la cotización (después se guarda el cambio con CotizacionDAO.actualizar).
    public void aceptar() {
        if (!"PENDIENTE".equals(estado)) {
            throw new IllegalStateException("Solo se acepta una cotización pendiente.");
        }
        estado = "ACEPTADA";
    }

    // Crea la póliza con los datos de esta cotización (RN-06). Falta ponerle número, fechas,
    // coberturas y usuario, y guardarla con PolizaDAO.
    public Poliza generarPoliza() {
        if (!"ACEPTADA".equals(estado)) {
            throw new IllegalStateException("Solo una cotización aceptada genera una póliza.");
        }
        Poliza p = new Poliza();
        p.setVehiculo(vehiculo);
        p.setAseguradora(aseguradora);
        p.setTipoPoliza(tipoPoliza);
        p.setCotizacion(this);
        p.setPrimaTotal(costo);
        return p;
    }

    // Getters y setters: leen y cambian los atributos privados (encapsulamiento).
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
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
