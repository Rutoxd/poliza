package Polizas.dao;

import Polizas.modelo.Cobertura;
import Polizas.modelo.Poliza;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// DAO de Poliza (Módulo 3): alta, consulta, modificación y eliminación en las tablas poliza
// y polizaCobertura. Usa los demás DAO para armar la póliza completa (asociación entre clases).
public class PolizaDAO implements Dao<Poliza> {

    private final VehiculoDAO vehiculoDAO = new VehiculoDAO();
    private final AseguradoraDAO aseguradoraDAO = new AseguradoraDAO();
    private final TipoPolizaDAO tipoPolizaDAO = new TipoPolizaDAO();
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final CoberturaDAO coberturaDAO = new CoberturaDAO();
    private final CotizacionDAO cotizacionDAO = new CotizacionDAO();
    private final PagoDAO pagoDAO = new PagoDAO();
    private final CancelacionDAO cancelacionDAO = new CancelacionDAO();

    // Guarda la póliza y sus coberturas en una sola transacción: o se guarda todo o nada (RNF-09).
    // Antes revisa RN-04: el vehículo no puede tener otra póliza vigente en esas fechas.
    @Override
    public void registrar(Poliza p) throws SQLException {
        p.validar();
        try (Connection cn = Conexion.conectar()) {
            cn.setAutoCommit(false);
            try {
                revisarVigencia(cn, p);
                p.setId(Conexion.insertar(cn, "INSERT INTO poliza (numero, idAseguradora, idTipoPoliza, idVehiculo, "
                        + "idCotizacion, idPolizaAnterior, idUsuario, fechaInicio, fechaFin, primaTotal) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                        p.getNumero(), p.getAseguradora().getId(), p.getTipoPoliza().getId(), p.getVehiculo().getId(),
                        p.getCotizacion() == null ? null : p.getCotizacion().getId(),
                        p.getPolizaAnterior() == null ? null : p.getPolizaAnterior().getId(),
                        p.getUsuario().getId(), p.getFechaInicio(), p.getFechaFin(), p.getPrimaTotal()));
                guardarCoberturas(cn, p);
                cn.commit();
            } catch (SQLException | RuntimeException e) {
                cn.rollback();
                throw e;
            }
        }
    }

    // Guarda los cambios de la póliza y reemplaza sus coberturas, también en una transacción.
    @Override
    public void actualizar(Poliza p) throws SQLException {
        p.validar();
        try (Connection cn = Conexion.conectar()) {
            cn.setAutoCommit(false);
            try {
                revisarVigencia(cn, p);
                Conexion.ejecutar(cn, "UPDATE poliza SET numero = ?, idAseguradora = ?, idTipoPoliza = ?, idVehiculo = ?, "
                        + "fechaInicio = ?, fechaFin = ?, primaTotal = ? WHERE idPoliza = ?",
                        p.getNumero(), p.getAseguradora().getId(), p.getTipoPoliza().getId(), p.getVehiculo().getId(),
                        p.getFechaInicio(), p.getFechaFin(), p.getPrimaTotal(), p.getId());
                Conexion.ejecutar(cn, "DELETE FROM polizaCobertura WHERE idPoliza = ?", p.getId());
                guardarCoberturas(cn, p);
                cn.commit();
            } catch (SQLException | RuntimeException e) {
                cn.rollback();
                throw e;
            }
        }
    }

    // RF-17: borra la póliza (sus coberturas se borran solas). La base no lo permite si ya
    // tiene pagos, avisos o cancelación.
    @Override
    public void eliminar(int id) throws SQLException {
        Conexion.ejecutar("DELETE FROM poliza WHERE idPoliza = ?", id);
    }

    // Regresa la póliza con su vehículo (y cliente), aseguradora, tipo, usuario, coberturas,
    // pagos y cancelación; o null si no existe.
    @Override
    public Poliza consultar(int id) throws SQLException {
        Poliza p = new Poliza();
        int idVehiculo, idAseguradora, idTipo, idUsuario, idCotizacion, idAnterior;
        try (Connection cn = Conexion.conectar();
             PreparedStatement ps = Conexion.preparar(cn, "SELECT * FROM poliza WHERE idPoliza = ?", id);
             ResultSet rs = ps.executeQuery()) {
            if (!rs.next()) {
                return null;
            }
            p.setId(id);
            p.setNumero(rs.getString("numero"));
            p.setFechaInicio(rs.getObject("fechaInicio", LocalDate.class));
            p.setFechaFin(rs.getObject("fechaFin", LocalDate.class));
            p.setPrimaTotal(rs.getDouble("primaTotal"));
            idVehiculo = rs.getInt("idVehiculo");
            idAseguradora = rs.getInt("idAseguradora");
            idTipo = rs.getInt("idTipoPoliza");
            idUsuario = rs.getInt("idUsuario");
            idCotizacion = rs.getInt("idCotizacion");
            idAnterior = rs.getInt("idPolizaAnterior");
        }
        p.setVehiculo(vehiculoDAO.consultar(idVehiculo));
        p.setAseguradora(aseguradoraDAO.consultar(idAseguradora));
        p.setTipoPoliza(tipoPolizaDAO.consultar(idTipo));
        p.setUsuario(usuarioDAO.consultar(idUsuario));
        p.setCoberturas(coberturaDAO.listarPorPoliza(id));
        p.setPagos(pagoDAO.listarPorPoliza(p));
        p.setCancelacion(cancelacionDAO.consultarPorPoliza(p));
        if (idCotizacion > 0) {
            p.setCotizacion(cotizacionDAO.consultar(idCotizacion));
        }
        if (idAnterior > 0) {
            Poliza anterior = new Poliza();   // solo su id, para no cargar toda la cadena de renovaciones
            anterior.setId(idAnterior);
            p.setPolizaAnterior(anterior);
        }
        return p;
    }

    // RF-12: todas las pólizas, de la que vence primero a la última.
    @Override
    public List<Poliza> listar() throws SQLException {
        return listarIds("SELECT idPoliza FROM poliza ORDER BY fechaFin");
    }

    // RF-12: pólizas de un cliente (a través de sus vehículos).
    public List<Poliza> listarPorCliente(int idCliente) throws SQLException {
        return listarIds("SELECT p.idPoliza FROM poliza p JOIN vehiculo v ON v.idVehiculo = p.idVehiculo "
                + "WHERE v.idCliente = ? ORDER BY p.fechaFin", idCliente);
    }

    // RF-15: historial de pólizas de un vehículo.
    public List<Poliza> listarPorVehiculo(int idVehiculo) throws SQLException {
        return listarIds("SELECT idPoliza FROM poliza WHERE idVehiculo = ? ORDER BY fechaInicio", idVehiculo);
    }

    // RF-28: pólizas que vencen en los próximos 7 días o que ya vencieron.
    public List<Poliza> listarPorVencer() throws SQLException {
        return listarIds("SELECT idPoliza FROM poliza WHERE fechaFin <= DATE_ADD(CURDATE(), INTERVAL ? DAY) "
                + "ORDER BY fechaFin", Poliza.DIAS_POR_VENCER);
    }

    // Busca los ids con el SELECT dado y arma cada póliza completa con consultar().
    private List<Poliza> listarIds(String sql, Object... datos) throws SQLException {
        List<Integer> ids = new ArrayList<>();
        try (Connection cn = Conexion.conectar();
             PreparedStatement ps = Conexion.preparar(cn, sql, datos);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                ids.add(rs.getInt(1));
            }
        }
        List<Poliza> lista = new ArrayList<>();
        for (int id : ids) {
            lista.add(consultar(id));
        }
        return lista;
    }

    // RN-04: si el vehículo ya tiene otra póliza en esas fechas, no deja guardar.
    private void revisarVigencia(Connection cn, Poliza p) throws SQLException {
        if (vehiculoDAO.tienePolizaVigente(cn, p.getVehiculo().getId(), p.getFechaInicio(), p.getFechaFin(), p.getId())) {
            throw new IllegalStateException("El vehículo ya tiene una póliza vigente en esas fechas (RN-04).");
        }
    }

    // Guarda en polizaCobertura una fila por cada cobertura de la póliza.
    private void guardarCoberturas(Connection cn, Poliza p) throws SQLException {
        for (Cobertura c : p.getCoberturas()) {
            Conexion.ejecutar(cn, "INSERT INTO polizaCobertura (idPoliza, idCobertura) VALUES (?, ?)", p.getId(), c.getId());
        }
    }
}
