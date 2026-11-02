package com.aprendizaje.gastoexagonal.infrastructure.adapter.out.persistence;

import com.aprendizaje.gastoexagonal.domain.model.Categoria;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "gasto")
public class GastoJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String descripcion;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal importe;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private Categoria categoria;

    protected GastoJpaEntity() {
    }

    public GastoJpaEntity(Long id, String descripcion, BigDecimal importe, LocalDate fecha, Categoria categoria) {
        this.id = id;
        this.descripcion = descripcion;
        this.importe = importe;
        this.fecha = fecha;
        this.categoria = categoria;
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
