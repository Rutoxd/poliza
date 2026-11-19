package Polizas.General;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

// Conexión a MySQL/MariaDB con JDBC. Los modelos (DAO) heredan de esta clase.
public class Conexion {

    // Constantes: datos de conexión (base creada con sql/polizas.sql)
    private static final String URL = "jdbc:mariadb://localhost:3306/polizas";
    private static final String USUARIO = "poliza";
    private static final String PASSWORD = "poliza123";

    // Atributos
    protected Connection cnx;        // conexión
    protected PreparedStatement ps;  // consulta SQL
    protected ResultSet rs;          // resultado del SELECT
    protected String error = "";     // último error, para mostrarlo en la vista

    // Método: abre la conexión
    public void conectar() {
        try {
            cnx = DriverManager.getConnection(URL, USUARIO, PASSWORD);
        } catch (SQLException e) {
            cnx = null;
            error = "No hay conexión con la base de datos: " + e.getMessage();
            System.out.println(error);
        }
    }

    // Método: cierra la conexión si existe
    public void close() {
        try {
            if (cnx != null) {
                cnx.close();
            }
        } catch (SQLException ex) {
            System.out.println("Error al cerrar: " + ex.getMessage());
        }
    }

    // Método: ejecuta un INSERT, UPDATE o DELETE con parámetros (? evita inyección SQL).
    // Regresa las filas afectadas, o 0 si hubo error (queda en getError()).
    protected int ejecutar(String sql, Object... parametros) {
        error = "";
        try {
            conectar();
            if (cnx == null) {
                return 0;
            }
            ps = cnx.prepareStatement(sql);
            for (int i = 0; i < parametros.length; i++) {
                ps.setObject(i + 1, parametros[i]);
            }
            return ps.executeUpdate();
        } catch (SQLException ex) {
            registrarError(ex);
            return 0;
        } finally {
            close();
        }
    }

    // Método: texto vacío se guarda como NULL
    protected static String nulo(String texto) {
        return texto == null || texto.isBlank() ? null : texto.trim();
    }

    // Método: traduce un error de la base a un mensaje claro (RNF-05)
    protected void registrarError(SQLException ex) {
        String msg = ex.getMessage() == null ? "" : ex.getMessage();
        if (ex instanceof SQLIntegrityConstraintViolationException || "23000".equals(ex.getSQLState())) {
            if (msg.contains("Duplicate")) {
                error = "Ya existe un registro con ese dato (no se permiten duplicados).";
            } else if (msg.contains("foreign key") && msg.contains("parent row")) {
                error = "No se puede eliminar: el registro tiene otros datos asociados.";
            } else if (msg.contains("foreign key")) {
                error = "El registro relacionado no existe.";
            } else if (msg.contains("CONSTRAINT")) {
                error = "Un dato no cumple las reglas del sistema: " + msg;
            } else {
                error = msg;
            }
        } else {
            error = msg;
        }
        System.out.println("Error SQL: " + msg);
    }

    // Método: regresa el último error
    public String getError() {
        return error;
    }
}
