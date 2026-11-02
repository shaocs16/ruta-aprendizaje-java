package com.aprendizaje.gastoexagonal.domain;

import com.aprendizaje.gastoexagonal.domain.exception.GastoInvalidoException;
import com.aprendizaje.gastoexagonal.domain.model.Categoria;
import com.aprendizaje.gastoexagonal.domain.model.Gasto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GastoTest {

    private static final LocalDate HOY = LocalDate.of(2026, 11, 2);

    @Test
    void normalizaDescripcionYConservaDatos() {
        Gasto gasto = new Gasto(1L, "  Compra  ", new BigDecimal("10.50"), HOY, Categoria.ALIMENTACION);

        assertEquals(1L, gasto.getId());
        assertEquals("Compra", gasto.getDescripcion());
        assertEquals(new BigDecimal("10.50"), gasto.getImporte());
        assertEquals(HOY, gasto.getFecha());
        assertEquals(Categoria.ALIMENTACION, gasto.getCategoria());
        assertDoesNotThrow(() -> gasto.validarFechaNoFutura(HOY));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   "})
    void rechazaDescripcionVacia(String descripcion) {
        assertThrows(GastoInvalidoException.class,
                () -> new Gasto(null, descripcion, BigDecimal.ONE, HOY, Categoria.OTROS));
    }

    @Test
    void rechazaDescripcionLarga() {
        assertThrows(GastoInvalidoException.class,
                () -> new Gasto(null, "x".repeat(101), BigDecimal.ONE, HOY, Categoria.OTROS));
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "-1", "1.001", "100000000.00"})
    void rechazaImporteInvalido(String importe) {
        assertThrows(GastoInvalidoException.class,
                () -> new Gasto(null, "Compra", new BigDecimal(importe), HOY, Categoria.OTROS));
    }

    @Test
    void aceptaLimitesDeDescripcionEImporte() {
        assertDoesNotThrow(() -> new Gasto(null, "x".repeat(100),
                new BigDecimal("99999999.99"), HOY, Categoria.OTROS));
    }

    @Test
    void rechazaImporteFechaOCategoriaNulos() {
        assertThrows(GastoInvalidoException.class,
                () -> new Gasto(null, "Compra", null, HOY, Categoria.OTROS));
        assertThrows(GastoInvalidoException.class,
                () -> new Gasto(null, "Compra", BigDecimal.ONE, null, Categoria.OTROS));
        assertThrows(GastoInvalidoException.class,
                () -> new Gasto(null, "Compra", BigDecimal.ONE, HOY, null));
    }

    @Test
    void rechazaFechaFuturaConReferenciaExplicita() {
        Gasto gasto = new Gasto(null, "Compra", BigDecimal.ONE, HOY.plusDays(1), Categoria.OTROS);

        assertThrows(GastoInvalidoException.class, () -> gasto.validarFechaNoFutura(HOY));
    }
}
