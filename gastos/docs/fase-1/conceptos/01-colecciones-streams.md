# Colecciones, Streams y programación funcional

## 1. Escoge la estructura por lo que representa

| Tipo | Significado | Uso en gastos | Precaución |
|---|---|---|---|
| `List<Gasto>` | Secuencia con posibles duplicados | Gastos filtrados y ordenados | No implica orden SQL si la consulta no lo define |
| `Set<Categoria>` | Valores únicos | Categorías con movimientos | Un HashSet no garantiza orden de iteración |
| `Map<Categoria, List<Gasto>>` | Asociación clave → valor | Gastos agrupados | No ordena sus claves automáticamente |

Un `Set<Gasto>` no elimina por arte de magia los gastos con el mismo importe o descripción. La igualdad depende de `equals`/`hashCode`; `Gasto` actualmente no los redefine. Para categorías únicas, proyecta primero a `Categoria`.

`List<Categoria>` expresa el tipo de sus elementos: eso son genéricos. El compilador impide introducir un `String`. Evita `List` sin parámetro y conversiones de tipo a mano. `Map<Categoria, BigDecimal>` tiene dos parámetros porque clave y valor cumplen funciones diferentes.

## 2. Una cadena tiene entrada, operaciones y resultado

Ejemplo pequeño, no una solución de los ejercicios:

```java
List<String> nombres = List.of("  cine  ", "", "  taxi");
List<String> limpios = nombres.stream()
        .map(String::strip)
        .filter(nombre -> !nombre.isBlank())
        .toList();
```

- `stream()` crea una secuencia de procesamiento, no otra tabla.
- `map` transforma cada elemento; aquí `String → String`, en el proyecto `Gasto → GastoResponse`.
- `filter` conserva elementos que cumplen un predicado; no cambia su tipo.
- `toList` es terminal: ejecuta el recorrido y devuelve una lista no modificable.
- Las operaciones intermedias son perezosas. Sin operación terminal no se procesa la cadena.
- Un stream consumido no se puede reutilizar. Crea otro a partir de la colección.

Una lambda como `nombre -> !nombre.isBlank()` implementa una interfaz funcional. Reconoce estas firmas:

| Interfaz | Forma | Ejemplo de responsabilidad |
|---|---|---|
| `Predicate<Gasto>` | `Gasto → boolean` | Aceptar un gasto según filtros |
| `Function<Gasto, GastoResponse>` | `Gasto → DTO` | Mapear una entidad |
| `BinaryOperator<BigDecimal>` | `(importe, importe) → importe` | Acumular una suma |
| `Supplier<RuntimeException>` | `() → excepción` | Crear un error solo si falta un valor |

`Gasto::getFecha` equivale a `gasto -> gasto.getFecha()`. Prefiere el que resulte más claro.

## 3. Agrupar, aplanar y reducir no son lo mismo

- `Collectors.groupingBy(...)`: crea un mapa de grupos, por ejemplo por categoría.
- `Collectors.toSet()`: elimina duplicados según igualdad, sin prometer orden.
- `map`: una lista de listas sigue teniendo listas como elementos.
- `flatMap`: transforma cada elemento en un stream y los concatena en un solo nivel.
- `reduce`: combina elementos hasta obtener un resultado; la suma necesita una identidad cero y una operación asociativa.

```java
List<List<String>> bloques = List.of(List.of("A", "B"), List.of("C"));
List<String> elementos = bloques.stream()
        .flatMap(List::stream)
        .toList(); // [A, B, C]
```

En E6 aplanarás listas de DTO de un informe, sin inventar una relación JPA que el modelo no tiene. En E5 decide qué ocurre con una entrada vacía antes de escoger la sobrecarga de `reduce`.

## 4. Ordenar con Comparator

```java
Comparator<String> porLongitudYTexto = Comparator
        .comparingInt(String::length)
        .thenComparing(Comparator.naturalOrder());
```

En gastos definirás un criterio principal y desempates por fecha e id. `reversed()` aplicado al comparador completo invierte también sus desempates; aplicado solo al primer criterio no. Comprueba qué contrato necesitas.

## 5. Evita efectos laterales

No uses `map` para modificar la entidad con setters ni `peek` para validar o guardar. Una operación de lectura no debe producir escrituras. Tampoco acumules en una lista externa con `forEach` cuando `map(...).toList()` expresa la transformación.

No uses `parallelStream()` para estas listas ni para acceder al repositorio: añade concurrencia innecesaria y complica el contexto transaccional.

### Comprueba que lo entiendes

1. Dibuja el tipo resultante después de cada operación de una cadena.
2. Explica por qué `filter` antes de `map` puede ahorrar transformaciones.
3. ¿Por qué un mapa agrupado no garantiza el orden de su JSON?
4. ¿Por qué una lista no modificable que contiene entidades mutables no es completamente inmutable?
