package com.juanjose.ganadex.module.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Nunca incluye el password (ni el hash). Un Response no es un espejo de la
 * entidad: expone solo lo que un consumidor de la API debe ver.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioResponse {

    private Long id;
    private String nombre;
    private String apellido;
    private String username;
    private String email;
    private boolean activo;
    private LocalDateTime fechaCreacion;
}
