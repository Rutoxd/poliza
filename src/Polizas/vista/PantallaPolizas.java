package Polizas.vista;

import Polizas.dao.AseguradoraDAO;
import Polizas.dao.CoberturaDAO;
import Polizas.dao.Conexion;
import Polizas.dao.PolizaDAO;
import Polizas.dao.TipoPolizaDAO;
import Polizas.dao.UsuarioDAO;
import Polizas.dao.VehiculoDAO;
import Polizas.modelo.Aseguradora;
import Polizas.modelo.Cobertura;
import Polizas.modelo.Poliza;
import Polizas.modelo.TipoPoliza;
import Polizas.modelo.Usuario;
import Polizas.modelo.Vehiculo;
import java.awt.GridLayout;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

// Capa de vista - Módulo 3, Gestión de Pólizas: alta, consulta, modificación y eliminación.
// El vehículo (y con él el cliente), la aseguradora, el tipo y el usuario se eligen de listas,
// y las coberturas se marcan con casillas. Guarda con PolizaDAO; aquí no hay SQL.
public class PantallaPolizas extends Pantalla {

    private final PolizaDAO dao = new PolizaDAO();   // acceso a datos (capa DAO)

    private JTextField txtNumero = new JTextField();
    private JComboBox<Vehiculo> cmbVehiculo = new JComboBox<>();
    private JLabel lblCliente = new JLabel();
    private JComboBox<Aseguradora> cmbAseguradora = new JComboBox<>();
    private JComboBox<TipoPoliza> cmbTipo = new JComboBox<>();
    private JComboBox<Usuario> cmbUsuario = new JComboBox<>();
    private JTextField txtInicio = new JTextField();
    private JTextField txtFin = new JTextField();
    private JTextField txtPrima = new JTextField();
    private List<JCheckBox> casillas = new ArrayList<>();
    private List<Cobertura> todasCoberturas = new ArrayList<>();
    private JButton btnVer = new JButton("Ver póliza");
    private List<Poliza> polizas;

    // Arma la pantalla: campos, listas con los catálogos, casillas de coberturas y la tabla.
    public PantallaPolizas() {
        super("Pólizas", "Id", "Número", "Cliente", "Vehículo", "Aseguradora", "Tipo", "Inicio", "Fin", "Prima", "Estado");
        campo("Número de póliza:", txtNumero);
        campo("Vehículo:", cmbVehiculo);
        campo("Cliente:", lblCliente);
        campo("Aseguradora:", cmbAseguradora);
        campo("Tipo de póliza:", cmbTipo);
        campo("Registró:", cmbUsuario);
        campo("Inicio (AAAA-MM-DD):", txtInicio);
        campo("Fin (AAAA-MM-DD):", txtFin);
        campo("Prima total:", txtPrima);

        JPanel panelCoberturas = new JPanel(new GridLayout(0, 3));
        panelCoberturas.setBorder(BorderFactory.createTitledBorder("Coberturas (al menos una)"));
        debajo.add(panelCoberturas);
        botones.add(btnVer);

        try {
            for (Vehiculo v : new VehiculoDAO().listar()) {
                cmbVehiculo.addItem(v);
            }
            for (Aseguradora a : new AseguradoraDAO().listar()) {
                cmbAseguradora.addItem(a);
            }
            for (TipoPoliza t : new TipoPolizaDAO().listar()) {
                cmbTipo.addItem(t);
            }
            for (Usuario u : new UsuarioDAO().listarActivos()) {
                cmbUsuario.addItem(u);
            }
            todasCoberturas = new CoberturaDAO().listar();
            for (Cobertura c : todasCoberturas) {
                JCheckBox casilla = new JCheckBox(c.getNombre());
                casillas.add(casilla);
                panelCoberturas.add(casilla);
            }
        } catch (SQLException e) {
            aviso(Conexion.mensaje(e));
        }

        // Al elegir el vehículo se muestra su dueño: el cliente de la póliza sale del vehículo (RN-01).
        cmbVehiculo.addActionListener(e -> mostrarCliente());
        btnVer.addActionListener(e -> verPoliza());
        mostrarCliente();
        limpiar();
        recargar();
    }

    // Arma el objeto Poliza con el formulario y revisa sus reglas con validar() del modelo.
    private Poliza leer() {
        Poliza p = new Poliza();
        p.setNumero(obligatorio(txtNumero, "Número de póliza").toUpperCase());
        p.setVehiculo((Vehiculo) cmbVehiculo.getSelectedItem());
        p.setAseguradora((Aseguradora) cmbAseguradora.getSelectedItem());
        p.setTipoPoliza((TipoPoliza) cmbTipo.getSelectedItem());
        p.setUsuario((Usuario) cmbUsuario.getSelectedItem());
        try {
            p.setFechaInicio(LocalDate.parse(obligatorio(txtInicio, "Inicio")));
            p.setFechaFin(LocalDate.parse(obligatorio(txtFin, "Fin")));
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Escribe las fechas con el formato AAAA-MM-DD (ejemplo: 2026-12-31).");
        }
        try {
            p.setPrimaTotal(Double.parseDouble(obligatorio(txtPrima, "Prima total")));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("La prima total debe ser un número.");
        }
        List<Cobertura> elegidas = new ArrayList<>();
        for (int i = 0; i < casillas.size(); i++) {
            if (casillas.get(i).isSelected()) {
                elegidas.add(todasCoberturas.get(i));
            }
        }
        p.setCoberturas(elegidas);
        p.validar();
        return p;
    }

    // Trae las pólizas con listar() del DAO y llena la tabla con su estado calculado (RF-14).
    @Override
    protected void cargarTabla() throws SQLException {
        polizas = dao.listar();
        datos.setRowCount(0);
        for (Poliza p : polizas) {
            datos.addRow(new Object[]{p.getId(), p.getNumero(), p.getVehiculo().getCliente().getNombre(),
                p.getVehiculo().getPlacas(), p.getAseguradora().getNombre(), p.getTipoPoliza().getNombre(),
                p.getFechaInicio(), p.getFechaFin(), String.format("%,.2f", p.getPrimaTotal()), p.calcularEstado()});
        }
    }

    // Botón Agregar: arma la póliza y la guarda con registrar() del DAO (póliza y coberturas juntas).
    @Override
    protected void agregar() throws SQLException {
        Poliza p = leer();
        if (!p.getTipoPoliza().isActivo()) {
            throw new IllegalArgumentException("Ese tipo de póliza está desactivado; elige otro.");
        }
        dao.registrar(p);
    }

    // Botón Actualizar: arma la póliza, le pone el id de la fila elegida y la guarda con actualizar() del DAO.
    @Override
    protected void actualizar() throws SQLException {
        int fila = filaElegida();
        Poliza p = leer();
        p.setId(polizas.get(fila).getId());
        dao.actualizar(p);
    }

    // Botón Eliminar: pide al DAO borrar la póliza elegida (no se puede si ya tiene pagos).
    @Override
    protected void eliminar() throws SQLException {
        dao.eliminar(polizas.get(filaElegida()).getId());
    }

    // Pasa los datos de la póliza elegida al formulario y marca sus coberturas.
    @Override
    protected void mostrar(int fila) {
        Poliza p = polizas.get(fila);
        txtNumero.setText(p.getNumero());
        elegir(cmbVehiculo, v -> v.getId() == p.getVehiculo().getId());
        elegir(cmbAseguradora, a -> a.getId() == p.getAseguradora().getId());
        elegir(cmbTipo, t -> t.getId() == p.getTipoPoliza().getId());
        elegir(cmbUsuario, u -> p.getUsuario() != null && u.getId() == p.getUsuario().getId());
        txtInicio.setText(p.getFechaInicio().toString());
        txtFin.setText(p.getFechaFin().toString());
        txtPrima.setText(String.valueOf(p.getPrimaTotal()));
        for (int i = 0; i < casillas.size(); i++) {
            int id = todasCoberturas.get(i).getId();
            casillas.get(i).setSelected(p.getCoberturas().stream().anyMatch(c -> c.getId() == id));
        }
    }

    // Vacía el formulario; propone una vigencia de un año a partir de hoy.
    @Override
    protected void limpiar() {
        txtNumero.setText("");
        txtInicio.setText(LocalDate.now().toString());
        txtFin.setText(LocalDate.now().plusYears(1).toString());
        txtPrima.setText("");
        for (JCheckBox c : casillas) {
            c.setSelected(false);
        }
    }

    // Muestra el nombre del dueño del vehículo elegido.
    private void mostrarCliente() {
        Vehiculo v = (Vehiculo) cmbVehiculo.getSelectedItem();
        lblCliente.setText(v == null ? "Primero registra un vehículo." : v.getCliente().getNombre());
    }

    // RF-12 y RF-18: muestra la póliza elegida completa (coberturas, estado y saldo) con imprimir().
    private void verPoliza() {
        try {
            JTextArea texto = new JTextArea(polizas.get(filaElegida()).imprimir());
            texto.setEditable(false);
            JOptionPane.showMessageDialog(this, texto, "Póliza", JOptionPane.INFORMATION_MESSAGE);
        } catch (IllegalArgumentException e) {
            aviso(e.getMessage());
        }
    }
}
