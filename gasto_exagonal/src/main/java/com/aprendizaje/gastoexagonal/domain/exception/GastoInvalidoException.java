package com.aprendizaje.gastoexagonal.domain.exception;

public class GastoInvalidoException extends IllegalArgumentException {

    public GastoInvalidoException(String message) {
        super(message);
    }
}
