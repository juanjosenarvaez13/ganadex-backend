package com.juanjose.ganadex.module.breed.repository;

import com.juanjose.ganadex.module.breed.entity.Raza;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RazaRepository extends JpaRepository<Raza, Long> {

    Optional<Raza> findByNombre(String nombre);

    boolean existsByNombre(String nombre);
}
