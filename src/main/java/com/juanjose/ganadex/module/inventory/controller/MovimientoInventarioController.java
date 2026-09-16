package com.juanjose.ganadex.module.inventory.controller;

import com.juanjose.ganadex.common.response.ApiResponse;
import com.juanjose.ganadex.module.inventory.dto.request.MovimientoInventarioRequest;
import com.juanjose.ganadex.module.inventory.dto.response.MovimientoInventarioResponse;
import com.juanjose.ganadex.module.inventory.service.MovimientoInventarioService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Solo POST para registrar y GET para consultar — no hay PUT/DELETE, porque
 * un movimiento de inventario es un registro de auditoría inmutable.
 */
@RestController
@RequestMapping("/api/v1/movimientos-inventario")
@RequiredArgsConstructor
@Tag(name = "Movimientos de Inventario", description = "Entradas, salidas y ajustes de stock")
public class MovimientoInventarioController {

    private final MovimientoInventarioService movimientoInventarioService;

    @PostMapping
    public ResponseEntity<ApiResponse<MovimientoInventarioResponse>> registrar(
            @Valid @RequestBody MovimientoInventarioRequest request) {
        MovimientoInventarioResponse creado = movimientoInventarioService.registrar(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Movimiento de inventario registrado correctamente.", creado));
    }

    @GetMapping("/productos/{productoId}")
    public ResponseEntity<ApiResponse<Page<MovimientoInventarioResponse>>> listarPorProducto(
            @PathVariable Long productoId,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(
                ApiResponse.success(movimientoInventarioService.listarPorProducto(productoId, pageable)));
    }

    @GetMapping("/usuarios/{usuarioId}")
    public ResponseEntity<ApiResponse<Page<MovimientoInventarioResponse>>> listarPorUsuario(
            @PathVariable Long usuarioId,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(
                ApiResponse.success(movimientoInventarioService.listarPorUsuario(usuarioId, pageable)));
    }
}
