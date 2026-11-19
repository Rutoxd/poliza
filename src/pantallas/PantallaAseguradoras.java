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

    public PantallaAseguradoras() {
        super("Aseguradoras", "Id", "Nombre", "Teléfono", "Correo");
        campo("Nombre:", txtNombre);
        campo("Teléfono:", txtTelefono);
        campo("Correo:", txtCorreo);
        recargar();
    }

    private Aseguradora leer() {
        Aseguradora a = new Aseguradora();
        a.setNombre(obligatorio(txtNombre, "Nombre"));
        a.setTelefono(opcional(txtTelefono));
        a.setCorreo(opcional(txtCorreo));
        return a;
    }

    @Override
    protected void cargarTabla() throws SQLException {
        aseguradoras = Aseguradora.listar();
        datos.setRowCount(0);
        for (Aseguradora a : aseguradoras) {
            datos.addRow(new Object[]{a.getId(), a.getNombre(), a.getTelefono(), a.getCorreo()});
        }
    }

    @Override
    protected void agregar() throws SQLException {
        leer().registrar();
    }

    @Override
    protected void actualizar() throws SQLException {
        Aseguradora a = leer();
        a.setId(aseguradoras.get(filaElegida()).getId());
        a.actualizar();
    }

    @Override
    protected void eliminar() throws SQLException {
        aseguradoras.get(filaElegida()).eliminar();
    }

    @Override
    protected void mostrar(int fila) {
        Aseguradora a = aseguradoras.get(fila);
        txtNombre.setText(a.getNombre());
        txtTelefono.setText(a.getTelefono());
        txtCorreo.setText(a.getCorreo());
    }

    @Override
    protected void limpiar() {
        txtNombre.setText("");
        txtTelefono.setText("");
        txtCorreo.setText("");
    }
}
