# Práctica 3 POO — Sistema de Gestión de Pólizas

## 1. Base de datos
Con MariaDB encendido (`sudo systemctl start mariadb`), corre el script como root. Crea la base `polizas`, la tabla `marca` y el usuario `poliza` / `poliza123` que usa la aplicación:

```bash
sudo mariadb < sql/polizas.sql
```

## 2. Driver JDBC
Ya viene incluido: `lib/mariadb-java-client.jar` (MariaDB Connector/J 3.5.3). NetBeans lo toma de `nbproject/project.properties` y VS Code de `.vscode/settings.json`.

## 3. Ejecutar en VS Code
- Requiere el **Extension Pack for Java**.
- Abre la carpeta `poliza` y presiona **F5** (configuración "Polizas").
- O en la terminal: `./run.sh` (Linux) o `run.bat` (Windows).

## 4. Uso
**Base de Datos → Marcas**, escribe una marca y presiona **Agregar**. Usa **Consultar** para recargar la tabla. Para **Actualizar** o **Eliminar**, primero selecciona la marca en la tabla.

## Correcciones respecto al código del protocolo
1. **Conexion**: la conexión apuntaba a la base `trapy` con usuario `j`/`123`. Ahora apunta a `polizas` en MariaDB con el usuario `poliza` / `poliza123`, que crea el script. Los datos están en constantes. Además, se muestra el mensaje real del error y se valida `cnx` antes de cerrar.
2. **Mensaje**: se eliminó `import Trapy.*;`, que no existe.
3. **MarcasVista**: se eliminó `import Grafico.Conexion;`, que no existe, y el import redundante de `MarcasTM`.
4. **MarcasModelo**: las consultas usaban la tabla `marcas` con columnas `id` y `nombre`. Se ajustaron a la tabla real del script: `marca(idMarca, nombreMarca)`. Si no hay conexión, regresa una lista vacía o 0 en vez de fallar.
5. **MarcasVista.actualizarVista**: `fireTableDataChanged()` se llama después de llenar la lista, para que la tabla se refresque.
6. **MarcasControl**:
   - Se valida que haya una fila seleccionada antes de leerla.
   - Se valida que el nombre no esté vacío al agregar.
   - El mensaje ahora dice "Marca registrada" o "No se pudo registrar".
7. **Bienvenida**: la tarjeta inicial del CardLayout tiene nombre (`"home"`). Se corrigió `MAXIMIZED_BOTH`.
8. **MarcasModelo / MarcasVista / MarcasControl**: se implementaron `update` y `delete` (en el protocolo regresaban 0) con los botones **Actualizar** y **Eliminar**, para completar el CRUD.
9. **Vista** y **Marcas/Conexion**: aparecen en la estructura, pero el protocolo no trae su código. Se crearon como clases base mínimas.
