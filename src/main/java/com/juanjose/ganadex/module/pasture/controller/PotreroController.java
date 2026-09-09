package com.juanjose.ganadex.module.pasture.controller;

import com.juanjose.ganadex.common.response.ApiResponse;
import com.juanjose.ganadex.module.pasture.dto.request.PotreroCambiarEstadoRequest;
import com.juanjose.ganadex.module.pasture.dto.request.PotreroRequest;
import com.juanjose.ganadex.module.pasture.dto.response.PotreroResponse;
import com.juanjose.ganadex.module.pasture.service.PotreroService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/potreros")
@RequiredArgsConstructor
@Tag(name = "Potreros", description = "Catálogo de potreros de la finca")
public class PotreroController {

    private final PotreroService potreroService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<PotreroResponse>>> listar() {
        return ResponseEntity.ok(ApiResponse.success(potreroService.listarTodos()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PotreroResponse>> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(potreroService.obtenerPorId(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PotreroResponse>> crear(@Valid @RequestBody PotreroRequest request) {
        PotreroResponse creado = potreroService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Potrero creado correctamente.", creado));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PotreroResponse>> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody PotreroRequest request) {
        PotreroResponse actualizado = potreroService.actualizar(id, request);
        return ResponseEntity.ok(ApiResponse.success("Potrero actualizado correctamente.", actualizado));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<ApiResponse<PotreroResponse>> cambiarEstado(
            @PathVariable Long id,
            @Valid @RequestBody PotreroCambiarEstadoRequest request) {
        PotreroResponse actualizado = potreroService.cambiarEstado(id, request);
        return ResponseEntity.ok(ApiResponse.success("Estado del potrero actualizado correctamente.", actualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id) {
        potreroService.eliminar(id);
        return ResponseEntity.ok(ApiResponse.success("Potrero eliminado correctamente."));
    }
}
