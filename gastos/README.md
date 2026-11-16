# Gastos: proyecto de aprendizaje backend

API de gastos personales con Java 21, Spring Boot 4.1.2, Maven, Spring Data JPA y MySQL.

## Empieza aquí

La [fase 1: Java aplicado al backend](docs/fase-1/README.md) adapta el [roadmap general](../doc/mejora-spring-boot.md) a este proyecto. Incluye teoría, ejercicios, datos de prueba y criterios de revisión; **las funcionalidades propuestas todavía no están implementadas**. No contiene soluciones completas, para que puedas resolverlas primero.

## Funcionalidades actuales

| Método | Ruta | Comportamiento actual |
|---|---|---|
| GET | `/api/v1/gastos` | Listar entidades; filtro opcional `categoria` |
| GET | `/api/v1/gastos/{id}` | Consultar un gasto |
| POST | `/api/v1/gastos` | Crear; devuelve 201 y cabecera Location, sin cuerpo |
| PUT | `/api/v1/gastos/{id}` | Sustituir los datos de un gasto |
| DELETE | `/api/v1/gastos/{id}` | Eliminar; devuelve 204 |
| GET | `/api/v1/gastos/total?desde=2026-10-01&hasta=2026-10-31` | Sumar en JPA entre fechas inclusivas |
| GET | `/api/v1/categorias` | Listar valores del enum |

Consulta el [mapa del código y sus limitaciones](docs/fase-1/00-proyecto-y-preparacion.md) antes de empezar.

## Arranque local (PowerShell)

Requisitos: JDK 21, MySQL 8 y acceso a Maven Central. Desde `gastos/`:

1. Ejecuta `src/main/resources/db/gastos.sql` en una instancia **local de aprendizaje**. Crea la base, la tabla y cuatro gastos. No es un script idempotente: la tabla no se recrea automáticamente si ya existe.
2. Configura la contraseña de tu MySQL local y arranca:

```powershell
$env:DB_PASSWORD = 'tu-password-local'
.\mvnw.cmd spring-boot:run
```

La URL configurada es `jdbc:mysql://localhost:3306/gastos_db` (con parámetros adicionales en `application.yaml`); el usuario es `root` y el puerto HTTP es 8080. No guardes contraseñas reales en Git. Hibernate usa `ddl-auto: validate`: no crea la tabla.

```powershell
Invoke-RestMethod 'http://localhost:8080/api/v1/categorias'
.\mvnw.cmd test
```

**Estado inicial comprobado:** `contextLoads` falla con `Cannot load driver class: org.h2.Driver`. La configuración de tests utiliza H2, pero el `pom.xml` no incluye su dependencia. La preparación de la fase explica cómo abordarlo. No se ha modificado el código de la aplicación ni la configuración para ocultar este fallo.
