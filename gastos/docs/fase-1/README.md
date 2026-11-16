# Fase 1 · Java aplicado al desarrollo backend

## Objetivo y alcance

Pasar del CRUD a manipular datos y expresar reglas de negocio con Java dentro de `gastos`. Trabajarás sobre el proyecto por capas existente, no sobre `gasto_exagonal` ni `libreria`.

Este material **prepara la fase; no acredita que ya la domines ni implementa los endpoints futuros**. Sigue la metodología del roadmap: entender → analizar → implementar → resolver → revisar → refactorizar → testear → consolidar. Intenta cada ejercicio antes de pedir una solución.

No vamos a incorporar Specifications, paginación JPA, seguridad ni Kafka. Cargar una lista pequeña y procesarla en Java es deliberado para aprender; no es una recomendación para consultar millones de registros.

## Itinerario

| Paso | Material | Entrega |
|---|---|---|
| 0 | [Proyecto y preparación](00-proyecto-y-preparacion.md) | API entendida y entorno de tests preparado |
| 1 | [Colecciones, Streams y funciones](conceptos/01-colecciones-streams.md) | Entender tipos y operaciones |
| 2 | [Dinero, fechas y validación](conceptos/02-dinero-fechas-validacion.md) | Reglas explícitas y casos límite |
| 3 | [Optional, DTO e inmutabilidad](conceptos/03-optional-dto-inmutabilidad.md) | Separar entidad y respuesta |
| 4 | [Ejercicios progresivos](ejercicios/README.md) | Resolver E1–E7 en orden |
| 5 | [Datos y resultados esperados](practica/datos-y-resultados.md) | Comprobar resultados reproducibles |
| 6 | [Pruebas y depuración](practica/pruebas-y-depuracion.md) | Escribir tests y corregir errores |
| 7 | [Evaluación](evaluacion.md) | Justificar decisiones y resolver el reto final |

## Dónde irá tu implementación

Desde `gastos/src/main/java/com/aprendizaje/gastos/`:

- `controller/`: parámetros HTTP y delegación; sin cálculos ni Streams de negocio.
- `service/`: validaciones, filtros y cálculos; puedes extraer un `GastoAnalizador` para operar sobre listas sin Spring.
- `dto/`: records de entrada/salida propuestos por los ejercicios.
- `exception/`: excepciones de negocio y traducción HTTP.
- `repository/`: conservar el acceso a datos; no hace falta crear consultas avanzadas.
- `entity/`: mantener `Gasto` como entidad JPA y `Categoria` como enum.

Tests nuevos bajo `src/test/java/com/aprendizaje/gastos/`, reflejando los paquetes que prueban. No crees clases vacías solamente por completar la estructura.

## Cobertura del roadmap

| Concepto | Aplicación |
|---|---|
| List, Set, Map y genéricos | E3 y E5: resultados, categorías únicas y agrupaciones tipadas |
| Streams, filter, map y programación funcional | E3 y E4: selección y transformación sin efectos laterales |
| Comparator | E4: orden estable con desempates |
| reduce y BigDecimal | E5: suma, media y porcentajes |
| Optional y excepciones personalizadas | E2: ausencia esperada frente a error |
| Enum y record | E3 y E4: ordenación cerrada y respuestas explícitas |
| LocalDate y LocalDateTime | E1 y E6: fecha del gasto e instante local de generación |
| Inmutabilidad y flatMap | E4 y E6: informes compuestos sin exponer entidades |

## Forma de trabajar

Para cada ejercicio: escribe primero dos ejemplos de entrada/salida; intenta resolverlo con un bucle; después compara una versión con Streams. No todo bucle necesita convertirse en una cadena funcional. Reserva aproximadamente 20% a lectura, 50% a implementar, 20% a depurar y 10% a pruebas/refactorización.

Anota en cada entrega: requisito, decisión, error encontrado, prueba que lo detecta y qué resolverías de otra forma. Marca los ejercicios como completados solo cuando puedas explicar el resultado sin leer el código.
