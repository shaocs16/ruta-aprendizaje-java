package com.aprendizaje.gastoexagonal.infrastructure.config;

import com.aprendizaje.gastoexagonal.application.dto.CalcularTotal;
import com.aprendizaje.gastoexagonal.application.dto.GastoCommand;
import com.aprendizaje.gastoexagonal.application.port.in.GestionGastosUseCase;
import com.aprendizaje.gastoexagonal.domain.model.Categoria;
import com.aprendizaje.gastoexagonal.domain.model.Gasto;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/** Decorador técnico: la transacción abarca el caso de uso sin contaminar la aplicación con Spring. */
@Transactional(readOnly = true)
public class TransactionalGastoUseCase implements GestionGastosUseCase {

    private final GestionGastosUseCase delegate;

    public TransactionalGastoUseCase(GestionGastosUseCase delegate) {
        this.delegate = delegate;
    }

    @Override
    public List<Gasto> findAll() {
        return delegate.findAll();
    }

    @Override
    public List<Gasto> findByCategoria(Categoria categoria) {
        return delegate.findByCategoria(categoria);
    }

    @Override
    public Gasto findById(Long id) {
        return delegate.findById(id);
    }

    @Override
    @Transactional
    public Gasto create(GastoCommand command) {
        return delegate.create(command);
    }

    @Override
    @Transactional
    public Gasto update(Long id, GastoCommand command) {
        return delegate.update(id, command);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        delegate.delete(id);
    }

    @Override
    public CalcularTotal calcularTotalEntreFechas(LocalDate desde, LocalDate hasta) {
        return delegate.calcularTotalEntreFechas(desde, hasta);
    }
}
