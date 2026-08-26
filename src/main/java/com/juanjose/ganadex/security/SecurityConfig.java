package com.juanjose.ganadex.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuración de Spring Security.
 * <p>
 * ⚠️ TEMPORAL — Paso 6 del plan (Swagger) vs. Paso 9 (JWT).
 * Mientras no exista autenticación JWT, esta clase deja todos los endpoints
 * abiertos ({@code permitAll()}). Sin esto, Spring Security protegería todo
 * automáticamente con Basic Auth y una contraseña generada en el log, lo que
 * bloquearía el acceso a Swagger UI y a cualquier endpoint que construyamos
 * antes de tener JWT.
 * <p>
 * Cuando implementemos el módulo de autenticación (paso 9), esta clase se
 * reemplaza por reglas reales: rutas públicas ({@code /auth/**},
 * {@code /swagger-ui/**}, {@code /v3/api-docs/**}) vs. rutas protegidas,
 * más el filtro que valida el JWT en cada petición.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());

        return http.build();
    }
}
