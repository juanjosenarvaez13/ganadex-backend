package com.juanjose.ganadex.security;

import com.juanjose.ganadex.common.exception.ResourceNotFoundException;
import com.juanjose.ganadex.module.user.entity.Usuario;
import com.juanjose.ganadex.module.user.repository.UsuarioRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

/**
 * Verifica el puente entre Spring Security y Usuario: el username lo lee del
 * SecurityContext (poblado por JwtAuthenticationFilter en producción), y
 * resuelve el Usuario real vía UsuarioRepository.
 */
@ExtendWith(MockitoExtension.class)
class AuthenticatedUserProviderTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private AuthenticatedUserProvider authenticatedUserProvider;

    @AfterEach
    void limpiarContextoDeSeguridad() {
        // Evita que la autenticación simulada de un test "se filtre" al siguiente.
        SecurityContextHolder.clearContext();
    }

    private void autenticarComo(String username) {
        var authentication = new UsernamePasswordAuthenticationToken(username, null, List.of());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    @DisplayName("con un usuario autenticado que existe en BD, debe retornarlo")
    void usuarioAutenticadoExistente_debeRetornarlo() {
        autenticarComo("jbernal");
        Usuario usuario = Usuario.builder().id(1L).username("jbernal").build();
        when(usuarioRepository.findByUsername("jbernal")).thenReturn(Optional.of(usuario));

        Usuario resultado = authenticatedUserProvider.obtenerUsuarioActual();

        assertThat(resultado.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("si el usuario autenticado ya no existe en BD, debe lanzar ResourceNotFoundException")
    void usuarioAutenticadoYaNoExiste_debeLanzarResourceNotFoundException() {
        autenticarComo("usuario-borrado");
        when(usuarioRepository.findByUsername("usuario-borrado")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authenticatedUserProvider.obtenerUsuarioActual())
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("usuario-borrado");
    }
}
