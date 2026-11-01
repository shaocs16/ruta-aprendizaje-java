package com.aprendizaje.gastos.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CalcularTotal(LocalDate desde, LocalDate hasta, BigDecimal totalGastos) {
}
