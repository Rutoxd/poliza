package Polizas.clases;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

// Conexión con la base de datos "polizas" (MySQL o MariaDB).
// Las demás clases la usan dentro de sus métodos registrar(), actualizar(), eliminar() y consultar().
// Si se ejecuta sola (tiene main), prueba la conexión y muestra cuántos registros hay en cada tabla.
public class Conexion {

    // Datos de conexión: la base se crea con sql/polizas.sql
    private static final String URL = "jdbc:mariadb://localhost:3306/polizas";
    private static final String USUARIO = "poliza";
    private static final String PASSWORD = "poliza123";

    // Abre una conexión nueva. Quien la usa la cierra (try-with-resources).
    public static Connection conectar() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, PASSWORD);
    }

    // Ejecuta un INSERT, UPDATE o DELETE. Cada ? del SQL se cambia por un dato, en orden.
    // Usar ? en lugar de pegar el texto evita la inyección SQL.
    public static void ejecutar(String sql, Object... datos) throws SQLException {
        try (Connection cn = conectar(); PreparedStatement ps = cn.prepareStatement(sql)) {
            for (int i = 0; i < datos.length; i++) {
                ps.setObject(i + 1, datos[i]);
            }
            ps.executeUpdate();
        }
    }

    // Igual que ejecutar(), pero para un INSERT: regresa el id que la base le dio al registro nuevo.
    public static int insertar(String sql, Object... datos) throws SQLException {
        try (Connection cn = conectar(); PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            for (int i = 0; i < datos.length; i++) {
                ps.setObject(i + 1, datos[i]);
            }
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    // Convierte un error de la base en un mensaje claro para el usuario (RNF-05).
    public static String mensaje(SQLException e) {
        String m = e.getMessage() == null ? "" : e.getMessage();
        if (m.contains("Duplicate")) {
            return "Ya existe un registro con ese dato (no se permiten duplicados).";
        }
        if (m.contains("foreign key") && m.contains("parent row")) {
            return "No se puede eliminar: el registro tiene otros datos asociados.";
        }
        if (m.contains("foreign key")) {
            return "El registro relacionado no existe.";
        }
        if (m.contains("CONSTRAINT")) {
            return "Un dato no cumple las reglas del sistema: " + m;
        }
        if (m.contains("Could not connect") || m.contains("Access denied") || m.contains("Socket")) {
            return "No hay conexión con la base de datos. Revisa que MariaDB esté encendido.";
        }
        return m;
    }

    // Prueba la conexión y regresa un resumen: servidor, base y registros por tabla.
    public static String probar() {
        String[] tablas = {"marca", "modelo", "aseguradora", "tipoPoliza", "cobertura", "cliente", "usuario",
            "vehiculo", "cotizacion", "poliza", "polizaCobertura", "pago", "avisoVencimiento", "cancelacion"};
        StringBuilder sb = new StringBuilder();
        try (Connection cn = conectar()) {
            DatabaseMetaData meta = cn.getMetaData();
            sb.append("Conexión exitosa\n");
            sb.append("Servidor: ").append(meta.getDatabaseProductName()).append(' ')
              .append(meta.getDatabaseProductVersion()).append('\n');
            sb.append("Base:     ").append(cn.getCatalog()).append("\n\nRegistros por tabla:\n");
            for (String tabla : tablas) {
                try (PreparedStatement ps = cn.prepareStatement("SELECT COUNT(*) FROM " + tabla);
                     ResultSet rs = ps.executeQuery()) {
                    rs.next();
                    sb.append(String.format("  %-18s %d%n", tabla, rs.getInt(1)));
                }
            }
        } catch (SQLException e) {
            sb.append(mensaje(e));
        }
        return sb.toString();
    }

    // Al ejecutar esta clase sola, muestra en consola el resultado de probar().
    public static void main(String[] args) {
        System.out.println(probar());
    }
}
