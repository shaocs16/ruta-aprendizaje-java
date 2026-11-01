**Los cinco niveles** [1. Gastos personales](#n1) [2. Biblioteca comunitaria](#n2) [3. Reservas de coworking](#n3) [4. Pedidos y stock](#n4) [5. Venta de entradas](#n5)

# Ruta de aprendizaje: Java, Spring Boot y MySQL

Cinco proyectos independientes que suben de dificultad. Tú escribes todo el código: aquí tienes qué debe hacer cada pieza, no cómo programarla. Al terminar un proyecto, envíame tu implementación y la reviso como una revisión de código senior (arquitectura, Clean Code, SOLID, persistencia, errores, rendimiento y pruebas). No avanzamos al siguiente hasta que lo pidas.

## 1. Control de gastos personales Fácil

A. Información general

**Objetivo:** construir tu primera API REST completa con Spring Boot y persistirla en MySQL.\
**Problema real:** anotar los gastos diarios y saber en qué se va el dinero cada mes, sin hojas de cálculo dispersas.\
**Funcionamiento:** la API registra gastos (descripción, importe, fecha, categoría), permite consultarlos y filtrarlos, y calcula el total gastado en un periodo.

### Funcionalidades

- CRUD de gastos.
- Listar por categoría y entre dos fechas.
- Total gastado en un rango de fechas.

### Conceptos que se trabajan

Estructura de un proyecto Spring Boot, inyección de dependencias por constructor, `@Entity`, enums con `@Enumerated`, `BigDecimal` y `LocalDate`, `JpaRepository`, métodos de consulta derivados, capas controller/service/repository, `ResponseEntity`, `application.yml`.

### Al terminar sabrás

Crear un proyecto desde cero, conectarlo a MySQL, mapear una tabla a una entidad, exponer un CRUD REST y probar la lógica del servicio de forma aislada.

### Conocimientos previos

POO en Java, colecciones y streams básicos, SQL básico, HTTP/JSON y Postman o curl.

B. Requisitos funcionales y técnicos

### Obligatorios

- [ ] CRUD completo de gastos.
- [ ] Descripción obligatoria, sin ser solo espacios, máximo 100 caracteres.
- [ ] Importe mayor que 0, con 2 decimales como máximo, sin usar tipos de coma flotante.
- [ ] Fecha obligatoria y no posterior a hoy.
- [ ] Categoría obligatoria dentro de una lista cerrada: ALIMENTACION, TRANSPORTE, VIVIENDA, OCIO, SALUD, OTROS.
- [ ] Listado filtrado por categoría y por rango de fechas (ambos extremos incluidos).
- [ ] Total del rango: suma de importes; si no hay gastos, 0.00.

### Casos límite

- Id inexistente en consulta, actualización o borrado.
- Rango con «desde» posterior a «hasta», o con fechas mal formadas.
- Categoría que no existe.
- Importe 0, negativo o con más de 2 decimales.
- Gasto cuya fecha es exactamente un extremo del rango.

### Integridad

Ningún campo admite NULL; la base de datos debe rechazar por sí misma importes no positivos y categorías no válidas. Una sola entidad, sin relaciones todavía.

### Opcionales

- [ ] Orden por fecha descendente.
- [ ] Filtro combinado categoría + rango.
- [ ] Búsqueda por texto en la descripción, sin distinguir mayúsculas.
- [ ] Total por categoría en un rango.

C. Instalación y configuración

En **start.spring.io**: Project **Maven**, Language **Java**, Packaging **Jar**, Group `com.aprendizaje`, Artifact `gastos`, Package `com.aprendizaje.gastos`. Elige **Java 25** (LTS; Spring Boot 4 exige 17 como mínimo, usa 21 si es lo que tienes instalado) y **Spring Boot 4.1.x**, la última estable (nada de SNAPSHOT ni milestones). La rama 4.0 pierde soporte gratuito a final de 2026, por eso no la usamos en proyectos nuevos.

| Dependencia | Para qué |
| --- | --- |
| Spring Web | Controladores REST y servidor embebido. |
| Spring Data JPA | JPA/Hibernate y repositorios. |
| MySQL Driver | Conector JDBC para MySQL. |
| Spring Boot DevTools (opcional) | Reinicio rápido al guardar. |

El starter de pruebas (JUnit y Mockito) se añade solo. Boot 4 puede traer JUnit Jupiter 6: para lo que usarás (`@Test`, aserciones) es igual que JUnit 5. Validation se añade en el proyecto 2.

```
server:
  port: 8080
spring:
  application:
    name: gastos
  datasource:
    url: jdbc:mysql://localhost:3306/gastos_db?serverTimezone=UTC&allowPublicKeyRetrieval=true&useSSL=false
    username: root          # mejor un usuario propio con permisos solo sobre gastos_db
    password: tu_password
  jpa:
    open-in-view: false
    show-sql: true
    hibernate:
      ddl-auto: validate    # el esquema lo creas tú con el script SQL
    properties:
      hibernate:
        format_sql: true
```

D. Diseño de la base de datos

Una tabla, `gasto`, sin claves foráneas ni relaciones. Clave primaria autoincremental; todas las columnas NOT NULL; CHECK en importe y categoría; índices en fecha y categoría porque son los criterios de búsqueda.

```
CREATE DATABASE IF NOT EXISTS gastos_db
  CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE gastos_db;

CREATE TABLE gasto (
  id          BIGINT        NOT NULL AUTO_INCREMENT,
  descripcion VARCHAR(100)  NOT NULL,
  importe     DECIMAL(10,2) NOT NULL,
  fecha       DATE          NOT NULL,
  categoria   VARCHAR(20)   NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT ck_gasto_importe CHECK (importe > 0),
  CONSTRAINT ck_gasto_categoria CHECK (categoria IN
    ('ALIMENTACION','TRANSPORTE','VIVIENDA','OCIO','SALUD','OTROS')),
  INDEX idx_gasto_fecha (fecha),
  INDEX idx_gasto_categoria (categoria)
) ENGINE=InnoDB;

INSERT INTO gasto (descripcion, importe, fecha, categoria) VALUES
 ('Compra semanal', 54.30, '2026-10-03', 'ALIMENTACION'),
 ('Abono transporte', 40.00, '2026-10-01', 'TRANSPORTE'),
 ('Cine', 18.50, '2026-10-10', 'OCIO'),
 ('Farmacia', 12.75, '2026-10-12', 'SALUD');
```

E. Arquitectura y estructura

```
gastos/
├─ pom.xml
└─ src/
   ├─ main/
   │  ├─ java/com/aprendizaje/gastos/
   │  │  ├─ GastosApplication.java
   │  │  ├─ controller/GastoController.java
   │  │  ├─ service/GastoService.java
   │  │  ├─ repository/GastoRepository.java
   │  │  └─ entity/
   │  │     ├─ Gasto.java
   │  │     └─ Categoria.java
   │  └─ resources/application.yml
   └─ test/java/com/aprendizaje/gastos/
      ├─ GastosApplicationTests.java
      └─ service/GastoServiceTest.java
```

| Clase | Paquete | Responsabilidad | Relación |
| --- | --- | --- | --- |
| GastosApplication | raíz | Punto de arranque: activa la configuración automática y el escaneo de componentes. | Ninguna directa. |
| Categoria | entity | Enum con las seis categorías permitidas. | La usan Gasto, el repository y el controller. |
| Gasto | entity | Representa una fila de la tabla `gasto`; es a la vez modelo de dominio y de persistencia. | La gestionan el repository y el service. |
| GastoRepository | repository | Acceso a datos de Gasto: operaciones básicas y las consultas por categoría y por rango de fechas. | Solo la usa el service. |
| GastoService | service | Reglas de negocio: comprueba los datos, decide cuándo una operación falla y calcula el total del periodo. | Usa el repository; la usa el controller. |
| GastoController | controller | Expone los endpoints, traduce HTTP a llamadas al service y elige el código de estado. | Usa el service; no toca el repository. |
| GastoServiceTest | test.service | Verifica las reglas del service con el repository simulado. | Prueba GastoService. |

F. Diseño de la API REST

| Método y ruta | Descripción | Entrada | Éxito | Errores |
| --- | --- | --- | --- | --- |
| POST /api/gastos | Crea un gasto | Body JSON | 201 | 400 |
| GET /api/gastos | Lista todos | — | 200 | — |
| GET /api/gastos/{id} | Detalle | id en la ruta | 200 | 404 |
| PUT /api/gastos/{id} | Reemplaza un gasto | id + Body JSON | 200 | 400, 404 |
| DELETE /api/gastos/{id} | Elimina | id en la ruta | 204 | 404 |
| GET /api/gastos?categoria=OCIO | Filtra por categoría | categoria | 200 | 400 |
| GET /api/gastos?desde=2026-10-01&hasta=2026-10-31 | Filtra por rango (inclusivo) | desde, hasta (ISO yyyy-MM-dd) | 200 | 400 |
| GET /api/gastos/total?desde=…&hasta=… | Total del rango | desde, hasta | 200 | 400 |

```
// POST /api/gastos  — petición
{ "descripcion": "Cine", "importe": 18.50, "fecha": "2026-10-10", "categoria": "OCIO" }

// 201 Created — respuesta
{ "id": 3, "descripcion": "Cine", "importe": 18.50, "fecha": "2026-10-10", "categoria": "OCIO" }

// GET /api/gastos/total?desde=2026-10-01&hasta=2026-10-31 — 200
{ "desde": "2026-10-01", "hasta": "2026-10-31", "total": 125.55 }

// 404 / 400 — formato de error único para toda la API
{ "status": 400, "error": "Bad Request", "message": "El importe debe ser mayor que 0" }
```

G. Validaciones y manejo de errores

- Obligatoriedad: descripción, importe, fecha y categoría; un body vacío o incompleto es 400.
- Longitud: descripción entre 1 y 100 caracteres tras eliminar espacios sobrantes.
- Valores: importe > 0 y máximo 2 decimales; fecha no futura; categoría dentro del enum.
- Parámetros: fechas en formato ISO; «desde» no posterior a «hasta».
- Recurso inexistente: 404 con mensaje que indique el id buscado.
- Todas las respuestas de error siguen el mismo JSON (status, error, message). Aún no hay manejo global: en este nivel lo resuelves en el propio service/controller y en el nivel 2 lo centralizarás.

H. Pruebas

Pruebas de servicio con JUnit y Mockito (repository simulado). Escenarios a cubrir:

- Crear un gasto válido lo guarda y devuelve el gasto con id.
- Importe 0, negativo o con 3 decimales: se rechaza y no se guarda nada.
- Fecha futura y descripción en blanco: se rechazan.
- Buscar, actualizar y eliminar un id inexistente: falla de forma distinguible.
- Rango invertido: se rechaza antes de consultar.
- Total de un rango sin gastos: 0.00; total con varios gastos: suma exacta (por ejemplo 0.10 + 0.20 = 0.30).
- Prueba de arranque (`contextLoads`) con la base de datos local levantada.

Repositorios, controladores e integración llegan en los niveles 2 a 4.

I. Criterios de finalización

- [ ] Todos los endpoints responden con los códigos y el JSON definidos.
- [ ] Los casos límite de la sección B se comportan como se describe.
- [ ] El controller no contiene reglas de negocio ni accede al repository.
- [ ] La entidad coincide con la tabla y la app arranca con `ddl-auto: validate`.
- [ ] Ningún importe pasa por tipos de coma flotante.
- [ ] Formato de error único en toda la API.
- [ ] Los escenarios de la sección H pasan.
- [ ] Nombres claros, sin código muerto ni valores fijos repetidos, sin contraseñas reales en el repositorio.

## 2. Biblioteca comunitaria Fácil-medio

**Problema:** una biblioteca de barrio necesita catalogar libros, autores y categorías, y saber qué hay disponible.\
**Modelo:** Libro N—1 Autor; Libro N—M Categoría.\
**Conceptos nuevos:** `@ManyToOne` y `@ManyToMany`, DTOs de entrada y salida, Bean Validation, consultas con `@Query`, `@RestControllerAdvice`, pruebas de repositorio y de controlador.\
**Reto:** ISBN único, no borrar un autor con libros y no exponer entidades en la API.

A–C. Información, requisitos y configuración

### A. Información general

**Objetivo:** modelar relaciones entre entidades y separar la API del modelo con DTOs. **Funcionamiento:** catálogo de autores, categorías y libros con búsqueda por título, autor y categoría. **Conocimientos previos:** proyecto 1. **Al terminar sabrás:** relaciones JPA, validar entradas, centralizar errores y probar capas por separado.

### B. Requisitos

- [ ] CRUD de autores, categorías y libros.
- [ ] ISBN de 13 dígitos, único. Título de 1 a 150 caracteres. Copias >= 0. Año de publicación entre 1450 y el año actual.
- [ ] Todo libro tiene un autor existente y al menos una categoría existente.
- [ ] Nombre de categoría único sin distinguir mayúsculas.
- [ ] No se borra un autor ni una categoría con libros asociados (409).
- [ ] Búsqueda de libros por fragmento de título, autor y categoría, combinables.

**Casos límite:** ISBN repetido al crear y al actualizar (el propio libro no cuenta como repetido), autor o categoría inexistentes en el body, lista de categorías vacía o con repetidas. **Opcionales:** ordenar por título; contar libros por autor.

### C. Configuración

Igual que el proyecto 1 con base de datos `biblioteca_db` y una dependencia nueva: **Validation** (Bean Validation para anotar los DTOs).

D–E. Base de datos y arquitectura

```
CREATE DATABASE IF NOT EXISTS biblioteca_db CHARACTER SET utf8mb4;
USE biblioteca_db;
CREATE TABLE autor (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(100) NOT NULL,
  nacionalidad VARCHAR(50) NULL) ENGINE=InnoDB;
CREATE TABLE categoria (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(50) NOT NULL UNIQUE) ENGINE=InnoDB;
CREATE TABLE libro (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  isbn CHAR(13) NOT NULL UNIQUE,
  titulo VARCHAR(150) NOT NULL,
  anio_publicacion SMALLINT NOT NULL,
  copias INT NOT NULL,
  autor_id BIGINT NOT NULL,
  CONSTRAINT ck_libro_copias CHECK (copias >= 0),
  CONSTRAINT ck_libro_anio CHECK (anio_publicacion >= 1450),
  CONSTRAINT fk_libro_autor FOREIGN KEY (autor_id) REFERENCES autor(id),
  INDEX idx_libro_titulo (titulo)) ENGINE=InnoDB;
CREATE TABLE libro_categoria (
  libro_id BIGINT NOT NULL, categoria_id BIGINT NOT NULL,
  PRIMARY KEY (libro_id, categoria_id),
  FOREIGN KEY (libro_id) REFERENCES libro(id),
  FOREIGN KEY (categoria_id) REFERENCES categoria(id)) ENGINE=InnoDB;
-- Datos: inserta 3 autores, 4 categorías y 6 libros de ejemplo.
```

Cardinalidades: autor 1—N libro; libro N—M categoría (tabla intermedia).

```
com.aprendizaje.biblioteca/
├─ controller/   AutorController, CategoriaController, LibroController
├─ service/      AutorService, CategoriaService, LibroService
├─ repository/   AutorRepository, CategoriaRepository, LibroRepository
├─ entity/       Autor, Categoria, Libro
├─ dto/          *Request y *Response de cada recurso
├─ exception/    RecursoNoEncontradoException, ConflictoException,
│                GlobalExceptionHandler, ErrorResponse
└─ BibliotecaApplication
```

- **entity.\***: filas de las tablas; Libro es el lado propietario de la relación con Categoría y la referencia a Autor.
- **dto.\*Request**: datos de entrada con sus validaciones. **dto.\*Response**: lo que el cliente ve; nunca incluye entidades.
- **repository.\***: acceso a datos; LibroRepository aporta las búsquedas combinadas y la comprobación de libros por autor y por categoría.
- **service.\***: reglas de negocio, conversión entre entidades y DTOs, lanza las excepciones de dominio.
- **controller.\***: HTTP y validación de entrada; sin lógica de negocio.
- **exception.\***: dos excepciones de dominio (404 y 409), ErrorResponse (formato único de error) y GlobalExceptionHandler, que las traduce a HTTP en un solo lugar.

F–I. API, validaciones, pruebas y criterios

| Ruta | Métodos y códigos |
| --- | --- |
| /api/autores, /api/categorias, /api/libros | POST 201 (400, 409); GET lista 200; GET /{id} 200 (404); PUT /{id} 200 (400, 404, 409); DELETE /{id} 204 (404, 409) |
| /api/libros?titulo=&autorId=&categoriaId= | GET 200; filtros opcionales y combinables |
| /api/autores/{id}/libros | GET 200 (404) |

```
// POST /api/libros
{ "isbn": "9780307474728", "titulo": "Cien años de soledad", "anioPublicacion": 1967,
  "copias": 3, "autorId": 1, "categoriaIds": [1, 2] }
// 201
{ "id": 5, "isbn": "9780307474728", "titulo": "Cien años de soledad", "anioPublicacion": 1967,
  "copias": 3, "autor": { "id": 1, "nombre": "Gabriel García Márquez" },
  "categorias": [ { "id": 1, "nombre": "Novela" }, { "id": 2, "nombre": "Clásicos" } ] }
// 400 con detalle por campo
{ "status": 400, "error": "Validation failed", "message": "Datos no válidos",
  "fields": { "isbn": "Debe tener 13 dígitos" } }
```

### G. Validaciones

En los DTOs: obligatorios, longitudes, patrón del ISBN, mínimos numéricos. En el service: existencia de autor y categorías, unicidad de ISBN y de nombre, borrados con dependencias. Un único `GlobalExceptionHandler` produce 400 (con detalle por campo), 404 y 409 con el mismo formato.

### H. Pruebas

- Servicio (Mockito): crear con ISBN repetido, autor inexistente, categorías vacías; borrar autor con libros.
- Repositorio (`@DataJpaTest`): búsqueda por título sin distinguir mayúsculas, filtros combinados, comprobación de libros por autor.
- Controlador (`@WebMvcTest`): cada código HTTP y el JSON de error; body inválido.

### I. Criterios de finalización

- [ ] Ninguna entidad sale por la API.
- [ ] Todos los errores salen del manejador global con el mismo formato.
- [ ] Las relaciones coinciden con el esquema y arranca con `validate`.
- [ ] Las pruebas de las tres capas pasan.
- [ ] Sin lógica de negocio en controllers ni en DTOs.

## 3. Reservas de salas de coworking Medio

**Problema:** reservar salas por franjas horarias sin que dos reservas se pisen.\
**Modelo:** Sala 1—N Reserva N—1 Miembro, con tarifa y cuota mensual de horas.\
**Conceptos nuevos:** JPQL, paginación y ordenación (`Pageable`), `@Transactional`, mappers propios, lógica con varias operaciones.\
**Reto:** detectar solapamientos, cancelar con política de antelación y respetar la cuota.

A–C. Información, requisitos y configuración

### A. Información general

**Objetivo:** programar reglas de negocio con varias operaciones dentro de una transacción y consultar con paginación. **Funcionamiento:** los miembros reservan salas por franjas; el sistema impide solapamientos, calcula el precio y controla la cuota mensual. **Previos:** proyectos 1 y 2. **Aprenderás:** JPQL, `Pageable`, `@Transactional` y mappers.

### B. Requisitos

- [ ] CRUD de salas y miembros; una sala se desactiva en lugar de borrarse si tiene reservas.
- [ ] Reserva: mismo día, entre 08:00 y 20:00, inicio y fin en hora o media hora, duración de 1 a 4 horas.
- [ ] Dos reservas CONFIRMADAS de la misma sala no pueden solaparse; que una termine cuando empieza otra no es solapamiento.
- [ ] Sala inactiva no reservable; la reserva debe ser futura.
- [ ] Precio total = horas x precio por hora de la sala en ese momento; no cambia si la tarifa cambia después.
- [ ] Cuota: las horas CONFIRMADAS del miembro en el mes de la reserva más las nuevas no superan sus horas mensuales.
- [ ] Cancelar solo con al menos 24 horas de antelación; la reserva pasa a CANCELADA y libera la franja y la cuota.
- [ ] Listado de reservas paginado, ordenable y filtrable.

**Casos límite:** cancelar a 23 h 59 min y a 24 h exactas; reserva que cruza de mes; misma franja en salas distintas; reservar tras cancelar la misma franja. **Opcionales:** disponibilidad diaria de una sala; informe de horas por miembro.

### C. Configuración

Igual que el proyecto 2 con base de datos `coworking_db`; sin dependencias nuevas.

D–E. Base de datos y arquitectura

```
CREATE DATABASE IF NOT EXISTS coworking_db CHARACTER SET utf8mb4;
USE coworking_db;
CREATE TABLE sala (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(60) NOT NULL UNIQUE,
  capacidad INT NOT NULL,
  precio_hora DECIMAL(8,2) NOT NULL,
  activa BOOLEAN NOT NULL DEFAULT TRUE,
  CHECK (capacidad > 0), CHECK (precio_hora > 0)) ENGINE=InnoDB;
CREATE TABLE miembro (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(100) NOT NULL,
  email VARCHAR(120) NOT NULL UNIQUE,
  horas_mensuales INT NOT NULL DEFAULT 20,
  CHECK (horas_mensuales >= 0)) ENGINE=InnoDB;
CREATE TABLE reserva (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  sala_id BIGINT NOT NULL,
  miembro_id BIGINT NOT NULL,
  inicio DATETIME NOT NULL,
  fin DATETIME NOT NULL,
  estado VARCHAR(12) NOT NULL,
  precio_total DECIMAL(10,2) NOT NULL,
  CHECK (fin > inicio),
  CHECK (estado IN ('CONFIRMADA','CANCELADA')),
  FOREIGN KEY (sala_id) REFERENCES sala(id),
  FOREIGN KEY (miembro_id) REFERENCES miembro(id),
  INDEX idx_reserva_sala_franja (sala_id, inicio, fin),
  INDEX idx_reserva_miembro (miembro_id, inicio)) ENGINE=InnoDB;
-- Datos: 3 salas, 3 miembros y algunas reservas CONFIRMADAS y CANCELADAS.
```

Cardinalidades: sala 1—N reserva; miembro 1—N reserva.

```
com.aprendizaje.coworking/
├─ controller/  SalaController, MiembroController, ReservaController
├─ service/     SalaService, MiembroService, ReservaService
├─ repository/  SalaRepository, MiembroRepository, ReservaRepository
├─ entity/      Sala, Miembro, Reserva, EstadoReserva
├─ dto/         requests y responses (reserva incluye datos de sala y miembro)
├─ mapper/      SalaMapper, MiembroMapper, ReservaMapper
└─ exception/   excepciones de dominio + GlobalExceptionHandler
```

- **ReservaService**: orquesta cada alta y cancelación como una única operación transaccional y concentra todas las reglas de la sección B.
- **ReservaRepository**: consultas JPQL de solapamiento, de horas consumidas por miembro y mes, y de listado paginado con filtros.
- **mapper.\***: convierten entre entidad y DTO; el service no construye DTOs a mano.
- **EstadoReserva**: enum de estados. El resto de clases mantienen las responsabilidades del nivel 2.

F–I. API, validaciones, pruebas y criterios

| Método y ruta | Descripción | Códigos |
| --- | --- | --- |
| CRUD /api/salas y /api/miembros | Como en el nivel 2; DELETE de sala con reservas responde 409 | 201, 200, 204, 400, 404, 409 |
| POST /api/reservas | Crea reserva (salaId, miembroId, inicio, fin) | 201; 400, 404, 409 |
| GET /api/reservas/{id} | Detalle | 200; 404 |
| GET /api/reservas?salaId&miembroId&estado&page&size&sort | Listado paginado | 200; 400 |
| PATCH /api/reservas/{id}/cancelar | Cancela | 200; 404, 409 |
| GET /api/salas/{id}/disponibilidad?fecha= | Franjas libres del día (opcional) | 200; 404 |

```
// POST /api/reservas
{ "salaId": 2, "miembroId": 7, "inicio": "2026-11-10T10:00:00", "fin": "2026-11-10T12:30:00" }
// 201
{ "id": 41, "sala": "Sala Olmo", "miembro": "Ana Ruiz", "inicio": "2026-11-10T10:00:00",
  "fin": "2026-11-10T12:30:00", "estado": "CONFIRMADA", "precioTotal": 37.50 }
// GET /api/reservas?page=0&size=5&sort=inicio,desc
{ "content": [ ... ], "page": 0, "size": 5, "totalElements": 12, "totalPages": 3 }
// 409
{ "status": 409, "error": "Conflict", "message": "La sala ya está reservada en esa franja" }
```

### G. Validaciones

Formato y obligatoriedad en DTOs; reglas de horario, duración, antelación, solapamiento y cuota en el service (409 para conflictos, 400 para datos mal formados). Parámetros de paginación fuera de rango o campos de orden inexistentes: 400.

### H. Pruebas

- Servicio: solapamiento con bordes (termina cuando empieza otra, contiene, queda contenida), cuota justo en el límite y superada, cancelación a 23 h 59 y a 24 h, sala inactiva.
- Repositorio: la consulta de solapamiento ignora reservas CANCELADAS; suma de horas por mes; paginación y orden.
- Transacción: si falla una regla, no queda ninguna reserva guardada.
- Controlador: códigos, JSON paginado y errores.

### I. Criterios de finalización

- [ ] Imposible crear solapamientos por la API.
- [ ] Precio y cuota correctos en los casos límite.
- [ ] Los listados son paginados y no cargan toda la tabla.
- [ ] Reglas solo en el service; mappers separados.
- [ ] Pruebas de servicio, repositorio y controlador pasan.

## 4. Pedidos y stock de una tienda online Medio-avanzado

**Problema:** gestionar pedidos con varias líneas y mantener el stock coherente.\
**Modelo:** Cliente 1—N Pedido 1—N LineaPedido N—1 Producto, más MovimientoStock.\
**Conceptos nuevos:** máquina de estados (BORRADOR, CONFIRMADO, ENVIADO, ENTREGADO, CANCELADO), transacciones entre entidades, problema N+1 con `JOIN FETCH` y `@EntityGraph`, proyecciones, pruebas de integración con `@SpringBootTest`.\
**Reto:** confirmar un pedido descuenta stock de forma atómica y cancelarlo lo repone.

A–C. Información, requisitos y configuración

### A. Información general

**Objetivo:** gestionar un proceso de negocio con estados y varias entidades, y detectar y evitar problemas de rendimiento. **Funcionamiento:** un cliente crea un pedido en borrador, añade líneas y lo confirma; al confirmar se descuenta el stock; el pedido avanza hasta la entrega o se cancela. **Previos:** proyectos 1 a 3. **Aprenderás:** transacciones entre entidades, N+1, proyecciones y pruebas de integración.

### B. Requisitos

- [ ] CRUD de clientes y productos; un producto con histórico se desactiva en lugar de borrarse.
- [ ] Estados: BORRADOR, CONFIRMADO, ENVIADO, ENTREGADO, CANCELADO. Transiciones permitidas: BORRADOR a CONFIRMADO o CANCELADO; CONFIRMADO a ENVIADO o CANCELADO; ENVIADO a ENTREGADO. Cualquier otra, 409.
- [ ] Solo se añaden o quitan líneas en BORRADOR; un producto aparece una sola vez por pedido (sumar cantidad si se repite).
- [ ] Confirmar: pedido no vacío, productos activos y stock suficiente en todas las líneas; descuenta el stock, registra un movimiento por línea y fija precios y total. Si una línea falla, no cambia nada.
- [ ] Cancelar un CONFIRMADO repone el stock y registra los movimientos inversos.
- [ ] Reposición manual de stock con movimiento asociado.
- [ ] Listados paginados de pedidos con cliente y líneas, sin consultas por cada pedido.

**Casos límite:** dos líneas que juntas superan el stock; reconfirmar; cancelar un ENVIADO; borrar un cliente con pedidos. **Opcionales:** productos con stock bajo; resumen de cliente (nº de pedidos y total gastado).

### C. Configuración

Dos bases de datos: `tienda_db` para desarrollo y `tienda_test_db` para pruebas de integración (con su propio `application-test.yml`). Sin dependencias nuevas.

D–E. Base de datos y arquitectura

```
CREATE DATABASE IF NOT EXISTS tienda_db CHARACTER SET utf8mb4;
USE tienda_db;
CREATE TABLE cliente (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(100) NOT NULL,
  email VARCHAR(120) NOT NULL UNIQUE,
  direccion VARCHAR(200) NOT NULL) ENGINE=InnoDB;
CREATE TABLE producto (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  sku VARCHAR(20) NOT NULL UNIQUE,
  nombre VARCHAR(120) NOT NULL,
  precio DECIMAL(10,2) NOT NULL,
  stock INT NOT NULL DEFAULT 0,
  activo BOOLEAN NOT NULL DEFAULT TRUE,
  CHECK (precio > 0), CHECK (stock >= 0)) ENGINE=InnoDB;
CREATE TABLE pedido (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  cliente_id BIGINT NOT NULL,
  estado VARCHAR(12) NOT NULL,
  fecha_creacion DATETIME NOT NULL,
  total DECIMAL(12,2) NOT NULL DEFAULT 0,
  CHECK (estado IN ('BORRADOR','CONFIRMADO','ENVIADO','ENTREGADO','CANCELADO')),
  FOREIGN KEY (cliente_id) REFERENCES cliente(id),
  INDEX idx_pedido_cliente_estado (cliente_id, estado)) ENGINE=InnoDB;
CREATE TABLE linea_pedido (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  pedido_id BIGINT NOT NULL,
  producto_id BIGINT NOT NULL,
  cantidad INT NOT NULL,
  precio_unitario DECIMAL(10,2) NOT NULL,
  CHECK (cantidad > 0),
  UNIQUE (pedido_id, producto_id),
  FOREIGN KEY (pedido_id) REFERENCES pedido(id),
  FOREIGN KEY (producto_id) REFERENCES producto(id)) ENGINE=InnoDB;
CREATE TABLE movimiento_stock (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  producto_id BIGINT NOT NULL,
  pedido_id BIGINT NULL,
  cantidad INT NOT NULL,
  tipo VARCHAR(12) NOT NULL,
  fecha DATETIME NOT NULL,
  CHECK (cantidad <> 0),
  CHECK (tipo IN ('VENTA','REPOSICION','CANCELACION')),
  FOREIGN KEY (producto_id) REFERENCES producto(id),
  FOREIGN KEY (pedido_id) REFERENCES pedido(id)) ENGINE=InnoDB;
-- Datos: 3 clientes, 8 productos con stock variado, 2 pedidos de ejemplo.
```

Cardinalidades: cliente 1—N pedido; pedido 1—N línea; producto 1—N línea; producto 1—N movimiento; pedido 1—N movimiento (opcional). Los precios de la línea se copian del producto para conservar el histórico.

```
com.aprendizaje.tienda/
├─ controller/  ClienteController, ProductoController, PedidoController
├─ service/     ClienteService, ProductoService, PedidoService, StockService
├─ repository/  Cliente, Producto, Pedido, MovimientoStock (un repository cada uno)
├─ entity/      Cliente, Producto, Pedido, LineaPedido, MovimientoStock,
│                EstadoPedido, TipoMovimiento
├─ dto/         requests, responses y proyecciones (ResumenCliente)
├─ mapper/      un mapper por recurso
└─ exception/   TransicionInvalidaException, StockInsuficienteException, + las anteriores
```

- **PedidoService**: ciclo de vida del pedido y las transiciones de estado; delega en StockService.
- **StockService**: único punto que modifica stock y registra movimientos; así la regla vive en un solo sitio.
- **EstadoPedido**: estados y qué transiciones admite cada uno.
- **PedidoRepository**: consultas que traen pedido, cliente y líneas en pocas sentencias, y el listado paginado.
- **dto.ResumenCliente**: proyección de solo lectura para el informe.

F–I. API, validaciones, pruebas y criterios

| Método y ruta | Descripción | Códigos |
| --- | --- | --- |
| CRUD /api/clientes, /api/productos | Como niveles anteriores | 201, 200, 204, 400, 404, 409 |
| POST /api/productos/{id}/reposicion | Body: cantidad > 0 | 200; 400, 404 |
| POST /api/pedidos | Crea borrador (clienteId) | 201; 404 |
| POST /api/pedidos/{id}/lineas | Añade producto y cantidad | 201; 400, 404, 409 |
| DELETE /api/pedidos/{id}/lineas/{lineaId} | Quita línea | 204; 404, 409 |
| POST /api/pedidos/{id}/confirmar \| enviar \| entregar \| cancelar | Transiciones de estado | 200; 404, 409 |
| GET /api/pedidos/{id} | Pedido con cliente y líneas | 200; 404 |
| GET /api/pedidos?clienteId&estado&page&size | Listado paginado | 200; 400 |
| GET /api/clientes/{id}/resumen | Opcional: proyección | 200; 404 |

```
// POST /api/pedidos/12/lineas
{ "productoId": 4, "cantidad": 2 }
// POST /api/pedidos/12/confirmar — 200
{ "id": 12, "estado": "CONFIRMADO", "cliente": "Marta Gil", "total": 83.40,
  "lineas": [ { "producto": "Taza", "cantidad": 2, "precioUnitario": 9.20 } ] }
// 409
{ "status": 409, "error": "Conflict", "message": "Stock insuficiente para el producto SKU-004" }
```

### G. Validaciones

DTOs: cantidades positivas, identificadores obligatorios, SKU con formato. Service: transiciones válidas (409), stock, producto activo, pedido editable, pedido no vacío. Paginación con límites máximos.

### H. Pruebas

- Servicio: cada transición válida e inválida; confirmar con stock justo y con una línea sin stock; cancelar repone.
- Integración (`@SpringBootTest` + `tienda_test_db`): si falla la última línea al confirmar, el stock de las anteriores queda intacto y no hay movimientos.
- Rendimiento: listar 20 pedidos con líneas ejecuta un número de sentencias constante, no proporcional a los pedidos (compruébalo con el log SQL).
- Controlador: errores 409 y paginación.

### I. Criterios de finalización

- [ ] Confirmar y cancelar son atómicos y dejan el stock coherente con los movimientos.
- [ ] Ninguna transición inválida es posible.
- [ ] Has medido y eliminado el N+1 del listado.
- [ ] Stock modificado solo desde StockService.
- [ ] Pruebas unitarias e integración pasan con una base de datos de pruebas separada.

## 5. Venta de entradas con aforo limitado Avanzado

**Problema:** vender entradas de eventos sin sobrepasar nunca el aforo, aunque haya compras simultáneas.\
**Modelo:** Evento 1—N Zona 1—N Entrada N—1 Compra N—1 Cliente, más ListaEspera.\
**Conceptos nuevos:** bloqueo optimista (`@Version`) y pesimista, reservas temporales con caducidad (`@Scheduled`), aislamiento de transacciones, índices y operaciones por lotes, pruebas de concurrencia, refactorización.\
**Reto:** consistencia bajo carga y rendimiento medible.

A–C. Información, requisitos y configuración

### A. Información general

**Objetivo:** garantizar consistencia cuando varias peticiones compiten por el mismo recurso, y diseñar con calidad un dominio extenso. **Funcionamiento:** los clientes reservan entradas de una zona; la reserva es temporal y caduca si no se paga; al liberarse plazas se avisa a la lista de espera. **Previos:** proyectos 1 a 4. **Aprenderás:** bloqueo optimista y pesimista, niveles de aislamiento, tareas programadas, operaciones por lotes, pruebas de concurrencia y refactorización.

### B. Requisitos

- [ ] Eventos con estados BORRADOR, PUBLICADO, AGOTADO y CANCELADO; solo los PUBLICADOS admiten compras. Zonas con aforo y precio propios.
- [ ] Reservar: de 1 a 6 entradas de una zona; crea una compra PENDIENTE que expira a los 10 minutos y ya cuenta contra el aforo.
- [ ] Nunca se venden ni reservan más entradas que el aforo, ni con peticiones simultáneas.
- [ ] Pagar (simulado, sin pasarela): solo una compra PENDIENTE no caducada pasa a PAGADA y genera los códigos de entrada, únicos.
- [ ] Tarea programada: las compras PENDIENTES vencidas pasan a CADUCADA y liberan plazas.
- [ ] Cancelar una compra PAGADA con al menos 48 horas de antelación libera las plazas.
- [ ] Lista de espera por zona: al liberarse plazas se marca como NOTIFICADA a quien le toque por orden de llegada y le quepa su cantidad (sin envío real de mensajes).
- [ ] Informe de ocupación por evento y zona sin consultar fila a fila.

**Casos límite:** pagar justo al vencer; dos clientes por las últimas plazas; liberación que atiende a varios de la lista; evento que se cancela con compras vivas (todas se cancelan). **Opcional:** compras de un cliente con filtros y paginación.

### C. Configuración

Como el proyecto 4 con `entradas_db` y `entradas_test_db`; la tarea programada se activa con la anotación de scheduling de Spring (ya incluida en el framework). Un pool de conexiones adecuado para las pruebas de concurrencia (propiedades de Hikari en el yml).

D–E. Base de datos y arquitectura

```
CREATE DATABASE IF NOT EXISTS entradas_db CHARACTER SET utf8mb4;
USE entradas_db;
CREATE TABLE cliente (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(100) NOT NULL,
  email VARCHAR(120) NOT NULL UNIQUE) ENGINE=InnoDB;
CREATE TABLE evento (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(120) NOT NULL,
  fecha_hora DATETIME NOT NULL,
  estado VARCHAR(12) NOT NULL,
  CHECK (estado IN ('BORRADOR','PUBLICADO','AGOTADO','CANCELADO')),
  INDEX idx_evento_estado_fecha (estado, fecha_hora)) ENGINE=InnoDB;
CREATE TABLE zona (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  evento_id BIGINT NOT NULL,
  nombre VARCHAR(50) NOT NULL,
  aforo INT NOT NULL,
  ocupadas INT NOT NULL DEFAULT 0,
  precio DECIMAL(8,2) NOT NULL,
  version BIGINT NOT NULL DEFAULT 0,
  CHECK (aforo > 0), CHECK (ocupadas >= 0 AND ocupadas <= aforo), CHECK (precio >= 0),
  UNIQUE (evento_id, nombre),
  FOREIGN KEY (evento_id) REFERENCES evento(id)) ENGINE=InnoDB;
CREATE TABLE compra (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  cliente_id BIGINT NOT NULL,
  zona_id BIGINT NOT NULL,
  cantidad INT NOT NULL,
  estado VARCHAR(12) NOT NULL,
  creada_en DATETIME NOT NULL,
  expira_en DATETIME NOT NULL,
  total DECIMAL(10,2) NOT NULL,
  CHECK (cantidad BETWEEN 1 AND 6),
  CHECK (estado IN ('PENDIENTE','PAGADA','CADUCADA','CANCELADA')),
  FOREIGN KEY (cliente_id) REFERENCES cliente(id),
  FOREIGN KEY (zona_id) REFERENCES zona(id),
  INDEX idx_compra_estado_expira (estado, expira_en)) ENGINE=InnoDB;
CREATE TABLE entrada (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  compra_id BIGINT NOT NULL,
  codigo CHAR(12) NOT NULL UNIQUE,
  FOREIGN KEY (compra_id) REFERENCES compra(id)) ENGINE=InnoDB;
CREATE TABLE lista_espera (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  zona_id BIGINT NOT NULL,
  cliente_id BIGINT NOT NULL,
  cantidad INT NOT NULL,
  estado VARCHAR(12) NOT NULL DEFAULT 'ACTIVA',
  creada_en DATETIME NOT NULL,
  CHECK (cantidad BETWEEN 1 AND 6),
  CHECK (estado IN ('ACTIVA','NOTIFICADA','CANCELADA')),
  FOREIGN KEY (zona_id) REFERENCES zona(id),
  FOREIGN KEY (cliente_id) REFERENCES cliente(id),
  INDEX idx_espera_zona_estado (zona_id, estado, creada_en)) ENGINE=InnoDB;
-- Datos: 2 eventos, 3 zonas (una con aforo 5 para las pruebas) y 4 clientes.
```

Cardinalidades: evento 1—N zona; zona 1—N compra; cliente 1—N compra; compra 1—N entrada; zona 1—N lista de espera; cliente 1—N lista de espera. El contador `ocupadas` de zona es el punto de contención: decide tú cómo protegerlo y justifícalo.

```
com.aprendizaje.entradas/
├─ controller/  Evento, Zona, Compra, ListaEspera, Informe
├─ service/     EventoService, CompraService, DisponibilidadService,
│                ListaEsperaService, InformeService
├─ scheduler/   CaducidadCompraJob
├─ repository/  uno por entidad
├─ entity/      Evento, Zona, Cliente, Compra, Entrada, ListaEspera + enums
├─ dto/         requests, responses, proyecciones de informe
├─ mapper/
├─ config/      configuración de scheduling y de transacciones
└─ exception/   conflicto de concurrencia, aforo agotado, compra caducada, etc.
```

- **DisponibilidadService**: única autoridad para reservar y liberar plazas de una zona; el resto de servicios lo usan.
- **CompraService**: reservar, pagar y cancelar compras; define las fronteras transaccionales.
- **CaducidadCompraJob**: dispara periódicamente la caducidad y delega en el service; no contiene lógica.
- **ListaEsperaService**: decide a quién avisar cuando se liberan plazas, por orden de llegada.
- **InformeService / proyecciones**: ocupación agregada con consultas de solo lectura optimizadas.

F–I. API, validaciones, pruebas y criterios

| Método y ruta | Descripción | Códigos |
| --- | --- | --- |
| CRUD /api/clientes, /api/eventos, /api/eventos/{id}/zonas | Gestión básica | 201, 200, 204, 400, 404, 409 |
| POST /api/eventos/{id}/publicar \| cancelar | Cambia el estado del evento | 200; 404, 409 |
| GET /api/eventos?estado&page&size | Eventos paginados | 200 |
| GET /api/zonas/{id}/disponibilidad | Plazas libres | 200; 404 |
| POST /api/compras | Reserva (clienteId, zonaId, cantidad) | 201; 400, 404, 409 |
| POST /api/compras/{id}/pagar | Paga y emite entradas | 200; 404, 409, 410 si caducó |
| POST /api/compras/{id}/cancelar | Cancela compra pagada | 200; 404, 409 |
| GET /api/compras/{id} | Detalle con entradas | 200; 404 |
| POST /api/zonas/{id}/lista-espera | Apuntarse (clienteId, cantidad) | 201; 400, 404, 409 |
| GET /api/informes/ocupacion?eventoId= | Ocupación por zona | 200; 404 |

```
// POST /api/compras
{ "clienteId": 3, "zonaId": 2, "cantidad": 4 }
// 201
{ "id": 88, "estado": "PENDIENTE", "cantidad": 4, "total": 120.00,
  "expiraEn": "2026-11-01T18:10:00" }
// POST /api/compras/88/pagar — 200
{ "id": 88, "estado": "PAGADA", "entradas": [ { "codigo": "K7M2QX9TB4AD" } ] }
// 409
{ "status": 409, "error": "Conflict", "message": "No quedan plazas suficientes en la zona" }
```

### G. Validaciones

Cantidad de 1 a 6; estados válidos para cada operación; compra caducada (410) frente a no encontrada (404); conflicto por concurrencia con mensaje claro y decisión explícita sobre si el cliente debe reintentar. Mismo formato de error global.

### H. Pruebas

- Concurrencia (`ExecutorService`): 50 hilos compitiendo por las 5 últimas plazas; resultado exacto, ocupadas igual al aforo, nunca superior. Repite varias veces.
- Caducidad: compras vencidas liberan plazas; no se paga una compra caducada.
- Lista de espera: orden de llegada, cantidades que no caben, varias liberaciones.
- Servicio y repositorio: transiciones, cancelación a 47 h y 48 h, informe.
- Rendimiento: informe y disponibilidad con un número de sentencias constante; inserción de entradas por lotes.

### I. Criterios de finalización

- [ ] La prueba de concurrencia pasa de forma estable.
- [ ] La estrategia de bloqueo está elegida y justificada por escrito.
- [ ] Fronteras transaccionales claras; el job no contiene lógica.
- [ ] Informes sin N+1 y con índices útiles.
- [ ] Has refactorizado al menos un servicio largo y puedes explicar el motivo.
- [ ] Revisión final: arquitectura, SOLID y cobertura de lo crítico.