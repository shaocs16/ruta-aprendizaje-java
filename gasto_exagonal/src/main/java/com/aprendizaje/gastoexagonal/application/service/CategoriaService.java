package com.aprendizaje.gastoexagonal.application.service;

import com.aprendizaje.gastoexagonal.application.port.in.ConsultarCategoriasUseCase;
import com.aprendizaje.gastoexagonal.domain.model.Categoria;

import java.util.List;

public class CategoriaService implements ConsultarCategoriasUseCase {

    @Override
    public List<Categoria> getCategorias() {
        return List.of(Categoria.values());
    }
}
