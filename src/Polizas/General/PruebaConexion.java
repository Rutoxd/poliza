package Polizas.General;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;

// Evidencia de la Fase 3: abre la conexión con la clase Conexion,
// muestra los datos del servidor y cuenta los registros de cada tabla
public class PruebaConexion extends Conexion {

    private static final String[] TABLAS = {
        "marca", "modelo", "aseguradora", "tipoPoliza", "cobertura", "cliente", "usuario",
        "vehiculo", "cotizacion", "poliza", "polizaCobertura", "pago", "avisoVencimiento", "cancelacion"
    };

    public static void main(String[] args) {
        new PruebaConexion().probar();
    }

    // Método: prueba la conexión y consulta la base
    public void probar() {
        try {
            conectar();
            if (cnx == null) {
                System.out.println("No se pudo conectar a la base de datos.");
                return;
            }
            DatabaseMetaData meta = cnx.getMetaData();
            System.out.println("Conexión exitosa");
            System.out.println("Servidor: " + meta.getDatabaseProductName() + " " + meta.getDatabaseProductVersion());
            System.out.println("Driver:   " + meta.getDriverName() + " " + meta.getDriverVersion());
            System.out.println("Base:     " + cnx.getCatalog());
            System.out.println();
            System.out.println("Registros por tabla:");
            for (String tabla : TABLAS) {
                ps = cnx.prepareStatement("SELECT COUNT(*) FROM " + tabla);
                rs = ps.executeQuery();
                rs.next();
                System.out.printf("  %-18s %d%n", tabla, rs.getInt(1));
            }
            System.out.println();
            System.out.println("Estado de las pólizas (vistaPoliza):");
            ps = cnx.prepareStatement("SELECT numero, cliente, fechaFin, estado FROM vistaPoliza ORDER BY numero");
            rs = ps.executeQuery();
            while (rs.next()) {
                System.out.printf("  %-10s %-20s %s  %s%n", rs.getString("numero"), rs.getString("cliente"),
                        rs.getDate("fechaFin"), rs.getString("estado"));
            }
        } catch (SQLException ex) {
            System.out.println("Error: " + ex.getMessage());
        } finally {
            close();
        }
    }
}
