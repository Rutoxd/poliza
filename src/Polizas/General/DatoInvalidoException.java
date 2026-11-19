package Polizas.General;

// Excepción propia: un dato del formulario falta o no es válido (RNF-05)
public class DatoInvalidoException extends Exception {

    public DatoInvalidoException(String mensaje) {
        super(mensaje);
    }
}
