package com.juanjose.ganadex.module.breed.dto.response;

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
public class RazaResponse {

    private Long id;
    private String nombre;
    private String descripcion;
}
