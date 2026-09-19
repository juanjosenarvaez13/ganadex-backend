package com.juanjose.ganadex.module.dashboard.dto.response;

import java.math.BigDecimal;

/**
 * Fila de la alerta de "stock bajo" del dashboard. No reutiliza
 * {@code ProductoResponse} completo a propósito: el dashboard solo necesita
 * los campos que se muestran en la alerta, no el producto entero.
 */
public record ProductoStockBajoResponse(
        Long id,
        String nombre,
        BigDecimal stockActual,
        BigDecimal stockMinimo,
        String unidadMedida
) {
}
