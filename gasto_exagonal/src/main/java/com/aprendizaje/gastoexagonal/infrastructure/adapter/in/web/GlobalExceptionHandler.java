package com.aprendizaje.gastoexagonal.infrastructure.adapter.in.web;

import com.aprendizaje.gastoexagonal.domain.exception.GastoInvalidoException;
import com.aprendizaje.gastoexagonal.domain.exception.GastoNoEncontradoException;
import com.aprendizaje.gastoexagonal.infrastructure.adapter.in.web.dto.ApiError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(GastoInvalidoException.class)
    public ResponseEntity<ApiError> handleBadRequest(GastoInvalidoException ex) {
        return error(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(GastoNoEncontradoException.class)
    public ResponseEntity<ApiError> handleNotFound(GastoNoEncontradoException ex) {
        return error(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class,
            MissingServletRequestParameterException.class})
    public ResponseEntity<ApiError> handleInvalidRequest(Exception ex) {
        return error(HttpStatus.BAD_REQUEST, "Peticion no valida: revisa el JSON, los parametros, las fechas y la categoria");
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiError> handleUnexpected(RuntimeException ex) {
        LOGGER.error("Error inesperado al gestionar gastos", ex);
        return error(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno del servidor");
    }

    private ResponseEntity<ApiError> error(HttpStatus status, String message) {
        return ResponseEntity.status(status)
                .body(new ApiError(status.value(), status.getReasonPhrase(), message));
    }
}
