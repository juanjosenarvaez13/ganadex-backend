package com.juanjose.ganadex.module.user.mapper;

import com.juanjose.ganadex.module.user.dto.request.UsuarioCreateRequest;
import com.juanjose.ganadex.module.user.dto.request.UsuarioUpdateRequest;
import com.juanjose.ganadex.module.user.dto.response.UsuarioResponse;
import com.juanjose.ganadex.module.user.entity.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {

    /**
     * El password llega en texto plano en el DTO y NUNCA se mapea
     * automáticamente a la entidad: el Service es responsable de tomar
     * {@code request.getPassword()}, encriptarlo (BCrypt, paso 9) y recién
     * ahí asignarlo con {@code entity.setPassword(hash)}.
     */
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "activo", ignore = true) // default true (field initializer de la entidad)
    @Mapping(target = "fechaCreacion", ignore = true) // @CreationTimestamp la asigna Hibernate
    Usuario toEntity(UsuarioCreateRequest request);

    UsuarioResponse toResponse(Usuario entity);

    // username y password no se tocan aquí a propósito: cambiarlos requiere
    // su propio flujo (ver Javadoc de UsuarioUpdateRequest).
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "username", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "activo", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    void updateEntityFromRequest(UsuarioUpdateRequest request, @MappingTarget Usuario entity);
}
