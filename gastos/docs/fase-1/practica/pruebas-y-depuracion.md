# Pruebas y depuración de la fase

## Qué probar sin adelantar toda la fase de testing

No necesitas arrancar Spring para comprobar una suma, un filtro o un comparador. Empieza con JUnit y objetos Java; utiliza Mockito cuando el servicio tenga que consultar el repositorio.

| Clase de prueba propuesta | Responsabilidad |
|---|---|
| `service/GastoServiceTest` | Creación/edición, búsqueda ausente y colaboración con repositorio |
| `service/GastoAnalizadorTest` | Filtros, ordenación y estadísticas sobre listas; si extraes ese colaborador |
| `dto/InformeMensualTest` | Copias defensivas e inmutabilidad |
| `exception/GlobalExceptionHandlerTest` | Estados de las excepciones de negocio |
| `controller/GastoControllerTest` | Opcionalmente MockMvc para conversión de parámetros y contrato HTTP |

Las ubicaciones son relativas a `src/test/java/com/aprendizaje/gastos/`. Usa solo las clases que correspondan a tu diseño. Una prueba unitaria de un handler no comprueba por sí sola el comportamiento HTTP completo; para comprobar conversiones necesitas la capa web.

## Patrón de una prueba

**Preparar:** construir datos mínimos y dependencias controladas.

**Actuar:** ejecutar una sola operación relevante.

**Comprobar:** observar resultado, estado de entrada y colaboraciones importantes.

Ejemplo aislado de precisión monetaria (no resuelve las funcionalidades):

```java
@org.junit.jupiter.api.Test
void sumaDecimalesSinPerderPrecision() {
    var primero = new java.math.BigDecimal("0.10");
    var segundo = new java.math.BigDecimal("0.20");

    var resultado = primero.add(segundo);

    org.junit.jupiter.api.Assertions.assertEquals(
            0, new java.math.BigDecimal("0.30").compareTo(resultado));
}
```

`compareTo` comprueba valor. Si el contrato exige además dos decimales, comprueba por separado `scale() == 2`. No conviertas a double ni añadas tolerancias a importes exactos.

## Matriz mínima de entrega

| Bloque | Camino correcto | Vacío / ausencia | Error o límite |
|---|---|---|---|
| E1 | POST y PUT normalizan y guardan | Nulos obligatorios rechazados | 100/101 caracteres, futuro, precisión, máximo monetario |
| E2 | Id existente | Id ausente en GET/PUT/DELETE | Inesperado no termina en 404; intervalo inválido 400 |
| E3 | Filtros aislados y combinados | Sin coincidencias, sin filtros | Rangos invertidos y extremos inclusivos |
| E4 | Cuatro órdenes y DTO correcto | Lista vacía | Empates, entrada barajada y orden desconocido |
| E5 | Totales, medias y porcentajes | Cero y mayor null | División periódica, duplicados y empates |
| E6 | Mes completo y vista plana | Mes sin gastos | Bisiesto, fecha de generación fija y listas protegidas |
| E7 | Solo excesos positivos | Mapa vacío y categoría sin movimientos | Presupuesto igual al gasto, inválido y empate |

Comprueba también que las lecturas no cambian entidades ni llaman a `save` y que una edición inválida no altera el objeto obtenido del repositorio.

## Ejecución (desde gastos/)

```powershell
.\mvnw.cmd test
```

Después de crear, por ejemplo, `GastoServiceTest`, puedes aislarla durante el desarrollo:

```powershell
.\mvnw.cmd '-Dtest=GastoServiceTest' test
```

Termina siempre con la suite completa, incluido `contextLoads`. No confundas «mis tests unitarios pasan» con «la aplicación arranca». El problema inicial de H2 y su preparación están descritos en [el paso 0](../00-proyecto-y-preparacion.md); este material no añade esa dependencia ni nuevas pruebas ejecutables.

## Depuración guiada

1. Reproduce el fallo con el conjunto de datos más pequeño posible.
2. Pon un breakpoint al entrar al servicio; comprueba parámetros y valores normalizados.
3. En una cadena de Streams identifica el tipo en cada paso y el predicado que descarta el elemento.
4. Si necesitas inspeccionar etapas, divide temporalmente la cadena en variables o prueba cada función por separado. No dejes `peek` con lógica de negocio.
5. Si un importe es incorrecto, examina valor, escala, divisor y momento del redondeo.
6. Si un límite de fecha falla, comprueba inclusión y reloj de referencia.
7. Si la respuesta es un 404 inesperado, inspecciona el handler antes de asumir que falta una fila.
8. Añade una prueba que falle antes del arreglo y pase después.

## Registro de revisión

Copia este bloque en tus notas por cada error real:

```text
Ejercicio:
Entrada mínima que reproduce el problema:
Esperaba:
Obtuve:
Causa:
Cambio realizado:
Prueba de regresión:
Qué haré distinto la próxima vez:
```
