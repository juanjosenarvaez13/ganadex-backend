package com.juanjose.ganadex.module.inventory.dto.response;

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
public class ProductoResponse {

    private Long id;
    private String nombre;
    private String descripcion;
    private String categoria;
    private String unidadMedida;
    private BigDecimal stockActual;
    private BigDecimal stockMinimo;
    private String observaciones;

    /** Derivado (stockActual <= stockMinimo) — útil para pintar una alerta en el frontend sin recalcular ahí. */
    private boolean stockBajo;
}
