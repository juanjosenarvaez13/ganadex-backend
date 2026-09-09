package com.juanjose.ganadex.module.animal.service;

import com.juanjose.ganadex.module.animal.dto.request.AnimalCambiarEstadoRequest;
import com.juanjose.ganadex.module.animal.dto.request.AnimalCreateRequest;
import com.juanjose.ganadex.module.animal.dto.request.AnimalUpdateRequest;
import com.juanjose.ganadex.module.animal.dto.response.AnimalResponse;
import com.juanjose.ganadex.module.animal.entity.EstadoAnimal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Nota: no existe un método "eliminar". Los animales nunca se borran
 * físicamente (regla de negocio definida desde el inicio del proyecto) —
 * su ciclo de vida se controla exclusivamente con {@link #cambiarEstado}.
 */
public interface AnimalService {

    Page<AnimalResponse> listar(EstadoAnimal estado, Pageable pageable);

    AnimalResponse obtenerPorId(Long id);

    AnimalResponse crear(AnimalCreateRequest request);

    AnimalResponse actualizar(Long id, AnimalUpdateRequest request);

    AnimalResponse cambiarEstado(Long id, AnimalCambiarEstadoRequest request);
}
