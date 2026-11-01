package com.aprendizaje.gastos.repository;

import com.aprendizaje.gastos.entity.Categoria;
import com.aprendizaje.gastos.entity.Gasto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface GastoRepository extends JpaRepository<Gasto, Long> {

    @Query("""
    SELECT COALESCE(SUM(g.importe), 0)
    FROM Gasto g
    WHERE g.fecha BETWEEN :desde AND :hasta
    """)
    BigDecimal calcularTotalEntreFechas(
            @Param("desde") LocalDate desde,
            @Param("hasta") LocalDate hasta
    );

    List<Gasto> findByCategoria(Categoria categoria);
}
