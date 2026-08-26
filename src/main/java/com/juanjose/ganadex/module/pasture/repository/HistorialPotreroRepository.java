package com.juanjose.ganadex.module.pasture.repository;

import com.juanjose.ganadex.module.pasture.entity.HistorialPotrero;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HistorialPotreroRepository extends JpaRepository<HistorialPotrero, Long> {

    /**
     * El registro "abierto" (fecha_salida IS NULL) de un animal, es decir,
     * su ocupación de potrero actual. La BD garantiza (índice único parcial
     * ux_historial_potreros_animal_abierto) que nunca hay más de uno.
     */
    Optional<HistorialPotrero> findByAnimalIdAndFechaSalidaIsNull(Long animalId);

    /** Historial completo (abierto + cerrados) de un animal, más reciente primero. */
    List<HistorialPotrero> findByAnimalIdOrderByFechaEntradaDesc(Long animalId);

    /** Animales actualmente ocupando un potrero determinado. */
    List<HistorialPotrero> findByPotreroIdAndFechaSalidaIsNull(Long potreroId);

    /** Historial completo de ocupación de un potrero. */
    List<HistorialPotrero> findByPotreroIdOrderByFechaEntradaDesc(Long potreroId);
}
