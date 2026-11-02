package com.aprendizaje.gastoexagonal.domain.exception;

public class GastoNoEncontradoException extends RuntimeException {

    public GastoNoEncontradoException(Long id) {
        super("Gasto no encontrado: " + id);
    }
}
