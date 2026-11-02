package com.aprendizaje.gastoexagonal.infrastructure.adapter.in.web;

import com.aprendizaje.gastoexagonal.application.port.in.ConsultarCategoriasUseCase;
import com.aprendizaje.gastoexagonal.domain.model.Categoria;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categorias")
public class CategoriaController {

    private final ConsultarCategoriasUseCase categorias;

    public CategoriaController(ConsultarCategoriasUseCase categorias) {
        this.categorias = categorias;
    }

    @GetMapping
    public List<Categoria> getCategorias() {
        return categorias.getCategorias();
    }
}
