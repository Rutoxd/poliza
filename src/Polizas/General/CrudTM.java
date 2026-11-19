package Polizas.General;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

// Modelo de tabla genérico: cada módulo solo define sus columnas y el valor de cada celda
public abstract class CrudTM<T> extends AbstractTableModel {

    // Atributos
    protected List<T> lista = new ArrayList<>();
    protected final String[] encabezados;

    // Constructor. Parámetro encabezados: títulos de las columnas
    protected CrudTM(String... encabezados) {
        this.encabezados = encabezados;
    }

    // Método abstracto: valor de la columna para un objeto (polimorfismo)
    protected abstract Object valor(T obj, int columna);

    @Override
    public int getRowCount() {
        return lista.size();
    }

    @Override
    public int getColumnCount() {
        return encabezados.length;
    }

    @Override
    public String getColumnName(int columna) {
        return encabezados[columna];
    }

    @Override
    public Object getValueAt(int fila, int columna) {
        return valor(lista.get(fila), columna);
    }

    // Método: reemplaza los datos y refresca la tabla
    public void llenar(List<T> datos) {
        lista = new ArrayList<>(datos);
        fireTableDataChanged();
    }

    // Método: regresa el objeto de una fila
    public T get(int fila) {
        return lista.get(fila);
    }
}
