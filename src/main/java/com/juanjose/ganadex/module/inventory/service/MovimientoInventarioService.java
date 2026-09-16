package com.juanjose.ganadex.module.inventory.service;

import com.juanjose.ganadex.module.inventory.dto.request.MovimientoInventarioRequest;
import com.juanjose.ganadex.module.inventory.dto.response.MovimientoInventarioResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * No hay "actualizar" ni "eliminar": un movimiento de inventario es un
 * registro de auditoría inmutable (ver nota del paso 13). Cualquier
 * corrección se hace registrando un movimiento nuevo (típicamente AJUSTE),
 * nunca editando uno existente.
 */
public interface MovimientoInventarioService {

    MovimientoInventarioResponse registrar(MovimientoInventarioRequest request);

    Page<MovimientoInventarioResponse> listarPorProducto(Long productoId, Pageable pageable);

    Page<MovimientoInventarioResponse> listarPorUsuario(Long usuarioId, Pageable pageable);
}
