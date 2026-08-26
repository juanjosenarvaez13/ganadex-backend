package com.juanjose.ganadex.module.animal.dto.request;

import com.juanjose.ganadex.module.animal.entity.SexoAnimal;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnimalCreateRequest {

    @NotBlank(message = "El número de arete es obligatorio.")
    @Size(max = 50)
    private String numeroArete;

    @Size(max = 100)
    private String nombre;

    @NotNull(message = "El sexo es obligatorio.")
    private SexoAnimal sexo;

    @PastOrPresent(message = "La fecha de nacimiento no puede ser futura.")
    private LocalDate fechaNacimiento;

    @Positive(message = "El peso debe ser mayor a cero.")
    private BigDecimal pesoActual;

    @Size(max = 50)
    private String color;

    private String observaciones;

    @Size(max = 255)
    private String foto;

    @NotNull(message = "La raza es obligatoria.")
    private Long razaId;

    // TODO (paso 9 - JWT): una vez exista autenticación, este campo se
    // elimina del DTO y el usuario se toma del SecurityContext, no del body.
    @NotNull(message = "El usuario es obligatorio.")
    private Long usuarioId;
}
