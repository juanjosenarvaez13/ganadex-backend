package com.juanjose.ganadex.module.animal.repository;

import com.juanjose.ganadex.module.animal.entity.Animal;
import com.juanjose.ganadex.module.animal.entity.EstadoAnimal;
import com.juanjose.ganadex.module.animal.entity.SexoAnimal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AnimalRepository extends JpaRepository<Animal, Long> {

    Optional<Animal> findByNumeroArete(String numeroArete);

    boolean existsByNumeroArete(String numeroArete);

    Page<Animal> findByEstado(EstadoAnimal estado, Pageable pageable);

    Page<Animal> findByRazaId(Long razaId, Pageable pageable);

    boolean existsByRazaId(Long razaId);

    /** Conteos usados por el dashboard — se resuelven con COUNT en la BD, no trayendo filas. */
    long countByEstado(EstadoAnimal estado);

    long countByEstadoAndSexo(EstadoAnimal estado, SexoAnimal sexo);
}
