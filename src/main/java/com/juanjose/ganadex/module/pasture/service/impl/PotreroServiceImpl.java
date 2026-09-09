package com.juanjose.ganadex.module.pasture.service.impl;

import com.juanjose.ganadex.common.exception.BusinessException;
import com.juanjose.ganadex.common.exception.ResourceNotFoundException;
import com.juanjose.ganadex.module.pasture.dto.request.PotreroCambiarEstadoRequest;
import com.juanjose.ganadex.module.pasture.dto.request.PotreroRequest;
import com.juanjose.ganadex.module.pasture.dto.response.PotreroResponse;
import com.juanjose.ganadex.module.pasture.entity.EstadoPotrero;
import com.juanjose.ganadex.module.pasture.entity.Potrero;
import com.juanjose.ganadex.module.pasture.mapper.PotreroMapper;
import com.juanjose.ganadex.module.pasture.repository.HistorialPotreroRepository;
import com.juanjose.ganadex.module.pasture.repository.PotreroRepository;
import com.juanjose.ganadex.module.pasture.service.PotreroService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PotreroServiceImpl implements PotreroService {

    private final PotreroRepository potreroRepository;
    private final HistorialPotreroRepository historialPotreroRepository;
    private final PotreroMapper potreroMapper;

    @Override
    @Transactional(readOnly = true)
    public List<PotreroResponse> listarTodos() {
        return potreroRepository.findAll().stream()
                .map(potreroMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PotreroResponse obtenerPorId(Long id) {
        return potreroMapper.toResponse(buscarPorIdOLanzar(id));
    }

    @Override
    @Transactional
    public PotreroResponse crear(PotreroRequest request) {
        validarNombreDisponible(request.getNombre(), null);

        Potrero potrero = potreroMapper.toEntity(request);
        Potrero guardado = potreroRepository.save(potrero);

        log.info("Potrero creado: id={}, nombre={}", guardado.getId(), guardado.getNombre());
        return potreroMapper.toResponse(guardado);
    }

    @Override
    @Transactional
    public PotreroResponse actualizar(Long id, PotreroRequest request) {
        Potrero potrero = buscarPorIdOLanzar(id);
        validarNombreDisponible(request.getNombre(), id);

        potreroMapper.updateEntityFromRequest(request, potrero);
        Potrero actualizado = potreroRepository.save(potrero);

        log.info("Potrero actualizado: id={}", actualizado.getId());
        return potreroMapper.toResponse(actualizado);
    }

    @Override
    @Transactional
    public PotreroResponse cambiarEstado(Long id, PotreroCambiarEstadoRequest request) {
        Potrero potrero = buscarPorIdOLanzar(id);
        EstadoPotrero nuevoEstado = request.getNuevoEstado();

        if (nuevoEstado == EstadoPotrero.OCUPADO) {
            throw new BusinessException(
                    "El estado OCUPADO se asigna automáticamente cuando un animal entra al potrero; "
                            + "no se puede establecer manualmente.");
        }

        boolean tieneAnimalesActualmente = !historialPotreroRepository
                .findByPotreroIdAndFechaSalidaIsNull(id).isEmpty();
        if (tieneAnimalesActualmente) {
            throw new BusinessException(
                    "No se puede cambiar el estado del potrero '%s' porque actualmente tiene animales. "
                            + "Registra su salida primero.".formatted(potrero.getNombre()));
        }

        potrero.setEstado(nuevoEstado);
        Potrero actualizado = potreroRepository.save(potrero);

        log.info("Potrero id={} cambió de estado a {}", id, nuevoEstado);
        return potreroMapper.toResponse(actualizado);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        Potrero potrero = buscarPorIdOLanzar(id);

        if (historialPotreroRepository.existsByPotreroId(id)) {
            throw new BusinessException(
                    "No se puede eliminar el potrero '%s' porque tiene historial de ocupación asociado."
                            .formatted(potrero.getNombre()));
        }

        potreroRepository.delete(potrero);
        log.info("Potrero eliminado: id={}", id);
    }

    private Potrero buscarPorIdOLanzar(Long id) {
        return potreroRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Potrero", id));
    }

    private void validarNombreDisponible(String nombre, Long excludeId) {
        potreroRepository.findByNombre(nombre).ifPresent(existente -> {
            if (!existente.getId().equals(excludeId)) {
                throw new BusinessException(
                        "Ya existe un potrero registrado con el nombre '%s'.".formatted(nombre));
            }
        });
    }
}
