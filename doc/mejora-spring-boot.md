# Roadmap de aprendizaje: Java + Spring Boot Backend Developer

## 1. Objetivo

Evolucionar desde el desarrollo de operaciones CRUD básicas hasta ser capaz de diseñar, desarrollar, probar y mantener aplicaciones backend robustas con Java y Spring Boot.

**Stack principal:**
- Java 17/21+
- Spring Boot
- Spring Data JPA / Hibernate
- MySQL
- Maven
- JUnit 5
- Mockito
- Git / GitHub
- Docker
- Apache Kafka (fase avanzada)

**Metodología:** aprender mediante funcionalidades reales implementadas en proyectos Spring Boot, evitando estudiar Java de forma excesivamente teórica o mediante ejercicios aislados.

---

## 2. Cómo debes aprender

Cada concepto nuevo debe seguir este proceso:

1. **Entender:** comprender qué problema resuelve y cuándo se utiliza.
2. **Analizar:** estudiar su funcionamiento y sintaxis mediante ejemplos.
3. **Implementar:** desarrollar una funcionalidad real utilizando Spring Boot.
4. **Resolver:** intentar solucionar el problema sin consultar directamente la solución.
5. **Revisar:** analizar el código, detectar errores y mejorar su implementación.
6. **Refactorizar:** aplicar buenas prácticas, legibilidad y principios SOLID.
7. **Testear:** comprobar que la funcionalidad funciona correctamente.
8. **Consolidar:** realizar una variación del ejercicio sin ayuda.

### Distribución del aprendizaje

- 20% teoría y conceptos.
- 50% implementación práctica.
- 20% resolución de problemas y depuración.
- 10% testing y refactorización.

---

## 3. Fases de aprendizaje

### FASE 1. Java aplicado al desarrollo backend

**Objetivo:** mejorar la lógica de programación utilizando Java dentro de Spring Boot.

Conceptos:
- Collections: List, Set y Map.
- Streams API.
- map(), filter(), flatMap(), reduce().
- Optional.
- Comparator y ordenaciones.
- Programación funcional.
- Genéricos.
- Enums y Records.
- BigDecimal.
- LocalDate y LocalDateTime.
- Excepciones personalizadas.
- Inmutabilidad.

Práctica:
- Filtrar registros.
- Agrupar información.
- Calcular estadísticas.
- Ordenar resultados.
- Validar información.
- Transformar entidades en DTO.

**Resultado esperado:** dominar la manipulación de datos y desarrollar lógica de negocio limpia.

### FASE 2. Spring Data JPA avanzado

**Objetivo:** aprender a consultar y gestionar bases de datos de manera eficiente.

Conceptos:
- Derived Query Methods.
- @Query.
- JPQL.
- Consultas nativas.
- JpaSpecificationExecutor.
- Specifications dinámicas.
- Pageable y Page.
- Sort.
- Proyecciones.
- Relaciones entre entidades.
- FetchType.LAZY y EAGER.
- JOIN FETCH.
- Problema N+1.
- Cascade y orphanRemoval.

Práctica:
- Buscadores con múltiples filtros.
- Consultas dinámicas.
- Paginación y ordenación.
- Informes y estadísticas.
- Consultas entre varias entidades.

**Resultado esperado:** construir consultas complejas y eficientes con Spring Data JPA.

### FASE 3. SQL y gestión de datos

**Objetivo:** dejar de depender exclusivamente de los métodos automáticos de JPA.

Conceptos:
- INNER JOIN y LEFT JOIN.
- GROUP BY y HAVING.
- Subconsultas.
- COUNT, SUM, AVG, MAX y MIN.
- Índices.
- Claves primarias y foráneas.
- Normalización.
- Transacciones.
- EXPLAIN y optimización de consultas.

Práctica:
- Consultas de estadísticas.
- Informes mensuales.
- Agrupaciones.
- Consultas de rendimiento.
- Resolución de problemas de integridad.

**Resultado esperado:** entender qué sucede realmente en MySQL cuando Spring Boot ejecuta una operación.

### FASE 4. Lógica de negocio y transacciones

**Objetivo:** desarrollar funcionalidades empresariales que garanticen la consistencia de los datos.

Conceptos:
- @Transactional.
- Propagation.
- Rollback.
- Validaciones de negocio.
- Operaciones atómicas.
- Idempotencia.
- Concurrencia.
- Optimistic Locking.
- Pessimistic Locking.

Práctica:
- Transferencias bancarias.
- Gestión de inventario.
- Reservas.
- Control de stock.
- Operaciones con múltiples entidades.

**Resultado esperado:** implementar operaciones complejas sin comprometer la integridad de los datos.

### FASE 5. Testing profesional

**Objetivo:** garantizar que el código funciona y evitar regresiones.

Conceptos:
- JUnit 5.
- Mockito.
- @Mock.
- @InjectMocks.
- @SpringBootTest.
- @WebMvcTest.
- MockMvc.
- @DataJpaTest.
- Testcontainers.
- Tests unitarios.
- Tests de integración.

Práctica:
- Testear Services.
- Testear Validators.
- Testear Controllers.
- Testear Repositories.
- Simular excepciones.
- Comprobar escenarios límite.

**Resultado esperado:** desarrollar funcionalidades verificables y mantenibles.

### FASE 6. Spring Boot avanzado

**Objetivo:** conocer las herramientas principales del ecosistema Spring.

Conceptos:
- Spring Validation.
- Spring Security.
- Spring Cache.
- Spring Actuator.
- SLF4J y logging.
- Profiles.
- Configuration Properties.
- OpenAPI / Swagger.
- Procesamiento asíncrono.
- Gestión de configuración.

Práctica:
- Configuración de diferentes entornos.
- Monitorización de aplicaciones.
- Caché de consultas.
- Documentación de APIs.
- Gestión avanzada de errores.

**Resultado esperado:** construir APIs preparadas para entornos de producción.

### FASE 7. Arquitectura y buenas prácticas

**Objetivo:** mejorar la estructura y mantenibilidad de los proyectos.

Conceptos:
- SOLID.
- Clean Code.
- Arquitectura por capas.
- Arquitectura hexagonal.
- DTO y mapeadores.
- Separación de responsabilidades.
- Patrones de diseño.
- Modularización.
- Gestión de dependencias.

Práctica:
- Refactorizar proyectos existentes.
- Separar responsabilidades.
- Diseñar servicios reutilizables.
- Mejorar la estructura de paquetes.

**Resultado esperado:** desarrollar aplicaciones escalables y fáciles de mantener.

### FASE 8. Sistemas distribuidos y herramientas profesionales

**Objetivo:** adquirir conocimientos para desarrollar sistemas backend distribuidos.

Conceptos:
- Microservicios.
- Apache Kafka.
- Redis.
- Docker.
- Docker Compose.
- Comunicación REST.
- Event-driven architecture.
- GitHub Actions.
- CI/CD.
- Observabilidad.

Práctica:
- Crear varios servicios independientes.
- Comunicar servicios mediante Kafka.
- Implementar consumidores y productores.
- Contenerizar aplicaciones.
- Automatizar tests y builds.

**Resultado esperado:** comprender cómo se construyen y despliegan sistemas backend modernos.

---

## 4. Plan temporal orientativo

| Semana | Contenido |
|---|---|
| 1 | Java aplicado: Collections, Streams y Optional |
| 2 | JPA avanzado |
| 3 | SQL y optimización |
| 4 | Lógica de negocio y transacciones |
| 5 | JUnit 5 y Mockito |
| 6 | Spring Boot avanzado |
| 7 | SOLID y arquitectura |
| 8 | Docker y Kafka |

El calendario es orientativo. No debes avanzar por haber terminado una semana, sino cuando puedas resolver los ejercicios de cada bloque con autonomía.

---

## 5. Metodología para cada ejercicio

Cada ejercicio debe contener:

1. Enunciado con un requisito empresarial.
2. Conceptos que necesitas conocer.
3. Endpoint que debes desarrollar, si corresponde.
4. Comportamiento esperado.
5. Casos límite que debes contemplar.
6. Tests que debes implementar.
7. Criterios para evaluar la solución.

**Importante:** no recibirás directamente el código completo. Primero debes intentar resolver el problema. Después revisaremos tu implementación y analizaremos las alternativas.

---

## 6. Proyectos para consolidar conocimientos

Utilizar proyectos progresivamente más complejos:

| Proyecto | Conocimientos |
|---|---|
| Gasto | Java, JPA, Streams, DTO y testing |
| Biblioteca | Relaciones JPA, consultas y filtros |
| FinTrack | Lógica financiera, estadísticas y arquitectura |
| Inventory Manager | Transacciones, stock y concurrencia |
| Event Management | Microservicios, Kafka y Docker |

Cada proyecto debe aportar funcionalidades nuevas y servir como portfolio profesional en GitHub.

---

## 7. Criterios para considerar que dominas una fase

No avanzar hasta que seas capaz de:

- Explicar el concepto con tus propias palabras.
- Implementarlo sin copiar un tutorial.
- Identificar cuándo utilizarlo.
- Resolver errores mediante depuración.
- Escribir tests de los casos importantes.
- Justificar las decisiones técnicas.
- Refactorizar el código cuando sea necesario.

## 8. Prioridad actual

**Tu punto de partida es la FASE 1: Java aplicado al desarrollo backend.**

No necesitas abandonar Spring Boot ni crear otro proyecto.

Utilizaremos tus aplicaciones actuales para introducir progresivamente Streams, Collections, Optional, lógica de negocio y consultas avanzadas.

El objetivo final no es conocer muchas anotaciones de Spring, sino saber construir software backend de calidad utilizando Java y su ecosistema.
