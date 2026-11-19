package Polizas.General;

import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

// Controlador genérico de un CRUD: une la vista con su modelo (DAO).
// Cada módulo hereda de él y puede redefinir los métodos protegidos.
public abstract class CrudControl<T> {

    // Atributos
    protected final CrudVista<T> vista;
    protected final DAO<T> modelo;
    protected final String entidad;   // nombre para los mensajes, ej. "cliente"

    // Constructor. Parámetros: vista, modelo DAO y nombre de la entidad
    protected CrudControl(CrudVista<T> vista, DAO<T> modelo, String entidad) {
        this.vista = vista;
        this.modelo = modelo;
        this.entidad = entidad;
        eventos();
    }

    // Método: carga inicial; se llama al terminar de construir el controlador hijo
    public void iniciar() {
        cargarCatalogos();
        consultar();
    }

    // Método: asigna la acción de cada botón
    private void eventos() {
        vista.btnConsultar.addActionListener(e -> consultar());
        vista.btnLimpiar.addActionListener(e -> {
            vista.limpiar();
            vista.tabla.clearSelection();
        });
        vista.btnAgregar.addActionListener(e -> agregar());
        vista.btnActualizar.addActionListener(e -> actualizar());
        vista.btnEliminar.addActionListener(e -> eliminar());

        // Al seleccionar una fila, sus datos pasan al formulario
        vista.tabla.getSelectionModel().addListSelectionListener(e -> {
            int fila = vista.filaSeleccionada();
            if (!e.getValueIsAdjusting() && fila >= 0) {
                vista.mostrar(vista.modeloTabla.get(fila));
            }
        });

        // Búsqueda mientras se escribe
        vista.txtBuscar.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { vista.filtrar(); }
            public void removeUpdate(DocumentEvent e) { vista.filtrar(); }
            public void changedUpdate(DocumentEvent e) { vista.filtrar(); }
        });
    }

    // Método: llena los combos de la vista (los módulos con llaves foráneas lo redefinen)
    protected void cargarCatalogos() {
    }

    // Método: validaciones de negocio extra antes de guardar (se redefine si hace falta)
    protected void validar(T obj, boolean nuevo) throws DatoInvalidoException {
    }

    // Método: pone el id del registro seleccionado en el objeto leído del formulario
    protected abstract void copiarId(T origen, T destino);

    // Método: texto con el que se identifica el registro en la confirmación
    protected abstract String descripcion(T obj);

    public void consultar() {
        List<T> datos = modelo.read();
        vista.modeloTabla.llenar(datos);
        if (datos.isEmpty() && !modelo.getError().isEmpty()) {
            aviso(modelo.getError());
        }
    }

    private void agregar() {
        try {
            T obj = vista.leerFormulario();
            validar(obj, true);
            if (modelo.create(obj) == 1) {
                aviso("Registro de " + entidad + " guardado.");
                vista.limpiar();
                consultar();
            } else {
                aviso("No se pudo guardar. " + modelo.getError());
            }
        } catch (DatoInvalidoException ex) {
            aviso(ex.getMessage());
        }
    }

    private void actualizar() {
        int fila = vista.filaSeleccionada();
        if (fila < 0) {
            aviso("Selecciona un registro de la tabla.");
            return;
        }
        try {
            T obj = vista.leerFormulario();
            copiarId(vista.modeloTabla.get(fila), obj);
            validar(obj, false);
            if (modelo.update(obj) == 1) {
                aviso("Registro de " + entidad + " actualizado.");
                consultar();
            } else {
                aviso("No se pudo actualizar. " + modelo.getError());
            }
        } catch (DatoInvalidoException ex) {
            aviso(ex.getMessage());
        }
    }

    private void eliminar() {
        int fila = vista.filaSeleccionada();
        if (fila < 0) {
            aviso("Selecciona un registro de la tabla.");
            return;
        }
        T obj = vista.modeloTabla.get(fila);
        int opcion = JOptionPane.showConfirmDialog(vista,
                "¿Eliminar " + entidad + " \"" + descripcion(obj) + "\"?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (opcion != JOptionPane.YES_OPTION) {
            return;
        }
        if (modelo.delete(obj) == 1) {
            aviso("Registro de " + entidad + " eliminado.");
            vista.limpiar();
            consultar();
        } else {
            aviso("No se pudo eliminar. " + modelo.getError());
        }
    }

    protected void aviso(String mensaje) {
        JOptionPane.showMessageDialog(vista, mensaje);
    }
}
