package com.aprendizaje.gastos.dto;

public record ApiError(
        int status,
        String error,
        String message
) {
}
