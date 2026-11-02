package com.aprendizaje.gastoexagonal.application.port.in;

import com.aprendizaje.gastoexagonal.application.dto.CalcularTotal;
import com.aprendizaje.gastoexagonal.application.dto.GastoCommand;
import com.aprendizaje.gastoexagonal.domain.model.Categoria;
import com.aprendizaje.gastoexagonal.domain.model.Gasto;

import java.time.LocalDate;
import java.util.List;

public interface GestionGastosUseCase {
    List<Gasto> findAll();
    List<Gasto> findByCategoria(Categoria categoria);
    Gasto findById(Long id);
    Gasto create(GastoCommand command);
    Gasto update(Long id, GastoCommand command);
    void delete(Long id);
    CalcularTotal calcularTotalEntreFechas(LocalDate desde, LocalDate hasta);
}
