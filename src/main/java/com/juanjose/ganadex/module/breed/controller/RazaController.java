package com.juanjose.ganadex.module.breed.controller;

import com.juanjose.ganadex.common.response.ApiResponse;
import com.juanjose.ganadex.module.breed.dto.request.RazaRequest;
import com.juanjose.ganadex.module.breed.dto.response.RazaResponse;
import com.juanjose.ganadex.module.breed.service.RazaService;
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

/**
 * Controller = solo recibe/valida entrada y devuelve respuesta. Ninguna
 * regla de negocio vive aquí — todo eso está en {@link RazaService}.
 */
@RestController
@RequestMapping("/api/v1/razas")
@RequiredArgsConstructor
@Tag(name = "Razas", description = "Catálogo de razas de ganado")
public class RazaController {

    private final RazaService razaService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<RazaResponse>>> listar() {
        return ResponseEntity.ok(ApiResponse.success(razaService.listarTodas()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RazaResponse>> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(razaService.obtenerPorId(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<RazaResponse>> crear(@Valid @RequestBody RazaRequest request) {
        RazaResponse creada = razaService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Raza creada correctamente.", creada));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<RazaResponse>> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody RazaRequest request) {
        RazaResponse actualizada = razaService.actualizar(id, request);
        return ResponseEntity.ok(ApiResponse.success("Raza actualizada correctamente.", actualizada));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id) {
        razaService.eliminar(id);
        return ResponseEntity.ok(ApiResponse.success("Raza eliminada correctamente."));
    }
}
