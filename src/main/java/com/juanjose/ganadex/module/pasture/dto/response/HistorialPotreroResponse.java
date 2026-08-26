package com.juanjose.ganadex.module.pasture.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistorialPotreroResponse {

    private Long id;

    private Long animalId;
    private String animalNumeroArete;

    private Long potreroId;
    private String potreroNombre;

    private LocalDateTime fechaEntrada;
    private LocalDateTime fechaSalida;
    private String observaciones;
}
