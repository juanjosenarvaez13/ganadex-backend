package com.juanjose.ganadex.module.pasture.service.impl;

import com.juanjose.ganadex.common.exception.BusinessException;
import com.juanjose.ganadex.common.exception.ResourceNotFoundException;
import com.juanjose.ganadex.module.animal.entity.Animal;
import com.juanjose.ganadex.module.animal.repository.AnimalRepository;
import com.juanjose.ganadex.module.pasture.dto.request.RegistrarEntradaPotreroRequest;
import com.juanjose.ganadex.module.pasture.dto.request.RegistrarSalidaPotreroRequest;
import com.juanjose.ganadex.module.pasture.dto.response.HistorialPotreroResponse;
import com.juanjose.ganadex.module.pasture.entity.EstadoPotrero;
import com.juanjose.ganadex.module.pasture.entity.HistorialPotrero;
import com.juanjose.ganadex.module.pasture.entity.Potrero;
import com.juanjose.ganadex.module.pasture.mapper.HistorialPotreroMapper;
import com.juanjose.ganadex.module.pasture.repository.HistorialPotreroRepository;
import com.juanjose.ganadex.module.pasture.repository.PotreroRepository;
import com.juanjose.ganadex.module.pasture.service.HistorialPotreroService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class HistorialPotreroServiceImpl implements HistorialPotreroService {

    private final HistorialPotreroRepository historialPotreroRepository;
    private final AnimalRepository animalRepository;
    private final PotreroRepository potreroRepository;
    private final HistorialPotreroMapper historialPotreroMapper;

    @Override
    @Transactional
    public HistorialPotreroResponse registrarEntrada(RegistrarEntradaPotreroRequest request) {
        Animal animal = animalRepository.findById(request.getAnimalId())
                .orElseThrow(() -> ResourceNotFoundException.of("Animal", request.getAnimalId()));
        Potrero potrero = potreroRepository.findById(request.getPotreroId())
                .orElseThrow(() -> ResourceNotFoundException.of("Potrero", request.getPotreroId()));

        // Chequeo anticipado y amigable. La garantía real e infalible es el
        // índice único parcial ux_historial_potreros_animal_abierto en la
        // BD (V1__initial_schema.sql): aunque hubiera una condición de
        // carrera entre este chequeo y el INSERT, la base de datos igual
        // rechazaría un segundo registro abierto para el mismo animal.
        historialPotreroRepository.findByAnimalIdAndFechaSalidaIsNull(animal.getId())
                .ifPresent(abierto -> {
                    throw new BusinessException(
                            "El animal '%s' ya se encuentra en el potrero '%s'. Registra su salida antes de moverlo."
                                    .formatted(animal.getNumeroArete(), abierto.getPotrero().getNombre()));
                });

        HistorialPotrero historial = HistorialPotrero.builder()
                .animal(animal)
                .potrero(potrero)
                .fechaEntrada(request.getFechaEntrada())
                .observaciones(request.getObservaciones())
                .build();

        HistorialPotrero guardado = historialPotreroRepository.save(historial);

        potrero.setEstado(EstadoPotrero.OCUPADO);
        potreroRepository.save(potrero);

        log.info("Entrada registrada: animal={}, potrero={}, fechaEntrada={}",
                animal.getNumeroArete(), potrero.getNombre(), request.getFechaEntrada());
        return historialPotreroMapper.toResponse(guardado);
    }

    @Override
    @Transactional
    public HistorialPotreroResponse registrarSalida(Long animalId, RegistrarSalidaPotreroRequest request) {
        HistorialPotrero historial = historialPotreroRepository
                .findByAnimalIdAndFechaSalidaIsNull(animalId)
                .orElseThrow(() -> new BusinessException(
                        "El animal no tiene una ocupación de potrero abierta actualmente."));

        if (request.getFechaSalida().isBefore(historial.getFechaEntrada())) {
            throw new BusinessException(
                    "La fecha de salida no puede ser anterior a la fecha de entrada ("
                            + historial.getFechaEntrada() + ").");
        }

        historial.setFechaSalida(request.getFechaSalida());
        if (request.getObservaciones() != null) {
            historial.setObservaciones(request.getObservaciones());
        }
        HistorialPotrero actualizado = historialPotreroRepository.save(historial);

        // Si ya no queda ningún animal en ese potrero, vuelve a DISPONIBLE.
        Potrero potrero = historial.getPotrero();
        boolean sigueOcupado = !historialPotreroRepository
                .findByPotreroIdAndFechaSalidaIsNull(potrero.getId()).isEmpty();
        if (!sigueOcupado) {
            potrero.setEstado(EstadoPotrero.DISPONIBLE);
            potreroRepository.save(potrero);
        }

        log.info("Salida registrada: animalId={}, potrero={}, fechaSalida={}",
                animalId, potrero.getNombre(), request.getFechaSalida());
        return historialPotreroMapper.toResponse(actualizado);
    }

    @Override
    @Transactional(readOnly = true)
    public HistorialPotreroResponse potreroActualDeAnimal(Long animalId) {
        HistorialPotrero historial = historialPotreroRepository
                .findByAnimalIdAndFechaSalidaIsNull(animalId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "El animal con id %d no está asignado a ningún potrero actualmente.".formatted(animalId)));
        return historialPotreroMapper.toResponse(historial);
    }

    @Override
    @Transactional(readOnly = true)
    public List<HistorialPotreroResponse> historialDeAnimal(Long animalId) {
        return historialPotreroRepository.findByAnimalIdOrderByFechaEntradaDesc(animalId).stream()
                .map(historialPotreroMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<HistorialPotreroResponse> historialDePotrero(Long potreroId) {
        return historialPotreroRepository.findByPotreroIdOrderByFechaEntradaDesc(potreroId).stream()
                .map(historialPotreroMapper::toResponse)
                .toList();
    }
}
