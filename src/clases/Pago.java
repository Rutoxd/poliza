package Polizas.clases;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

// Cuota de pago de una póliza (composición: no existe sin su póliza). RF-26.
// Tabla: pago. Corrección de Fase 3: el estado no se guarda; estaVencido() lo calcula.
public class Pago {

    private int id;
    private Poliza poliza;
    private int numeroCuota;
    private double monto;
    private LocalDate fechaLimite;
    private LocalDate fechaPago;     // vacía mientras no se paga
    private String metodoPago;       // EFECTIVO, TRANSFERENCIA, TARJETA o DEPOSITO
    private String comprobante;

    public Pago() {
    }

    // Guarda la cuota nueva (con o sin fecha de pago).
    public void registrar() throws SQLException {
        if (!validarMonto()) {
            throw new IllegalStateException("El monto debe ser mayor que cero y no pasar del saldo de la póliza.");
        }
        id = Conexion.insertar("INSERT INTO pago (idPoliza, numeroCuota, monto, fechaLimite, fechaPago, metodoPago, comprobante) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)", poliza.getId(), numeroCuota, monto, fechaLimite, fechaPago, metodoPago, comprobante);
    }

    // Regresa la cuota con ese id (junto con su póliza), o null si no existe.
    public static Pago consultar(int id) throws SQLException {
        int idPoliza;
        try (Connection cn = Conexion.conectar();
             PreparedStatement ps = cn.prepareStatement("SELECT idPoliza FROM pago WHERE idPago = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                idPoliza = rs.getInt(1);
            }
        }
        for (Pago p : Poliza.consultar(idPoliza).getPagos()) {
            if (p.getId() == id) {
                return p;
            }
        }
        return null;
    }

    // Arma un Pago con la fila actual de un SELECT * FROM pago (lo usa Poliza.consultar).
    static Pago leer(ResultSet rs, Poliza poliza) throws SQLException {
        Pago p = new Pago();
        p.id = rs.getInt("idPago");
        p.poliza = poliza;
        p.numeroCuota = rs.getInt("numeroCuota");
        p.monto = rs.getDouble("monto");
        p.fechaLimite = rs.getObject("fechaLimite", LocalDate.class);
        p.fechaPago = rs.getObject("fechaPago", LocalDate.class);
        p.metodoPago = rs.getString("metodoPago");
        p.comprobante = rs.getString("comprobante");
        return p;
    }

    // RN-08 y RN-09: monto positivo y que no pase del saldo de la póliza.
    public boolean validarMonto() {
        return monto > 0 && (poliza == null || monto <= poliza.calcularSaldo());
    }

    // RN-10: la cuota está vencida si pasó la fecha límite y no se ha pagado.
    public boolean estaVencido() {
        return fechaPago == null && fechaLimite.isBefore(LocalDate.now());
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
    public String getMetodoPago() { return metodoPago; }
    public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }
    public String getComprobante() { return comprobante; }
    public void setComprobante(String comprobante) { this.comprobante = comprobante; }
}
