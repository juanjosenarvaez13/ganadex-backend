package com.juanjose.ganadex.module.inventory.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Crea/edita el catálogo de un producto. Deliberadamente NO incluye
 * "stockActual": el stock se deriva únicamente de MovimientoInventario
 * (ENTRADA/SALIDA/AJUSTE), nunca se edita directamente — así la trazabilidad
 * de inventario queda completa y auditable.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductoRequest {

    @NotBlank(message = "El nombre del producto es obligatorio.")
    @Size(max = 150)
    private String nombre;

    private String descripcion;

    @Size(max = 100)
    private String categoria;

    @NotBlank(message = "La unidad de medida es obligatoria.")
    @Size(max = 20)
    private String unidadMedida;

    @NotNull(message = "El stock mínimo es obligatorio.")
    @PositiveOrZero(message = "El stock mínimo no puede ser negativo.")
    private BigDecimal stockMinimo;

    private String observaciones;
}
