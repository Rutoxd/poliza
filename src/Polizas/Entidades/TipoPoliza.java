package Polizas.Entidades;

// Tipo de póliza (catálogo, RF-25). Se desactiva en vez de borrarse.
public class TipoPoliza {

    private int id;
    private String nombre;
    private String descripcion;
    private boolean activo = true;

    public TipoPoliza() {
    }

    public TipoPoliza(int id, String nombre, String descripcion, boolean activo) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.activo = activo;
    }

    public void desactivar() {
        this.activo = false;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    @Override
    public String toString() {
        return nombre;
    }
}
