package com.aprendizaje.gastoexagonal.infrastructure.adapter.in.web.dto;

public record ApiError(int status, String error, String message) {
}
