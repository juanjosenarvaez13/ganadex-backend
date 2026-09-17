package com.juanjose.ganadex.security;

import com.juanjose.ganadex.module.user.entity.Usuario;
import com.juanjose.ganadex.module.user.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * Spring Security no conoce la entidad Usuario — trabaja con su propia
 * interfaz UserDetails. Esta clase busca el Usuario real por username y lo
 * envuelve en un UserDetails. "disabled(!activo)" es lo que hace que un
 * usuario desactivado (paso 14) no pueda loguearse, aunque su contraseña
 * sea correcta.
 */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        return User.builder()
                .username(usuario.getUsername())
                .password(usuario.getPassword())
                .disabled(!usuario.isActivo())
                // Sin roles/permisos todavía — el MVP no distingue tipos de
                // usuario. Se agregaría acá el día que exista esa necesidad.
                .authorities(Collections.emptyList())
                .build();
    }
}
