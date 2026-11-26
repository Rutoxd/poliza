package Polizas.dao;

import Polizas.modelo.AvisoVencimiento;
import Polizas.modelo.MedioAviso;
import Polizas.modelo.TipoAviso;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// DAO de AvisoVencimiento: alta, consulta, modificación y eliminación en la tabla avisoVencimiento.
// Un aviso de tipo POLIZA guarda solo idPoliza; uno de tipo PAGO, solo idPago (ck_aviso_tipo).
public class AvisoVencimientoDAO implements Dao<AvisoVencimiento> {

    // RF-31 y RF-32: guarda el aviso generado.
    @Override
    public void registrar(AvisoVencimiento a) throws SQLException {
        boolean esPoliza = a.getTipo() == TipoAviso.POLIZA;
        a.setId(Conexion.insertar("INSERT INTO avisoVencimiento (tipo, idPoliza, idPago, fechaAviso, medio, "
                + "costoRenovacion, resultado) VALUES (?, ?, ?, ?, ?, ?, ?)", a.getTipo().name(),
                esPoliza ? a.getPoliza().getId() : null, esPoliza ? null : a.getPago().getId(),
                a.getFechaAviso(), a.getMedio().name(), esPoliza ? a.getCostoRenovacion() : null, a.getResultado()));
    }

    // RF-33: guarda el seguimiento (lo que respondió el cliente) y el medio.
    @Override
    public void actualizar(AvisoVencimiento a) throws SQLException {
        Conexion.ejecutar("UPDATE avisoVencimiento SET medio = ?, resultado = ? WHERE idAviso = ?",
                a.getMedio().name(), a.getResultado(), a.getId());
    }

    @Override
    public void eliminar(int id) throws SQLException {
        Conexion.ejecutar("DELETE FROM avisoVencimiento WHERE idAviso = ?", id);
    }

    @Override
    public AvisoVencimiento consultar(int id) throws SQLException {
        List<AvisoVencimiento> lista = buscar("SELECT * FROM avisoVencimiento WHERE idAviso = ?", id);
        return lista.isEmpty() ? null : lista.get(0);
    }

    @Override
    public List<AvisoVencimiento> listar() throws SQLException {
        return buscar("SELECT * FROM avisoVencimiento ORDER BY fechaAviso DESC");
    }

    // Ejecuta un SELECT de avisoVencimiento y liga cada aviso con su póliza o su cuota.
    private List<AvisoVencimiento> buscar(String sql, Object... datos) throws SQLException {
        List<AvisoVencimiento> lista = new ArrayList<>();
        List<Integer> referencias = new ArrayList<>();   // idPoliza o idPago de cada fila
        try (Connection cn = Conexion.conectar();
             PreparedStatement ps = Conexion.preparar(cn, sql, datos);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                AvisoVencimiento a = new AvisoVencimiento();
                a.setId(rs.getInt("idAviso"));
                a.setTipo(TipoAviso.valueOf(rs.getString("tipo")));
                a.setFechaAviso(rs.getObject("fechaAviso", LocalDate.class));
                a.setMedio(MedioAviso.valueOf(rs.getString("medio")));
                a.setCostoRenovacion(rs.getDouble("costoRenovacion"));
                a.setResultado(rs.getString("resultado"));
                lista.add(a);
                referencias.add(a.getTipo() == TipoAviso.POLIZA ? rs.getInt("idPoliza") : rs.getInt("idPago"));
            }
        }
        for (int i = 0; i < lista.size(); i++) {
            AvisoVencimiento a = lista.get(i);
            if (a.getTipo() == TipoAviso.POLIZA) {
                a.setPoliza(new PolizaDAO().consultar(referencias.get(i)));
            } else {
                a.setPago(new PagoDAO().consultar(referencias.get(i)));
            }
        }
        return lista;
    }
}
