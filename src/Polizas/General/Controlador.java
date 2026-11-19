package Polizas.General;

import Polizas.Aseguradoras.AseguradorasControl;
import Polizas.Aseguradoras.AseguradorasVista;
import Polizas.Clientes.ClientesControl;
import Polizas.Clientes.ClientesVista;
import Polizas.Coberturas.CoberturasControl;
import Polizas.Coberturas.CoberturasVista;
import Polizas.Marcas.MarcasControl;
import Polizas.Marcas.MarcasVista;
import Polizas.Modelos.ModelosControl;
import Polizas.Modelos.ModelosVista;
import Polizas.TiposPoliza.TiposPolizaControl;
import Polizas.TiposPoliza.TiposPolizaVista;
import Polizas.Vehiculos.VehiculosControl;
import Polizas.Vehiculos.VehiculosVista;
import javax.swing.JOptionPane;

// Controlador del menú principal: cada opción crea su vista y su controlador
public class Controlador {

    private final Bienvenida vista;

    public Controlador(Bienvenida vista) {
        this.vista = vista;
        eventos();
        probarConexion(false);
    }

    private void eventos() {
        vista.mInicio.addActionListener(e -> vista.cl.show(vista.pc, "home"));
        vista.mSalir.addActionListener(e -> System.exit(0));
        vista.mPrueba.addActionListener(e -> probarConexion(true));

        vista.mClientes.addActionListener(e -> {
            ClientesVista v = new ClientesVista();
            new ClientesControl(v);
            vista.mostrar(v, "clientes");
        });
        vista.mVehiculos.addActionListener(e -> {
            VehiculosVista v = new VehiculosVista();
            new VehiculosControl(v);
            vista.mostrar(v, "vehiculos");
        });
        vista.mAseguradoras.addActionListener(e -> {
            AseguradorasVista v = new AseguradorasVista();
            new AseguradorasControl(v);
            vista.mostrar(v, "aseguradoras");
        });
        vista.mTiposPoliza.addActionListener(e -> {
            TiposPolizaVista v = new TiposPolizaVista();
            new TiposPolizaControl(v);
            vista.mostrar(v, "tipos");
        });
        vista.mCoberturas.addActionListener(e -> {
            CoberturasVista v = new CoberturasVista();
            new CoberturasControl(v);
            vista.mostrar(v, "coberturas");
        });
        vista.mMarcas.addActionListener(e -> {
            MarcasVista v = new MarcasVista();
            new MarcasControl(v);
            vista.mostrar(v, "marcas");
        });
        vista.mModelos.addActionListener(e -> {
            ModelosVista v = new ModelosVista();
            new ModelosControl(v);
            vista.mostrar(v, "modelos");
        });
    }

    // Método: abre y cierra una conexión; muestra el resultado en la barra de estado
    private void probarConexion(boolean avisar) {
        Conexion c = new Conexion();
        c.conectar();
        boolean ok = c.cnx != null;
        c.close();
        String texto = ok ? "Conectado a la base de datos polizas" : c.getError();
        vista.lblEstado.setText(texto);
        if (avisar) {
            JOptionPane.showMessageDialog(vista, texto);
        }
    }
}
