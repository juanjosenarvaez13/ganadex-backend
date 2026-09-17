package com.juanjose.ganadex.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.juanjose.ganadex.common.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Sin esto, un 401 de Spring Security devuelve un body genérico que no
 * respeta el contrato {success, message, data} del resto de la API. Este
 * componente intercepta ese caso y lo devuelve con el mismo formato.
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException) throws IOException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        ApiResponse<Void> body = ApiResponse.error(
                "No autenticado. Debes iniciar sesión (POST /api/v1/auth/login) para acceder a este recurso.");
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}
