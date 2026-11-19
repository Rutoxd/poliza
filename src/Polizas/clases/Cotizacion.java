package Polizas.clases;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

// Cotización de un seguro para un vehículo, con una aseguradora y un tipo de póliza.
// Si el cliente la acepta, de ella se genera la póliza (RF-09, RF-10).
// Tabla: cotizacion.
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

    // Guarda la cotización nueva y se queda con el id que le dio la base.
    public void registrar() throws SQLException {
        id = Conexion.insertar("INSERT INTO cotizacion (idVehiculo, idAseguradora, idTipoPoliza, fecha, costo, formaPago, estado) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)", vehiculo.getId(), aseguradora.getId(), tipoPoliza.getId(),
                fecha, costo, formaPago.name(), estado);
    }

    // Regresa la cotización con ese id, o null si no existe.
    public static Cotizacion consultar(int id) throws SQLException {
        try (Connection cn = Conexion.conectar();
             PreparedStatement ps = cn.prepareStatement("SELECT * FROM cotizacion WHERE idCotizacion = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                Cotizacion c = new Cotizacion();
                c.id = id;
                c.vehiculo = Vehiculo.consultar(rs.getInt("idVehiculo"));
                c.aseguradora = Aseguradora.consultar(rs.getInt("idAseguradora"));
                c.tipoPoliza = TipoPoliza.consultar(rs.getInt("idTipoPoliza"));
                c.fecha = rs.getObject("fecha", LocalDate.class);
                c.costo = rs.getDouble("costo");
                c.formaPago = FormaPago.valueOf(rs.getString("formaPago"));
                c.estado = rs.getString("estado");
                return c;
            }
        }
    }

    // El cliente acepta la cotización.
    public void aceptar() throws SQLException {
        Conexion.ejecutar("UPDATE cotizacion SET estado = 'ACEPTADA' WHERE idCotizacion = ?", id);
        estado = "ACEPTADA";
    }

    // Crea la póliza con los datos de esta cotización (RN-06). Falta ponerle número, fechas y
    // usuario, y después llamar a registrar() de la póliza.
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
