package com.aprendizaje.gastoexagonal.infrastructure.adapter.in.web.dto;

import com.aprendizaje.gastoexagonal.application.dto.GastoCommand;
import com.aprendizaje.gastoexagonal.domain.model.Categoria;

import java.math.BigDecimal;
import java.time.LocalDate;

public record GastoRequest(String descripcion, BigDecimal importe, LocalDate fecha, Categoria categoria) {

    public GastoCommand toCommand() {
        return new GastoCommand(descripcion, importe, fecha, categoria);
    }
}
