package Polizas.modelo;

// Capa de modelo: tipo de póliza del catálogo (amplia, limitada...). Es clase y no enumeración
// porque los tipos pueden cambiar según el contrato con la aseguradora.
// Se guarda en la tabla tipoPoliza por medio de TipoPolizaDAO. No se borra: se desactiva.
public class TipoPoliza {

    private int id;
    private String nombre;
    private String descripcion;
    private boolean activo = true;

    // Constructor vacío: crea el objeto para después llenarlo con los métodos set.
    public TipoPoliza() {
    }

    // Constructor con todos los datos (lo usan los DAO al leer de la base).
    public TipoPoliza(int id, String nombre, String descripcion, boolean activo) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.activo = activo;
    }

    // Getters y setters: leen y cambian los atributos privados (encapsulamiento).
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
