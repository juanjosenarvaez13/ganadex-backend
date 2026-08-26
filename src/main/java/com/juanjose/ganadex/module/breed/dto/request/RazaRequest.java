package com.juanjose.ganadex.module.breed.dto.request;

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
public class RazaRequest {

    @NotBlank(message = "El nombre de la raza es obligatorio.")
    @Size(max = 100, message = "El nombre no puede superar los 100 caracteres.")
    private String nombre;

    @Size(max = 2000, message = "La descripción no puede superar los 2000 caracteres.")
    private String descripcion;
}
