package com.juanjose.ganadex.module.animal.service.impl;

import com.juanjose.ganadex.common.exception.BusinessException;
import com.juanjose.ganadex.common.exception.ResourceNotFoundException;
import com.juanjose.ganadex.module.animal.dto.request.AnimalCambiarEstadoRequest;
import com.juanjose.ganadex.module.animal.dto.request.AnimalCreateRequest;
import com.juanjose.ganadex.module.animal.dto.request.AnimalUpdateRequest;
import com.juanjose.ganadex.module.animal.dto.response.AnimalResponse;
import com.juanjose.ganadex.module.animal.entity.Animal;
import com.juanjose.ganadex.module.animal.entity.EstadoAnimal;
import com.juanjose.ganadex.module.animal.mapper.AnimalMapper;
import com.juanjose.ganadex.module.animal.repository.AnimalRepository;
import com.juanjose.ganadex.module.animal.service.AnimalService;
import com.juanjose.ganadex.module.breed.entity.Raza;
import com.juanjose.ganadex.module.breed.repository.RazaRepository;
import com.juanjose.ganadex.module.user.entity.Usuario;
import com.juanjose.ganadex.module.user.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnimalServiceImpl implements AnimalService {

    /**
     * Mapa de transiciones de estado permitidas: la clave es el estado
     * actual, el valor son los estados a los que puede pasar. ACTIVO es el
     * único estado "vivo"; VENDIDO/MUERTO/DESCARTADO son terminales (no
     * aparecen como clave => no tienen ninguna transición válida hacia
     * adelante).
     */
    private static final Map<EstadoAnimal, Set<EstadoAnimal>> TRANSICIONES_VALIDAS = Map.of(
            EstadoAnimal.ACTIVO, Set.of(EstadoAnimal.VENDIDO, EstadoAnimal.MUERTO, EstadoAnimal.DESCARTADO)
    );

    private final AnimalRepository animalRepository;
    private final RazaRepository razaRepository;
    private final UsuarioRepository usuarioRepository;
    private final AnimalMapper animalMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<AnimalResponse> listar(EstadoAnimal estado, Pageable pageable) {
        Page<Animal> pagina = (estado != null)
                ? animalRepository.findByEstado(estado, pageable)
                : animalRepository.findAll(pageable);
        return pagina.map(animalMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public AnimalResponse obtenerPorId(Long id) {
        return animalMapper.toResponse(buscarPorIdOLanzar(id));
    }

    @Override
    @Transactional
    public AnimalResponse crear(AnimalCreateRequest request) {
        validarAreteDisponible(request.getNumeroArete(), null);

        Raza raza = buscarRazaOLanzar(request.getRazaId());
        Usuario usuario = usuarioRepository.findById(request.getUsuarioId())
                .orElseThrow(() -> ResourceNotFoundException.of("Usuario", request.getUsuarioId()));

        Animal animal = animalMapper.toEntity(request);
        animal.setRaza(raza);
        animal.setUsuario(usuario);

        Animal guardado = animalRepository.save(animal);
        log.info("Animal creado: id={}, arete={}", guardado.getId(), guardado.getNumeroArete());
        return animalMapper.toResponse(guardado);
    }

    @Override
    @Transactional
    public AnimalResponse actualizar(Long id, AnimalUpdateRequest request) {
        Animal animal = buscarPorIdOLanzar(id);
        validarAreteDisponible(request.getNumeroArete(), id);

        Raza raza = buscarRazaOLanzar(request.getRazaId());

        animalMapper.updateEntityFromRequest(request, animal);
        animal.setRaza(raza);

        Animal actualizado = animalRepository.save(animal);
        log.info("Animal actualizado: id={}", actualizado.getId());
        return animalMapper.toResponse(actualizado);
    }

    @Override
    @Transactional
    public AnimalResponse cambiarEstado(Long id, AnimalCambiarEstadoRequest request) {
        Animal animal = buscarPorIdOLanzar(id);
        EstadoAnimal estadoActual = animal.getEstado();
        EstadoAnimal estadoNuevo = request.getNuevoEstado();

        Set<EstadoAnimal> transicionesPermitidas = TRANSICIONES_VALIDAS.getOrDefault(estadoActual, Set.of());
        if (!transicionesPermitidas.contains(estadoNuevo)) {
            throw new BusinessException(
                    "No se puede cambiar el estado del animal de %s a %s.".formatted(estadoActual, estadoNuevo));
        }

        animal.setEstado(estadoNuevo);
        if (request.getObservaciones() != null) {
            animal.setObservaciones(request.getObservaciones());
        }

        Animal actualizado = animalRepository.save(animal);
        log.info("Animal id={} cambió de estado {} -> {}", id, estadoActual, estadoNuevo);
        return animalMapper.toResponse(actualizado);
    }

    private Animal buscarPorIdOLanzar(Long id) {
        return animalRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Animal", id));
    }

    private Raza buscarRazaOLanzar(Long razaId) {
        return razaRepository.findById(razaId)
                .orElseThrow(() -> ResourceNotFoundException.of("Raza", razaId));
    }

    private void validarAreteDisponible(String numeroArete, Long excludeId) {
        animalRepository.findByNumeroArete(numeroArete).ifPresent(existente -> {
            if (!existente.getId().equals(excludeId)) {
                throw new BusinessException(
                        "Ya existe un animal registrado con el número de arete '%s'.".formatted(numeroArete));
            }
        });
    }
}
