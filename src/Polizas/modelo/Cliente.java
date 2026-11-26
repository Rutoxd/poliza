package Polizas.modelo;

// Capa de modelo: cliente del despacho. Hereda nombre, teléfono y correo de Persona.
// Se guarda en la tabla cliente por medio de ClienteDAO.
public class Cliente extends Persona {

    private String rfc;
    private String domicilio;

    // Constructor vacío: crea el objeto para después llenarlo con los métodos set.
    public Cliente() {
    }

    // Constructor con todos los datos (lo usa ClienteDAO al leer de la base).
    public Cliente(int id, String nombre, String telefono, String correo, String rfc, String domicilio) {
        super(id, nombre, telefono, correo);
        this.rfc = rfc;
        this.domicilio = domicilio;
    }

    // Revisa el formato del RFC: 3 o 4 letras, 6 dígitos de fecha y 3 de homoclave.
    public boolean validarRfc() {
        return rfc != null && rfc.toUpperCase().matches("^[A-ZÑ&]{3,4}\\d{6}[A-Z0-9]{3}$");
    }

    // Getters y setters: leen y cambian los atributos privados (encapsulamiento).
    public String getRfc() { return rfc; }
    public void setRfc(String rfc) { this.rfc = rfc; }
    public String getDomicilio() { return domicilio; }
    public void setDomicilio(String domicilio) { this.domicilio = domicilio; }
}
