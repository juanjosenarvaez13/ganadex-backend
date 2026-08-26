package com.juanjose.ganadex.module.pasture.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/** Registra que un animal entra a un potrero (abre un registro de historial). */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistrarEntradaPotreroRequest {

    @NotNull(message = "El animal es obligatorio.")
    private Long animalId;

    @NotNull(message = "El potrero es obligatorio.")
    private Long potreroId;

    @NotNull(message = "La fecha de entrada es obligatoria.")
    @PastOrPresent(message = "La fecha de entrada no puede ser futura.")
    private LocalDateTime fechaEntrada;

    private String observaciones;
}
