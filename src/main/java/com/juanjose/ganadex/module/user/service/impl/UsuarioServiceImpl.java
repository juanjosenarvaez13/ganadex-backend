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
import com.juanjose.ganadex.module.user.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioResponse> listar() {
        return usuarioRepository.findAll().stream()
                .map(usuarioMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioResponse obtenerPorId(Long id) {
        return usuarioMapper.toResponse(buscarPorIdOLanzar(id));
    }

    @Override
    @Transactional
    public UsuarioResponse crear(UsuarioCreateRequest request) {
        if (usuarioRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException(
                    "Ya existe un usuario registrado con el username '%s'.".formatted(request.getUsername()));
        }
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException(
                    "Ya existe un usuario registrado con el email '%s'.".formatted(request.getEmail()));
        }

        Usuario usuario = usuarioMapper.toEntity(request);
        // El mapper ignora "password" a propósito (paso 13): se hashea acá,
        // nunca se guarda el texto plano que llegó en el request.
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));

        Usuario guardado = usuarioRepository.save(usuario);
        log.info("Usuario creado: id={}, username={}", guardado.getId(), guardado.getUsername());
        return usuarioMapper.toResponse(guardado);
    }

    @Override
    @Transactional
    public UsuarioResponse actualizar(Long id, UsuarioUpdateRequest request) {
        Usuario usuario = buscarPorIdOLanzar(id);

        usuarioRepository.findByEmail(request.getEmail()).ifPresent(existente -> {
            if (!existente.getId().equals(id)) {
                throw new BusinessException(
                        "Ya existe un usuario registrado con el email '%s'.".formatted(request.getEmail()));
            }
        });

        usuarioMapper.updateEntityFromRequest(request, usuario);
        Usuario actualizado = usuarioRepository.save(usuario);

        log.info("Usuario actualizado: id={}", actualizado.getId());
        return usuarioMapper.toResponse(actualizado);
    }

    @Override
    @Transactional
    public void cambiarPassword(Long id, CambiarPasswordRequest request) {
        Usuario usuario = buscarPorIdOLanzar(id);

        if (!passwordEncoder.matches(request.getPasswordActual(), usuario.getPassword())) {
            throw new BusinessException("La contraseña actual no es correcta.");
        }

        usuario.setPassword(passwordEncoder.encode(request.getPasswordNueva()));
        usuarioRepository.save(usuario);
        log.info("Contraseña actualizada para usuario id={}", id);
    }

    @Override
    @Transactional
    public UsuarioResponse desactivar(Long id) {
        Usuario usuario = buscarPorIdOLanzar(id);
        if (!usuario.isActivo()) {
            throw new BusinessException("El usuario '%s' ya está inactivo.".formatted(usuario.getUsername()));
        }
        usuario.setActivo(false);
        Usuario actualizado = usuarioRepository.save(usuario);
        log.info("Usuario desactivado: id={}", id);
        return usuarioMapper.toResponse(actualizado);
    }

    @Override
    @Transactional
    public UsuarioResponse reactivar(Long id) {
        Usuario usuario = buscarPorIdOLanzar(id);
        if (usuario.isActivo()) {
            throw new BusinessException("El usuario '%s' ya está activo.".formatted(usuario.getUsername()));
        }
        usuario.setActivo(true);
        Usuario actualizado = usuarioRepository.save(usuario);
        log.info("Usuario reactivado: id={}", id);
        return usuarioMapper.toResponse(actualizado);
    }

    private Usuario buscarPorIdOLanzar(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Usuario", id));
    }
}
