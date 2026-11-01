package com.aprendizaje.gastos.controller;

import com.aprendizaje.gastos.dto.CalcularTotal;
import com.aprendizaje.gastos.entity.Categoria;
import com.aprendizaje.gastos.entity.Gasto;
import com.aprendizaje.gastos.service.GastoService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/gastos")
public class GastoController {

    private GastoService gastoService;

    public GastoController(GastoService gastoService) {
        this.gastoService = gastoService;
    }


    @GetMapping
    public List<Gasto> findAll(@RequestParam(required = false) Categoria categoria) {

        if (categoria == null) {
            return gastoService.findAll();
        }

        return gastoService.findByCategoria(categoria);
    }

    @GetMapping("/{id}")
    public Gasto findById(@PathVariable Long id) {
        return gastoService.findById(id);
    }

    @GetMapping("/total")
    public CalcularTotal calcularTotalEntreFechas(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return gastoService.calcularTotalEntreFechas(desde, hasta);
    }

    @PostMapping
    public ResponseEntity<Gasto> save(@RequestBody Gasto gasto) {

        Gasto created = gastoService.create(gasto);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();

        return ResponseEntity.created(location).build();

    }

    @PutMapping("/{id}")
    public ResponseEntity<Gasto> update(@PathVariable Long id, @RequestBody Gasto gasto) {
        Gasto updated = gastoService.update(id, gasto);

        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        gastoService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
