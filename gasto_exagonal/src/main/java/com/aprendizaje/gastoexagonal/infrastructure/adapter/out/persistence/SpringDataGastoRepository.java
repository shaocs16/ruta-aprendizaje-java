package com.aprendizaje.gastoexagonal.infrastructure.adapter.out.persistence;

import com.aprendizaje.gastoexagonal.domain.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface SpringDataGastoRepository extends JpaRepository<GastoJpaEntity, Long> {

    List<GastoJpaEntity> findByCategoria(Categoria categoria);

    @Query("""
            SELECT COALESCE(SUM(g.importe), 0)
            FROM GastoJpaEntity g
            WHERE g.fecha BETWEEN :desde AND :hasta
            """)
    BigDecimal calcularTotalEntreFechas(@Param("desde") LocalDate desde, @Param("hasta") LocalDate hasta);
}
