# Gestor de Pólizas — Proyecto final de POO (Fase 3)

Sistema de gestión de pólizas de seguros en Java (Swing, MVC) con MySQL/MariaDB.
Fase 3: base de datos normalizada a 3FN, conexión JDBC y CRUD adaptado al dominio del proyecto.

Equipo: Rubén, Rodrigo y José Luis.

## Requisitos

- Java 17 o superior.
- MySQL 8.0.16+ o MariaDB 10.4+.
- El driver JDBC ya viene en `lib/mariadb-java-client.jar` (MariaDB Connector/J 3.5.3; también conecta con MySQL).

## 1. Crear la base de datos

Como root, corre el script. Crea la base `polizas` (14 tablas y 3 vistas) y el usuario de desarrollo que usa la aplicación:

```bash
mysql -u root -p < sql/polizas.sql
mysql -u root -p polizas < sql/datos_prueba.sql   # opcional: datos ficticios
```

El script borra y vuelve a crear la base `polizas`.

## 2. Ejecutar

- **NetBeans**: abre la carpeta como proyecto y presiona Run (clase principal `Polizas.Inicio`).
- **VS Code**: con el Extension Pack for Java, presiona F5 (configuración "Polizas").
- **Terminal**: `./run.sh` (Linux/macOS) o `run.bat` (Windows).

Prueba de conexión (evidencia de la Fase 3):

```bash
java -cp "bin:lib/*" Polizas.General.PruebaConexion      # en Windows usa "bin;lib/*"
```

## Estructura

```
src/Polizas/
├── Inicio.java              Clase principal
├── General/                 Ventana principal, conexión y clases base del CRUD
│   ├── Conexion.java        Conexión JDBC; los modelos heredan de ella
│   ├── DAO.java             Interfaz genérica create/read/update/delete
│   ├── CrudVista.java       Pantalla base: formulario, botones y tabla
│   ├── CrudControl.java     Controlador base: eventos de los botones
│   ├── CrudTM.java          Modelo de tabla base
│   ├── Bienvenida.java      Ventana principal con menú
│   ├── Controlador.java     Acciones del menú
│   └── PruebaConexion.java  Evidencia de la conexión
├── Entidades/               Clases del diagrama UML y enumeraciones
└── Clientes/ Vehiculos/ Aseguradoras/ TiposPoliza/ Coberturas/ Marcas/ Modelos/
                             Un módulo MVC por pantalla: Modelo (DAO), Vista, Control y TM
sql/
├── polizas.sql              Script de creación (3FN)
└── datos_prueba.sql         Datos ficticios
```

## Del diagrama de clases al código

- Las clases de `Entidades` siguen el diagrama UML con las correcciones de la Fase 3:
  `Persona` es abstracta y `Cliente` y `Usuario` heredan de ella; `Vehiculo` usa `Modelo`, que pertenece a una `Marca`;
  `Poliza` obtiene al cliente por su vehículo.
- Los métodos `registrar()`, `consultar()`, `actualizar()` y `eliminar()` del diagrama viven en los modelos de cada
  módulo (`ClientesModelo`, `VehiculosModelo`, …), que implementan la interfaz `DAO`. Así la entidad no depende de la base.
- Los métodos de negocio quedan en las entidades: `Cliente.validarRfc()`, `Persona.validarCorreo()`,
  `Poliza.calcularEstado()`, `calcularSaldo()`, `renovar()`, `cancelar()`, `Pago.estaVencido()`,
  `AvisoVencimiento.calcularDiasRestantes()`, `Cancelacion.validar()/autorizar()/registrarBaja()`, `Usuario.iniciarSesion()`.
- El estado de una póliza y el de un pago no se guardan: se calculan (en Java y en las vistas `vistaPoliza` y `vistaPago`).

## Módulos de esta fase

| Menú | Pantalla | Requerimientos |
|---|---|---|
| Clientes | Clientes | RF-01 a RF-04 |
| Clientes | Vehículos | RF-05 a RF-08 |
| Catálogos | Aseguradoras | RF-24 |
| Catálogos | Tipos de póliza, Coberturas | RF-25 |
| Catálogos | Marcas, Modelos | Catálogo de vehículos (Fase 3) |

Pólizas, pagos, avisos, cancelaciones, usuarios e inicio de sesión se implementan en la Fase 4; sus entidades ya están en `Entidades/`.

## Nota de seguridad

`Conexion.java` y `sql/polizas.sql` traen la contraseña del usuario de desarrollo local. No uses esa contraseña en un servidor real.
