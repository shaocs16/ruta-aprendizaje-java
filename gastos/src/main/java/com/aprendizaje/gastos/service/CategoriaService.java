package com.aprendizaje.gastos.service;

import com.aprendizaje.gastos.entity.Categoria;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class CategoriaService {

    public List<Categoria> getCategorias() {
        return Arrays.asList(Categoria.values());
    }

}
