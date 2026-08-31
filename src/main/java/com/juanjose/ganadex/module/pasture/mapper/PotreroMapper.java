package com.juanjose.ganadex.module.pasture.mapper;

import com.juanjose.ganadex.module.pasture.dto.request.PotreroRequest;
import com.juanjose.ganadex.module.pasture.dto.response.PotreroResponse;
import com.juanjose.ganadex.module.pasture.entity.Potrero;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PotreroMapper {

    // "estado" no está en PotreroRequest a propósito (ver PotreroCambiarEstadoRequest):
    // queda sin mapear y la entidad usa su default (DISPONIBLE) al crear.
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "estado", ignore = true)
    Potrero toEntity(PotreroRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "estado", ignore = true)
    void updateEntityFromRequest(PotreroRequest request, @MappingTarget Potrero entity);

    PotreroResponse toResponse(Potrero entity);
}
