package com.juanjose.ganadex.module.pasture.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PotreroRequest {

    @NotBlank(message = "El nombre del potrero es obligatorio.")
    @Size(max = 100)
    private String nombre;

    @NotNull(message = "El área es obligatoria.")
    @Positive(message = "El área debe ser mayor a cero.")
    private BigDecimal area;

    @Size(max = 100)
    private String tipoPasto;

    private String observaciones;
}
