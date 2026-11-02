package com.aprendizaje.gastoexagonal.application.dto;

import com.aprendizaje.gastoexagonal.domain.model.Categoria;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Datos de entrada del caso de uso, independientes del formato HTTP. */
public record GastoCommand(String descripcion, BigDecimal importe, LocalDate fecha, Categoria categoria) {
}
