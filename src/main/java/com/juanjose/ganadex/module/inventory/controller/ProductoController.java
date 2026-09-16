package com.juanjose.ganadex.module.inventory.controller;

import com.juanjose.ganadex.common.response.ApiResponse;
import com.juanjose.ganadex.module.inventory.dto.request.ProductoRequest;
import com.juanjose.ganadex.module.inventory.dto.response.ProductoResponse;
import com.juanjose.ganadex.module.inventory.service.ProductoService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/productos")
@RequiredArgsConstructor
@Tag(name = "Productos", description = "Catálogo de insumos/productos de inventario")
public class ProductoController {

    private final ProductoService productoService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductoResponse>>> listar() {
        return ResponseEntity.ok(ApiResponse.success(productoService.listarTodos()));
    }

    @GetMapping("/stock-bajo")
    public ResponseEntity<ApiResponse<List<ProductoResponse>>> listarConStockBajo() {
        return ResponseEntity.ok(ApiResponse.success(productoService.listarConStockBajo()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductoResponse>> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(productoService.obtenerPorId(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ProductoResponse>> crear(@Valid @RequestBody ProductoRequest request) {
        ProductoResponse creado = productoService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Producto creado correctamente.", creado));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductoResponse>> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ProductoRequest request) {
        ProductoResponse actualizado = productoService.actualizar(id, request);
        return ResponseEntity.ok(ApiResponse.success("Producto actualizado correctamente.", actualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id) {
        productoService.eliminar(id);
        return ResponseEntity.ok(ApiResponse.success("Producto eliminado correctamente."));
    }
}
