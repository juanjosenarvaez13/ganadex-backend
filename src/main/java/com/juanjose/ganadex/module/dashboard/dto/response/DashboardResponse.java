package com.juanjose.ganadex.module.dashboard.dto.response;

import java.util.List;

/**
 * Resumen agregado que alimenta la pantalla principal del sistema. Es de
 * solo lectura: no representa una tabla propia, solo agrupa conteos que ya
 * viven en Animal, Potrero y Producto.
 *
 * @param totalAnimalesActivos    animales con estado ACTIVO.
 * @param totalMachosActivos      subconjunto de los activos que son macho.
 * @param totalHembrasActivas     subconjunto de los activos que son hembra.
 * @param totalPotreros           total de potreros registrados (cualquier estado).
 * @param potrerosDisponibles     potreros en estado DISPONIBLE.
 * @param potrerosOcupados        potreros en estado OCUPADO.
 * @param potrerosEnMantenimiento potreros en estado MANTENIMIENTO.
 * @param totalProductos          total de productos registrados en inventario.
 * @param productosConStockBajo   productos cuyo stock actual ya llegó a su mínimo (o menos).
 */
public record DashboardResponse(
        long totalAnimalesActivos,
        long totalMachosActivos,
        long totalHembrasActivas,
        long totalPotreros,
        long potrerosDisponibles,
        long potrerosOcupados,
        long potrerosEnMantenimiento,
        long totalProductos,
        List<ProductoStockBajoResponse> productosConStockBajo
) {
}
