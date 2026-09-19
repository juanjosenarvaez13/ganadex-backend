package com.juanjose.ganadex.module.dashboard.controller;

import com.juanjose.ganadex.common.response.ApiResponse;
import com.juanjose.ganadex.module.dashboard.dto.response.DashboardResponse;
import com.juanjose.ganadex.module.dashboard.service.DashboardService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Expone el resumen agregado que alimenta la pantalla principal. Es de solo
 * lectura (GET único) — no tiene create/update/delete porque no gestiona su
 * propia entidad.
 */
@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "Resumen agregado de animales, potreros e inventario")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/resumen")
    public ResponseEntity<ApiResponse<DashboardResponse>> obtenerResumen() {
        return ResponseEntity.ok(ApiResponse.success(dashboardService.obtenerResumen()));
    }
}
