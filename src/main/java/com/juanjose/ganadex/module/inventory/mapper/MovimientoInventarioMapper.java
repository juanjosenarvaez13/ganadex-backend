package com.juanjose.ganadex.module.inventory.mapper;

import com.juanjose.ganadex.module.inventory.dto.request.MovimientoInventarioRequest;
import com.juanjose.ganadex.module.inventory.dto.response.MovimientoInventarioResponse;
import com.juanjose.ganadex.module.inventory.entity.MovimientoInventario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Sin método "update": un movimiento de inventario es un registro de
 * auditoría inmutable (ver nota en el paso 13). "producto" y "usuario" se
 * ignoran en toEntity: el Service los resuelve por id antes de guardar.
 */
@Mapper(componentModel = "spring")
public interface MovimientoInventarioMapper {

    @Mapping(target = "producto", ignore = true)
    @Mapping(target = "usuario", ignore = true)
    @Mapping(target = "id", ignore = true)
    MovimientoInventario toEntity(MovimientoInventarioRequest request);

    @Mapping(source = "producto.id", target = "productoId")
    @Mapping(source = "producto.nombre", target = "productoNombre")
    @Mapping(source = "usuario.id", target = "usuarioId")
    @Mapping(source = "usuario.nombre", target = "usuarioNombre")
    MovimientoInventarioResponse toResponse(MovimientoInventario entity);
}
