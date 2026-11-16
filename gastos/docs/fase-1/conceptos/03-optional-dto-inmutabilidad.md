# Optional, excepciones, records e inmutabilidad

## Optional describe una ausencia posible

`GastoRepository.findById` ya devuelve `Optional<Gasto>`: puede que esa fila no exista. No llames a `get()` sin comprobar ni envuelvas listas vacías en Optional; una lista sin elementos expresa por sí misma ausencia de resultados.

- `map`: transforma el valor si existe.
- `orElse`: recibe un valor alternativo, evaluado antes de la llamada.
- `orElseGet`: calcula la alternativa solo si no existe el valor.
- `orElseThrow`: convierte la ausencia en una excepción concreta.

```java
String etiqueta = java.util.Optional.ofNullable("  comida  ")
        .map(String::strip)
        .filter(texto -> !texto.isBlank())
        .orElse("Sin etiqueta");
```

No uses un texto alternativo para campos obligatorios del gasto: el ejemplo enseña la API, no una regla de aceptación. Tampoco uses Optional como campo de una entidad JPA.

## Excepciones que expresan qué ha ocurrido

E2 propone `GastoNoEncontradoException` para un id ausente y `GastoInvalidoException` para reglas de negocio. Define qué información necesitan y un mensaje útil. Captura tipos concretos en `GlobalExceptionHandler`:

| Situación | HTTP esperado |
|---|---|
| Campo obligatorio o intervalo inválido | 400 |
| JSON o parámetro que no se puede convertir | 400 |
| Identificador inexistente | 404 |
| Fallo inesperado de programación o infraestructura | 500 |

Los errores de conversión se producen antes de entrar al servicio: no todos pasarán por tus excepciones de negocio. Conserva su 400. Un handler genérico, si decides tenerlo, no debe filtrar detalles internos y debe preservar los errores HTTP conocidos. Para esta fase evita capturar toda `RuntimeException` como si significase «no encontrado».

## Entidad frente a DTO

La entidad representa persistencia y puede ser mutable. Un DTO define qué datos expones. Cambiar una tabla no debería obligar a cambiar toda tu API accidentalmente.

Ya tienes los records `CalcularTotal` y `ApiError`. Un record genera constructor, accesores (`totalGastos()`, no `getTotalGastos()`), igualdad, hash y representación textual.

Ejemplo distinto del DTO que deberás diseñar:

```java
public record Periodo(java.time.LocalDate desde, java.time.LocalDate hasta) {
    public Periodo {
        if (desde == null || hasta == null || desde.isAfter(hasta)) {
            throw new IllegalArgumentException("Periodo inválido");
        }
    }
}
```

No conviertas `Gasto` en record: JPA necesita su modelo de entidad y este ejercicio no pretende cambiar la persistencia.

## Inmutabilidad no significa solamente final

Un record con `List<Gasto>` puede seguir exponiendo entidades modificables. Para las respuestas de informes:

1. Transforma entidades en records con valores inmutables (`String`, enum, BigDecimal y tipos de fecha).
2. Copia listas recibidas con `List.copyOf` en el constructor si pueden venir de fuera.
3. No conserves mapas o listas mutables externos sin copia defensiva.
4. Recuerda que las copias de colecciones son superficiales; sus elementos también importan.

`Stream.toList()` devuelve una lista no modificable. `Collectors.toList()` no garantiza contractualmente ni tipo concreto ni mutabilidad. `Arrays.asList`, usado actualmente en categorías, tiene tamaño fijo pero permite reemplazar elementos: no es lo mismo.

**Autocomprobación:** ¿podría alguien modificar el informe después de crearlo? ¿Y modificar el gasto original mediante una referencia obtenida del informe? Si sí, aún no has separado ambos modelos.
