package Polizas.clases;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

// Aviso al cliente de que se acerca el fin de su póliza (tipo POLIZA) o una cuota (tipo PAGO). RF-31 a RF-33.
// Tabla: avisoVencimiento. Corrección de Fase 3: la fecha y el monto no se guardan aquí,
// se toman de la póliza o de la cuota; solo se guarda costoRenovacion en avisos de póliza.
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

    // RF-31: avisa el fin de la vigencia con el costo de renovación y lo guarda.
    public void generarAvisoPoliza(Poliza poliza, MedioAviso medio, double costoRenovacion) throws SQLException {
        if (poliza.calcularEstado() == EstadoPoliza.CANCELADA) {
            throw new IllegalStateException("Una póliza cancelada no recibe avisos.");
        }
        this.tipo = TipoAviso.POLIZA;
        this.poliza = poliza;
        this.medio = medio;
        this.costoRenovacion = costoRenovacion;
        this.fechaAviso = LocalDate.now();
        id = Conexion.insertar("INSERT INTO avisoVencimiento (tipo, idPoliza, fechaAviso, medio, costoRenovacion) "
                + "VALUES ('POLIZA', ?, ?, ?, ?)", poliza.getId(), fechaAviso, medio.name(), costoRenovacion);
    }

    // RF-32: avisa una cuota pendiente y lo guarda (RN-13: solo cuotas sin pagar).
    public void generarAvisoPago(Pago pago, MedioAviso medio) throws SQLException {
        if (pago.getFechaPago() != null) {
            throw new IllegalStateException("Solo se avisan cuotas pendientes.");
        }
        this.tipo = TipoAviso.PAGO;
        this.pago = pago;
        this.medio = medio;
        this.fechaAviso = LocalDate.now();
        id = Conexion.insertar("INSERT INTO avisoVencimiento (tipo, idPago, fechaAviso, medio) VALUES ('PAGO', ?, ?, ?)",
                pago.getId(), fechaAviso, medio.name());
    }

    // Días entre el aviso y el vencimiento (RN-12: deben ser 7 o más).
    public int calcularDiasRestantes() {
        LocalDate vence = tipo == TipoAviso.POLIZA ? poliza.getFechaFin() : pago.getFechaLimite();
        return (int) ChronoUnit.DAYS.between(fechaAviso, vence);
    }

    // RF-33: guarda lo que respondió el cliente.
    public void registrarSeguimiento(String resultado) throws SQLException {
        Conexion.ejecutar("UPDATE avisoVencimiento SET resultado = ? WHERE idAviso = ?", resultado, id);
        this.resultado = resultado;
    }

    // Regresa el aviso con ese id (con su póliza o su cuota), o null si no existe.
    public static AvisoVencimiento consultar(int id) throws SQLException {
        try (Connection cn = Conexion.conectar();
             PreparedStatement ps = cn.prepareStatement("SELECT * FROM avisoVencimiento WHERE idAviso = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                AvisoVencimiento a = new AvisoVencimiento();
                a.id = id;
                a.tipo = TipoAviso.valueOf(rs.getString("tipo"));
                a.fechaAviso = rs.getObject("fechaAviso", LocalDate.class);
                a.medio = MedioAviso.valueOf(rs.getString("medio"));
                a.costoRenovacion = rs.getDouble("costoRenovacion");
                a.resultado = rs.getString("resultado");
                if (a.tipo == TipoAviso.POLIZA) {
                    a.poliza = Poliza.consultar(rs.getInt("idPoliza"));
                } else {
                    a.pago = Pago.consultar(rs.getInt("idPago"));
                }
                return a;
            }
        }
    }

    // Getters y setters: leen y cambian los atributos privados (encapsulamiento).
    public int getId() { return id; }
    public TipoAviso getTipo() { return tipo; }
    public Poliza getPoliza() { return poliza; }
    public Pago getPago() { return pago; }
    public LocalDate getFechaAviso() { return fechaAviso; }
    public MedioAviso getMedio() { return medio; }
    public double getCostoRenovacion() { return costoRenovacion; }
    public String getResultado() { return resultado; }
}
