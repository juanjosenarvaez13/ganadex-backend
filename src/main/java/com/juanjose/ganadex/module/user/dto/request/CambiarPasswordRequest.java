package com.juanjose.ganadex.module.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Exige la contraseña actual para confirmar identidad antes de aceptar la
 * nueva — nunca se cambia una contraseña "a ciegas" solo por conocer el id.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CambiarPasswordRequest {

    @NotBlank(message = "La contraseña actual es obligatoria.")
    private String passwordActual;

    @NotBlank(message = "La nueva contraseña es obligatoria.")
    @Size(min = 8, message = "La nueva contraseña debe tener al menos 8 caracteres.")
    private String passwordNueva;
}
