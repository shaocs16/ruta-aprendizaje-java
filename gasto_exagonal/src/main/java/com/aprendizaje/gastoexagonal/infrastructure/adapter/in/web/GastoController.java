package com.aprendizaje.gastoexagonal.infrastructure.adapter.in.web;

import com.aprendizaje.gastoexagonal.application.dto.CalcularTotal;
import com.aprendizaje.gastoexagonal.application.port.in.GestionGastosUseCase;
import com.aprendizaje.gastoexagonal.domain.model.Categoria;
import com.aprendizaje.gastoexagonal.domain.model.Gasto;
import com.aprendizaje.gastoexagonal.infrastructure.adapter.in.web.dto.GastoRequest;
import com.aprendizaje.gastoexagonal.infrastructure.adapter.in.web.dto.GastoResponse;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/gastos")
public class GastoController {

    private final GestionGastosUseCase gastos;

    public GastoController(GestionGastosUseCase gastos) {
        this.gastos = gastos;
    }

    @GetMapping
    public List<GastoResponse> findAll(@RequestParam(required = false) Categoria categoria) {
        List<Gasto> resultado = categoria == null ? gastos.findAll() : gastos.findByCategoria(categoria);
        return resultado.stream().map(GastoResponse::fromDomain).toList();
    }

    @GetMapping("/{id}")
    public GastoResponse findById(@PathVariable Long id) {
        return GastoResponse.fromDomain(gastos.findById(id));
    }

    @GetMapping("/total")
    public CalcularTotal calcularTotalEntreFechas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return gastos.calcularTotalEntreFechas(desde, hasta);
    }

    @PostMapping
    public ResponseEntity<Void> save(@RequestBody GastoRequest request) {
        Gasto created = gastos.create(request.toCommand());
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(location).build();
    }

    @PutMapping("/{id}")
    public GastoResponse update(@PathVariable Long id, @RequestBody GastoRequest request) {
        return GastoResponse.fromDomain(gastos.update(id, request.toCommand()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        gastos.delete(id);
        return ResponseEntity.noContent().build();
    }
}
