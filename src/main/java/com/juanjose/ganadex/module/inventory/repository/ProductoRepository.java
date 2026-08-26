package com.juanjose.ganadex.module.inventory.repository;

import com.juanjose.ganadex.module.inventory.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    Optional<Producto> findByNombre(String nombre);

    boolean existsByNombre(String nombre);

    /** Productos cuyo stock actual ya llegó (o está por debajo de) su mínimo — para alertas del dashboard. */
    @Query("SELECT p FROM Producto p WHERE p.stockActual <= p.stockMinimo")
    List<Producto> findConStockBajoMinimo();
}
