package com.juanjose.ganadex.module.animal.controller;

import com.juanjose.ganadex.common.response.ApiResponse;
import com.juanjose.ganadex.module.animal.dto.request.AnimalCambiarEstadoRequest;
import com.juanjose.ganadex.module.animal.dto.request.AnimalCreateRequest;
import com.juanjose.ganadex.module.animal.dto.request.AnimalUpdateRequest;
import com.juanjose.ganadex.module.animal.dto.response.AnimalResponse;
import com.juanjose.ganadex.module.animal.entity.EstadoAnimal;
import com.juanjose.ganadex.module.animal.service.AnimalService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/animales")
@RequiredArgsConstructor
@Tag(name = "Animales", description = "Gestión de los animales de la finca")
public class AnimalController {

    private final AnimalService animalService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<AnimalResponse>>> listar(
            @RequestParam(required = false) EstadoAnimal estado,
            @PageableDefault(size = 20, sort = "numeroArete") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(animalService.listar(estado, pageable)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AnimalResponse>> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(animalService.obtenerPorId(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AnimalResponse>> crear(@Valid @RequestBody AnimalCreateRequest request) {
        AnimalResponse creado = animalService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Animal registrado correctamente.", creado));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AnimalResponse>> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody AnimalUpdateRequest request) {
        AnimalResponse actualizado = animalService.actualizar(id, request);
        return ResponseEntity.ok(ApiResponse.success("Animal actualizado correctamente.", actualizado));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<ApiResponse<AnimalResponse>> cambiarEstado(
            @PathVariable Long id,
            @Valid @RequestBody AnimalCambiarEstadoRequest request) {
        AnimalResponse actualizado = animalService.cambiarEstado(id, request);
        return ResponseEntity.ok(ApiResponse.success("Estado del animal actualizado correctamente.", actualizado));
    }
}
