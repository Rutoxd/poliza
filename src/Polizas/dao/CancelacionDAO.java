package Polizas.dao;

import Polizas.modelo.Cancelacion;
import Polizas.modelo.EstadoCancelacion;
import Polizas.modelo.Poliza;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// DAO de Cancelacion: alta, consulta, modificación y eliminación en la tabla cancelacion.
public class CancelacionDAO implements Dao<Cancelacion> {

    // RF-21: guarda la solicitud. La base solo permite una cancelación por póliza.
    @Override
    public void registrar(Cancelacion c) throws SQLException {
        c.setId(Conexion.insertar("INSERT INTO cancelacion (idPoliza, fechaSolicitud, motivo, estado, montoDevolucion) "
                + "VALUES (?, ?, ?, ?, ?)", c.getPoliza().getId(), c.getFechaSolicitud(), c.getMotivo(),
                c.getEstado().name(), c.getMontoDevolucion() > 0 ? c.getMontoDevolucion() : null));
    }

    // Guarda el paso al que avanzó (validar, autorizar, registrar baja) y la devolución.
    @Override
    public void actualizar(Cancelacion c) throws SQLException {
        Conexion.ejecutar("UPDATE cancelacion SET motivo = ?, estado = ?, montoDevolucion = ? WHERE idCancelacion = ?",
                c.getMotivo(), c.getEstado().name(), c.getMontoDevolucion() > 0 ? c.getMontoDevolucion() : null, c.getId());
    }

    @Override
    public void eliminar(int id) throws SQLException {
        Conexion.ejecutar("DELETE FROM cancelacion WHERE idCancelacion = ?", id);
    }

    // Regresa la cancelación con ese id, junto con su póliza completa; o null si no existe.
    @Override
    public Cancelacion consultar(int id) throws SQLException {
        int idPoliza;
        try (Connection cn = Conexion.conectar();
             PreparedStatement ps = Conexion.preparar(cn, "SELECT idPoliza FROM cancelacion WHERE idCancelacion = ?", id);
             ResultSet rs = ps.executeQuery()) {
            if (!rs.next()) {
                return null;
            }
            idPoliza = rs.getInt(1);
        }
        return new PolizaDAO().consultar(idPoliza).getCancelacion();
    }

    // Todas las cancelaciones.
    @Override
    public List<Cancelacion> listar() throws SQLException {
        List<Cancelacion> lista = new ArrayList<>();
        for (Poliza p : new PolizaDAO().listar()) {
            if (p.getCancelacion() != null) {
                lista.add(p.getCancelacion());
            }
        }
        return lista;
    }

    // La cancelación de una póliza, ligada a ella; o null si no tiene.
    public Cancelacion consultarPorPoliza(Poliza poliza) throws SQLException {
        try (Connection cn = Conexion.conectar();
             PreparedStatement ps = Conexion.preparar(cn, "SELECT * FROM cancelacion WHERE idPoliza = ?", poliza.getId());
             ResultSet rs = ps.executeQuery()) {
            if (!rs.next()) {
                return null;
            }
            Cancelacion c = new Cancelacion();
            c.setId(rs.getInt("idCancelacion"));
            c.setPoliza(poliza);
            c.setFechaSolicitud(rs.getObject("fechaSolicitud", LocalDate.class));
            c.setMotivo(rs.getString("motivo"));
            c.setEstado(EstadoCancelacion.valueOf(rs.getString("estado")));
            c.setMontoDevolucion(rs.getDouble("montoDevolucion"));
            return c;
        }
    }
}
