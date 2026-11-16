# Dinero, fechas y reglas de negocio

## BigDecimal: exactitud antes que comodidad

No uses `double` para sumar euros. Construye ejemplos con `new BigDecimal("0.10")`, no con `new BigDecimal(0.10)`: el segundo arrastra la representación binaria del literal.

| Operación | Herramienta | Riesgo |
|---|---|---|
| Comparar valor numérico | `compareTo` | `equals` también considera la escala: 10.0 y 10.00 no son iguales para este método |
| Sumar | `add` | Devuelve otro valor; no modifica el original |
| Media | `divide` con escala y redondeo explícitos | Una división periódica puede lanzar ArithmeticException |
| Normalizar moneda | `setScale(2, ...)` | Redondear datos inválidos puede esconder un error de entrada |

Reglas acordadas para esta fase:

- Importe obligatorio, positivo y como máximo `99999999.99`, compatible con `DECIMAL(10,2)`.
- Se aceptan valores representables exactamente con dos decimales (`10`, `10.5`, `10.500`). `10.501` se rechaza; **no** se redondea una entrada de escritura.
- Guarda la representación monetaria con dos decimales. Puedes investigar `RoundingMode.UNNECESSARY` para detectar pérdida de precisión.
- Sumas exactas; medias y porcentajes con escala 2 y `RoundingMode.HALF_UP`.
- Redondea al final del cálculo, no cada gasto antes de sumarlo. El total agregado puede superar el máximo de una fila.

Estas reglas son el objetivo de E1; el código actual usa `scale() <= 2`, que no se comporta igual con `10.500`.

## LocalDate frente a LocalDateTime

`Gasto.fecha` es un `LocalDate`: un día del calendario, sin hora ni zona. Un gasto del 1 de octubre entra en un filtro desde el 1 de octubre.

Intervalo inclusivo: fecha no anterior a `desde` y no posterior a `hasta`. Si los extremos coinciden, se filtra un solo día. En filtros opcionales, un extremo ausente deja ese lado abierto; si ambos existen y `desde > hasta`, el resultado es un error, no una lista vacía.

`LocalDateTime` representa fecha y hora locales **sin zona ni offset**. En E6 se usará para informar cuándo se generó el resumen local. No sustituye a `Instant` para auditoría distribuida. No añadas una columna a la entidad solo para practicar este tipo.

Para que los tests no dependan del día de ejecución, captura el día de referencia una vez y pásalo a la validación, o inyecta un `Clock`. En ejemplos de prueba puedes fijar hoy en `2026-11-16`; para probar futuro usa `hoy.plusDays(1)`.

## Validar y normalizar son responsabilidades distintas

1. Rechazar objeto o campos obligatorios nulos.
2. Normalizar descripción con `strip`.
3. Validar descripción normalizada de 1 a 100 caracteres y no formada solo por blancos.
4. Validar dinero, fecha no futura y categoría obligatoria.
5. Solo después, copiar los valores normalizados y persistir.

Reutiliza estas reglas entre creación y actualización. No alteres parcialmente una entidad administrada por JPA y valides después: un error no debería dejar cambios en memoria pendientes de persistencia.

`@Column(nullable = false)` protege el esquema, pero no proporciona por sí sola mensajes útiles a quien consume HTTP. En esta fase practica validación Java explícita; Bean Validation se estudia más adelante.

### Preguntas de revisión

- ¿Qué ocurre si olvidas asignar el resultado de `importe.add(otro)`?
- ¿Por qué una lista vacía puede sumar cero pero no tiene media matemática?
- ¿Por qué `null` como fecha no debe convertirse silenciosamente en hoy?
- ¿Qué harás si llega `999999999.99` antes de que MySQL lo rechace?
