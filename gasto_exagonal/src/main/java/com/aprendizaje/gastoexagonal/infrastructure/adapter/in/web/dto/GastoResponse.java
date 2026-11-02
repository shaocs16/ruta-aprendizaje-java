package com.aprendizaje.gastoexagonal.infrastructure.adapter.in.web.dto;

import com.aprendizaje.gastoexagonal.domain.model.Categoria;
import com.aprendizaje.gastoexagonal.domain.model.Gasto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record GastoResponse(Long id, String descripcion, BigDecimal importe, LocalDate fecha, Categoria categoria) {

    public static GastoResponse fromDomain(Gasto gasto) {
        return new GastoResponse(gasto.getId(), gasto.getDescripcion(), gasto.getImporte(),
                gasto.getFecha(), gasto.getCategoria());
    }
}
