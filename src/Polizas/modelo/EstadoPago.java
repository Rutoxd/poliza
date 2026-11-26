package Polizas.modelo;

// Estado de una cuota. No se guarda: se deduce de la fecha límite y la fecha de pago.
public enum EstadoPago {
    PENDIENTE, PAGADO, VENCIDO
}
