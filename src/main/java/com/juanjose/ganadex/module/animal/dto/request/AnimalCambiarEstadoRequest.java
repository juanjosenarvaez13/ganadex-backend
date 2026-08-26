package com.juanjose.ganadex.module.animal.dto.request;

import com.juanjose.ganadex.module.animal.entity.EstadoAnimal;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** DTO para el endpoint dedicado de cambio de estado (VENDIDO/MUERTO/DESCARTADO). */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnimalCambiarEstadoRequest {

    @NotNull(message = "El nuevo estado es obligatorio.")
    private EstadoAnimal nuevoEstado;

    private String observaciones;
}
