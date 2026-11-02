# Gastos con arquitectura hexagonal

Proyecto independiente creado a partir de `../gastos`, sin modificar el original. Se conserva el nombre solicitado de la carpeta, `gasto_exagonal`; el nombre habitual del patrón es **arquitectura hexagonal**, también llamada **puertos y adaptadores**.

La guía visual equivalente está en [arquitectura-hexagonal.html](arquitectura-hexagonal.html). Se puede abrir directamente en el navegador; no necesita servidor ni conexión a Internet.

## 1. Qué cambia y por qué

En el original, `Gasto` es simultáneamente el cuerpo HTTP, el objeto de negocio y una entidad JPA. `GastoService` importa Spring, HTTP y el repositorio de Spring Data. Eso hace más difícil probar las reglas sin arrancar la infraestructura o cambiar la base de datos.

En esta versión se separan esas responsabilidades:

```text
ENTRADA HTTP                         NÚCLEO                         SALIDA A DATOS
GastoController ──→ GestionGastosUseCase ←── GastoService ──→ GastoRepository
                           ↑                       │                   ↑
              TransactionalGastoUseCase            ↓           GastoPersistenceAdapter
                                                Gasto                  │
                                             Categoria                 ↓
                                                             SpringDataGastoRepository
                                                                      │
                                                                      ↓
                                                              GastoJpaEntity / MySQL
```

Las flechas anteriores combinan llamadas y relaciones de implementación; no representan únicamente imports. Las **dependencias de código** siempre apuntan hacia dentro:

```text
infrastructure → application → domain
infrastructure → domain
domain → Java estándar
```

- Un **puerto de entrada** es una interfaz con las operaciones que ofrece la aplicación.
- Un **puerto de salida** es una interfaz con lo que necesita la aplicación de un sistema externo.
- Un **adaptador de entrada** traduce una llamada externa a un caso de uso.
- Un **adaptador de salida** implementa un puerto con una tecnología concreta.
- No se trata de dibujar seis capas: el hexágono representa un núcleo aislado de sus tecnologías.

## 2. Qué hay dentro de cada capa

El paquete raíz es `com.aprendizaje.gastoexagonal`, dentro de `src/main/java`.

```text
gasto_exagonal/
├── pom.xml
├── README.md
├── arquitectura-hexagonal.html
└── src/
    ├── main/
    │   ├── java/com/aprendizaje/gastoexagonal/
    │   │   ├── GastosApplication.java
    │   │   ├── domain/
    │   │   │   ├── model/          Gasto, Categoria
    │   │   │   └── exception/      GastoInvalidoException, GastoNoEncontradoException
    │   │   ├── application/
    │   │   │   ├── dto/            GastoCommand, CalcularTotal
    │   │   │   ├── port/in/        GestionGastosUseCase, ConsultarCategoriasUseCase
    │   │   │   ├── port/out/       GastoRepository
    │   │   │   └── service/        GastoService, CategoriaService
    │   │   └── infrastructure/
    │   │       ├── adapter/in/web/
    │   │       │   ├── GastoController, CategoriaController, GlobalExceptionHandler
    │   │       │   └── dto/        GastoRequest, GastoResponse, ApiError
    │   │       ├── adapter/out/persistence/
    │   │       │   └── GastoJpaEntity, GastoPersistenceMapper,
    │   │       │       SpringDataGastoRepository, GastoPersistenceAdapter
    │   │       └── config/         BeanConfiguration, TransactionalGastoUseCase
    │   └── resources/
    │       ├── application.yaml
    │       └── db/gastos.sql
    └── test/
        ├── java/com/aprendizaje/gastoexagonal/
        │   ├── ArchitectureTest
        │   ├── domain/GastoTest
        │   ├── application/GastoServiceTest
        │   └── infrastructure/    GastosApiIntegrationTest, TransactionAndErrorsIntegrationTest
        └── resources/application.yaml
```

### 2.1. Dominio: qué es un gasto válido

**Carpeta:** `domain/`.

| Clase | Responsabilidad |
|---|---|
| `Gasto` | Modelo inmutable con identificador, descripción, importe, fecha y categoría. Contiene las reglas de validez. No lleva `@Entity`, anotaciones JSON ni setters. |
| `Categoria` | Enum original: `ALIMENTACION`, `TRANSPORTE`, `VIVIENDA`, `OCIO`, `SALUD`, `OTROS`. |
| `GastoInvalidoException` | Expresa un incumplimiento de las reglas; extiende `IllegalArgumentException`. No sabe qué es un HTTP 400. |
| `GastoNoEncontradoException` | Expresa que el gasto solicitado no existe. No sabe qué es un HTTP 404. |

**Reglas:** descripción recortada de 1 a 100 caracteres; importe positivo, con escala máxima de 2 decimales y hasta `99999999.99` (compatible con `DECIMAL(10,2)`); fecha y categoría obligatorias; no se admiten fechas futuras al crear o actualizar.

El constructor comprueba los datos estructurales. `validarFechaNoFutura(hoy)` comprueba la regla temporal con una fecha recibida como argumento. El servicio llama a este método antes de guardar. La lectura desde persistencia reconstruye el modelo sin volver a comprobar la regla temporal: leer un registro no es una nueva creación.

**No puede depender de:** Spring, JPA, controladores, SQL ni clases de `application` o `infrastructure`.

### 2.2. Aplicación: qué puede hacer el sistema

**Carpeta:** `application/`.

| Elemento | Responsabilidad |
|---|---|
| `GestionGastosUseCase` | Puerto de entrada para listar, filtrar, buscar, crear, actualizar, eliminar y calcular el total. |
| `ConsultarCategoriasUseCase` | Puerto de entrada para listar las categorías. |
| `GastoService` | Implementa el puerto de entrada, comprueba existencia y rangos, construye el modelo y usa el puerto de salida. |
| `CategoriaService` | Devuelve las constantes del enum; no necesita base de datos. |
| `GastoRepository` | Puerto de salida con métodos propios de la aplicación. **No extiende `JpaRepository`.** |
| `GastoCommand` | Datos para crear o actualizar, sin dependencias HTTP. No contiene un ID: en una actualización manda el de la URL. |
| `CalcularTotal` | Resultado del caso de uso con `desde`, `hasta` y `totalGastos`, conservando el record original. |

`Clock` es una abstracción de Java estándar. Se inyecta en el servicio para obtener la fecha actual de manera reproducible en los tests. En ejecución se configura en UTC.

Los puertos se colocan en aplicación porque describen las capacidades y necesidades de sus casos de uso. Otra distribución hexagonal puede colocar ciertos puertos en dominio; lo importante es que el núcleo no importe los adaptadores.

**No puede depender de:** Spring, HTTP, Hibernate, H2, MySQL ni infraestructura.

### 2.3. Adaptador de entrada: HTTP

**Carpeta:** `infrastructure/adapter/in/web/`.

- `GastoController` y `CategoriaController` conservan las rutas originales. Reciben **interfaces de entrada**, no servicios concretos ni repositorios.
- `GastoRequest` representa el JSON de entrada y lo convierte a `GastoCommand`.
- `GastoResponse` convierte el modelo de dominio a un JSON explícito de salida.
- `ApiError` conserva los campos `status`, `error` y `message`.
- `GlobalExceptionHandler` traduce excepciones a respuestas HTTP. Los códigos de estado pertenecen aquí, nunca al servicio de aplicación.

El controlador no ejecuta consultas ni decide las reglas del importe. El núcleo no sabe si lo invoca un navegador, una consola o una tarea programada.

### 2.4. Adaptador de salida: persistencia

**Carpeta:** `infrastructure/adapter/out/persistence/`.

| Clase | Responsabilidad |
|---|---|
| `GastoJpaEntity` | Representa la tabla `gasto`. Aquí viven `@Entity`, `@Id`, `@Column` y `@Enumerated`. |
| `GastoPersistenceMapper` | Convierte `Gasto ↔ GastoJpaEntity`. No contiene consultas ni reglas nuevas. |
| `SpringDataGastoRepository` | Extiende `JpaRepository`, filtra por categoría y realiza la suma JPQL con `BETWEEN` y `COALESCE`. |
| `GastoPersistenceAdapter` | Implementa el puerto `GastoRepository`, delega a Spring Data y devuelve modelos de dominio. |

La **inversión de dependencias** está aquí: `GastoService` conoce una interfaz del núcleo; la clase JPA externa implementa esa interfaz. El núcleo no importa al adaptador.

Cambiar MySQL por otro almacenamiento requiere otro adaptador y su configuración, no cambiar las reglas de `Gasto`. No significa que baste con cambiar una URL para cualquier tecnología.

### 2.5. Configuración y arranque: conectar las piezas

**Carpeta:** `infrastructure/config/` y clase raíz `GastosApplication`.

1. `GastosApplication` arranca Spring Boot y activa el descubrimiento de componentes.
2. Spring Data crea la implementación de `SpringDataGastoRepository`.
3. Spring inyecta ese repositorio en `GastoPersistenceAdapter`.
4. `BeanConfiguration` crea `Clock`, `GastoService` y `CategoriaService`.
5. El servicio de gastos se envuelve en `TransactionalGastoUseCase`, que implementa el mismo puerto de entrada.
6. Spring aplica un proxy transaccional al decorador y lo inyecta en `GastoController`.

El decorador conserva el comportamiento transaccional del original **sin introducir `@Transactional` en aplicación**. Las lecturas son `readOnly`; crear, actualizar y eliminar usan transacciones de escritura. En una actualización, la búsqueda y el guardado pertenecen a la misma transacción; si falla una excepción de ejecución, se revierte. Esto no añade control de concurrencia optimista: no hay campo `@Version`.

## 3. Migración: de las clases originales a las nuevas

| Original en `gastos` | Destino o transformación |
|---|---|
| `GastosApplication` | Se mantiene como punto de arranque, con nuevo paquete. |
| `entity/Gasto` | Se divide en `domain/model/Gasto`, `GastoJpaEntity` y DTOs HTTP. |
| `entity/Categoria` | Se mueve al dominio y conserva todos sus valores. |
| `service/GastoService` | Se mantiene el nombre; ahora implementa un puerto, sin imports de Spring ni HTTP. |
| `service/CategoriaService` | Se mantiene como implementación Java del puerto de categorías. |
| `repository/GastoRepository` | Se separa en puerto propio, adaptador y repositorio Spring Data. |
| `controller/*` | Se convierten en adaptadores de entrada que dependen de interfaces. |
| `dto/CalcularTotal` | Se mueve a aplicación porque es el resultado de un caso de uso. |
| `dto/ApiError` | Se mueve al adaptador HTTP porque describe errores del transporte. |
| `exception/GlobalExceptionHandler` | Se mueve al adaptador HTTP; ahora traduce excepciones específicas. |
| `RuntimeException`, `IllegalArgumentException`, `ResponseStatusException` usadas en servicios | Se sustituyen por `GastoNoEncontradoException` y `GastoInvalidoException`. El original no tenía excepciones de negocio propias. |
| `@Service`, `@Transactional` en servicios | La creación de beans y las transacciones pasan a infraestructura. |

## 4. Cada proceso, paso a paso

### 4.1. Crear un gasto

1. El cliente envía `POST /api/v1/gastos` con JSON.
2. Spring deserializa el JSON a `GastoRequest`; si no puede hacerlo, responde 400.
3. El controlador convierte el request a `GastoCommand` y llama al puerto de entrada.
4. El decorador abre la transacción de escritura.
5. `GastoService` construye un `Gasto`; el dominio valida sus campos.
6. El servicio obtiene el día con `Clock` y pide al dominio validar que la fecha no sea futura.
7. El servicio llama a `GastoRepository.save` sin conocer JPA.
8. El adaptador convierte el modelo a entidad y Spring Data inserta la fila.
9. MySQL genera el ID; el adaptador devuelve un `Gasto` con ese ID y se confirma la transacción.
10. El controlador responde **201**, cabecera `Location` con la URL del recurso y cuerpo vacío, igual que el original.

### 4.2. Listar o filtrar

1. `GET /api/v1/gastos` llama a `findAll`; con `?categoria=OCIO`, llama a `findByCategoria`.
2. El decorador abre una transacción de lectura.
3. El servicio delega en el puerto y el adaptador consulta Spring Data.
4. Las entidades se convierten a dominio y después a `GastoResponse` en el controlador.
5. Se devuelve **200** con una lista, o `[]` si no hay resultados. Un enum desconocido produce 400.

### 4.3. Buscar por identificador

1. `GET /api/v1/gastos/{id}` llega al puerto de entrada.
2. El servicio valida que el ID sea positivo y consulta el puerto de salida.
3. Si existe, se transforma el dominio a respuesta y se devuelve **200**.
4. Si el `Optional` está vacío, se lanza `GastoNoEncontradoException`; el adaptador HTTP la convierte en **404**.

### 4.4. Actualizar

1. `PUT /api/v1/gastos/{id}` recibe todos los campos del gasto; no es una actualización parcial.
2. Se abre una transacción de escritura y se busca el gasto; si no existe, se devuelve 404.
3. Se construye un nuevo modelo inmutable con el ID existente y los datos recibidos.
4. Se aplican **las mismas validaciones que al crear**, incluida la fecha no futura.
5. El adaptador guarda la entidad y devuelve el modelo actualizado.
6. La respuesta es **200** con el gasto. Los datos inválidos generan 400 sin modificar el registro anterior.

### 4.5. Eliminar

1. `DELETE /api/v1/gastos/{id}` abre una transacción de escritura.
2. Se comprueba la existencia; un gasto ausente produce 404.
3. El adaptador elimina por el ID del modelo encontrado.
4. Se confirma la transacción y se devuelve **204**, sin cuerpo.

### 4.6. Calcular el total entre fechas

1. `GET /api/v1/gastos/total?desde=2026-10-01&hasta=2026-10-31` convierte las fechas ISO a `LocalDate`.
2. El caso de uso exige ambas fechas y que `desde` sea anterior o igual a `hasta`.
3. El adaptador ejecuta `SUM(importe)` con `BETWEEN`: **los dos extremos están incluidos**.
4. `COALESCE` convierte una suma sin filas en cero.
5. El servicio crea `CalcularTotal` y el controlador devuelve **200** con el resultado. Un rango invertido genera 400, no 404.

### 4.7. Consultar categorías

1. `GET /api/v1/categorias` llama a `ConsultarCategoriasUseCase`.
2. `CategoriaService` devuelve los seis valores de `Categoria`.
3. Se responde **200** con un array de cadenas. Este flujo no consulta persistencia ni necesita una transacción.

### 4.8. Gestionar errores

| Situación | Tratamiento | HTTP |
|---|---|---|
| Campos inválidos, ID no positivo, fecha futura o rango invertido | `GastoInvalidoException` → `ApiError` | 400 |
| JSON inválido, categoría desconocida, parámetro ausente o formato incorrecto | Excepción de Spring MVC → `ApiError` | 400 |
| Gasto inexistente | `GastoNoEncontradoException` → `ApiError` | 404 |
| Fallo inesperado de ejecución, por ejemplo de persistencia | Se registra la excepción; se devuelve un mensaje genérico, sin detalles internos | 500 |

```json
{"status":404,"error":"Not Found","message":"Gasto no encontrado: 123"}
```

El manejador original convertía cualquier `RuntimeException` en 404, incluso algunos fallos que no eran de recurso inexistente. Aquí no se ocultan errores del servidor como si fueran un gasto ausente.

## 5. Contrato HTTP conservado

| Método | Ruta | Respuesta correcta |
|---|---|---|
| GET | `/api/v1/gastos` | 200, lista |
| GET | `/api/v1/gastos?categoria=OCIO` | 200, lista filtrada |
| GET | `/api/v1/gastos/{id}` | 200, gasto |
| POST | `/api/v1/gastos` | 201, `Location`, sin cuerpo |
| PUT | `/api/v1/gastos/{id}` | 200, gasto actualizado |
| DELETE | `/api/v1/gastos/{id}` | 204, sin cuerpo |
| GET | `/api/v1/gastos/total?desde=AAAA-MM-DD&hasta=AAAA-MM-DD` | 200, total |
| GET | `/api/v1/categorias` | 200, categorías |

JSON para crear o actualizar:

```json
{
  "descripcion": "Compra semanal",
  "importe": 54.30,
  "fecha": "2026-10-03",
  "categoria": "ALIMENTACION"
}
```

**Cambios intencionados respecto al original:** se valida también al actualizar; se requieren fecha y categoría; se ajusta el importe a la capacidad de la columna; se guarda la descripción recortada; se distinguen errores 400/404/500; el reloj se fija en UTC. El modelo ya no tiene setters ni anotaciones JPA.

## 6. Ejecutar el proyecto

### Requisitos

- JDK 21 y Maven 3.9 o posterior.
- MySQL 8 para la ejecución normal.
- Acceso a los repositorios Maven para resolver Spring Boot **4.1.2**, la misma versión declarada por el proyecto original. Si un repositorio o proxy corporativo no la sirve, Maven no podrá construir; no se ha cambiado de versión silenciosamente.

### Preparar una base independiente

Abre MySQL con un usuario que pueda crear la base y ejecuta:

```sql
SOURCE C:/Users/0019601/Documents/ruta-aprendizaje-java/gasto_exagonal/src/main/resources/db/gastos.sql;
```

Adapta la ruta si has movido el repositorio. El script crea `gasto_exagonal_db` y datos de ejemplo. **Es un script de inicialización manual, no una migración ni un script para ejecutar repetidamente**: la tabla y los inserts no son idempotentes. No toca `gastos_db`.

### Arrancar desde PowerShell

```powershell
cd gasto_exagonal
$env:DB_USERNAME = "root"
$env:DB_PASSWORD = "tu_password_local"
mvn spring-boot:run
```

El puerto predeterminado es **8081**, para no colisionar con el 8080 de `gastos`. Variables opcionales: `SERVER_PORT`, `DB_USERNAME` y `DB_URL`. La contraseña no está incluida en el repositorio. `ddl-auto: validate` comprueba el esquema; no crea tablas automáticamente. La URL local desactiva TLS: no es una configuración de producción.

### Probar desde PowerShell

```powershell
$body = @{
    descripcion = "Compra semanal"
    importe = 54.30
    fecha = "2026-10-03"
    categoria = "ALIMENTACION"
} | ConvertTo-Json

$respuesta = Invoke-WebRequest -Method Post `
    -Uri "http://localhost:8081/api/v1/gastos" `
    -ContentType "application/json" -Body $body
$respuesta.Headers.Location

Invoke-RestMethod "http://localhost:8081/api/v1/gastos?categoria=ALIMENTACION"
Invoke-RestMethod "http://localhost:8081/api/v1/gastos/total?desde=2026-10-01&hasta=2026-10-31"
Invoke-RestMethod "http://localhost:8081/api/v1/categorias"
```

No se añade autenticación: es un ejemplo de aprendizaje, igual que el proyecto de partida.

## 7. Pruebas y forma de aprender con ellas

Desde `gasto_exagonal`:

```powershell
mvn test
mvn package
```

| Prueba | Qué comprueba | Infraestructura necesaria |
|---|---|---|
| `GastoTest` | Reglas de descripción, importe, nulos, límites y fecha futura | Ninguna: JUnit y Java |
| `GastoServiceTest` | CRUD, errores, filtrado, rangos, reloj fijo y categorías | Puerto de salida simulado con Mockito, sin Spring |
| `GastosApiIntegrationTest` | JSON → controlador → caso de uso → JPA → H2; estados HTTP, persistencia, filtros y suma inclusiva | Spring Boot + H2 en memoria, sin MySQL ni contraseña |
| `TransactionAndErrorsIntegrationTest` | El decorador abre transacciones de lectura/escritura y un fallo inesperado devuelve 500 sin exponer detalles | Spring Boot con el puerto de salida simulado; sin transacción de test que oculte la del decorador |
| `ArchitectureTest` | Imports permitidos en el núcleo y separación de controladores respecto a servicios concretos y persistencia | Lectura del código fuente; comprobación didáctica, no un análisis completo de bytecode |

Los tests de API con persistencia son transaccionales y revierten sus datos al terminar. Los tests del decorador no abren una transacción de test: comprueban la que inicia la infraestructura real y simulan el puerto de salida. H2 se declara explícitamente como dependencia de test; la configuración de test sustituye el datasource normal. **H2 no sustituye una validación de compatibilidad real con MySQL**: no ejecuta el script SQL de MySQL, sino que Hibernate crea el esquema de test.

Recorrido recomendado: lee `Gasto`, después los puertos, después `GastoService`, luego los controladores, el adaptador JPA y finalmente `BeanConfiguration` y el decorador. Sigue un POST con el depurador. Para comprobar la utilidad de la arquitectura, prueba el servicio con un repositorio en memoria: no deberías tener que modificar su lógica.
