package com.juanjose.ganadex.module.pasture.repository;

import com.juanjose.ganadex.module.pasture.entity.EstadoPotrero;
import com.juanjose.ganadex.module.pasture.entity.Potrero;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PotreroRepository extends JpaRepository<Potrero, Long> {

    Optional<Potrero> findByNombre(String nombre);

    boolean existsByNombre(String nombre);

    List<Potrero> findByEstado(EstadoPotrero estado);
}
