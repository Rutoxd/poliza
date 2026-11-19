package Polizas.clases;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

// Cancelación de una póliza (composición: no existe sin su póliza). RF-21 a RF-23.
// Pasos: SOLICITADA -> VALIDADA (vendedor) -> AUTORIZADA (encargado) -> BAJA_REGISTRADA (administrador).
// Tabla: cancelacion.
public class Cancelacion {

    private int id;
    private Poliza poliza;
    private LocalDate fechaSolicitud;
    private String motivo;
    private EstadoCancelacion estado;
    private double montoDevolucion;

    // Constructor vacío: crea el objeto para después llenarlo con los métodos set.
    public Cancelacion() {
    }

    // El cliente pide la cancelación: se guarda como SOLICITADA. Se llama desde Poliza.cancelar().
    public void solicitar() throws SQLException {
        if (motivo == null || motivo.isBlank()) {
            throw new IllegalStateException("El motivo es obligatorio.");
        }
        fechaSolicitud = LocalDate.now();
        estado = EstadoCancelacion.SOLICITADA;
        id = Conexion.insertar("INSERT INTO cancelacion (idPoliza, fechaSolicitud, motivo, estado) VALUES (?, ?, ?, ?)",
                poliza.getId(), fechaSolicitud, motivo, estado.name());
    }

    // RN-16: el vendedor la valida.
    public void validar() throws SQLException {
        cambiarEstado(EstadoCancelacion.SOLICITADA, EstadoCancelacion.VALIDADA);
    }

    // RN-17: el Encargado de gestión interna la autoriza.
    public void autorizar() throws SQLException {
        cambiarEstado(EstadoCancelacion.VALIDADA, EstadoCancelacion.AUTORIZADA);
    }

    // RF-23: el Administrador registra la baja ante la aseguradora. Desde aquí la póliza queda CANCELADA.
    public void registrarBaja() throws SQLException {
        cambiarEstado(EstadoCancelacion.AUTORIZADA, EstadoCancelacion.BAJA_REGISTRADA);
    }

    // La devolución la decide la aseguradora; el sistema solo la guarda.
    public double calcularDevolucion() {
        return montoDevolucion;
    }

    // Avanza un paso solo si la cancelación está en el paso anterior.
    private void cambiarEstado(EstadoCancelacion requerido, EstadoCancelacion nuevo) throws SQLException {
        if (estado != requerido) {
            throw new IllegalStateException("La cancelación debe estar " + requerido + " para pasar a " + nuevo + ".");
        }
        Conexion.ejecutar("UPDATE cancelacion SET estado = ? WHERE idCancelacion = ?", nuevo.name(), id);
        estado = nuevo;
    }

    // Arma una Cancelacion con la fila actual de un SELECT * FROM cancelacion (lo usa Poliza.consultar).
    static Cancelacion leer(ResultSet rs, Poliza poliza) throws SQLException {
        Cancelacion c = new Cancelacion();
        c.id = rs.getInt("idCancelacion");
        c.poliza = poliza;
        c.fechaSolicitud = rs.getObject("fechaSolicitud", LocalDate.class);
        c.motivo = rs.getString("motivo");
        c.estado = EstadoCancelacion.valueOf(rs.getString("estado"));
        c.montoDevolucion = rs.getDouble("montoDevolucion");
        return c;
    }

    // Getters y setters: leen y cambian los atributos privados (encapsulamiento).
    public int getId() { return id; }
    public Poliza getPoliza() { return poliza; }
    public void setPoliza(Poliza poliza) { this.poliza = poliza; }
    public LocalDate getFechaSolicitud() { return fechaSolicitud; }
    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
    public EstadoCancelacion getEstado() { return estado; }
    public double getMontoDevolucion() { return montoDevolucion; }
    public void setMontoDevolucion(double montoDevolucion) { this.montoDevolucion = montoDevolucion; }
}
