package com.juanjose.ganadex.module.user.controller;

import com.juanjose.ganadex.common.response.ApiResponse;
import com.juanjose.ganadex.module.user.dto.request.CambiarPasswordRequest;
import com.juanjose.ganadex.module.user.dto.request.UsuarioCreateRequest;
import com.juanjose.ganadex.module.user.dto.request.UsuarioUpdateRequest;
import com.juanjose.ganadex.module.user.dto.response.UsuarioResponse;
import com.juanjose.ganadex.module.user.service.UsuarioService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Gestión de usuarios (CRUD + activación). NO incluye login/token — eso es
 * el paso 9 (JWT), todavía pendiente.
 */
@RestController
@RequestMapping("/api/v1/usuarios")
@RequiredArgsConstructor
@Tag(name = "Usuarios", description = "Gestión de usuarios del sistema")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<UsuarioResponse>>> listar() {
        return ResponseEntity.ok(ApiResponse.success(usuarioService.listar()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UsuarioResponse>> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(usuarioService.obtenerPorId(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<UsuarioResponse>> crear(@Valid @RequestBody UsuarioCreateRequest request) {
        UsuarioResponse creado = usuarioService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Usuario creado correctamente.", creado));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UsuarioResponse>> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody UsuarioUpdateRequest request) {
        UsuarioResponse actualizado = usuarioService.actualizar(id, request);
        return ResponseEntity.ok(ApiResponse.success("Usuario actualizado correctamente.", actualizado));
    }

    @PatchMapping("/{id}/password")
    public ResponseEntity<ApiResponse<Void>> cambiarPassword(
            @PathVariable Long id,
            @Valid @RequestBody CambiarPasswordRequest request) {
        usuarioService.cambiarPassword(id, request);
        return ResponseEntity.ok(ApiResponse.success("Contraseña actualizada correctamente."));
    }

    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<ApiResponse<UsuarioResponse>> desactivar(@PathVariable Long id) {
        UsuarioResponse actualizado = usuarioService.desactivar(id);
        return ResponseEntity.ok(ApiResponse.success("Usuario desactivado correctamente.", actualizado));
    }

    @PatchMapping("/{id}/reactivar")
    public ResponseEntity<ApiResponse<UsuarioResponse>> reactivar(@PathVariable Long id) {
        UsuarioResponse actualizado = usuarioService.reactivar(id);
        return ResponseEntity.ok(ApiResponse.success("Usuario reactivado correctamente.", actualizado));
    }
}
