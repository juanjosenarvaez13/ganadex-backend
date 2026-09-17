package com.juanjose.ganadex.module.auth.controller;

import com.juanjose.ganadex.common.exception.BusinessException;
import com.juanjose.ganadex.common.response.ApiResponse;
import com.juanjose.ganadex.module.auth.dto.request.LoginRequest;
import com.juanjose.ganadex.module.auth.dto.response.LoginResponse;
import com.juanjose.ganadex.security.JwtService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Único endpoint público sin excepción (ver SecurityConfig). No hay
 * "registro" público aquí a propósito: crear usuarios sigue siendo
 * responsabilidad de UsuarioController (POST /api/v1/usuarios) — separar
 * "gestionar cuentas" de "autenticarse" es intencional.
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "Login y emisión de JWT")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        } catch (BadCredentialsException | DisabledException e) {
            // Mismo mensaje para "no existe", "clave incorrecta" o "usuario
            // desactivado": no le damos a un atacante pistas de cuál de los
            // tres casos ocurrió (previene enumeración de usuarios).
            throw new BusinessException("Usuario o contraseña incorrectos.");
        }

        String token = jwtService.generarToken(authentication.getName());
        LoginResponse response = LoginResponse.builder()
                .token(token)
                .username(authentication.getName())
                .build();

        return ResponseEntity.ok(ApiResponse.success("Inicio de sesión exitoso.", response));
    }
}
