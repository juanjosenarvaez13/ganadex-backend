package com.juanjose.ganadex.security;

import com.juanjose.ganadex.common.exception.ResourceNotFoundException;
import com.juanjose.ganadex.module.user.entity.Usuario;
import com.juanjose.ganadex.module.user.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Reemplaza el patrón "usuarioId viene en el body del request" (el TODO que
 * dejamos en AnimalCreateRequest y MovimientoInventarioRequest desde el
 * paso 12). Con JWT, el usuario autenticado ya está disponible en el
 * SecurityContext de cada petición — no hay que confiar en lo que el
 * cliente diga que es su id.
 */
@Component
@RequiredArgsConstructor
public class AuthenticatedUserProvider {

    private final UsuarioRepository usuarioRepository;

    public Usuario obtenerUsuarioActual() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "El usuario autenticado ('%s') ya no existe en el sistema.".formatted(username)));
    }
}
