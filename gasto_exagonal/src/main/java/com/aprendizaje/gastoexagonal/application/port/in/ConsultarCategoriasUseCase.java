package com.aprendizaje.gastoexagonal.application.port.in;

import com.aprendizaje.gastoexagonal.domain.model.Categoria;

import java.util.List;

public interface ConsultarCategoriasUseCase {
    List<Categoria> getCategorias();
}
