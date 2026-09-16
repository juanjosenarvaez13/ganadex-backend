package com.juanjose.ganadex.module.inventory.service;

import com.juanjose.ganadex.module.inventory.dto.request.ProductoRequest;
import com.juanjose.ganadex.module.inventory.dto.response.ProductoResponse;

import java.util.List;

public interface ProductoService {

    List<ProductoResponse> listarTodos();

    /** Productos con stockActual <= stockMinimo — para el dashboard/alertas. */
    List<ProductoResponse> listarConStockBajo();

    ProductoResponse obtenerPorId(Long id);

    ProductoResponse crear(ProductoRequest request);

    ProductoResponse actualizar(Long id, ProductoRequest request);

    void eliminar(Long id);
}
