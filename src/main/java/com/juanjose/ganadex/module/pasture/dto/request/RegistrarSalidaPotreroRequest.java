package com.juanjose.ganadex.module.pasture.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Registra la salida de un animal de su potrero actual (cierra el registro
 * abierto). El id del registro de historial a cerrar, o el animalId, se pasa
 * en el path del endpoint — no en este body.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistrarSalidaPotreroRequest {

    @NotNull(message = "La fecha de salida es obligatoria.")
    @PastOrPresent(message = "La fecha de salida no puede ser futura.")
    private LocalDateTime fechaSalida;

    private String observaciones;
}
