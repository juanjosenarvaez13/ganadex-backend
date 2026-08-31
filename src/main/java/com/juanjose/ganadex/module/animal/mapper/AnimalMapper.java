package com.juanjose.ganadex.module.animal.mapper;

import com.juanjose.ganadex.module.animal.dto.request.AnimalCreateRequest;
import com.juanjose.ganadex.module.animal.dto.request.AnimalUpdateRequest;
import com.juanjose.ganadex.module.animal.dto.response.AnimalResponse;
import com.juanjose.ganadex.module.animal.entity.Animal;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface AnimalMapper {

    /**
     * "raza" y "usuario" se ignoran a propósito: el Service busca esas
     * entidades por id (razaId/usuarioId del request) vía su repository, y
     * las asigna después de llamar a este método. MapStruct no debe intentar
     * resolver relaciones — no tiene acceso a un repository.
     */
    @Mapping(target = "raza", ignore = true)
    @Mapping(target = "usuario", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "estado", ignore = true) // default ACTIVO (field initializer de la entidad)
    @Mapping(target = "fechaRegistro", ignore = true) // @CreationTimestamp la asigna Hibernate
    Animal toEntity(AnimalCreateRequest request);

    // "usuario" (el dueño/registrador) no cambia en una edición — solo se
    // asigna al crear. "raza" se resuelve en el Service a partir de razaId.
    @Mapping(target = "raza", ignore = true)
    @Mapping(target = "usuario", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "estado", ignore = true) // cambia solo por el endpoint dedicado
    @Mapping(target = "fechaRegistro", ignore = true)
    void updateEntityFromRequest(AnimalUpdateRequest request, @MappingTarget Animal entity);

    @Mapping(source = "raza.id", target = "razaId")
    @Mapping(source = "raza.nombre", target = "razaNombre")
    @Mapping(source = "usuario.id", target = "usuarioId")
    AnimalResponse toResponse(Animal entity);
}
