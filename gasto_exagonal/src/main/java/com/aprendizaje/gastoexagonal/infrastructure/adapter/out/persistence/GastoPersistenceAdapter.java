package com.aprendizaje.gastoexagonal.infrastructure.adapter.out.persistence;

import com.aprendizaje.gastoexagonal.application.port.out.GastoRepository;
import com.aprendizaje.gastoexagonal.domain.model.Categoria;
import com.aprendizaje.gastoexagonal.domain.model.Gasto;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public class GastoPersistenceAdapter implements GastoRepository {

    private final SpringDataGastoRepository repository;

    public GastoPersistenceAdapter(SpringDataGastoRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Gasto> findAll() {
        return repository.findAll().stream().map(GastoPersistenceMapper::toDomain).toList();
    }

    @Override
    public Optional<Gasto> findById(Long id) {
        return repository.findById(id).map(GastoPersistenceMapper::toDomain);
    }

    @Override
    public List<Gasto> findByCategoria(Categoria categoria) {
        return repository.findByCategoria(categoria).stream().map(GastoPersistenceMapper::toDomain).toList();
    }

    @Override
    public Gasto save(Gasto gasto) {
        return GastoPersistenceMapper.toDomain(repository.save(GastoPersistenceMapper.toEntity(gasto)));
    }

    @Override
    public void delete(Gasto gasto) {
        repository.deleteById(gasto.getId());
    }

    @Override
    public BigDecimal calcularTotalEntreFechas(LocalDate desde, LocalDate hasta) {
        return repository.calcularTotalEntreFechas(desde, hasta);
    }
}
