package com.juanjose.ganadex.module.inventory.mapper;

import com.juanjose.ganadex.module.inventory.dto.request.ProductoRequest;
import com.juanjose.ganadex.module.inventory.dto.response.ProductoResponse;
import com.juanjose.ganadex.module.inventory.entity.Producto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProductoMapper {

    // "stockActual" no está en ProductoRequest: queda sin mapear y la
    // entidad usa su default (BigDecimal.ZERO) al crear.
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "stockActual", ignore = true)
    Producto toEntity(ProductoRequest request);

    // Al actualizar, por la misma razón, "stockActual" del entity existente
    // queda intacto — este DTO nunca lo toca, ni en create ni en update.
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "stockActual", ignore = true)
    void updateEntityFromRequest(ProductoRequest request, @MappingTarget Producto entity);

    @Mapping(target = "stockBajo",
            expression = "java(entity.getStockActual().compareTo(entity.getStockMinimo()) <= 0)")
    ProductoResponse toResponse(Producto entity);
}
