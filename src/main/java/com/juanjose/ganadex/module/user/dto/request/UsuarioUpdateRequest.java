package com.juanjose.ganadex.module.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Actualización de datos básicos de un usuario. Deliberadamente NO incluye
 * username ni password: el cambio de contraseña merece su propio endpoint
 * (con validación de contraseña actual), no un PUT genérico.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioUpdateRequest {

    @NotBlank(message = "El nombre es obligatorio.")
    @Size(max = 100)
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio.")
    @Size(max = 100)
    private String apellido;

    @NotBlank(message = "El email es obligatorio.")
    @Email(message = "El email no tiene un formato válido.")
    @Size(max = 150)
    private String email;
}
