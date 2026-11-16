# Ejercicios · De reglas sencillas a informes compuestos

Todos los contratos siguientes son **objetivos por implementar**, no rutas disponibles actualmente. Trabaja con los [datos compartidos](../practica/datos-y-resultados.md). Escribe las pruebas descritas y compara con los criterios antes de continuar.

## E1 · Validar igual al crear y actualizar

**Requisito:** no admitir gastos inválidos ni guardar descripciones con espacios exteriores. Una edición debe cumplir las mismas reglas que una creación.

**Conceptos:** BigDecimal, LocalDate, normalización, funciones sin efectos laterales.

**Endpoints:** POST `/api/v1/gastos` y PUT `/api/v1/gastos/{id}` existentes; conserva sus estados de éxito.

**Trabajo:** centraliza las reglas del capítulo [dinero y fechas](../conceptos/02-dinero-fechas-validacion.md). Valida todos los campos antes de modificar la entidad existente. No implementes reglas distintas en el controlador.

**Casos límite y tests:**

- Descripción nula, vacía, solo blancos, longitud normalizada 100 y 101.
- `"  Compra  "` se almacena como `"Compra"`.
- Importe nulo, negativo, cero, `0.01`, `10.500`, `10.501`, máximo admitido y máximo más un céntimo.
- Fecha nula, hoy y mañana; categoría nula.
- Entrada inválida en POST y PUT: no llamar a `save`; en PUT, la entidad original no cambia.

**Criterio:** reglas compartidas, mensajes que describen el fallo real y cálculos monetarios sin double.

**Consolidación:** incorpora una prueba de descripción con tabulaciones y explica por qué escoges `strip` o `trim`. No cambies requisitos de negocio sin documentarlo.

## E2 · Distinguir «no existe» de «es inválido»

**Requisito:** un cliente debe saber si el id falta, la entrada es inválida o el servidor ha fallado.

**Conceptos:** Optional, `orElseThrow`, excepciones personalizadas.

**Endpoints:** GET, PUT y DELETE `/api/v1/gastos/{id}`; errores de E1 y `/total`.

**Trabajo:** crea las excepciones propuestas en [Optional y DTO](../conceptos/03-optional-dto-inmutabilidad.md), unifica las búsquedas por id y revisa `GlobalExceptionHandler`. Conserva `ApiError` para errores de negocio y no uses una captura genérica para devolver siempre 404.

**Comportamiento:** id inexistente → 404; validación → 400; fallo inesperado → 500. Un id existente se procesa normalmente. Un intervalo invertido en `/total` debe ser 400, no 404.

**Casos límite y tests:** Optional vacío y con valor; id inexistente en las tres operaciones; fallo inesperado no traducido a 404; parámetros de fecha e importe mal formados mantienen 400. No es obligatorio unificar aquí el cuerpo de todos los errores del framework.

**Criterio:** no hay `Optional.get()` sin control ni `RuntimeException("Gasto no encontrado")` genérica. La excepción de ausencia incluye el id para diagnóstico.

**Consolidación:** explica por qué cero resultados en un listado devuelve 200 con `[]`, no 404.

## E3 · Buscar gastos combinando filtros

**Requisito:** consultar gastos por categoría, fechas e importes sin crear un método por cada combinación.

**Conceptos:** List, Predicate, filter, genéricos y enum.

**Endpoint objetivo:** ampliar GET `/api/v1/gastos` con parámetros opcionales `categoria`, `desde`, `hasta`, `importeMin`, `importeMax`. No crear una ruta nueva que duplique el listado.

**Comportamiento:**

- Todos los filtros presentes se combinan con AND y límites inclusivos.
- Sin filtros devuelve todos los gastos; filtro ausente no restringe.
- Fechas invertidas, importes negativos, mínimo mayor que máximo o límites no representables exactamente con dos decimales → 400. Cero es un límite de búsqueda válido aunque no sea válido al crear un gasto. Aplica también el máximo de importe individual definido en E1.
- Categoría desconocida y fecha mal formada → 400; lista sin coincidencias → `[]`.
- Para esta práctica recupera la lista una sola vez y filtra en el servicio. No lances consultas dentro del stream.

**Trabajo:** puedes crear `FiltroGastos` como record interno de parámetros. No es necesario exponerlo como cuerpo GET. Resuelve primero con condiciones explícitas; extrae Predicate solo si mejora la lectura.

**Tests:** cada filtro aislado, extremos incluidos, todos combinados, solo uno de los extremos, sin filtros y resultado vacío. Con los datos compartidos, `categoria=ALIMENTACION&importeMin=20.00&importeMax=60.00` devuelve ids 1 y 5 (aún no evalúes su orden).

**Criterio:** el controlador no contiene lógica de filtrado; no se modifican los gastos ni se duplican consultas por filtro.

**Consolidación:** añade `texto` opcional para buscar en descripción sin distinguir mayúsculas; define antes cómo tratar blancos y Locale. Es una ampliación, no requisito de los demás ejercicios.

## E4 · Ordenar y transformar en DTO

**Requisito:** devolver resultados estables y un contrato independiente de JPA.

**Conceptos:** map, Comparator, enum, record e inmutabilidad.

**Endpoint objetivo:** GET `/api/v1/gastos` admite `orden`, con enum `OrdenGasto`: `FECHA_ASC`, `FECHA_DESC`, `IMPORTE_ASC`, `IMPORTE_DESC`. Predeterminado: `FECHA_DESC`.

**Contrato propuesto:** cada elemento es `GastoResponse` con `id`, `descripcion`, `importe`, `fecha`, `categoria` (mismos valores y tipos de dominio, sin entidad anidada). Aplica también este DTO al GET por id y al cuerpo del PUT. Conserva POST 201 sin cuerpo y su Location; no cambies el DTO existente de `/total`.

**Orden exacto:**

- FECHA_ASC / FECHA_DESC: fecha ascendente / descendente y, en empate, id ascendente.
- IMPORTE_ASC / IMPORTE_DESC: importe ascendente / descendente, después fecha descendente y por último id ascendente.
- Orden desconocido → 400. Usa entidades persistidas con id no nulo para estas respuestas.

**Tests:** todos los órdenes, empate de importe y fecha, orden por defecto, lista vacía y mapeo campo a campo. Con los datos compartidos, IMPORTE_DESC empieza por 8, 1, 2. Usa copias/barajados de la entrada para demostrar que no dependes del orden del repositorio.

**Criterio:** transformar no altera las entidades ni ordena in situ la lista recibida. Comparadores legibles; sin convertir BigDecimal a double. GET por id y PUT no exponen `Gasto`.

**Consolidación:** explica el efecto de mover `reversed()` al final de toda la cadena de desempates.

## E5 · Estadísticas y categorías con movimientos

**Requisito:** saber cuánto se gasta, cómo se distribuye y cuál es el gasto mayor de un periodo.

**Conceptos:** Set, Map, groupingBy, reduce, Optional, BigDecimal y división.

**Endpoints nuevos:**

- GET `/api/v1/gastos/estadisticas?desde=2026-10-01&hasta=2026-10-31`
- GET `/api/v1/gastos/categorias-utilizadas?desde=2026-10-01&hasta=2026-10-31`

Ambas fechas son obligatorias aquí. Son periodos inclusivos; invertidos o incompletos → 400. No cambies GET `/api/v1/categorias`, que sigue siendo el catálogo completo.

**Contrato de estadísticas propuesto:** `desde`, `hasta`, `cantidad` (long), `total`, `media`, `gastoMayor` (GastoResponse o null), `porCategoria` (lista de resúmenes). Cada resumen contiene `categoria`, `cantidad`, `total`, `porcentaje`.

**Reglas:**

- Total con `reduce` y cero como identidad; media = total / cantidad.
- Porcentaje = total de categoría × 100 / total general, redondeado al final a dos decimales. No fuerces que los porcentajes redondeados sumen exactamente 100.
- Mayor por importe; empate: fecha más reciente y luego menor id. Internamente puedes usar Optional; en el contrato acordado, vacío se serializa como null, no como Optional.
- `porCategoria` contiene solo categorías presentes y se ordena por nombre de categoría ascendente; no dependas de la iteración de un HashMap.
- Periodo vacío → cantidad 0, total 0.00, media 0.00, gastoMayor null, porCategoria `[]`.
- Categorías utilizadas: construir un Set de categorías únicas y devolver un array ordenado por nombre. Sin movimientos → `[]`.

**Tests:** lista vacía, un solo gasto, sumas `0.10 + 0.20`, media periódica, categoría repetida, empates del mayor y porcentajes. Contrasta los cálculos con la tabla compartida.

**Criterio:** no dividir entre cero, no realizar una consulta por categoría, no usar claves String cuando el dominio es un enum. No cambies la suma JPA de `/total`; comprueba que coincide en el mismo periodo.

**Consolidación:** explica cuándo trasladarías estas agregaciones a SQL y qué coste tiene `findAll()` al crecer la tabla.

## E6 · Informe mensual compuesto

**Requisito:** entregar los gastos del mes agrupados y una vista plana ordenada para poder exportarlos más adelante.

**Conceptos:** LocalDate, LocalDateTime, flatMap, listas de records e inmutabilidad defensiva.

**Endpoint nuevo:** GET `/api/v1/gastos/informe-mensual?anio=2026&mes=10`.

**Contrato propuesto:** `anio`, `mes`, `generadoEn` (LocalDateTime), `total`, `grupos` y `gastosOrdenados`. Cada grupo tiene `categoria`, `total`, `gastos` (lista de GastoResponse).

**Reglas:**

- Admitir años 1–9999 y meses 1–12; valores fuera de rango o parámetros ausentes → 400. Construir primer y último día del mes con la API de fechas, sin cadenas ni asumir 30 días.
- Solo categorías con movimientos, grupos por nombre ascendente; dentro de cada grupo, fecha descendente e id ascendente.
- Construir `gastosOrdenados` aplanando los DTO de los grupos con flatMap; ordenar globalmente por fecha descendente e id ascendente. No deduplicar: cada gasto debe aparecer exactamente una vez.
- Calcular `generadoEn` una sola vez usando un Clock controlable o un valor de referencia pasado al método. Capturar también una única referencia temporal para validar el día en E1.
- Sin movimientos: total 0.00 y ambas listas vacías. Todos los datos derivados de una única lista cargada.
- Copiar defensivamente listas de grupos y gastos; no exponer entidades mutables.

**Tests:** febrero de año bisiesto y no bisiesto, diciembre, mes 0 y 13, límites de año, periodo vacío, instante de generación fijo, tamaño y contenido de la vista plana, inmutabilidad. Alterar una lista usada en la construcción no debe alterar el informe.

**Criterio:** puedes explicar la diferencia entre `Stream<List<GastoResponse>>` y `Stream<GastoResponse>`. No se añade persistencia a LocalDateTime ni relaciones artificiales.

**Consolidación:** compara el total del informe con el de estadísticas del mismo mes; deben coincidir.

## E7 · Reto sin guía: detectar categorías que superan un presupuesto

**Requisito:** dado un presupuesto positivo por categoría, devolver únicamente categorías cuyo gasto mensual lo supera.

**Conceptos:** integración de Map, record, filtros, ordenación y cálculos monetarios.

**Interfaz:** método de servicio, sin endpoint obligatorio. Recibe año, mes y `Map<Categoria, BigDecimal>`; devuelve DTO con categoría, presupuesto, gastado y exceso. No guarda nada.

**Reglas:** presupuestos obligatorios, positivos y representables con dos decimales; mapa nulo o clave/valor nulo → error de negocio. Mapa vacío → lista vacía. Categorías sin presupuesto se ignoran; igualar exactamente el presupuesto no es excederlo. Ordenar por exceso descendente y nombre de categoría ascendente en empate. Validar año y mes como en E6. No modificar el mapa recibido.

**Tests:** con datos compartidos de octubre y presupuestos ALIMENTACION=70.00, OCIO=30.00 y SALUD=12.75, solo ALIMENTACION aparece, con gastado 74.30 y exceso 4.30. Añade presupuesto inválido, empate, categoría sin gasto y entrada vacía.

**Criterio final:** resolver sin copiar E5/E6, justificar las estructuras, escribir pruebas antes de pedir revisión y explicar cómo evitar duplicar reglas sin crear una abstracción innecesaria.
