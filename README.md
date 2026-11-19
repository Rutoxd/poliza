# Gestor de Pólizas — Proyecto final de POO (Fase 3)

Sistema de gestión de pólizas de seguros en Java (Swing) con MySQL/MariaDB.
El código sigue el diagrama de clases del documento: cada clase tiene sus atributos y sus métodos
`registrar()`, `actualizar()`, `eliminar()` y `consultar()`, que guardan y leen en la base de datos.

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

- **VS Code**: abre la carpeta del proyecto y presiona F5 (configuración "Polizas").
- **NetBeans**: abre la carpeta como proyecto y presiona Run.
- **Terminal**: `./run.sh` (Linux/macOS) o `run.bat` (Windows).

El programa empieza en `Polizas.pantallas.Inicio`.
Para probar solo la conexión, ejecuta la clase `Polizas.clases.Conexion` (muestra cuántos registros hay en cada tabla).

## Carpetas

```
src/Polizas/
├── clases/      Las clases del diagrama UML y la conexión con la base de datos
└── pantallas/   Las ventanas del programa (Swing)
sql/
├── polizas.sql        Crea la base de datos (normalizada a 3FN)
└── datos_prueba.sql   Datos ficticios para probar
lib/             Driver JDBC de MariaDB
```

## Qué hace cada archivo

### `clases/` — el diagrama UML

| Archivo | Qué es | Qué hace |
|---|---|---|
| `Conexion.java` | Conexión | Abre la conexión con la base `polizas`, ejecuta los `INSERT`, `UPDATE` y `DELETE`, y traduce los errores de la base a mensajes claros. Si se ejecuta sola, prueba la conexión. |
| `Persona.java` | Clase abstracta | Datos que comparten Cliente y Usuario: id, nombre, teléfono y correo. `validarCorreo()`. |
| `Cliente.java` | Hereda de Persona | Cliente del despacho (RFC y domicilio). Registra, actualiza, elimina y consulta clientes. `validarRfc()`. |
| `Usuario.java` | Hereda de Persona | Quien entra al sistema. `iniciarSesion()` compara la contraseña cifrada; `desactivar()` en lugar de borrar; `tienePermiso()` según su rol. |
| `Marca.java` | Catálogo | Marcas de vehículo. Registra, actualiza, elimina y consulta. |
| `Modelo.java` | Catálogo | Modelos de vehículo; cada uno pertenece a una marca. |
| `Vehiculo.java` | Clase | Vehículo de un cliente, con su modelo, año, placas y número de serie. `tienePolizaVigente()` revisa la RN-04. |
| `Aseguradora.java` | Catálogo | Aseguradoras con las que trabaja el despacho. |
| `Cobertura.java` | Catálogo | Coberturas que puede incluir una póliza. |
| `TipoPoliza.java` | Catálogo | Tipos de póliza. No se borran: `desactivar()`. |
| `Cotizacion.java` | Clase | Cotización de un vehículo con una aseguradora. `aceptar()` y `generarPoliza()`. |
| `Poliza.java` | Clase central | Póliza con sus coberturas, pagos y cancelación. `calcularEstado()`, `calcularSaldo()`, `obtenerSiguienteCuota()`, `renovar()`, `cancelar()` e `imprimir()`. |
| `Pago.java` | Parte de Poliza | Cuota de pago. `validarMonto()` y `estaVencido()`. |
| `AvisoVencimiento.java` | Clase | Aviso al cliente por el fin de su póliza o por una cuota. `generarAvisoPoliza()`, `generarAvisoPago()`, `calcularDiasRestantes()` y `registrarSeguimiento()`. |
| `Cancelacion.java` | Parte de Poliza | Cancelación paso a paso: `solicitar()`, `validar()`, `autorizar()` y `registrarBaja()`. |
| `EstadoPoliza`, `EstadoPago`, `TipoAviso`, `Rol`, `MedioAviso`, `FormaPago`, `EstadoCancelacion` | Enumeraciones | Los valores fijos que puede tener cada dato. |

Además de los métodos del diagrama, cada catálogo tiene `listar()`, que trae todos los registros para llenar las tablas
de las pantallas.

### `pantallas/` — las ventanas

| Archivo | Qué hace |
|---|---|
| `Inicio.java` | Ventana principal con el menú. Aquí está el `main`: es lo que se ejecuta. |
| `Pantalla.java` | Molde de todas las pantallas: título, formulario, botones (Agregar, Actualizar, Eliminar, Limpiar) y tabla. Muestra los mensajes de error. |
| `PantallaClientes.java` | Captura de clientes (RF-01 a RF-04). |
| `PantallaVehiculos.java` | Captura de vehículos (RF-05 a RF-08); el cliente y el modelo se eligen de listas. |
| `PantallaAseguradoras.java` | Catálogo de aseguradoras (RF-24). |
| `PantallaTiposPoliza.java` | Catálogo de tipos de póliza (RF-25). El botón Eliminar se llama Desactivar. |
| `PantallaCoberturas.java` | Catálogo de coberturas (RF-25). |
| `PantallaMarcas.java` | Catálogo de marcas. |
| `PantallaModelos.java` | Catálogo de modelos; la marca se elige de una lista. |

## Cómo funciona un botón

Ejemplo: en Clientes, el botón **Agregar**:

1. `PantallaClientes.leer()` arma un objeto `Cliente` con lo escrito y revisa el RFC y el correo.
2. Se llama a `cliente.registrar()`, que hace el `INSERT` con `Conexion.ejecutar()`.
3. Si la base rechaza el dato (por ejemplo, un RFC repetido), `Conexion.mensaje()` lo convierte en un aviso claro.
4. La tabla se vuelve a llenar con `Cliente.listar()`.

## Correcciones de la Fase 3 que se ven en el código

- `Vehiculo` ya no guarda marca y modelo como texto: guarda un `Modelo`, y el `Modelo` tiene su `Marca`.
- El estado de una póliza y el de un pago no se guardan: se calculan con `calcularEstado()` y `estaVencido()`.
- La póliza no guarda al cliente: se obtiene de su vehículo.
- `Cotizacion` usa `formaPago` (un solo valor) en lugar de `opcionesPago`.
- `AvisoVencimiento` toma la fecha y el monto de la póliza o de la cuota; solo guarda `costoRenovacion`.

Las pantallas de pólizas, pagos, avisos, cancelaciones, usuarios e inicio de sesión se hacen en la Fase 4;
sus clases ya están listas en `clases/`.

## Nota de seguridad

`Conexion.java` y `sql/polizas.sql` traen la contraseña del usuario de desarrollo local. No uses esa contraseña en un servidor real.
