package com.aprendizaje.gastoexagonal.application.port.out;

import com.aprendizaje.gastoexagonal.domain.model.Categoria;
import com.aprendizaje.gastoexagonal.domain.model.Gasto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/** Puerto de salida: describe lo que necesita la aplicación, no una tecnología concreta. */
public interface GastoRepository {
    List<Gasto> findAll();
    Optional<Gasto> findById(Long id);
    List<Gasto> findByCategoria(Categoria categoria);
    Gasto save(Gasto gasto);
    void delete(Gasto gasto);
    BigDecimal calcularTotalEntreFechas(LocalDate desde, LocalDate hasta);
}
