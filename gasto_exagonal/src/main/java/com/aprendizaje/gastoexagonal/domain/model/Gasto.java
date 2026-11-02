package com.aprendizaje.gastoexagonal.domain.model;

import com.aprendizaje.gastoexagonal.domain.exception.GastoInvalidoException;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Modelo de negocio inmutable: no conoce HTTP, Spring ni JPA. */
public final class Gasto {

    private final Long id;
    private final String descripcion;
    private final BigDecimal importe;
    private final LocalDate fecha;
    private final Categoria categoria;

    public Gasto(Long id, String descripcion, BigDecimal importe, LocalDate fecha, Categoria categoria) {
        if (descripcion == null || descripcion.trim().isEmpty() || descripcion.trim().length() > 100) {
            throw new GastoInvalidoException("Descripcion debe tener entre 1 y 100 caracteres");
        }
        if (importe == null || importe.signum() <= 0 || importe.scale() > 2
                || importe.compareTo(new BigDecimal("99999999.99")) > 0) {
            throw new GastoInvalidoException("Importe debe ser positivo, tener como maximo 2 decimales y no superar 99999999.99");
        }
        if (fecha == null) {
            throw new GastoInvalidoException("Fecha es obligatoria");
        }
        if (categoria == null) {
            throw new GastoInvalidoException("Categoria es obligatoria");
        }
        this.id = id;
        this.descripcion = descripcion.trim();
        this.importe = importe;
        this.fecha = fecha;
        this.categoria = categoria;
    }

    /** La fecha actual llega desde fuera para poder probar esta regla sin depender del reloj real. */
    public void validarFechaNoFutura(LocalDate hoy) {
        if (fecha.isAfter(hoy)) {
            throw new GastoInvalidoException("Fecha no debe ser futura");
        }
    }

    public Long getId() {
        return id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public BigDecimal getImporte() {
        return importe;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public Categoria getCategoria() {
        return categoria;
    }
}
