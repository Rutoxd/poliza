-- =====================================================================
-- Sistema de Gestión de Pólizas — Script de creación de la base de datos
-- Fase 3: modelo relacional normalizado hasta 3FN
-- Motor: MySQL 8.0.16+ o MariaDB 10.4+ (ambos validan CHECK)
-- Ejecutar como root:  mysql -u root -p < sql/polizas.sql
-- =====================================================================

DROP DATABASE IF EXISTS polizas;
CREATE DATABASE polizas CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE polizas;

-- ---------------------------------------------------------------------
-- Catálogos
-- ---------------------------------------------------------------------

CREATE TABLE marca (
    idMarca      INT AUTO_INCREMENT PRIMARY KEY,
    nombreMarca  VARCHAR(100) NOT NULL,
    CONSTRAINT uq_marca_nombre UNIQUE (nombreMarca)
) ENGINE = InnoDB;

CREATE TABLE modelo (
    idModelo      INT AUTO_INCREMENT PRIMARY KEY,
    idMarca       INT NOT NULL,
    nombreModelo  VARCHAR(100) NOT NULL,
    CONSTRAINT uq_modelo_marca UNIQUE (idMarca, nombreModelo),
    CONSTRAINT fk_modelo_marca FOREIGN KEY (idMarca) REFERENCES marca (idMarca)
) ENGINE = InnoDB;

CREATE TABLE aseguradora (
    idAseguradora  INT AUTO_INCREMENT PRIMARY KEY,
    nombre         VARCHAR(120) NOT NULL,
    telefono       VARCHAR(15),
    correo         VARCHAR(120),
    CONSTRAINT uq_aseguradora_nombre UNIQUE (nombre)
) ENGINE = InnoDB;

CREATE TABLE tipoPoliza (
    idTipoPoliza  INT AUTO_INCREMENT PRIMARY KEY,
    nombre        VARCHAR(60) NOT NULL,
    descripcion   VARCHAR(255),
    activo        BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT uq_tipoPoliza_nombre UNIQUE (nombre)
) ENGINE = InnoDB;

CREATE TABLE cobertura (
    idCobertura  INT AUTO_INCREMENT PRIMARY KEY,
    nombre       VARCHAR(80) NOT NULL,
    descripcion  VARCHAR(255),
    CONSTRAINT uq_cobertura_nombre UNIQUE (nombre)
) ENGINE = InnoDB;

-- ---------------------------------------------------------------------
-- Personas (Cliente y Usuario heredan de Persona en el diseño de clases)
-- ---------------------------------------------------------------------

CREATE TABLE cliente (
    idCliente  INT AUTO_INCREMENT PRIMARY KEY,
    nombre     VARCHAR(120) NOT NULL,
    rfc        VARCHAR(13) NOT NULL,
    telefono   VARCHAR(15),
    correo     VARCHAR(120),
    domicilio  VARCHAR(200),
    CONSTRAINT uq_cliente_rfc UNIQUE (rfc),
    CONSTRAINT ck_cliente_rfc CHECK (CHAR_LENGTH(rfc) IN (12, 13))
) ENGINE = InnoDB;

CREATE TABLE usuario (
    idUsuario   INT AUTO_INCREMENT PRIMARY KEY,
    nombre      VARCHAR(120) NOT NULL,
    telefono    VARCHAR(15),
    correo      VARCHAR(120),
    usuario     VARCHAR(40) NOT NULL,
    contrasena  VARCHAR(255) NOT NULL,
    rol         ENUM('ADMINISTRADOR', 'AGENTE_VENTAS', 'ENCARGADO_GESTION') NOT NULL,
    activo      BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT uq_usuario_usuario UNIQUE (usuario)
) ENGINE = InnoDB;

-- ---------------------------------------------------------------------
-- Vehículos y cotizaciones
-- ---------------------------------------------------------------------

CREATE TABLE vehiculo (
    idVehiculo   INT AUTO_INCREMENT PRIMARY KEY,
    idCliente    INT NOT NULL,
    idModelo     INT NOT NULL,
    anio         SMALLINT NOT NULL,
    placas       VARCHAR(10) NOT NULL,
    numeroSerie  CHAR(17) NOT NULL,
    CONSTRAINT uq_vehiculo_placas UNIQUE (placas),
    CONSTRAINT uq_vehiculo_serie UNIQUE (numeroSerie),
    CONSTRAINT ck_vehiculo_anio CHECK (anio BETWEEN 1950 AND 2100),
    CONSTRAINT fk_vehiculo_cliente FOREIGN KEY (idCliente) REFERENCES cliente (idCliente),
    CONSTRAINT fk_vehiculo_modelo FOREIGN KEY (idModelo) REFERENCES modelo (idModelo)
) ENGINE = InnoDB;

CREATE TABLE cotizacion (
    idCotizacion   INT AUTO_INCREMENT PRIMARY KEY,
    idVehiculo     INT NOT NULL,
    idAseguradora  INT NOT NULL,
    idTipoPoliza   INT NOT NULL,
    fecha          DATE NOT NULL,
    costo          DECIMAL(10, 2) NOT NULL,
    formaPago      ENUM('CONTADO', 'SEMESTRAL', 'TRIMESTRAL', 'MENSUAL') NOT NULL,
    estado         ENUM('PENDIENTE', 'ACEPTADA', 'RECHAZADA') NOT NULL DEFAULT 'PENDIENTE',
    CONSTRAINT ck_cotizacion_costo CHECK (costo > 0),
    CONSTRAINT fk_cotizacion_vehiculo FOREIGN KEY (idVehiculo) REFERENCES vehiculo (idVehiculo),
    CONSTRAINT fk_cotizacion_aseguradora FOREIGN KEY (idAseguradora) REFERENCES aseguradora (idAseguradora),
    CONSTRAINT fk_cotizacion_tipo FOREIGN KEY (idTipoPoliza) REFERENCES tipoPoliza (idTipoPoliza)
) ENGINE = InnoDB;

-- ---------------------------------------------------------------------
-- Pólizas
-- ---------------------------------------------------------------------

CREATE TABLE poliza (
    idPoliza          INT AUTO_INCREMENT PRIMARY KEY,
    numero            VARCHAR(30) NOT NULL,
    idAseguradora     INT NOT NULL,
    idTipoPoliza      INT NOT NULL,
    idVehiculo        INT NOT NULL,
    idCotizacion      INT NULL,
    idPolizaAnterior  INT NULL,
    idUsuario         INT NOT NULL,
    fechaInicio       DATE NOT NULL,
    fechaFin          DATE NOT NULL,
    primaTotal        DECIMAL(10, 2) NOT NULL,
    CONSTRAINT uq_poliza_numero UNIQUE (idAseguradora, numero),
    CONSTRAINT uq_poliza_cotizacion UNIQUE (idCotizacion),
    CONSTRAINT uq_poliza_anterior UNIQUE (idPolizaAnterior),
    CONSTRAINT ck_poliza_vigencia CHECK (fechaFin > fechaInicio),
    CONSTRAINT ck_poliza_prima CHECK (primaTotal > 0),
    CONSTRAINT fk_poliza_aseguradora FOREIGN KEY (idAseguradora) REFERENCES aseguradora (idAseguradora),
    CONSTRAINT fk_poliza_tipo FOREIGN KEY (idTipoPoliza) REFERENCES tipoPoliza (idTipoPoliza),
    CONSTRAINT fk_poliza_vehiculo FOREIGN KEY (idVehiculo) REFERENCES vehiculo (idVehiculo),
    CONSTRAINT fk_poliza_cotizacion FOREIGN KEY (idCotizacion) REFERENCES cotizacion (idCotizacion),
    CONSTRAINT fk_poliza_anterior FOREIGN KEY (idPolizaAnterior) REFERENCES poliza (idPoliza),
    CONSTRAINT fk_poliza_usuario FOREIGN KEY (idUsuario) REFERENCES usuario (idUsuario)
) ENGINE = InnoDB;

CREATE TABLE polizaCobertura (
    idPoliza     INT NOT NULL,
    idCobertura  INT NOT NULL,
    CONSTRAINT pk_polizaCobertura PRIMARY KEY (idPoliza, idCobertura),
    CONSTRAINT fk_pc_poliza FOREIGN KEY (idPoliza) REFERENCES poliza (idPoliza) ON DELETE CASCADE,
    CONSTRAINT fk_pc_cobertura FOREIGN KEY (idCobertura) REFERENCES cobertura (idCobertura)
) ENGINE = InnoDB;

CREATE TABLE pago (
    idPago       INT AUTO_INCREMENT PRIMARY KEY,
    idPoliza     INT NOT NULL,
    numeroCuota  TINYINT NOT NULL,
    monto        DECIMAL(10, 2) NOT NULL,
    fechaLimite  DATE NOT NULL,
    fechaPago    DATE NULL,
    metodoPago   ENUM('EFECTIVO', 'TRANSFERENCIA', 'TARJETA', 'DEPOSITO') NULL,
    comprobante  VARCHAR(255) NULL,
    CONSTRAINT uq_pago_cuota UNIQUE (idPoliza, numeroCuota),
    CONSTRAINT ck_pago_monto CHECK (monto > 0),
    CONSTRAINT ck_pago_cuota CHECK (numeroCuota >= 1),
    CONSTRAINT ck_pago_metodo CHECK (fechaPago IS NULL OR metodoPago IS NOT NULL),
    CONSTRAINT fk_pago_poliza FOREIGN KEY (idPoliza) REFERENCES poliza (idPoliza)
) ENGINE = InnoDB;

CREATE TABLE avisoVencimiento (
    idAviso          INT AUTO_INCREMENT PRIMARY KEY,
    tipo             ENUM('POLIZA', 'PAGO') NOT NULL,
    idPoliza         INT NULL,
    idPago           INT NULL,
    fechaAviso       DATE NOT NULL,
    medio            ENUM('WHATSAPP', 'CORREO', 'LLAMADA') NOT NULL,
    costoRenovacion  DECIMAL(10, 2) NULL,
    resultado        VARCHAR(200) NULL,
    CONSTRAINT ck_aviso_tipo CHECK (
        (tipo = 'POLIZA' AND idPoliza IS NOT NULL AND idPago IS NULL)
        OR (tipo = 'PAGO' AND idPago IS NOT NULL AND idPoliza IS NULL AND costoRenovacion IS NULL)
    ),
    CONSTRAINT ck_aviso_costo CHECK (costoRenovacion IS NULL OR costoRenovacion > 0),
    CONSTRAINT fk_aviso_poliza FOREIGN KEY (idPoliza) REFERENCES poliza (idPoliza),
    CONSTRAINT fk_aviso_pago FOREIGN KEY (idPago) REFERENCES pago (idPago)
) ENGINE = InnoDB;

CREATE TABLE cancelacion (
    idCancelacion    INT AUTO_INCREMENT PRIMARY KEY,
    idPoliza         INT NOT NULL,
    fechaSolicitud   DATE NOT NULL,
    motivo           VARCHAR(255) NOT NULL,
    estado           ENUM('SOLICITADA', 'VALIDADA', 'AUTORIZADA', 'RECHAZADA', 'BAJA_REGISTRADA')
                     NOT NULL DEFAULT 'SOLICITADA',
    montoDevolucion  DECIMAL(10, 2) NULL,
    CONSTRAINT uq_cancelacion_poliza UNIQUE (idPoliza),
    CONSTRAINT ck_cancelacion_monto CHECK (montoDevolucion IS NULL OR montoDevolucion >= 0),
    CONSTRAINT fk_cancelacion_poliza FOREIGN KEY (idPoliza) REFERENCES poliza (idPoliza)
) ENGINE = InnoDB;

-- ---------------------------------------------------------------------
-- Vistas: datos calculados que no se guardan, para respetar la 3FN
-- ---------------------------------------------------------------------

CREATE VIEW vistaPoliza AS
SELECT p.idPoliza, p.numero, a.nombre AS aseguradora, c.nombre AS cliente, v.placas,
       p.fechaInicio, p.fechaFin, p.primaTotal,
       CASE
           WHEN ca.estado = 'BAJA_REGISTRADA' THEN 'CANCELADA'
           WHEN p.fechaFin < CURDATE() THEN 'VENCIDA'
           WHEN DATEDIFF(p.fechaFin, CURDATE()) <= 7 THEN 'POR_VENCER'
           ELSE 'VIGENTE'
       END AS estado
FROM poliza p
JOIN aseguradora a ON a.idAseguradora = p.idAseguradora
JOIN vehiculo v ON v.idVehiculo = p.idVehiculo
JOIN cliente c ON c.idCliente = v.idCliente
LEFT JOIN cancelacion ca ON ca.idPoliza = p.idPoliza;

CREATE VIEW vistaPago AS
SELECT idPago, idPoliza, numeroCuota, monto, fechaLimite, fechaPago,
       CASE
           WHEN fechaPago IS NOT NULL THEN 'PAGADO'
           WHEN fechaLimite < CURDATE() THEN 'VENCIDO'
           ELSE 'PENDIENTE'
       END AS estado
FROM pago;

CREATE VIEW vistaSaldoPoliza AS
SELECT p.idPoliza, p.numero, p.primaTotal,
       COALESCE(SUM(CASE WHEN pg.fechaPago IS NOT NULL THEN pg.monto END), 0) AS pagado,
       p.primaTotal - COALESCE(SUM(CASE WHEN pg.fechaPago IS NOT NULL THEN pg.monto END), 0) AS saldo
FROM poliza p
LEFT JOIN pago pg ON pg.idPoliza = p.idPoliza
GROUP BY p.idPoliza, p.numero, p.primaTotal;

-- ---------------------------------------------------------------------
-- Usuario de la aplicación (credencial solo para desarrollo local)
-- ---------------------------------------------------------------------

CREATE USER IF NOT EXISTS 'poliza'@'localhost' IDENTIFIED BY 'poliza123';
GRANT ALL PRIVILEGES ON polizas.* TO 'poliza'@'localhost';
FLUSH PRIVILEGES;
