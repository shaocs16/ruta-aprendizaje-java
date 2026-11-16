# Datos compartidos y resultados esperados

## Conjunto de trabajo

Los cuatro primeros registros coinciden con el SQL actual; los otros cuatro son **datos adicionales propuestos**. No están cargados automáticamente. Para tests unitarios usa objetos en memoria y asigna ids de prueba con un helper de test (por ejemplo, ReflectionTestUtils), sin añadir un setter de id a producción solo para facilitar los tests.

| id de prueba | Descripción | Importe | Fecha | Categoría |
|---|---|---:|---|---|
| 1 | Compra semanal | 54.30 | 2026-10-03 | ALIMENTACION |
| 2 | Abono transporte | 40.00 | 2026-10-01 | TRANSPORTE |
| 3 | Cine | 18.50 | 2026-10-10 | OCIO |
| 4 | Farmacia | 12.75 | 2026-10-12 | SALUD |
| 5 | Fruta | 20.00 | 2026-10-03 | ALIMENTACION |
| 6 | Museo | 10.00 | 2026-10-10 | OCIO |
| 7 | Taxi | 10.00 | 2026-09-30 | TRANSPORTE |
| 8 | Alquiler | 600.00 | 2026-10-01 | VIVIENDA |

Los ids son etiquetas reproducibles del fixture, no una promesa de ids autogenerados en tu MySQL. Para pruebas HTTP introduce los datos en tu base local por separado y anota los ids devueltos en Location. No ejecutes inserciones sobre bases compartidas ni asumas que una tabla existente está vacía.

## Oráculos de cálculo

Periodo **2026-10-01 a 2026-10-31**, ambos incluidos:

- Cantidad: 7; total: **755.55**; media: **107.94**.
- Mayor: id 8, 600.00.
- FECHA_DESC: **4, 3, 6, 1, 5, 2, 8**.
- IMPORTE_DESC: **8, 1, 2, 5, 3, 4, 6**.
- ALIMENTACION: ids 1 y 5; total **74.30**.
- Rango de un día, 2026-10-03: cantidad 2 y total **74.30**.
- OCIO e importe mínimo 18.50: solo id 3 (límite incluido).
- Categorías utilizadas: ALIMENTACION, OCIO, SALUD, TRANSPORTE, VIVIENDA.
- Total de todos los datos, sin filtro temporal: **765.55** (incluye el taxi de septiembre).

| Categoría (orden de respuesta) | Cantidad | Total | Porcentaje de octubre |
|---|---:|---:|---:|
| ALIMENTACION | 2 | 74.30 | 9.83 |
| OCIO | 2 | 28.50 | 3.77 |
| SALUD | 1 | 12.75 | 1.69 |
| TRANSPORTE | 1 | 40.00 | 5.29 |
| VIVIENDA | 1 | 600.00 | 79.41 |

Los porcentajes suman 99.99 por redondeo independiente. Es esperado, no un fallo que se arregle alterando importes.

## Casos que requieren fixtures adicionales

- Empate total: crea dos gastos con mismo importe y fecha pero diferente id; debe ganar primero el menor id.
- Precisión: 0.10 y 0.20 suman exactamente 0.30.
- Media periódica: 1.00, 1.00 y 2.00 producen total 4.00 y media 1.33.
- Lista vacía: comprueba listas vacías, cero monetario y mayor null.
- Fechas: con hoy fijo en 2026-11-16, 2026-11-17 debe rechazarse como fecha de escritura.

No mezcles estos fixtures con los ocho registros al comparar los totales anteriores.

## Peticiones para comprobar tras implementar

Con la API arrancada, desde PowerShell:

```powershell
# Ya existe actualmente:
Invoke-RestMethod 'http://localhost:8080/api/v1/gastos/total?desde=2026-10-01&hasta=2026-10-31'

# Disponibles solo después de resolver los ejercicios correspondientes:
Invoke-RestMethod 'http://localhost:8080/api/v1/gastos?categoria=ALIMENTACION&importeMin=20.00&importeMax=60.00&orden=IMPORTE_DESC'
Invoke-RestMethod 'http://localhost:8080/api/v1/gastos/estadisticas?desde=2026-10-01&hasta=2026-10-31'
Invoke-RestMethod 'http://localhost:8080/api/v1/gastos/categorias-utilizadas?desde=2026-10-01&hasta=2026-10-31'
Invoke-RestMethod 'http://localhost:8080/api/v1/gastos/informe-mensual?anio=2026&mes=10'
```

Antes de E3, los parámetros adicionales del listado pueden ser ignorados por el controlador actual: recibir 200 **no demuestra** que el filtro esté implementado. Verifica contenido, orden y cantidades, no solamente el estado HTTP.
