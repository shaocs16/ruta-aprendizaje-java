package com.aprendizaje.gastoexagonal.application;

import com.aprendizaje.gastoexagonal.application.dto.GastoCommand;
import com.aprendizaje.gastoexagonal.application.port.out.GastoRepository;
import com.aprendizaje.gastoexagonal.application.service.CategoriaService;
import com.aprendizaje.gastoexagonal.application.service.GastoService;
import com.aprendizaje.gastoexagonal.domain.exception.GastoInvalidoException;
import com.aprendizaje.gastoexagonal.domain.exception.GastoNoEncontradoException;
import com.aprendizaje.gastoexagonal.domain.model.Categoria;
import com.aprendizaje.gastoexagonal.domain.model.Gasto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class GastoServiceTest {

    private static final LocalDate HOY = LocalDate.of(2026, 11, 2);
    private GastoRepository repository;
    private GastoService service;

    @BeforeEach
    void setUp() {
        repository = mock(GastoRepository.class);
        Clock clock = Clock.fixed(Instant.parse("2026-11-02T12:00:00Z"), ZoneOffset.UTC);
        service = new GastoService(repository, clock);
    }

    @Test
    void creaGastoSinIdYLoGuardaPorElPuerto() {
        when(repository.save(any(Gasto.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Gasto result = service.create(command(HOY));

        assertNull(result.getId());
        assertEquals("Compra", result.getDescripcion());
        verify(repository).save(result);
    }

    @Test
    void rechazaFechaFuturaAntesDeGuardar() {
        assertThrows(GastoInvalidoException.class, () -> service.create(command(HOY.plusDays(1))));
        verifyNoInteractions(repository);
    }

    @Test
    void rechazaComandoNulo() {
        assertThrows(GastoInvalidoException.class, () -> service.create(null));
        verifyNoInteractions(repository);
    }

    @Test
    void devuelveTodosYFiltraPorCategoria() {
        List<Gasto> gastos = List.of(gasto());
        when(repository.findAll()).thenReturn(gastos);
        when(repository.findByCategoria(Categoria.OTROS)).thenReturn(gastos);

        assertEquals(gastos, service.findAll());
        assertEquals(gastos, service.findByCategoria(Categoria.OTROS));
        assertThrows(GastoInvalidoException.class, () -> service.findByCategoria(null));
    }

    @Test
    void encuentraGastoPorId() {
        Gasto existente = gasto();
        when(repository.findById(1L)).thenReturn(Optional.of(existente));
        assertEquals(existente, service.findById(1L));
    }

    @Test
    void informaCuandoNoExisteElGasto() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(GastoNoEncontradoException.class, () -> service.findById(1L));
        assertThrows(GastoNoEncontradoException.class, () -> service.update(1L, command(HOY)));
        assertThrows(GastoNoEncontradoException.class, () -> service.delete(1L));
        verify(repository, never()).save(any());
        verify(repository, never()).delete(any());
    }

    @Test
    void rechazaIdentificadorInvalido() {
        assertThrows(GastoInvalidoException.class, () -> service.findById(null));
        assertThrows(GastoInvalidoException.class, () -> service.findById(0L));
        assertThrows(GastoInvalidoException.class, () -> service.delete(-1L));
        verifyNoInteractions(repository);
    }

    @Test
    void actualizaConservandoIdentificador() {
        when(repository.findById(1L)).thenReturn(Optional.of(gasto()));
        when(repository.save(any(Gasto.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Gasto result = service.update(1L, command(HOY));

        assertEquals(1L, result.getId());
        assertEquals("Compra", result.getDescripcion());
        verify(repository).save(result);
    }

    @Test
    void tambienValidaAlActualizar() {
        when(repository.findById(1L)).thenReturn(Optional.of(gasto()));

        assertThrows(GastoInvalidoException.class, () -> service.update(1L, command(HOY.plusDays(1))));
        verify(repository, never()).save(any());
    }

    @Test
    void eliminaGastoExistente() {
        Gasto existente = gasto();
        when(repository.findById(1L)).thenReturn(Optional.of(existente));

        service.delete(1L);

        verify(repository).delete(existente);
    }

    @Test
    void calculaTotalConRangoInclusivoInclusoUnSoloDia() {
        when(repository.calcularTotalEntreFechas(HOY, HOY)).thenReturn(new BigDecimal("10.50"));

        var result = service.calcularTotalEntreFechas(HOY, HOY);

        assertEquals(HOY, result.desde());
        assertEquals(HOY, result.hasta());
        assertEquals(new BigDecimal("10.50"), result.totalGastos());
    }

    @Test
    void rechazaRangoInvalidoONulo() {
        assertThrows(GastoInvalidoException.class, () -> service.calcularTotalEntreFechas(HOY, HOY.minusDays(1)));
        assertThrows(GastoInvalidoException.class, () -> service.calcularTotalEntreFechas(null, HOY));
        assertThrows(GastoInvalidoException.class, () -> service.calcularTotalEntreFechas(HOY, null));
        verifyNoInteractions(repository);
    }

    @Test
    void devuelveLasSeisCategoriasOriginales() {
        assertEquals(List.of(Categoria.values()), new CategoriaService().getCategorias());
    }

    private GastoCommand command(LocalDate fecha) {
        return new GastoCommand("Compra", new BigDecimal("10.50"), fecha, Categoria.OTROS);
    }

    private Gasto gasto() {
        return new Gasto(1L, "Anterior", BigDecimal.ONE, HOY.minusDays(1), Categoria.OTROS);
    }
}
