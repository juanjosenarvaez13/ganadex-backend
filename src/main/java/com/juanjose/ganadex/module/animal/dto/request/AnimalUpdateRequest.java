package com.juanjose.ganadex.module.animal.dto.request;

import com.juanjose.ganadex.module.animal.entity.SexoAnimal;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Actualización de datos descriptivos del animal. Deliberadamente NO incluye
 * "estado": los cambios de estado (VENDIDO, MUERTO, DESCARTADO) son eventos
 * de negocio con sus propias reglas (ej. no se puede "revivir" un animal
 * MUERTO), y van a tener su propio endpoint/método en el Service, no un
 * PUT genérico que permita cualquier transición.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnimalUpdateRequest {

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
}
