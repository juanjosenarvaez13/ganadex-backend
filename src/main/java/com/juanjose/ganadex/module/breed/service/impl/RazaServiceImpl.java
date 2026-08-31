package com.juanjose.ganadex.module.breed.service.impl;

import com.juanjose.ganadex.common.exception.BusinessException;
import com.juanjose.ganadex.common.exception.ResourceNotFoundException;
import com.juanjose.ganadex.module.animal.repository.AnimalRepository;
import com.juanjose.ganadex.module.breed.dto.request.RazaRequest;
import com.juanjose.ganadex.module.breed.dto.response.RazaResponse;
import com.juanjose.ganadex.module.breed.entity.Raza;
import com.juanjose.ganadex.module.breed.mapper.RazaMapper;
import com.juanjose.ganadex.module.breed.repository.RazaRepository;
import com.juanjose.ganadex.module.breed.service.RazaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RazaServiceImpl implements RazaService {

    private final RazaRepository razaRepository;
    private final AnimalRepository animalRepository;
    private final RazaMapper razaMapper;

    @Override
    @Transactional(readOnly = true)
    public List<RazaResponse> listarTodas() {
        return razaRepository.findAll().stream()
                .map(razaMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public RazaResponse obtenerPorId(Long id) {
        return razaMapper.toResponse(buscarPorIdOLanzar(id));
    }

    @Override
    @Transactional
    public RazaResponse crear(RazaRequest request) {
        validarNombreDisponible(request.getNombre(), null);

        Raza raza = razaMapper.toEntity(request);
        Raza guardada = razaRepository.save(raza);

        log.info("Raza creada: id={}, nombre={}", guardada.getId(), guardada.getNombre());
        return razaMapper.toResponse(guardada);
    }

    @Override
    @Transactional
    public RazaResponse actualizar(Long id, RazaRequest request) {
        Raza raza = buscarPorIdOLanzar(id);
        validarNombreDisponible(request.getNombre(), id);

        razaMapper.updateEntityFromRequest(request, raza);
        Raza actualizada = razaRepository.save(raza);

        log.info("Raza actualizada: id={}", actualizada.getId());
        return razaMapper.toResponse(actualizada);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        Raza raza = buscarPorIdOLanzar(id);

        if (animalRepository.existsByRazaId(id)) {
            throw new BusinessException(
                    "No se puede eliminar la raza '%s' porque tiene animales asociados."
                            .formatted(raza.getNombre()));
        }

        razaRepository.delete(raza);
        log.info("Raza eliminada: id={}", id);
    }

    private Raza buscarPorIdOLanzar(Long id) {
        return razaRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Raza", id));
    }

    /**
     * Valida que el nombre no esté en uso por OTRA raza. En update, excluye
     * la propia raza que se está editando (excludeId) para no rechazar el
     * caso trivial de "guardar sin cambiar el nombre".
     */
    private void validarNombreDisponible(String nombre, Long excludeId) {
        razaRepository.findByNombre(nombre).ifPresent(existente -> {
            if (!existente.getId().equals(excludeId)) {
                throw new BusinessException(
                        "Ya existe una raza registrada con el nombre '%s'.".formatted(nombre));
            }
        });
    }
}
