package Polizas.dao;

import Polizas.modelo.Pago;
import Polizas.modelo.Poliza;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// DAO de Pago: alta, consulta, modificación y eliminación en la tabla pago.
public class PagoDAO implements Dao<Pago> {

    // RF-26: guarda la cuota. RN-08 y RN-09: monto positivo y sin pasar del saldo de la póliza.
    @Override
    public void registrar(Pago p) throws SQLException {
        if (!p.validarMonto()) {
            throw new IllegalArgumentException("El monto debe ser mayor que cero y no pasar del saldo de la póliza.");
        }
        p.setId(Conexion.insertar("INSERT INTO pago (idPoliza, numeroCuota, monto, fechaLimite, fechaPago, metodoPago, "
                + "comprobante) VALUES (?, ?, ?, ?, ?, ?, ?)", p.getPoliza().getId(), p.getNumeroCuota(), p.getMonto(),
                p.getFechaLimite(), p.getFechaPago(), p.getMetodoPago(), p.getComprobante()));
    }

    // Guarda los cambios de la cuota; por ejemplo, cuando se paga (fecha, método y comprobante).
    @Override
    public void actualizar(Pago p) throws SQLException {
        Conexion.ejecutar("UPDATE pago SET numeroCuota = ?, monto = ?, fechaLimite = ?, fechaPago = ?, metodoPago = ?, "
                + "comprobante = ? WHERE idPago = ?", p.getNumeroCuota(), p.getMonto(), p.getFechaLimite(),
                p.getFechaPago(), p.getMetodoPago(), p.getComprobante(), p.getId());
    }

    // La base no deja borrar una cuota que ya tiene avisos.
    @Override
    public void eliminar(int id) throws SQLException {
        Conexion.ejecutar("DELETE FROM pago WHERE idPago = ?", id);
    }

    // Regresa la cuota con ese id, junto con su póliza completa; o null si no existe.
    @Override
    public Pago consultar(int id) throws SQLException {
        int idPoliza;
        try (Connection cn = Conexion.conectar();
             PreparedStatement ps = Conexion.preparar(cn, "SELECT idPoliza FROM pago WHERE idPago = ?", id);
             ResultSet rs = ps.executeQuery()) {
            if (!rs.next()) {
                return null;
            }
            idPoliza = rs.getInt(1);
        }
        for (Pago p : new PolizaDAO().consultar(idPoliza).getPagos()) {
            if (p.getId() == id) {
                return p;
            }
        }
        return null;
    }

    // Todas las cuotas de todas las pólizas.
    @Override
    public List<Pago> listar() throws SQLException {
        List<Pago> lista = new ArrayList<>();
        for (Poliza p : new PolizaDAO().listar()) {
            lista.addAll(p.getPagos());
        }
        return lista;
    }

    // RF-27: cuotas de una póliza, ordenadas por número de cuota. Cada una queda ligada a la póliza.
    public List<Pago> listarPorPoliza(Poliza poliza) throws SQLException {
        List<Pago> lista = new ArrayList<>();
        try (Connection cn = Conexion.conectar();
             PreparedStatement ps = Conexion.preparar(cn, "SELECT * FROM pago WHERE idPoliza = ? ORDER BY numeroCuota",
                     poliza.getId());
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Pago p = new Pago();
                p.setId(rs.getInt("idPago"));
                p.setPoliza(poliza);
                p.setNumeroCuota(rs.getInt("numeroCuota"));
                p.setMonto(rs.getDouble("monto"));
                p.setFechaLimite(rs.getObject("fechaLimite", LocalDate.class));
                p.setFechaPago(rs.getObject("fechaPago", LocalDate.class));
                p.setMetodoPago(rs.getString("metodoPago"));
                p.setComprobante(rs.getString("comprobante"));
                lista.add(p);
            }
        }
        return lista;
    }
}
