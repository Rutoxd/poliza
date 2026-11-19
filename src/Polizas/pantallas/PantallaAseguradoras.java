package Polizas.pantallas;

import Polizas.clases.Aseguradora;
import java.sql.SQLException;
import java.util.List;
import javax.swing.JTextField;

// Pantalla de aseguradoras: usa los métodos de la clase Aseguradora.
public class PantallaAseguradoras extends Pantalla {

    private JTextField txtNombre = new JTextField();
    private JTextField txtTelefono = new JTextField();
    private JTextField txtCorreo = new JTextField();
    private List<Aseguradora> aseguradoras;

    // Constructor vacío: crea el objeto para después llenarlo con los métodos set.
    public PantallaAseguradoras() {
        super("Aseguradoras", "Id", "Nombre", "Teléfono", "Correo");
        campo("Nombre:", txtNombre);
        campo("Teléfono:", txtTelefono);
        campo("Correo:", txtCorreo);
        recargar();
    }

    // Arma el objeto con lo escrito en el formulario y revisa que los datos estén completos.
    private Aseguradora leer() {
        Aseguradora a = new Aseguradora();
        a.setNombre(obligatorio(txtNombre, "Nombre"));
        a.setTelefono(opcional(txtTelefono));
        a.setCorreo(opcional(txtCorreo));
        return a;
    }

    // Trae los registros con listar() y llena la tabla.
    @Override
    protected void cargarTabla() throws SQLException {
        aseguradoras = Aseguradora.listar();
        datos.setRowCount(0);
        for (Aseguradora a : aseguradoras) {
            datos.addRow(new Object[]{a.getId(), a.getNombre(), a.getTelefono(), a.getCorreo()});
        }
    }

    // Botón Agregar: arma el objeto con el formulario y llama a registrar().
    @Override
    protected void agregar() throws SQLException {
        leer().registrar();
    }

    // Botón Actualizar: arma el objeto, le pone el id de la fila elegida y llama a actualizar().
    @Override
    protected void actualizar() throws SQLException {
        Aseguradora a = leer();
        a.setId(aseguradoras.get(filaElegida()).getId());
        a.actualizar();
    }

    // Botón Eliminar: llama al método que borra o desactiva el registro elegido.
    @Override
    protected void eliminar() throws SQLException {
        aseguradoras.get(filaElegida()).eliminar();
    }

    // Pasa los datos de la fila elegida al formulario.
    @Override
    protected void mostrar(int fila) {
        Aseguradora a = aseguradoras.get(fila);
        txtNombre.setText(a.getNombre());
        txtTelefono.setText(a.getTelefono());
        txtCorreo.setText(a.getCorreo());
    }

    // Vacía los campos del formulario.
    @Override
    protected void limpiar() {
        txtNombre.setText("");
        txtTelefono.setText("");
        txtCorreo.setText("");
    }
}
