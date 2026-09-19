package com.juanjose.ganadex.module.dashboard.service.impl;

import com.juanjose.ganadex.module.animal.entity.EstadoAnimal;
import com.juanjose.ganadex.module.animal.entity.SexoAnimal;
import com.juanjose.ganadex.module.animal.repository.AnimalRepository;
import com.juanjose.ganadex.module.dashboard.dto.response.DashboardResponse;
import com.juanjose.ganadex.module.dashboard.dto.response.ProductoStockBajoResponse;
import com.juanjose.ganadex.module.dashboard.service.DashboardService;
import com.juanjose.ganadex.module.inventory.entity.Producto;
import com.juanjose.ganadex.module.inventory.repository.ProductoRepository;
import com.juanjose.ganadex.module.pasture.entity.EstadoPotrero;
import com.juanjose.ganadex.module.pasture.repository.PotreroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Solo lectura y agregación: este service no tiene entidad ni tabla propia,
 * simplemente combina conteos que ya existen en Animal, Potrero y Producto.
 * Ninguna regla de negocio nueva vive aquí.
 */
@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final AnimalRepository animalRepository;
    private final PotreroRepository potreroRepository;
    private final ProductoRepository productoRepository;

    @Override
    @Transactional(readOnly = true)
    public DashboardResponse obtenerResumen() {
        long totalAnimalesActivos = animalRepository.countByEstado(EstadoAnimal.ACTIVO);
        long totalMachosActivos = animalRepository.countByEstadoAndSexo(EstadoAnimal.ACTIVO, SexoAnimal.MACHO);
        long totalHembrasActivas = animalRepository.countByEstadoAndSexo(EstadoAnimal.ACTIVO, SexoAnimal.HEMBRA);

        long totalPotreros = potreroRepository.count();
        long potrerosDisponibles = potreroRepository.countByEstado(EstadoPotrero.DISPONIBLE);
        long potrerosOcupados = potreroRepository.countByEstado(EstadoPotrero.OCUPADO);
        long potrerosEnMantenimiento = potreroRepository.countByEstado(EstadoPotrero.MANTENIMIENTO);

        long totalProductos = productoRepository.count();
        List<ProductoStockBajoResponse> productosConStockBajo = productoRepository.findConStockBajoMinimo()
                .stream()
                .map(this::toStockBajoResponse)
                .toList();

        return new DashboardResponse(
                totalAnimalesActivos,
                totalMachosActivos,
                totalHembrasActivas,
                totalPotreros,
                potrerosDisponibles,
                potrerosOcupados,
                potrerosEnMantenimiento,
                totalProductos,
                productosConStockBajo
        );
    }

    private ProductoStockBajoResponse toStockBajoResponse(Producto producto) {
        return new ProductoStockBajoResponse(
                producto.getId(),
                producto.getNombre(),
                producto.getStockActual(),
                producto.getStockMinimo(),
                producto.getUnidadMedida()
        );
    }
}
