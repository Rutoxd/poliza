package Polizas.General;

import java.util.List;

// Interfaz DAO genérica: métodos del CRUD que implementa cada modelo.
// Corresponde a registrar(), consultar(), actualizar() y eliminar() del diagrama de clases.
public interface DAO<T> {

    int create(T obj);   // registrar: regresa filas afectadas
    List<T> read();      // consultar todos
    int update(T obj);   // actualizar
    int delete(T obj);   // eliminar
    String getError();   // último error, para mostrarlo al usuario
}
