package Polizas.clases;

// Estado de una póliza. No se guarda: lo calcula Poliza.calcularEstado().
public enum EstadoPoliza {
    VIGENTE, POR_VENCER, VENCIDA, CANCELADA
}
