package com.aprendizaje.gastoexagonal.infrastructure.adapter.out.persistence;

import com.aprendizaje.gastoexagonal.domain.model.Gasto;

public final class GastoPersistenceMapper {

    private GastoPersistenceMapper() {
    }

    public static GastoJpaEntity toEntity(Gasto gasto) {
        return new GastoJpaEntity(gasto.getId(), gasto.getDescripcion(), gasto.getImporte(),
                gasto.getFecha(), gasto.getCategoria());
    }

    public static Gasto toDomain(GastoJpaEntity entity) {
        return new Gasto(entity.getId(), entity.getDescripcion(), entity.getImporte(),
                entity.getFecha(), entity.getCategoria());
    }
}
