package com.aprendizaje.gastoexagonal.infrastructure;

import com.aprendizaje.gastoexagonal.application.dto.GastoCommand;
import com.aprendizaje.gastoexagonal.application.port.in.GestionGastosUseCase;
import com.aprendizaje.gastoexagonal.application.port.out.GastoRepository;
import com.aprendizaje.gastoexagonal.domain.model.Categoria;
import com.aprendizaje.gastoexagonal.domain.model.Gasto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Sin @Transactional en el test: comprueba las transacciones abiertas por el decorador real. */
@SpringBootTest
@AutoConfigureMockMvc
class TransactionAndErrorsIntegrationTest {

    @MockitoBean
    private GastoRepository repository;

    @Autowired
    private GestionGastosUseCase gastos;

    @Autowired
    private MockMvc mvc;

    @Test
    void lecturaSeEjecutaEnTransaccionReadOnly() {
        when(repository.findAll()).thenAnswer(invocation -> {
            assertTransaction(true);
            return List.of();
        });

        assertFalse(TransactionSynchronizationManager.isActualTransactionActive());
        gastos.findAll();
        assertFalse(TransactionSynchronizationManager.isActualTransactionActive());
    }

    @Test
    void creacionSeEjecutaEnTransaccionDeEscritura() {
        when(repository.save(any())).thenAnswer(invocation -> {
            assertTransaction(false);
            return invocation.getArgument(0);
        });

        gastos.create(command());
        assertFalse(TransactionSynchronizationManager.isActualTransactionActive());
    }

    @Test
    void actualizacionYBorradoMantienenLaTransaccionEnTodasSusOperaciones() {
        when(repository.findById(1L)).thenAnswer(invocation -> {
            assertTransaction(false);
            return Optional.of(new Gasto(1L, "Compra", BigDecimal.ONE,
                    LocalDate.of(2020, 1, 1), Categoria.OTROS));
        });
        when(repository.save(any())).thenAnswer(invocation -> {
            assertTransaction(false);
            return invocation.getArgument(0);
        });
        doAnswer(invocation -> {
            assertTransaction(false);
            return null;
        }).when(repository).delete(any());

        gastos.update(1L, command());
        gastos.delete(1L);
        assertFalse(TransactionSynchronizationManager.isActualTransactionActive());
    }

    @Test
    void falloInesperadoEs500SinExponerDetallesInternos() throws Exception {
        when(repository.findAll()).thenThrow(new IllegalStateException("Detalle interno de la base de datos"));

        mvc.perform(get("/api/v1/gastos"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.error").value("Internal Server Error"))
                .andExpect(jsonPath("$.message").value("Error interno del servidor"));
        assertFalse(TransactionSynchronizationManager.isActualTransactionActive());
    }

    private void assertTransaction(boolean readOnly) {
        assertTrue(TransactionSynchronizationManager.isActualTransactionActive());
        if (readOnly) {
            assertTrue(TransactionSynchronizationManager.isCurrentTransactionReadOnly());
        } else {
            assertFalse(TransactionSynchronizationManager.isCurrentTransactionReadOnly());
        }
    }

    private GastoCommand command() {
        return new GastoCommand("Compra", BigDecimal.ONE, LocalDate.of(2020, 1, 1), Categoria.OTROS);
    }
}
