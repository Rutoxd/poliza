-- =====================================================================
-- Datos de prueba ficticios para comprobar que la base funciona
-- Ejecutar después de polizas.sql:  mysql -u poliza -p polizas < sql/datos_prueba.sql
-- =====================================================================

USE polizas;

INSERT INTO marca (nombreMarca) VALUES ('Nissan'), ('Volkswagen'), ('Chevrolet');

INSERT INTO modelo (idMarca, nombreModelo) VALUES
    (1, 'Versa'), (1, 'NP300'), (2, 'Jetta'), (3, 'Aveo');

INSERT INTO aseguradora (nombre, telefono, correo) VALUES
    ('Aseguradora Alfa', '9980000001', 'contacto@alfa.example'),
    ('Aseguradora Beta', '9980000002', 'contacto@beta.example');

INSERT INTO tipoPoliza (nombre, descripcion) VALUES
    ('Amplia', 'Daños materiales, robo total, responsabilidad civil y gastos médicos'),
    ('Limitada', 'Robo total y responsabilidad civil'),
    ('Responsabilidad civil', 'Solo daños a terceros');

INSERT INTO cobertura (nombre, descripcion) VALUES
    ('Daños materiales', 'Daños al vehículo asegurado por colisión o vuelco'),
    ('Robo total', 'Pérdida total del vehículo por robo'),
    ('Responsabilidad civil', 'Daños a terceros en sus bienes o personas'),
    ('Gastos médicos ocupantes', 'Atención médica a los ocupantes'),
    ('Asistencia vial', 'Grúa, paso de corriente y cambio de llanta');

-- La contraseña se guarda como hash SHA-256 (RNF-03); valor solo de prueba
INSERT INTO usuario (nombre, usuario, contrasena, rol) VALUES
    ('Administrador de prueba', 'admin', SHA2('admin', 256), 'ADMINISTRADOR'),
    ('Agente de prueba', 'agente', SHA2('agente', 256), 'AGENTE_VENTAS'),
    ('Encargado de prueba', 'gestion', SHA2('gestion', 256), 'ENCARGADO_GESTION');

INSERT INTO cliente (nombre, rfc, telefono, correo, domicilio) VALUES
    ('Cliente Prueba Uno', 'PUUC800101AB1', '9981000001', 'uno@correo.example', 'Calle 1, Cancún'),
    ('Cliente Prueba Dos', 'PUDC850505CD2', '9981000002', 'dos@correo.example', 'Calle 2, Playa del Carmen');

INSERT INTO vehiculo (idCliente, idModelo, anio, placas, numeroSerie) VALUES
    (1, 1, 2022, 'UVW123A', '3N1CN8AE0NL000001'),
    (1, 3, 2019, 'XYZ456B', '3VW5T7AJ0KM000002'),
    (2, 4, 2020, 'ABC789C', 'KL1TD5DE0LB000003');

INSERT INTO cotizacion (idVehiculo, idAseguradora, idTipoPoliza, fecha, costo, formaPago, estado) VALUES
    (1, 1, 1, '2025-11-10', 12000.00, 'SEMESTRAL', 'ACEPTADA'),
    (3, 2, 2, '2026-01-05', 6500.00, 'CONTADO', 'ACEPTADA');

-- Pólizas con fechas que producen los cuatro estados de vistaPoliza
INSERT INTO poliza (numero, idAseguradora, idTipoPoliza, idVehiculo, idCotizacion,
                    idPolizaAnterior, idUsuario, fechaInicio, fechaFin, primaTotal) VALUES
    ('ALF-0001', 1, 1, 1, 1, NULL, 1, DATE_SUB(CURDATE(), INTERVAL 360 DAY),
        DATE_ADD(CURDATE(), INTERVAL 5 DAY), 12000.00),
    ('BET-0001', 2, 2, 3, 2, NULL, 1, DATE_SUB(CURDATE(), INTERVAL 30 DAY),
        DATE_ADD(CURDATE(), INTERVAL 335 DAY), 6500.00),
    ('ALF-0002', 1, 3, 2, NULL, NULL, 1, DATE_SUB(CURDATE(), INTERVAL 400 DAY),
        DATE_SUB(CURDATE(), INTERVAL 35 DAY), 3200.00);

INSERT INTO polizaCobertura (idPoliza, idCobertura) VALUES
    (1, 1), (1, 2), (1, 3), (1, 4), (1, 5),
    (2, 2), (2, 3),
    (3, 3);

INSERT INTO pago (idPoliza, numeroCuota, monto, fechaLimite, fechaPago, metodoPago, comprobante) VALUES
    (1, 1, 6000.00, DATE_SUB(CURDATE(), INTERVAL 360 DAY), DATE_SUB(CURDATE(), INTERVAL 361 DAY),
        'TRANSFERENCIA', 'comprobantes/alf-0001-1.pdf'),
    (1, 2, 6000.00, DATE_SUB(CURDATE(), INTERVAL 180 DAY), NULL, NULL, NULL),
    (2, 1, 6500.00, DATE_SUB(CURDATE(), INTERVAL 30 DAY), DATE_SUB(CURDATE(), INTERVAL 31 DAY),
        'TARJETA', 'comprobantes/bet-0001-1.pdf'),
    (3, 1, 3200.00, DATE_SUB(CURDATE(), INTERVAL 400 DAY), DATE_SUB(CURDATE(), INTERVAL 400 DAY),
        'EFECTIVO', NULL);

INSERT INTO avisoVencimiento (tipo, idPoliza, idPago, fechaAviso, medio, costoRenovacion, resultado) VALUES
    ('POLIZA', 1, NULL, CURDATE(), 'WHATSAPP', 12600.00, 'Cliente interesado en renovar'),
    ('PAGO', NULL, 2, DATE_SUB(CURDATE(), INTERVAL 187 DAY), 'LLAMADA', NULL, 'Sin respuesta');

INSERT INTO cancelacion (idPoliza, fechaSolicitud, motivo, estado, montoDevolucion) VALUES
    (2, CURDATE(), 'Venta del vehículo', 'BAJA_REGISTRADA', 5800.00);
