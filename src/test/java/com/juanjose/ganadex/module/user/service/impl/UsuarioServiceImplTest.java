package com.juanjose.ganadex.module.user.service.impl;

import com.juanjose.ganadex.common.exception.BusinessException;
import com.juanjose.ganadex.common.exception.ResourceNotFoundException;
import com.juanjose.ganadex.module.user.dto.request.CambiarPasswordRequest;
import com.juanjose.ganadex.module.user.dto.request.UsuarioCreateRequest;
import com.juanjose.ganadex.module.user.dto.request.UsuarioUpdateRequest;
import com.juanjose.ganadex.module.user.dto.response.UsuarioResponse;
import com.juanjose.ganadex.module.user.entity.Usuario;
import com.juanjose.ganadex.module.user.mapper.UsuarioMapper;
import com.juanjose.ganadex.module.user.repository.UsuarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceImplTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private UsuarioMapper usuarioMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioServiceImpl usuarioService;

    private Usuario usuario(Long id, String username, String email, boolean activo) {
        return Usuario.builder().id(id).nombre("Juan").apellido("Bernal")
                .username(username).email(email).password("hash-existente").activo(activo).build();
    }

    @Nested
    @DisplayName("crear")
    class Crear {

        @Test
        @DisplayName("caso feliz: debe hashear el password antes de guardar, nunca guardar el texto plano")
        void casoFeliz_debeHashearPassword() {
            UsuarioCreateRequest request = UsuarioCreateRequest.builder()
                    .nombre("Juan").apellido("Bernal").username("jbernal")
                    .password("claveSegura123").email("juan@ganadex.com").build();
            Usuario nuevo = Usuario.builder().username("jbernal").email("juan@ganadex.com").build();
            Usuario guardado = usuario(1L, "jbernal", "juan@ganadex.com", true);

            when(usuarioRepository.existsByUsername("jbernal")).thenReturn(false);
            when(usuarioRepository.existsByEmail("juan@ganadex.com")).thenReturn(false);
            when(usuarioMapper.toEntity(request)).thenReturn(nuevo);
            when(passwordEncoder.encode("claveSegura123")).thenReturn("$2a$10$hashSimulado");
            when(usuarioRepository.save(nuevo)).thenReturn(guardado);
            when(usuarioMapper.toResponse(guardado)).thenReturn(
                    UsuarioResponse.builder().id(1L).username("jbernal").build());

            UsuarioResponse resultado = usuarioService.crear(request);

            assertThat(resultado.getId()).isEqualTo(1L);
            assertThat(nuevo.getPassword()).isEqualTo("$2a$10$hashSimulado");
            verify(passwordEncoder).encode("claveSegura123");
            verify(passwordEncoder, never()).encode("$2a$10$hashSimulado");
        }

        @Test
        @DisplayName("con username duplicado debe lanzar BusinessException")
        void usernameDuplicado_debeLanzarBusinessException() {
            UsuarioCreateRequest request = UsuarioCreateRequest.builder()
                    .username("jbernal").email("otro@ganadex.com").password("claveSegura123")
                    .nombre("Juan").apellido("Bernal").build();
            when(usuarioRepository.existsByUsername("jbernal")).thenReturn(true);

            assertThatThrownBy(() -> usuarioService.crear(request)).isInstanceOf(BusinessException.class);
            verify(usuarioRepository, never()).save(any());
        }

        @Test
        @DisplayName("con email duplicado debe lanzar BusinessException")
        void emailDuplicado_debeLanzarBusinessException() {
            UsuarioCreateRequest request = UsuarioCreateRequest.builder()
                    .username("nuevo").email("juan@ganadex.com").password("claveSegura123")
                    .nombre("Juan").apellido("Bernal").build();
            when(usuarioRepository.existsByUsername("nuevo")).thenReturn(false);
            when(usuarioRepository.existsByEmail("juan@ganadex.com")).thenReturn(true);

            assertThatThrownBy(() -> usuarioService.crear(request)).isInstanceOf(BusinessException.class);
            verify(usuarioRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("actualizar")
    class Actualizar {

        @Test
        @DisplayName("con email en uso por otro usuario debe lanzar BusinessException")
        void emailEnUsoPorOtroUsuario_debeLanzarBusinessException() {
            Usuario existente = usuario(1L, "jbernal", "juan@ganadex.com", true);
            Usuario otro = usuario(2L, "otro", "ocupado@ganadex.com", true);
            UsuarioUpdateRequest request = UsuarioUpdateRequest.builder()
                    .nombre("Juan").apellido("Bernal").email("ocupado@ganadex.com").build();

            when(usuarioRepository.findById(1L)).thenReturn(Optional.of(existente));
            when(usuarioRepository.findByEmail("ocupado@ganadex.com")).thenReturn(Optional.of(otro));

            assertThatThrownBy(() -> usuarioService.actualizar(1L, request)).isInstanceOf(BusinessException.class);
            verify(usuarioRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("cambiarPassword")
    class CambiarPassword {

        @Test
        @DisplayName("con contraseña actual correcta debe actualizar el hash")
        void actualCorrecta_debeActualizar() {
            Usuario existente = usuario(1L, "jbernal", "juan@ganadex.com", true);
            CambiarPasswordRequest request = CambiarPasswordRequest.builder()
                    .passwordActual("actual123").passwordNueva("nuevaClave123").build();

            when(usuarioRepository.findById(1L)).thenReturn(Optional.of(existente));
            when(passwordEncoder.matches("actual123", "hash-existente")).thenReturn(true);
            when(passwordEncoder.encode("nuevaClave123")).thenReturn("$2a$10$nuevoHash");
            when(usuarioRepository.save(existente)).thenReturn(existente);

            usuarioService.cambiarPassword(1L, request);

            assertThat(existente.getPassword()).isEqualTo("$2a$10$nuevoHash");
        }

        @Test
        @DisplayName("con contraseña actual incorrecta debe lanzar BusinessException y no modificar nada")
        void actualIncorrecta_debeLanzarBusinessException() {
            Usuario existente = usuario(1L, "jbernal", "juan@ganadex.com", true);
            CambiarPasswordRequest request = CambiarPasswordRequest.builder()
                    .passwordActual("incorrecta").passwordNueva("nuevaClave123").build();

            when(usuarioRepository.findById(1L)).thenReturn(Optional.of(existente));
            when(passwordEncoder.matches("incorrecta", "hash-existente")).thenReturn(false);

            assertThatThrownBy(() -> usuarioService.cambiarPassword(1L, request))
                    .isInstanceOf(BusinessException.class);

            assertThat(existente.getPassword()).isEqualTo("hash-existente");
            verify(usuarioRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("desactivar / reactivar (soft delete)")
    class DesactivarReactivar {

        @Test
        @DisplayName("desactivar un usuario activo debe ponerlo inactivo")
        void desactivarUsuarioActivo_debeDesactivar() {
            Usuario existente = usuario(1L, "jbernal", "juan@ganadex.com", true);
            when(usuarioRepository.findById(1L)).thenReturn(Optional.of(existente));
            when(usuarioRepository.save(existente)).thenReturn(existente);
            when(usuarioMapper.toResponse(existente)).thenReturn(UsuarioResponse.builder().activo(false).build());

            usuarioService.desactivar(1L);

            assertThat(existente.isActivo()).isFalse();
        }

        @Test
        @DisplayName("desactivar un usuario ya inactivo debe lanzar BusinessException")
        void desactivarUsuarioYaInactivo_debeLanzarBusinessException() {
            Usuario existente = usuario(1L, "jbernal", "juan@ganadex.com", false);
            when(usuarioRepository.findById(1L)).thenReturn(Optional.of(existente));

            assertThatThrownBy(() -> usuarioService.desactivar(1L)).isInstanceOf(BusinessException.class);
            verify(usuarioRepository, never()).save(any());
        }

        @Test
        @DisplayName("reactivar un usuario inactivo debe ponerlo activo")
        void reactivarUsuarioInactivo_debeReactivar() {
            Usuario existente = usuario(1L, "jbernal", "juan@ganadex.com", false);
            when(usuarioRepository.findById(1L)).thenReturn(Optional.of(existente));
            when(usuarioRepository.save(existente)).thenReturn(existente);
            when(usuarioMapper.toResponse(existente)).thenReturn(UsuarioResponse.builder().activo(true).build());

            usuarioService.reactivar(1L);

            assertThat(existente.isActivo()).isTrue();
        }

        @Test
        @DisplayName("reactivar un usuario ya activo debe lanzar BusinessException")
        void reactivarUsuarioYaActivo_debeLanzarBusinessException() {
            Usuario existente = usuario(1L, "jbernal", "juan@ganadex.com", true);
            when(usuarioRepository.findById(1L)).thenReturn(Optional.of(existente));

            assertThatThrownBy(() -> usuarioService.reactivar(1L)).isInstanceOf(BusinessException.class);
            verify(usuarioRepository, never()).save(any());
        }
    }

    @Test
    @DisplayName("obtenerPorId con id inexistente debe lanzar ResourceNotFoundException")
    void obtenerPorId_conIdInexistente_debeLanzarResourceNotFoundException() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> usuarioService.obtenerPorId(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
