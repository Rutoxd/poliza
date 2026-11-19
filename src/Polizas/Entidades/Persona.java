package Polizas.Entidades;

// Clase abstracta Persona: datos que comparten Cliente y Usuario (herencia)
public abstract class Persona {

    // Atributos privados (encapsulamiento)
    private int id;
    private String nombre;
    private String telefono;
    private String correo;

    protected Persona() {
    }

    protected Persona(int id, String nombre, String telefono, String correo) {
        this.id = id;
        this.nombre = nombre;
        this.telefono = telefono;
        this.correo = correo;
    }

    // Método: el correo es opcional, pero si se escribe debe tener formato válido
    public boolean validarCorreo() {
        return correo == null || correo.isBlank()
                || correo.matches("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$");
    }

    // Método: el teléfono es opcional; si se escribe, 10 dígitos
    public boolean validarTelefono() {
        return telefono == null || telefono.isBlank() || telefono.matches("\\d{10}");
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    @Override
    public String toString() {
        return nombre;
    }
}
