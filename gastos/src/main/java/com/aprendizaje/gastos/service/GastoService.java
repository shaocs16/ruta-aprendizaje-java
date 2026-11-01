package com.aprendizaje.gastos.service;

import com.aprendizaje.gastos.dto.CalcularTotal;
import com.aprendizaje.gastos.entity.Categoria;
import com.aprendizaje.gastos.entity.Gasto;
import com.aprendizaje.gastos.repository.GastoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class GastoService {

    private GastoRepository gastoRepository;

    public GastoService(GastoRepository gastoRepository) {
        this.gastoRepository = gastoRepository;
    }

    @Transactional(readOnly = true)
    public List<Gasto> findAll() {
        return gastoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Gasto findById(Long id) {
        return gastoRepository.findById(id).orElseThrow(() -> new RuntimeException("Gasto no encontrado"));
    }

    @Transactional
    public Gasto create(Gasto gasto) {

        Gasto created = new Gasto();

        if(checkDescripcionNull(gasto.getDescripcion())) {
            throw new IllegalArgumentException("Descripcion no puede ser vacia");
        }
        if(!checkDescripcionLength(gasto.getDescripcion())) {
            throw new IllegalArgumentException("Descripcion debe de tener entre 1 y 100 caracteres");
        }
        created.setDescripcion(gasto.getDescripcion());

        if(!checkImporte(gasto.getImporte())) {
            throw new IllegalArgumentException("Importe debe ser mayor que 0");
        }
        created.setImporte(gasto.getImporte());

        if(checkDate(gasto.getFecha())) {
            throw new IllegalArgumentException("Fecha no debe ser futuro");
        }
        created.setFecha(gasto.getFecha());

        created.setCategoria(gasto.getCategoria());

        return gastoRepository.save(created);
    }

    @Transactional
    public Gasto update(Long id, Gasto gasto) {

        Gasto data = gastoRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        data.setDescripcion(gasto.getDescripcion());
        data.setCategoria(gasto.getCategoria());
        data.setImporte(gasto.getImporte());
        data.setFecha(gasto.getFecha());

        return gastoRepository.save(data);
    }

    @Transactional
    public void delete(Long id) {

        Gasto data = gastoRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        gastoRepository.delete(data);
    }

    @Transactional(readOnly = true)
    public CalcularTotal calcularTotalEntreFechas(LocalDate start, LocalDate end) {
        if(start.isAfter(end)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Fechas no valida");
        }

        BigDecimal totalGastos = gastoRepository.calcularTotalEntreFechas(start, end);

        return new CalcularTotal(start, end, totalGastos);
    }

    @Transactional(readOnly = true)
    public List<Gasto> findByCategoria(Categoria categoria) {

        return gastoRepository.findByCategoria(categoria);

    }

    private boolean checkImporte(BigDecimal importe) {

        if(importe == null || importe.compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }

        return importe.scale() <= 2;
    }

    private boolean checkDate(LocalDate date) {
        return date != null && date.isAfter(LocalDate.now());
    }

    private boolean checkDescripcionNull(String descripcion) {
        return  descripcion == null || descripcion.isEmpty();
    }

    private boolean checkDescripcionLength(String descripcion) {
        String format = descripcion.trim();
        return format.length() >= 1 && format.length() <= 100;
    }

}
