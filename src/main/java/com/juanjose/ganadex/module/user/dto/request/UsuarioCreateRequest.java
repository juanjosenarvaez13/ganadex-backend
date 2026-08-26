package com.juanjose.ganadex.module.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioCreateRequest {

    @NotBlank(message = "El nombre es obligatorio.")
    @Size(max = 100)
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio.")
    @Size(max = 100)
    private String apellido;

    @NotBlank(message = "El username es obligatorio.")
    @Size(min = 4, max = 50, message = "El username debe tener entre 4 y 50 caracteres.")
    private String username;

    // Se valida en texto plano; el hash (BCrypt) se genera en el Service,
    // nunca en el DTO ni en el Controller.
    @NotBlank(message = "La contraseña es obligatoria.")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres.")
    private String password;

    @NotBlank(message = "El email es obligatorio.")
    @Email(message = "El email no tiene un formato válido.")
    @Size(max = 150)
    private String email;
}
