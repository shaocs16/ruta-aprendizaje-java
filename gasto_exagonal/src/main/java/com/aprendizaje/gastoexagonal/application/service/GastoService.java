package com.aprendizaje.gastoexagonal.application.service;

import com.aprendizaje.gastoexagonal.application.dto.CalcularTotal;
import com.aprendizaje.gastoexagonal.application.dto.GastoCommand;
import com.aprendizaje.gastoexagonal.application.port.in.GestionGastosUseCase;
import com.aprendizaje.gastoexagonal.application.port.out.GastoRepository;
import com.aprendizaje.gastoexagonal.domain.exception.GastoInvalidoException;
import com.aprendizaje.gastoexagonal.domain.exception.GastoNoEncontradoException;
import com.aprendizaje.gastoexagonal.domain.model.Categoria;
import com.aprendizaje.gastoexagonal.domain.model.Gasto;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

/** Orquesta los casos de uso; solo depende de Java, dominio y puertos. */
public class GastoService implements GestionGastosUseCase {

    private final GastoRepository gastoRepository;
    private final Clock clock;

    public GastoService(GastoRepository gastoRepository, Clock clock) {
        this.gastoRepository = gastoRepository;
        this.clock = clock;
    }

    @Override
    public List<Gasto> findAll() {
        return gastoRepository.findAll();
    }

    @Override
    public List<Gasto> findByCategoria(Categoria categoria) {
        if (categoria == null) {
            throw new GastoInvalidoException("Categoria es obligatoria");
        }
        return gastoRepository.findByCategoria(categoria);
    }

    @Override
    public Gasto findById(Long id) {
        validarId(id);
        return gastoRepository.findById(id).orElseThrow(() -> new GastoNoEncontradoException(id));
    }

    @Override
    public Gasto create(GastoCommand command) {
        return gastoRepository.save(construirGasto(null, command));
    }

    @Override
    public Gasto update(Long id, GastoCommand command) {
        Gasto existente = findById(id);
        return gastoRepository.save(construirGasto(existente.getId(), command));
    }

    @Override
    public void delete(Long id) {
        gastoRepository.delete(findById(id));
    }

    @Override
    public CalcularTotal calcularTotalEntreFechas(LocalDate desde, LocalDate hasta) {
        if (desde == null || hasta == null || desde.isAfter(hasta)) {
            throw new GastoInvalidoException("Fechas no validas: desde debe ser anterior o igual a hasta");
        }
        return new CalcularTotal(desde, hasta, gastoRepository.calcularTotalEntreFechas(desde, hasta));
    }

    private Gasto construirGasto(Long id, GastoCommand command) {
        if (command == null) {
            throw new GastoInvalidoException("Los datos del gasto son obligatorios");
        }
        Gasto gasto = new Gasto(id, command.descripcion(), command.importe(), command.fecha(), command.categoria());
        gasto.validarFechaNoFutura(LocalDate.now(clock));
        return gasto;
    }

    private void validarId(Long id) {
        if (id == null || id <= 0) {
            throw new GastoInvalidoException("Id debe ser un numero positivo");
        }
    }
}
