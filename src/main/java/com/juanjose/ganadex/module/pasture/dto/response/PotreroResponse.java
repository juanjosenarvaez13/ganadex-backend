package com.juanjose.ganadex.module.pasture.dto.response;

import com.juanjose.ganadex.module.pasture.entity.EstadoPotrero;
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
public class PotreroResponse {

    private Long id;
    private String nombre;
    private BigDecimal area;
    private String tipoPasto;
    private EstadoPotrero estado;
    private String observaciones;
}
