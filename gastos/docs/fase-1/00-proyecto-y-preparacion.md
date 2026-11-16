# 0 · Conoce el proyecto antes de ampliarlo

## Flujo de una petición

```text
HTTP → GastoController → GastoService → GastoRepository → MySQL
                            ↓
                       DTO de respuesta
Excepción → GlobalExceptionHandler → ApiError + estado HTTP
```

Las rutas siguientes son relativas a `gastos/`:

| Archivo | Qué debes localizar |
|---|---|
| `pom.xml` | Java 21, Spring Boot 4.1.2 y dependencias de tests |
| `src/main/java/com/aprendizaje/gastos/entity/Gasto.java` | Entidad mutable, id generado, descripción, importe, fecha y categoría |
| `src/main/java/com/aprendizaje/gastos/entity/Categoria.java` | Seis valores admitidos; no es otra tabla |
| `src/main/java/com/aprendizaje/gastos/controller/GastoController.java` | CRUD, filtro por categoría y total entre fechas |
| `src/main/java/com/aprendizaje/gastos/service/GastoService.java` | Validaciones actuales y uso de Optional |
| `src/main/java/com/aprendizaje/gastos/repository/GastoRepository.java` | `JpaRepository<Gasto, Long>`, filtro derivado y suma JPQL |
| `src/main/java/com/aprendizaje/gastos/dto/CalcularTotal.java` | Ya existe un record: no necesitas descubrir los DTO desde cero |
| `src/main/java/com/aprendizaje/gastos/exception/GlobalExceptionHandler.java` | Traducción actual de excepciones |
| `src/main/resources/db/gastos.sql` | `DECIMAL(10,2)`, restricciones y cuatro filas de ejemplo |

## Diagnóstico inicial: problemas que convertirás en aprendizaje

1. `create` valida algunos campos, pero `update` no reutiliza esas reglas.
2. La descripción se comprueba con `trim`, pero se guarda sin normalizar; `isEmpty` no descarta blancos.
3. La fecha nula pasa `checkDate`; la categoría nula no se valida explícitamente. Acaban dependiendo de la BBDD.
4. El importe con más de dos decimales recibe un mensaje que habla solo de ser mayor que cero.
5. Se usan `RuntimeException` y `ResponseStatusException` de forma diferente para ausencias.
6. El handler de **cualquier** `RuntimeException` devuelve 404. Puede disfrazar errores de programación e interceptar excepciones con otro estado previsto.
7. El listado devuelve entidades JPA directamente y no define una ordenación.
8. Solo existe `contextLoads`: no verifica reglas de negocio.

No son funcionalidades nuevas ya corregidas: son puntos de partida para E1–E4. No borres la consulta de total existente para practicar `reduce`; compara después ambas aproximaciones.

## Preparación de pruebas

Se ejecutó `.\mvnw.cmd test` antes de cambiar nada: un test ejecutado y un error de contexto por ausencia de `org.h2.Driver`.

`src/test/resources/application.yml` solicita H2. Como tarea de preparación, añade en `pom.xml` la dependencia `com.h2database:h2` con alcance `test`; deja que el BOM de Spring Boot gestione su versión. Vuelve a ejecutar el comando y revisa el resultado: eliminar el primer bloqueo no sustituye comprobar el arranque completo.

No cambies los tests a tu MySQL personal, no desactives `contextLoads` ni saltes los tests para dar el entorno por preparado. Los tests unitarios nuevos no necesitarán BBDD ni `@SpringBootTest`.

## Antes de continuar, responde

- ¿Quién crea la tabla si `ddl-auto` vale `validate`?
- ¿Qué significan `Gasto` y `Long` en `JpaRepository<Gasto, Long>`?
- ¿Por qué `Optional<Gasto>` no es lo mismo que `List<Gasto>`?
- ¿La suma de `/total` se hace actualmente en Java o en la base de datos?
- ¿Qué devolvería el handler actual ante un `NullPointerException` y por qué es un problema?

**Salida de este paso:** puedes arrancar la API local, explicar una petición completa y distinguir un fallo de infraestructura de un fallo de una regla de negocio.
