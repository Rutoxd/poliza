package Polizas.Entidades;

// Cliente (suscriptor): hereda de Persona
public class Cliente extends Persona {

    // Atributos
    private String rfc;
    private String domicilio;

    public Cliente() {
    }

    public Cliente(int id, String nombre, String rfc, String telefono, String correo, String domicilio) {
        super(id, nombre, telefono, correo);
        this.rfc = rfc;
        this.domicilio = domicilio;
    }

    // Método: RFC de persona física (13) o moral (12): letras, fecha AAMMDD y homoclave
    public boolean validarRfc() {
        return rfc != null && rfc.toUpperCase().matches("^[A-ZÑ&]{3,4}\\d{6}[A-Z0-9]{3}$");
    }

    public String getRfc() { return rfc; }
    public void setRfc(String rfc) { this.rfc = rfc; }
    public String getDomicilio() { return domicilio; }
    public void setDomicilio(String domicilio) { this.domicilio = domicilio; }

    @Override
    public String toString() {
        return getNombre() + " (" + rfc + ")";
    }
}
