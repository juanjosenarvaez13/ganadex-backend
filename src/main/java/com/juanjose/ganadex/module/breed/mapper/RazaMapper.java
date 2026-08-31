package com.juanjose.ganadex.module.breed.mapper;

import com.juanjose.ganadex.module.breed.dto.request.RazaRequest;
import com.juanjose.ganadex.module.breed.dto.response.RazaResponse;
import com.juanjose.ganadex.module.breed.entity.Raza;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface RazaMapper {

    // "id" lo genera la base de datos (IDENTITY), nunca viene del request.
    @Mapping(target = "id", ignore = true)
    Raza toEntity(RazaRequest request);

    RazaResponse toResponse(Raza entity);

    @Mapping(target = "id", ignore = true)
    void updateEntityFromRequest(RazaRequest request, @MappingTarget Raza entity);
}
