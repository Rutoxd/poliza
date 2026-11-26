package Polizas.dao;

import Polizas.modelo.Cotizacion;
import Polizas.modelo.FormaPago;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// DAO de Cotizacion: alta, consulta, modificación y eliminación en la tabla cotizacion.
public class CotizacionDAO implements Dao<Cotizacion> {

    private final VehiculoDAO vehiculoDAO = new VehiculoDAO();
    private final AseguradoraDAO aseguradoraDAO = new AseguradoraDAO();
    private final TipoPolizaDAO tipoPolizaDAO = new TipoPolizaDAO();

    // RF-09: guarda la cotización con su vehículo, aseguradora y tipo de póliza (RN-06).
    @Override
    public void registrar(Cotizacion c) throws SQLException {
        c.setId(Conexion.insertar("INSERT INTO cotizacion (idVehiculo, idAseguradora, idTipoPoliza, fecha, costo, "
                + "formaPago, estado) VALUES (?, ?, ?, ?, ?, ?, ?)", c.getVehiculo().getId(), c.getAseguradora().getId(),
                c.getTipoPoliza().getId(), c.getFecha(), c.getCosto(), c.getFormaPago().name(), c.getEstado()));
    }

    // Guarda los cambios; por ejemplo, cuando el cliente la acepta.
    @Override
    public void actualizar(Cotizacion c) throws SQLException {
        Conexion.ejecutar("UPDATE cotizacion SET idVehiculo = ?, idAseguradora = ?, idTipoPoliza = ?, fecha = ?, "
                + "costo = ?, formaPago = ?, estado = ? WHERE idCotizacion = ?", c.getVehiculo().getId(),
                c.getAseguradora().getId(), c.getTipoPoliza().getId(), c.getFecha(), c.getCosto(),
                c.getFormaPago().name(), c.getEstado(), c.getId());
    }

    // La base no deja borrar una cotización que ya generó una póliza.
    @Override
    public void eliminar(int id) throws SQLException {
        Conexion.ejecutar("DELETE FROM cotizacion WHERE idCotizacion = ?", id);
    }

    @Override
    public Cotizacion consultar(int id) throws SQLException {
        List<Cotizacion> lista = buscar("SELECT * FROM cotizacion WHERE idCotizacion = ?", id);
        return lista.isEmpty() ? null : lista.get(0);
    }

    @Override
    public List<Cotizacion> listar() throws SQLException {
        return buscar("SELECT * FROM cotizacion ORDER BY fecha DESC");
    }

    // Ejecuta un SELECT de cotizacion y arma cada cotización con su vehículo, aseguradora y tipo.
    private List<Cotizacion> buscar(String sql, Object... datos) throws SQLException {
        List<Cotizacion> lista = new ArrayList<>();
        List<int[]> llaves = new ArrayList<>();   // idVehiculo, idAseguradora, idTipoPoliza de cada fila
        try (Connection cn = Conexion.conectar();
             PreparedStatement ps = Conexion.preparar(cn, sql, datos);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Cotizacion c = new Cotizacion();
                c.setId(rs.getInt("idCotizacion"));
                c.setFecha(rs.getObject("fecha", LocalDate.class));
                c.setCosto(rs.getDouble("costo"));
                c.setFormaPago(FormaPago.valueOf(rs.getString("formaPago")));
                c.setEstado(rs.getString("estado"));
                lista.add(c);
                llaves.add(new int[]{rs.getInt("idVehiculo"), rs.getInt("idAseguradora"), rs.getInt("idTipoPoliza")});
            }
        }
        for (int i = 0; i < lista.size(); i++) {
            lista.get(i).setVehiculo(vehiculoDAO.consultar(llaves.get(i)[0]));
            lista.get(i).setAseguradora(aseguradoraDAO.consultar(llaves.get(i)[1]));
            lista.get(i).setTipoPoliza(tipoPolizaDAO.consultar(llaves.get(i)[2]));
        }
        return lista;
    }
}
