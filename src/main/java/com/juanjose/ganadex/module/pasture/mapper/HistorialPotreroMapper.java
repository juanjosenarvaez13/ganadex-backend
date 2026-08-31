package com.juanjose.ganadex.module.pasture.mapper;

import com.juanjose.ganadex.module.pasture.dto.response.HistorialPotreroResponse;
import com.juanjose.ganadex.module.pasture.entity.HistorialPotrero;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Solo Entity -> Response: el registro de historial no se crea a partir de
 * un mapeo automático (el Service arma el {@link HistorialPotrero} con el
 * Animal/Potrero ya resueltos), y no existe "update" — es un registro de
 * auditoría inmutable (ver nota en el paso 13).
 */
@Mapper(componentModel = "spring")
public interface HistorialPotreroMapper {

    @Mapping(source = "animal.id", target = "animalId")
    @Mapping(source = "animal.numeroArete", target = "animalNumeroArete")
    @Mapping(source = "potrero.id", target = "potreroId")
    @Mapping(source = "potrero.nombre", target = "potreroNombre")
    HistorialPotreroResponse toResponse(HistorialPotrero entity);
}
