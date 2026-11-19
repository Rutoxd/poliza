package Polizas.clases;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

// Póliza de seguro de un vehículo. Es la clase central del sistema.
// Tablas: poliza y polizaCobertura (las coberturas que incluye).
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

    public Poliza() {
    }

    // Guarda la póliza y sus coberturas. RN-03: debe tener al menos una cobertura.
    public void registrar() throws SQLException {
        if (coberturas.isEmpty()) {
            throw new IllegalStateException("La póliza debe incluir al menos una cobertura.");
        }
        id = Conexion.insertar("INSERT INTO poliza (numero, idAseguradora, idTipoPoliza, idVehiculo, idCotizacion, "
                + "idPolizaAnterior, idUsuario, fechaInicio, fechaFin, primaTotal) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                numero, aseguradora.getId(), tipoPoliza.getId(), vehiculo.getId(),
                cotizacion == null ? null : cotizacion.getId(),
                polizaAnterior == null ? null : polizaAnterior.getId(),
                usuario.getId(), fechaInicio, fechaFin, primaTotal);
        guardarCoberturas();
    }

    // Guarda los cambios de la póliza y vuelve a guardar sus coberturas.
    public void actualizar() throws SQLException {
        Conexion.ejecutar("UPDATE poliza SET numero = ?, idAseguradora = ?, idTipoPoliza = ?, idVehiculo = ?, "
                + "fechaInicio = ?, fechaFin = ?, primaTotal = ? WHERE idPoliza = ?",
                numero, aseguradora.getId(), tipoPoliza.getId(), vehiculo.getId(), fechaInicio, fechaFin, primaTotal, id);
        Conexion.ejecutar("DELETE FROM polizaCobertura WHERE idPoliza = ?", id);
        guardarCoberturas();
    }

    private void guardarCoberturas() throws SQLException {
        for (Cobertura c : coberturas) {
            Conexion.ejecutar("INSERT INTO polizaCobertura (idPoliza, idCobertura) VALUES (?, ?)", id, c.getId());
        }
    }

    // Borra la póliza (sus coberturas se borran solas). La base no lo permite si ya tiene pagos.
    public void eliminar() throws SQLException {
        Conexion.ejecutar("DELETE FROM poliza WHERE idPoliza = ?", id);
    }

    // Regresa la póliza con ese id, con sus coberturas, pagos y cancelación; o null si no existe.
    public static Poliza consultar(int id) throws SQLException {
        Poliza p = new Poliza();
        int idCotizacion, idAnterior, idUsuario;
        try (Connection cn = Conexion.conectar()) {
            try (PreparedStatement ps = cn.prepareStatement("SELECT * FROM poliza WHERE idPoliza = ?")) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        return null;
                    }
                    p.id = id;
                    p.numero = rs.getString("numero");
                    p.fechaInicio = rs.getObject("fechaInicio", LocalDate.class);
                    p.fechaFin = rs.getObject("fechaFin", LocalDate.class);
                    p.primaTotal = rs.getDouble("primaTotal");
                    p.vehiculo = Vehiculo.consultar(rs.getInt("idVehiculo"));
                    p.aseguradora = Aseguradora.consultar(rs.getInt("idAseguradora"));
                    p.tipoPoliza = TipoPoliza.consultar(rs.getInt("idTipoPoliza"));
                    idCotizacion = rs.getInt("idCotizacion");
                    idAnterior = rs.getInt("idPolizaAnterior");
                    idUsuario = rs.getInt("idUsuario");
                }
            }
            String sql = "SELECT c.* FROM polizaCobertura pc JOIN cobertura c ON c.idCobertura = pc.idCobertura "
                    + "WHERE pc.idPoliza = ?";
            try (PreparedStatement ps = cn.prepareStatement(sql)) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        p.coberturas.add(new Cobertura(rs.getInt("idCobertura"), rs.getString("nombre"),
                                rs.getString("descripcion")));
                    }
                }
            }
            try (PreparedStatement ps = cn.prepareStatement("SELECT * FROM pago WHERE idPoliza = ? ORDER BY numeroCuota")) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        p.pagos.add(Pago.leer(rs, p));
                    }
                }
            }
            try (PreparedStatement ps = cn.prepareStatement("SELECT * FROM cancelacion WHERE idPoliza = ?")) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        p.cancelacion = Cancelacion.leer(rs, p);
                    }
                }
            }
        }
        if (idCotizacion > 0) {
            p.cotizacion = Cotizacion.consultar(idCotizacion);
        }
        if (idAnterior > 0) {
            p.polizaAnterior = new Poliza();      // solo su id, para no cargar toda la cadena de renovaciones
            p.polizaAnterior.id = idAnterior;
        }
        p.usuario = new Usuario();
        p.usuario.setId(idUsuario);
        return p;
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
    // cuando termina esta. Falta ponerle número, prima y usuario, y llamar a registrar().
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

    // RF-21: inicia la cancelación de la póliza (RN-18: solo una vez).
    public void cancelar(String motivo) throws SQLException {
        if (cancelacion != null) {
            throw new IllegalStateException("La póliza ya tiene una cancelación.");
        }
        Cancelacion c = new Cancelacion();
        c.setPoliza(this);
        c.setMotivo(motivo);
        c.solicitar();
        cancelacion = c;
    }

    // RF-18: por ahora muestra la póliza en consola; el formato para imprimir llega en la Fase 4.
    public void imprimir() {
        System.out.println("Póliza " + numero + " - " + aseguradora);
        System.out.println("Cliente: " + vehiculo.getCliente());
        System.out.println("Vehículo: " + vehiculo);
        System.out.println("Vigencia: " + fechaInicio + " al " + fechaFin + " (" + calcularEstado() + ")");
        System.out.println("Coberturas: " + coberturas);
        System.out.println("Prima total: " + primaTotal + "  Saldo: " + calcularSaldo());
    }

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
    public Cancelacion getCancelacion() { return cancelacion; }

    @Override
    public String toString() {
        return numero;
    }
}
