# Cierre de la fase 1

## Lista de progreso

Este listado empieza vacío: preparar el material no equivale a haber implementado los ejercicios.

- [ ] Entiendo el flujo HTTP → servicio → repositorio y las limitaciones actuales.
- [ ] He resuelto la preparación de H2 y ejecutado la suite completa.
- [ ] E1: validación compartida, normalización y dinero exacto.
- [ ] E2: ausencia con Optional y errores HTTP diferenciados.
- [ ] E3: filtros opcionales combinados y límites inclusivos.
- [ ] E4: DTO y ordenaciones deterministas sin mutar entidades.
- [ ] E5: agrupaciones, categorías únicas y estadísticas.
- [ ] E6: informe compuesto, flatMap, fechas e inmutabilidad.
- [ ] E7: reto de presupuestos resuelto sin copiar una solución.
- [ ] He escrito y ejecutado las pruebas de casos importantes.
- [ ] He explicado mis decisiones y corregido al menos un fallo con una prueba de regresión.

## Cómo evaluar cada entrega

| Criterio | Pregunta |
|---|---|
| Corrección | ¿Cumple el contrato y los resultados esperados? |
| Límites | ¿Funciona con vacío, nulos, empates y entradas inválidas? |
| Diseño | ¿El controlador delega y las reglas compartidas no están duplicadas? |
| Java | ¿Los tipos expresan el dominio y las operaciones se entienden sin comentarios que traduzcan cada línea? |
| Dinero y fechas | ¿No hay double, redondeos implícitos ni dependencia accidental del reloj? |
| Seguridad de datos | ¿Una lectura o validación fallida deja intactas las entidades? |
| Pruebas | ¿Fallan si introduces un error deliberado en el filtro o la suma? |
| Autonomía | ¿Puedes reproducir una variación sin consultar tu solución? |

No uses únicamente la cantidad de tests o de Streams como medida de calidad.

## Defensa técnica: responde con tus propias palabras

1. ¿Por qué una lista, un conjunto y un mapa resuelven problemas diferentes?
2. ¿Qué garantizan los genéricos y qué problema evita `Map<Categoria, BigDecimal>`?
3. ¿Qué tipos entran y salen de filter, map, flatMap y reduce en tu código?
4. ¿Cómo representas un gasto mayor inexistente sin inventarte un gasto de importe cero?
5. ¿Por qué equals y compareTo de BigDecimal pueden producir resultados distintos?
6. ¿Qué criterio desempata dos gastos idénticos en importe y fecha?
7. ¿Por qué un record con una lista no es necesariamente inmutable?
8. ¿Por qué no capturas todas las RuntimeException como 404?
9. ¿Qué diferencia hay entre el LocalDate del gasto y el LocalDateTime del informe?
10. ¿Cuándo dejarías de usar findAll más Streams y moverías trabajo a la BBDD?

## Variación final sin ayuda

Antes de mirar otras soluciones, escribe los requisitos y tests para obtener las **tres categorías con mayor gasto** de un periodo. Decide qué hacer con empates, menos de tres categorías, entrada vacía y fechas inválidas. Devuelve DTO, no entidades, y reutiliza reglas sin copiar todos los métodos anteriores.

No necesitas otro endpoint ni persistencia nueva. La prueba de autonomía es justificar el diseño, implementarlo y contrastar el resultado con una suma manual.

## Qué entregar para revisión

- Código y tests de cada ejercicio, en cambios pequeños.
- Peticiones de ejemplo y sus respuestas reales, indicando los datos usados.
- Resultado de `.\mvnw.cmd test`, sin ocultar tests fallidos.
- Breve nota de decisiones y dudas pendientes.

Avanza a JPA avanzado solo cuando puedas explicar, implementar, depurar, probar y refactorizar estas operaciones sin depender de un tutorial. Un informe correcto sobre ocho gastos no demuestra que la consulta sea eficiente a gran escala: esa será la siguiente fase.
