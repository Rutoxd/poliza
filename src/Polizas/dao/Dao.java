package Polizas.dao;

import java.sql.SQLException;
import java.util.List;

// Capa de acceso a datos: contrato del patrón DAO (Data Access Object).
// Cada clase del modelo tiene su DAO, que es el único lugar donde está su SQL.
// T es la clase del modelo que guarda el DAO (Cliente, Vehiculo, Poliza...).
public interface Dao<T> {

    // Alta: guarda el objeto como registro nuevo y le asigna el id que dio la base.
    void registrar(T objeto) throws SQLException;

    // Modificación: guarda los cambios del objeto (lo busca por su id).
    void actualizar(T objeto) throws SQLException;

    // Eliminación: borra el registro con ese id. La base lo impide si tiene datos asociados.
    void eliminar(int id) throws SQLException;

    // Consulta: regresa el registro con ese id, o null si no existe.
    T consultar(int id) throws SQLException;

    // Consulta: regresa todos los registros, para llenar las tablas y listas de las pantallas.
    List<T> listar() throws SQLException;
}
