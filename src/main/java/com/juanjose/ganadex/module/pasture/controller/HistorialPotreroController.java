package com.juanjose.ganadex.module.pasture.controller;

import com.juanjose.ganadex.common.response.ApiResponse;
import com.juanjose.ganadex.module.pasture.dto.request.RegistrarEntradaPotreroRequest;
import com.juanjose.ganadex.module.pasture.dto.request.RegistrarSalidaPotreroRequest;
import com.juanjose.ganadex.module.pasture.dto.response.HistorialPotreroResponse;
import com.juanjose.ganadex.module.pasture.service.HistorialPotreroService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Acciones de negocio sobre la ocupación de potreros (entrar/salir), más
 * las consultas de historial. No es un CRUD genérico: no hay PUT/DELETE
 * sobre un registro de historial — ver la nota de "registros inmutables"
 * del paso 13.
 */
@RestController
@RequestMapping("/api/v1/historial-potreros")
@RequiredArgsConstructor
@Tag(name = "Historial de Potreros", description = "Entradas/salidas de animales a potreros")
public class HistorialPotreroController {

    private final HistorialPotreroService historialPotreroService;

    @PostMapping("/entradas")
    public ResponseEntity<ApiResponse<HistorialPotreroResponse>> registrarEntrada(
            @Valid @RequestBody RegistrarEntradaPotreroRequest request) {
        HistorialPotreroResponse creado = historialPotreroService.registrarEntrada(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Entrada al potrero registrada correctamente.", creado));
    }

    @PostMapping("/animales/{animalId}/salida")
    public ResponseEntity<ApiResponse<HistorialPotreroResponse>> registrarSalida(
            @PathVariable Long animalId,
            @Valid @RequestBody RegistrarSalidaPotreroRequest request) {
        HistorialPotreroResponse actualizado = historialPotreroService.registrarSalida(animalId, request);
        return ResponseEntity.ok(ApiResponse.success("Salida del potrero registrada correctamente.", actualizado));
    }

    @GetMapping("/animales/{animalId}/actual")
    public ResponseEntity<ApiResponse<HistorialPotreroResponse>> potreroActualDeAnimal(
            @PathVariable Long animalId) {
        return ResponseEntity.ok(ApiResponse.success(historialPotreroService.potreroActualDeAnimal(animalId)));
    }

    @GetMapping("/animales/{animalId}")
    public ResponseEntity<ApiResponse<List<HistorialPotreroResponse>>> historialDeAnimal(
            @PathVariable Long animalId) {
        return ResponseEntity.ok(ApiResponse.success(historialPotreroService.historialDeAnimal(animalId)));
    }

    @GetMapping("/potreros/{potreroId}")
    public ResponseEntity<ApiResponse<List<HistorialPotreroResponse>>> historialDePotrero(
            @PathVariable Long potreroId) {
        return ResponseEntity.ok(ApiResponse.success(historialPotreroService.historialDePotrero(potreroId)));
    }
}
